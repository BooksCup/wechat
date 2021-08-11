package com.bc.wechat.enums;

/**
 * 朋友圈类型
 *
 * @author zhou
 */
public enum MomentsType {

    /**
     * 朋友圈类型
     */
    HEADER("0", "头"),
    TEXT("1", "文字"),
    IMAGE("2", "图片"),
    VIDEO("3", "视频");

    MomentsType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}