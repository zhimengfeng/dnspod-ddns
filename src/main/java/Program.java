import config.Config;
import dnspod.DNSPod;
import org.yaml.snakeyaml.Yaml;
import utils.LogUtils;
import xiaomi.MiRouter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 程序启动入口
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-13 21:08
 */
public class Program {

    /**
     * 配置信息
     */
    public static Config config;

    /**
     * 定时器检测次数，根据此变量计算是否已运行一天
     * 如果已经运行一天，那么强制更新IP（即使IP未改变也调用一次dnspod的更新解析记录接口）
     */
    public static Integer testCount = 0;

    /**
     * 定时器执行周期，单位：分钟
     */
    public static Integer period = 30;

    /**
     * 配置文件读取工具
     */
    private static Yaml yaml = new Yaml();

    public static void main(String[] args) {
        LogUtils.println("程序启动");

        if (args.length > 0) {
            try {
                int delaySecond = Integer.parseInt(args[0]);
                LogUtils.println("延时" + delaySecond + "秒，等待网络就绪中。。。");
                Thread.sleep(delaySecond * 1000);
            } catch (InterruptedException e) {
                LogUtils.println("启动参数配置错误！");
                e.printStackTrace();
            }
        }

        // 读取配置信息
        config = yaml.loadAs(Program.class.getResourceAsStream("/config.yml"), Config.class);

        // 将配置信息，分配至对应的工具类中
        DNSPod.myDomain = config.getMyDomain();
        DNSPod.secretId = config.getDnspodSecretId();
        DNSPod.secretKey = config.getDnspodSecretKey();
        DNSPod.recordMatchRuleList = config.getDnspodRecordMatchRuleList();

        MiRouter.router_ip = config.getMiRouterIp();
        MiRouter.login_pass = config.getMiRouterLoginPass();

        // 启动线程
        IpCheckAndUpdateTask ipCheckAndUpdateTask = new IpCheckAndUpdateTask();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(ipCheckAndUpdateTask, 0, 30, TimeUnit.MINUTES);
    }

}
