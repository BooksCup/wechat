package com.bc.wechat.entity;

/**
 * 地区
 *
 * @author zhou
 */
public class Region {

    /**
     * 地区名
     */
    private String name;

    /**
     * 区号
     */
    private String code;

    public Region() {

    }

    public Region(String name) {
        this.name = name;
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
}
