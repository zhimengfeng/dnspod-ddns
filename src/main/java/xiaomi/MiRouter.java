package xiaomi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.HttpUtils;
import utils.LogUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.Key;
import java.util.*;

/**
 * 小米路由器，登录、获取公网IP
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-13 17:58
 */
public class MiRouter {

    /**
     * 路由器IP地址
     */
    public static String router_ip;

    /**
     * 登录密码
     */
    public static String login_pass;

    /**
     * 登录URI
     */
    private static String login_uri = "/cgi-bin/luci/api/xqsystem/login";

    /**
     * 获取公网IP的URI
     */
    private static String getPubIp_uri = "/cgi-bin/luci/";

    /**
     * 获取公网IP的URI后缀
     */
    private static String getPubIp_uri_suffix = "/api/xqnetwork/pppoe_status";

    /**
     * 宽带重新连接接口的URI后缀
     */
    private static String ppoeReConn_uri_suffix = "/api/xqnetwork/pppoe_start";

    /**
     * 登录成功后返回的访问令牌
     */
    private String token;

    /**
     * session末次活动时间
     */
    private Date lastActiveTime;

    /**
     * session连接超时时间，先保守估计一下10分钟
     */
    private static Integer sessionTimeoutMinutes = 10;

    /**
     * http请求工具
     */
    private HttpUtils httpUtils = new HttpUtils();

    /**
     * json解析工具
     */
    private JsonParser jsonParser = new JsonParser();

    /**
     * 登录小米路由器
     */
    public void login() {
        String loginUrl = "http://" + router_ip + login_uri;

        String mac = getMac();
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String random = String.valueOf((int)(Math.random() * 10000));

        String nonce = "0_" + mac + "_" + timestamp + "_" + random;

        String password = Encrypt.encode(login_pass, nonce);

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("logtype", 2);
        paramMap.put("username", "admin");
        paramMap.put("password", password);
        // "0_04:d9:f5:f9:cd:47_1592045134_2886"
        paramMap.put("nonce", nonce);

        String key;
        StringBuffer sb = new StringBuffer();
        Iterator<String> ite = paramMap.keySet().iterator();
        while (ite.hasNext()) {
            key = ite.next();
            sb.append(key).append("=").append(paramMap.get(key)).append("&");
        }

        String temp = sb.toString();
        String paramStr = temp.substring(0, temp.length() - 1);

        // {"url":"/cgi-bin/luci/;stok=7cd7111d1beae196e718ee3648e67fba/web/home","token":"7cd7111d1beae196e718ee3648e67fba","code":0}
        lastActiveTime = new Date();
        String resStr = httpUtils.post(loginUrl, paramStr);

        JsonObject jsonRoot = jsonParser.parse(resStr).getAsJsonObject();
        token = jsonRoot.get("token").getAsString();
    }

    /**
     * 获取公网IP
     *
     * @return 公网IP
     */
    public IpStatus getPubIp() {
        // 如未登录或超时则执行登录
        ifNotLoginOrTimeoutThenLogin();

        IpStatus ipStatus = new IpStatus();

        // http://192.168.1.1/cgi-bin/luci/;stok=7a920aa1098e73a494263e5223b47a4f/api/xqnetwork/pppoe_status
        String url = "http://" + router_ip + getPubIp_uri + ";stok=" + token + getPubIp_uri_suffix;
        lastActiveTime = new Date();
        String resStr = httpUtils.get(url, null);

        JsonObject jsonRoot = jsonParser.parse(resStr).getAsJsonObject();
        int code = jsonRoot.get("code").getAsInt();

        // 公网IP
        String ip = jsonRoot.get("ip").getAsJsonObject()
                .get("address").getAsString();
        ipStatus.setIp(ip);

        // 公网连接状态：2已连接, 4:已断开
        int status = jsonRoot.get("status").getAsInt();
        ipStatus.setStatus(status);


        return ipStatus;
    }

    /**
     * 重新宽带拨号
     *
     * @return 重连是否成功
     */
    public boolean reConn() {
        // 如未登录或超时则执行登录
        ifNotLoginOrTimeoutThenLogin();

        // http://192.168.1.1/cgi-bin/luci/;stok=7a920aa1098e73a494263e5223b47a4f/api/xqnetwork/pppoe_start
        String url = "http://" + router_ip + getPubIp_uri + ";stok=" + token + ppoeReConn_uri_suffix;
        lastActiveTime = new Date();
        String resStr = httpUtils.get(url, null);

        JsonObject jsonRoot = jsonParser.parse(resStr).getAsJsonObject();
        int code = jsonRoot.get("code").getAsInt();

        // 重新连接是否成功
        return code == 0;
    }

    /**
     * 获取本机mac地址
     *
     * @return mac地址
     */
    private String getMac() {
        InetAddress ia;
        byte[] mac = null;
        try {
            //获取本地IP对象
            ia = InetAddress.getLocalHost();
            //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //下面代码是把mac地址拼装成String
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<mac.length;i++){
            if(i!=0){
                sb.append(":");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length()==1?0+s:s);
        }

        //把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase();
    }

    /**
     * 检测是否已登录，如果未登录或者登录超时，则执行登录
     */
    private void ifNotLoginOrTimeoutThenLogin() {
        // 已登录则直接退出
        if (null != lastActiveTime && null != token && !"".equals(token)) {
            // 超时时间
            Date timeoutMoment = new Date(lastActiveTime.getTime() + sessionTimeoutMinutes * 60 * 1000);

            if (new Date().before(timeoutMoment)) {
                return;
            }
        }

        login();
    }
}
