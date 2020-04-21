package com.bc.wechat.entity;

import com.orm.SugarRecord;

/**
 * 地区
 *
 * @author zhou
 */
public class Area extends SugarRecord {
    /**
     * 地区名
     */
    private String name;

    /**
     * 父地区名
     */
    private String parentName;

    /**
     * 类型
     * "省" "市" "区"
     */
    private String type;

    /**
     * 排序
     */
    private Integer seq;

    private String postCode;

    public Area() {

    }

    public Area(String name, String parentName, String type, Integer seq) {
        this.name = name;
        this.parentName = parentName;
        this.type = type;
        this.seq = seq;
    }

    public Area(String name, String parentName, String type, Integer seq, String postCode) {
        this.name = name;
        this.parentName = parentName;
        this.type = type;
        this.seq = seq;
        this.postCode = postCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
