package config;

import java.util.List;
import java.util.Map;

/**
 * 系统配置参数
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-14 15:03
 */
public class Config {

    /**
     * 己方要操作的域名（主域名，不包括 www，例如：qcloud.com）
     */
    private String myDomain;

    /**
     * dnspod密钥ID,
     */
    private String dnspodSecretId;

    /**
     * dnspod密钥
     */
    private String dnspodSecretKey;

    /**
     * 需要修改记录的匹配规则
     */
    private List<Map<String, String>> dnspodRecordMatchRuleList;

    /**
     * 就近地域接入域名
     * https://cloud.tencent.com/document/api/1140/40508
     */
    private String endpoint = "dnspod.tencentcloudapi.com";

    /**
     * 小米路由器IP
     */
    private String miRouterIp;

    /**
     * 小米路由器登录密码
     */
    private String miRouterLoginPass;

    public String getMyDomain() {
        return myDomain;
    }

    public void setMyDomain(String myDomain) {
        this.myDomain = myDomain;
    }

    public String getDnspodSecretId() {
        return dnspodSecretId;
    }

    public void setDnspodSecretId(String dnspodSecretId) {
        this.dnspodSecretId = dnspodSecretId;
    }

    public String getDnspodSecretKey() {
        return dnspodSecretKey;
    }

    public void setDnspodSecretKey(String dnspodSecretKey) {
        this.dnspodSecretKey = dnspodSecretKey;
    }

    public List<Map<String, String>> getDnspodRecordMatchRuleList() {
        return dnspodRecordMatchRuleList;
    }

    public void setDnspodRecordMatchRuleList(List<Map<String, String>> dnspodRecordMatchRuleList) {
        this.dnspodRecordMatchRuleList = dnspodRecordMatchRuleList;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMiRouterIp() {
        return miRouterIp;
    }

    public void setMiRouterIp(String miRouterIp) {
        this.miRouterIp = miRouterIp;
    }

    public String getMiRouterLoginPass() {
        return miRouterLoginPass;
    }

    public void setMiRouterLoginPass(String miRouterLoginPass) {
        this.miRouterLoginPass = miRouterLoginPass;
    }
}
