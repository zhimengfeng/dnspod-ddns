package xiaomi;

/**
 * 公网IP连接状态
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-14 12:59
 */
public class IpStatus {

    /**
     * 连接状态：2已连接, 4已断开
     */
    private Integer status;

    /**
     * 公网IP
     */
    private String ip;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
