package com.bc.wechat.entity;


import com.orm.SugarRecord;

/**
 * 地区
 *
 * @author zhou
 */
public class Region extends SugarRecord {
    private String parentId;
    private String level;
    private String name;
    private String code;
    private Float seq;


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getSeq() {
        return seq;
    }

    public void setSeq(Float seq) {
        this.seq = seq;
    }
}
