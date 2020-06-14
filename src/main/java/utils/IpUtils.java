package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公网IP获取工具类，采用第三方网站(ip.chinaz.com)的方式获取
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-07 23:07
 */
public class IpUtils {

    /**
     * 查询公网IP的外部网站
     */
    public static String IP_CHECK_SOURCE_URL = "http://ip.chinaz.com";

    /**
     * 匹配的正则表达式
     */
    public static String REG_PATTERN = "\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>";

    public static void main(String[] args) {
        String ip = getV4IP();
        LogUtils.println("ip：" + ip);
    }

    /**
     * 获取公网IP
     *
     * @return
     */
    public static String getV4IP(){
        String ip = null;

        StringBuilder htmlSb = new StringBuilder();

        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        try {
            url = new URL(IP_CHECK_SOURCE_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));

            String read = "";
            while((read = in.readLine()) != null){
                htmlSb.append(read + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Pattern p = Pattern.compile(REG_PATTERN);
        Matcher m = p.matcher(htmlSb.toString());
        if(m.find()){
            ip = m.group(1);
        }
        return ip;
    }
}
