package com.bc.wechat.entity;

/**
 * 用户状态
 *
 * @author zhou
 */
public class Status {

    /**
     * 状态名
     */
    private String name;

    /**
     * 状态icon
     */
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}