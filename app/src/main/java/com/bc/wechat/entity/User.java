package com.bc.wechat.entity;

import com.orm.SugarRecord;

import java.util.List;

public class User extends SugarRecord {
    private String userId;
    private String userWxId;
    private String userNickName;
    private String userPhone;
    private String userPassword;
    private String userImPassword;
    private String userAvatar;
    private String userHeader;
    private String userSex;
    private String userSign;
    private String userQrCode;
    private String isFriend;

    private List<User> friendList;

    private String userLastestCirclePhotos;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserWxId() {
        return userWxId;
    }

    public void setUserWxId(String userWxId) {
        this.userWxId = userWxId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImPassword() {
        return userImPassword;
    }

    public void setUserImPassword(String userImPassword) {
        this.userImPassword = userImPassword;
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

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public String getUserQrCode() {
        return userQrCode;
    }

    public void setUserQrCode(String userQrCode) {
        this.userQrCode = userQrCode;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }

    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public String getUserLastestCirclePhotos() {
        return userLastestCirclePhotos;
    }

    public void setUserLastestCirclePhotos(String userLastestCirclePhotos) {
        this.userLastestCirclePhotos = userLastestCirclePhotos;
    }
}
