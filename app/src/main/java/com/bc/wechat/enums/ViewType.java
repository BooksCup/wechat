package com.bc.wechat.enums;

/**
 * 页面类型
 *
 * @author zhou
 */
public enum ViewType {

    /**
     * 页面类型
     */
    MAIN(0, "主页面"),
    CHAT(1, "聊天页面"),
    ;

    ViewType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}