package dnspod;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dnspod.bean.Record;
import utils.HmacSHA1Utils;
import utils.HttpUtils;
import utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DNSPod动态域名解析类
 *
 * 接口网站：https://cloud.tencent.com/document/product/302/4032
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-12 21:45
 */
public class DNSPod {

    /**
     * 己方要操作的域名（主域名，不包括 www，例如：qcloud.com）
     */
    public static String myDomain;

    /**
     * 密钥ID
     */
    public static String secretId;

    /**
     * 密钥
     */
    public static String secretKey;

    /**
     * 需要修改记录的匹配规则
     *
     * 例如：subDomain: "www", type: "A"
     */
    public static List<Map<String, String>> recordMatchRuleList;

    /**
     * http请求工具类
     */
    private HttpUtils httpUtils = new HttpUtils();

    /**
     * json解析工具
     */
    private Gson gson = new Gson();

    /**
     * DNSPod接口请求域名
     */
    private static String apiDomain = "cns.api.qcloud.com";

    /**
     * DNSPod接口请求路径
     */
    private static String apiPath = "/v2/index.php";

    /**
     * 接口请求方法
     */
    private static String apiRequestMethod = "GET";

    /**
     * urlEncoding编码方式
     */
    private static final String ENCODING_UTF8 = "UTF-8";

    /**
     * 从DNSPod查询所有的解析记录
     *
     * @return
     */
    public List<Record> queryRecordList() {

        // 查询入参
        Map<String, Object> inParamMap = new TreeMap<String, Object>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                }
        );

        // 接口参数部分
        // 要操作的域名（主域名，不包括 www，例如：qcloud.com）
        inParamMap.put("domain", myDomain);
        // 偏移量 默认0
        inParamMap.put("offset", 0);
        // 可选，返回数量，默认20，最大值100
        inParamMap.put("length", 100);
//        // 子域名 （过滤条件）根据子域名进行过滤
//        inParamMap.put("subDomain", "*");

        // 公共请求参数部分
        // 接口名称
        inParamMap.put("Action", "RecordList");
        inParamMap.put("Timestamp", System.currentTimeMillis()/1000);
        inParamMap.put("Nonce", 100000 + (int)(Math.random() * 100000));
        inParamMap.put("SecretId", secretId);
//        inParamMap.put("SignatureMethod", "HmacSHA1");

        String url = "https://" + apiDomain + apiPath;
        String params = signature(inParamMap);

        LogUtils.println("params:" + params);

        // 请求dnspod查询
        String resStr = httpUtils.get(url, params);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonRoot = jsonParser.parse(resStr).getAsJsonObject();

        int code = jsonRoot.get("code").getAsInt();
        if (code == 0) {
            JsonArray jsonArrayRecords = jsonRoot.getAsJsonObject("data").getAsJsonArray("records");
            return gson.fromJson(jsonArrayRecords, new TypeToken<List<Record>>() {}.getType());
        }
        else {
            LogUtils.println("查询解析列表失败！" + decodeUnicode(resStr));
            return null;
        }
    }

    /**
     * 更新解析记录解析的IP地址
     *
     * @param record 解析记录
     * @param ip 解析至主机的IP
     */
    public void updateRecordIp(Record record, String ip) {
        // 入参
        Map<String, Object> inParamMap = new TreeMap<String, Object>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                }
        );

        // 公共请求参数部分
        // 接口名称
        inParamMap.put("Action", "RecordModify");
        inParamMap.put("Timestamp", System.currentTimeMillis()/1000);
        inParamMap.put("Nonce", 100000 + (int)(Math.random() * 100000));
        inParamMap.put("SecretId", secretId);

        // 接口参数部分
        // 要操作的域名（主域名，不包括www，例如：qcloud.com）
        inParamMap.put("domain", myDomain);
        // 解析记录的 ID
        inParamMap.put("recordId", record.getId());
        // 子域名 （过滤条件）根据子域名进行过滤
        inParamMap.put("subDomain", record.getName());
        // 记录类型，可选的记录类型为："A"，"CNAME"，"MX"，"TXT"，"NS"，"AAAA"，"SRV"
        inParamMap.put("recordType", record.getType());
        // 记录的线路名称
        inParamMap.put("recordLine", record.getLine());
        // 记录值
        inParamMap.put("value", ip);

        String url = "https://" + apiDomain + apiPath;
        String paramStr = signature(inParamMap);

        // 请求dnspod执行更新
        String resStr = httpUtils.get(url, paramStr);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonRoot = jsonParser.parse(resStr).getAsJsonObject();
        int code = jsonRoot.get("code").getAsInt();

        if (code == 0) {
            LogUtils.println("类型为[" + record.getType() + "]的子域名["
                    + record.getName() + "] -> [" + ip + "]" + "更新成功！");
        }
        else {
            LogUtils.println("更新失败！" + decodeUnicode(resStr));
        }
    }

    /**
     * 签名
     *
     * @param inParamMap 入参
     * @return 签名完成的参数列表
     */
    private String signature(Map<String, Object> inParamMap) {
        String key;
        StringBuffer sb = new StringBuffer();
        Iterator<String> ite = inParamMap.keySet().iterator();
        while (ite.hasNext()) {
            key = ite.next();
            sb.append(key).append("=").append(inParamMap.get(key)).append("&");
        }

        String temp = sb.toString();
        // 请求字符串
        String requestStr = temp.substring(0, temp.length() - 1);
        // 签名原文字符串 请求方法 + 请求主机 +请求路径 + ? + 请求字符串
        String signatureSourceStr = apiRequestMethod + apiDomain + apiPath + "?" + requestStr;

        String signatureStr = null;
        HmacSHA1Utils sha1Utils = new HmacSHA1Utils();
        try {
            signatureStr = sha1Utils.signature(signatureSourceStr, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String signature = null;
        try {
            signature = URLEncoder.encode(signatureStr, ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sb = new StringBuffer();
        ite = inParamMap.keySet().iterator();
        try {
            while (ite.hasNext()) {
                key = ite.next();
                sb.append(key).append("=").append(URLEncoder.encode(inParamMap.get(key).toString(), ENCODING_UTF8)).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sb.append("Signature").append("=").append(signature);
        return sb.toString();
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
     * 将Unicode转为UTF-8中文
     *
     * @param str 入参字符串
     * @return
     */
    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher(str);
        int start = 0;
        int start2 = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find(start)) {
            start2 = m.start();
            if (start2 > start) {
                String seg = str.substring(start, start2);
                sb.append(seg);
            }
            String code = m.group(1);
            int i = Integer.valueOf(code, 16);
            byte[] bb = new byte[4];
            bb[0] = (byte) ((i >> 8) & 0xFF);
            bb[1] = (byte) (i & 0xFF);
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
            start = m.end();
        }
        start2 = str.length();
        if (start2 > start) {
            String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }
}
