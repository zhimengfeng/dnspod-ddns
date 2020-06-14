package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 简单的日志工具
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-13 22:04
 */
public class LogUtils {

    /**
     * 日志输出运行时间时，格式化日期
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void println(String str) {
        System.out.println(dateFormat.format(new Date()) + " " + str);
    }

}
