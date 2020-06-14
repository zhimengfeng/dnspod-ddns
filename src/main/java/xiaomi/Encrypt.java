package xiaomi;

import utils.HmacSHA1Utils;

/**
 * @Author: zhimengfeng
 * @Date: 2020-06-13 18:56
 */
public class Encrypt {

    private static String key = "a2ffa5c9be07488bbb04a3a47d3c5f6a";

    public static String encode(String password, String nonce) {
        String encodeStr = null;
        try {
            encodeStr = HmacSHA1Utils.sha1(nonce + HmacSHA1Utils.sha1(password + key));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encodeStr;
    }

}
