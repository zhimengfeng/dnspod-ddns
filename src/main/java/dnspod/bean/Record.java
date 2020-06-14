package dnspod.bean;

import java.util.List;

/**
 * 解析记录实体
 *
 * @Author: zhimengfeng
 * @Date: 2020-06-12 22:24
 */
public class Record {

    /**
     * 解析记录的ID
     */
    private Integer id;

    /**
     * 记录的 TTL 值
     */
    private Integer ttl;

    /**
     * 记录的值
     */
    private String value;

    /**
     * 记录的暂停、启用状态，1和0分别代表启用和暂停
     */
    private Integer enabled;

    /**
     *
     */
    private String status;

    /**
     * 解析记录的最后修改时间
     */
    private String updated_on;

    /**
     * 解析记录所属的项目 ID
     */
    private Integer q_project_id;

    /**
     * 子域名
     */
    private String name;

    /**
     * 解析记录的线路名称
     */
    private String line;

    /**
     * 解析记录的线路编号
     */
    private String line_id;

    /**
     * 解析记录的类型
     */
    private String type;

    /**
     * 解析记录的备注信息
     */
    private String remark;

    /**
     * MX 记录的优先级，非 MX 记录的话，该值为0
     */
    private Integer mx;

    /**
     *
     */
    private String hold;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public Integer getQ_project_id() {
        return q_project_id;
    }

    public void setQ_project_id(Integer q_project_id) {
        this.q_project_id = q_project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getMx() {
        return mx;
    }

    public void setMx(Integer mx) {
        this.mx = mx;
    }

    public String getHold() {
        return hold;
    }

    public void setHold(String hold) {
        this.hold = hold;
    }
}
