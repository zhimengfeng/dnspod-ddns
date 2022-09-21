package dnspod;

import com.google.gson.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordListResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyDynamicDNSRequest;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyDynamicDNSResponse;
import dnspod.bean.Record;
import utils.LogUtils;

import java.util.*;

/**
 * 腾讯云 API 3.0
 * dnspod.tencentcloudapi.com
 */
public class DNSPodV3 {

    /**
     * 己方要操作的域名（主域名，不包括 www，例如：qcloud.com）
     */
    private String myDomain;

    /**
     * 密钥ID, 可前往https://console.cloud.tencent.com/cam/capi网站进行获取
     */
    private String secretId;

    /**
     * 密钥, 可前往https://console.cloud.tencent.com/cam/capi网站进行获取
     */
    private String secretKey;

    /**
     * 需要修改记录的匹配规则
     * 例如：subDomain: "www", type: "A"
     */
    private List<Map<String, String>> recordMatchRuleList;

    /**
     * 就近地域接入域名
     */
    private String endpoint = "dnspod.tencentcloudapi.com";

    /**
     * json解析工具
     */
    private final Gson gson = new Gson();

    /**
     * 腾讯云认真对象
     */
    private Credential credential;

    /**
     * 初始化认证对象
     */
    private void initCredential() {
        if (credential == null)
            credential = new Credential(secretId, secretKey);
    }

    /**
     * 从DNSPod查询所有的解析记录
     *
     * @return 解析记录列表
     */
    public List<Record> queryRecordList() throws TencentCloudSDKException {
        List<Record> resultList = new ArrayList<>();

        // 初始化认证对象
        initCredential();

        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);

        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        // 实例化要请求产品的client对象,clientProfile是可选的
        DnspodClient client = new DnspodClient(credential, "", clientProfile);

        // 实例化一个请求对象,每个接口都会对应一个request对象
        DescribeRecordListRequest req = new DescribeRecordListRequest();
        req.setDomain(myDomain);
        // 返回的resp是一个DescribeRecordListResponse的实例，与请求对象对应
        DescribeRecordListResponse resp = client.DescribeRecordList(req);
        String resultStr = DescribeRecordListResponse.toJsonString(resp);

        // 输出json格式的字符串回包
        // LogUtils.println(resultStr);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonRoot = jsonParser.parse(resultStr).getAsJsonObject();

        JsonArray recordList = jsonRoot.get("RecordList").getAsJsonArray();
        if (recordList.size() >= 0) {
            Record record;
            JsonObject tempObject;
            for (int i = 0; i < recordList.size(); i++) {
                record = new Record();
                tempObject = recordList.get(i).getAsJsonObject();

                record.setId(tempObject.get("RecordId").getAsLong());
                record.setTtl(tempObject.get("TTL").getAsInt());
                record.setValue(tempObject.get("Value").getAsString());
                record.setEnabled("ENABLE".equals(tempObject.get("Status").getAsString()) ? 1 : 0);
                record.setStatus(tempObject.get("Status").getAsString());
                record.setUpdated_on(tempObject.get("UpdatedOn").getAsString());
                record.setName(tempObject.get("Name").getAsString());
                record.setLine(tempObject.get("Line").getAsString());
                record.setLine_id(tempObject.get("LineId").getAsString());
                record.setType(tempObject.get("Type").getAsString());
                record.setRemark(tempObject.get("Remark").getAsString());
                record.setMx(tempObject.get("MX").getAsInt());

                resultList.add(record);
            }
        }
        else {
            LogUtils.println("解析列表为空：" + resultStr);
        }

        return resultList;
    }

    /**
     * 获取匹配的解析记录列表（检索出需要修改IP地址的解析列表）
     *
     * @param recordList
     * @return
     */
    public List<Record> getMatchedRecordList(List<Record> recordList) {
        List<Record> matchedRecordList = new ArrayList<Record>();

        Map<String, String> ruleMap;
        Iterator<Map<String, String>> iteRule;
        for (Record item : recordList) {
            iteRule = recordMatchRuleList.iterator();
            while (iteRule.hasNext()) {
                ruleMap = iteRule.next();
                if (ruleMap.get("subDomain").equals(item.getName())
                        && ruleMap.get("type").equals(item.getType())) {
                    matchedRecordList.add(item);
                    break;
                }
            }
        }

        return matchedRecordList;
    }

    /**
     * 更新解析记录解析的IP地址
     *
     * @param record 解析记录
     * @param ip 解析至主机的IP
     */
    public void updateRecordIp(Record record, String ip) throws TencentCloudSDKException {
        // 初始化认证对象
        initCredential();

        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);

        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        // 实例化要请求产品的client对象,clientProfile是可选的
        DnspodClient client = new DnspodClient(credential, "", clientProfile);

        // 实例化一个请求对象,每个接口都会对应一个request对象
        ModifyDynamicDNSRequest req = new ModifyDynamicDNSRequest();
        req.setDomain(myDomain);
        req.setRecordId(record.getId());
        req.setSubDomain(record.getName());
        req.setRecordLine(record.getLine());
        req.setValue(ip);
        // 返回的resp是一个ModifyDynamicDNSResponse的实例，与请求对象对应
        ModifyDynamicDNSResponse resp = client.ModifyDynamicDNS(req);
        String resultStr = ModifyDynamicDNSResponse.toJsonString(resp);
        // 输出json格式的字符串回包
        LogUtils.println(resultStr);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonRoot = jsonParser.parse(resultStr).getAsJsonObject();

        if (jsonRoot.get("Error") != null && !"".equals(jsonRoot.get("Error").getAsString())) {
            LogUtils.println("更新失败！" + resultStr);
        }
        else {
            LogUtils.println("类型为[" + record.getType() + "]的子域名["
                    + record.getName() + "] -> [" + ip + "]" + "更新成功！");
        }
    }

    /**
     * 仅用于debug测试
     * @param args 未使用
     */
    public static void main(String[] args) {
        String secretId = "**************";
        String secretKey = "**************************";
        String myDomain = "baidu.com";
        String endpoint = "dnspod.tencentcloudapi.com";
        try{
            DNSPodV3 v3 = new DNSPodV3();
            v3.setSecretId(secretId);
            v3.setSecretKey(secretKey);
            v3.setMyDomain(myDomain);
            v3.setEndpoint(endpoint);

            List<Map<String, String>> matchRuleList = new ArrayList<>();
            Map<String, String> temp = new HashMap<>();
            temp.put("subDomain", "www");
            temp.put("type", "A");
            matchRuleList.add(temp);

            temp = new HashMap<>();
            temp.put("subDomain", "*");
            temp.put("type", "A");
            matchRuleList.add(temp);

            temp = new HashMap<>();
            temp.put("subDomain", "@");
            temp.put("type", "A");
            matchRuleList.add(temp);

            v3.setRecordMatchRuleList(matchRuleList);

            List<Record> list = v3.queryRecordList();
            System.out.println(list);

            List<Record> matchedRecordList = v3.getMatchedRecordList(list);
            if (matchedRecordList.size() > 0) {
                for (Record item : matchedRecordList) {
                    v3.updateRecordIp(item, "111.111.111.111");
                }
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

    public String getMyDomain() {
        return myDomain;
    }

    public void setMyDomain(String myDomain) {
        this.myDomain = myDomain;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Map<String, String>> getRecordMatchRuleList() {
        return recordMatchRuleList;
    }

    public void setRecordMatchRuleList(List<Map<String, String>> recordMatchRuleList) {
        this.recordMatchRuleList = recordMatchRuleList;
    }
}
