package utils;

import java.util.Date;

/**
 * 日期处理工具
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-14 13:46
 */
public class DateUtils {

    /**
     * 给日期添加分钟
     *
     * @param date 日期
     * @param minutes 分钟
     * @return 添加分钟后的日期
     */
    public static Date addMinutes(Date date, int minutes) {
        return new Date(date.getTime() + minutes * 60 * 1000);
    }

}
