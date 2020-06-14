package utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * HmacSHA1工具类
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-12 23:56
 */
public class HmacSHA1Utils {

    private static final String MAC_NAME = "HmacSHA1";

    public static final String ENCODING = "UTF-8";

    /**
     * Hmac-SHA1 签名算法
     *
     * @param encryptText 加密字符源串
     * @param encryptKey 加密密钥
     * @return 签名值
     * @throws Exception
     */
    public static String signature(String encryptText, String encryptKey) throws Exception{
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);

        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        byte[] digest = mac.doFinal(text);
        return new String(Base64.getEncoder().encode(digest));
    }

    /**
     * @Comment SHA1实现
     * @Author Ron
     * @Date 2017年9月13日 下午3:30:36
     * @return
     */
    public static String sha1(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            LogUtils.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
