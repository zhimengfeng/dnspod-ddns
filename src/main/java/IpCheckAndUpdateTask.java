import dnspod.DNSPod;
import dnspod.bean.Record;
import utils.LogUtils;
import xiaomi.IpStatus;
import xiaomi.MiRouter;

import java.util.List;

/**
 * 检测公网IP是否变化，并在变化时，更新域名解析记录。并且每24小时强制更新解析记录。
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-13 21:21
 */
public class IpCheckAndUpdateTask implements Runnable {

    private static String pubIp;

    /**
     * dnspod接口类
     */
    private DNSPod dnsPod = new DNSPod();

    /**
     * 小米路由器接口类
     */
    private MiRouter miRouter = new MiRouter();

    @Override
    public void run() {

        try {
            // 检测计数
            if (Program.testCount * Program.period > 24 * 60) {
                Program.testCount = 0;

                // 每24小时强制更新
                LogUtils.println("强制更新！");
                updateRecord();
                return;
            }

            Program.testCount++;

            // 公网IP是否正常连接（是否已正常拨号至宽带）
            IpStatus ipStatus = miRouter.getPubIp();

            // 非"已连接"状态
            if (ipStatus.getStatus() != 2) {
                // 尝试重连（多次尝试）
                if (!tryReConn()) {
                    // 多次尝试均失败，暂时先停止此次任务
                    return;
                }

                // 重新获取IP
                ipStatus = miRouter.getPubIp();
            }

            if (ipStatus.getIp() == null || "".equals(ipStatus.getIp())) {
                LogUtils.println("公网IP获取错误！");
                // 暂时先停止此次任务
                return;
            }

            if (pubIp == null) {
                pubIp = ipStatus.getIp();
                LogUtils.println("首次启动，调用接口更新！");
            }
            // 公网IP未改变
            else if (pubIp.equals(ipStatus.getIp())) {
                LogUtils.println("公网IP未改变！");
                return;
            }
            else {
                pubIp = ipStatus.getIp();
                LogUtils.println("公网IP发生改变，开始更新！");
            }

            // 更新解析记录
            updateRecord();
        }
        catch (Exception ex) {
            LogUtils.println("程序出现异常：" + ex.toString());
            ex.printStackTrace();
        }
    }

    /**
     * 尝试(如果不成功，则多次重试)重连宽带
     *
     * @return
     */
    private boolean tryReConn() {
        int times = 0;

        try {
            // 如果不成功，则多次尝试重连
            while(!miRouter.reConn()) {
                times++;
                if (times > 5) {
                    return false;
                }
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 更新解析记录
     */
    private void updateRecord() {
        // 获取所有的解析记录列表
        List<Record> recordList = dnsPod.queryRecordList();

        if (recordList == null) {
            return;
        }

        // 获取需要更新的解析记录列表
        List<Record> matchedRecordList = dnsPod.getMatchedRecordList(recordList);

        // 更新解析记录
        for (Record record : matchedRecordList) {
            dnsPod.updateRecordIp(record, pubIp);
        }
    }
}
