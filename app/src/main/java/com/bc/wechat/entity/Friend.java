package com.bc.wechat.entity;

import com.orm.SugarRecord;

/**
 * 好友
 */
public class Friend extends SugarRecord {
    private String userId;
    private String userNickName;
    private String userWxId;
    private String userSex;
    private String userAvatar;
    private String userHeader;
    private String userLastestCirclePhotos;

    // 好友相关
    private String userFriendPhone;
    private String userFriendRemark;
    private String userFriendDesc;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserWxId() {
        return userWxId;
    }

    public void setUserWxId(String userWxId) {
        this.userWxId = userWxId;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserHeader() {
        return userHeader;
    }

    public void setUserHeader(String userHeader) {
        this.userHeader = userHeader;
    }

    public String getUserLastestCirclePhotos() {
        return userLastestCirclePhotos;
    }

    public void setUserLastestCirclePhotos(String userLastestCirclePhotos) {
        this.userLastestCirclePhotos = userLastestCirclePhotos;
    }

    public String getUserFriendPhone() {
        return userFriendPhone;
    }

    public void setUserFriendPhone(String userFriendPhone) {
        this.userFriendPhone = userFriendPhone;
    }

    public String getUserFriendRemark() {
        return userFriendRemark;
    }

    public void setUserFriendRemark(String userFriendRemark) {
        this.userFriendRemark = userFriendRemark;
    }

    public String getUserFriendDesc() {
        return userFriendDesc;
    }

    public void setUserFriendDesc(String userFriendDesc) {
        this.userFriendDesc = userFriendDesc;
    }
}
