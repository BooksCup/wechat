package com.bc.wechat.entity;

import com.orm.SugarRecord;

import java.util.List;

public class FriendsCircle extends SugarRecord {
    private String circleId;
    private String userId;
    private String userNickName;
    private String userAvatar;
    private String circleContent;
    private String circlePhotos;
    private String createTime;
    private Long timestamp;
    private List<User> likeUserList;
    private String likeUserJsonArray;

    private List<FriendsCircleComment> friendsCircleCommentList;
    private String friendsCircleCommentJsonArray;

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

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

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getCircleContent() {
        return circleContent;
    }

    public void setCircleContent(String circleContent) {
        this.circleContent = circleContent;
    }

    public String getCirclePhotos() {
        return circlePhotos;
    }

    public void setCirclePhotos(String circlePhotos) {
        this.circlePhotos = circlePhotos;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<User> getLikeUserList() {
        return likeUserList;
    }

    public void setLikeUserList(List<User> likeUserList) {
        this.likeUserList = likeUserList;
    }

    public String getLikeUserJsonArray() {
        return likeUserJsonArray;
    }

    public void setLikeUserJsonArray(String likeUserJsonArray) {
        this.likeUserJsonArray = likeUserJsonArray;
    }

    public List<FriendsCircleComment> getFriendsCircleCommentList() {
        return friendsCircleCommentList;
    }

    public void setFriendsCircleCommentList(List<FriendsCircleComment> friendsCircleCommentList) {
        this.friendsCircleCommentList = friendsCircleCommentList;
    }

    public String getFriendsCircleCommentJsonArray() {
        return friendsCircleCommentJsonArray;
    }

    public void setFriendsCircleCommentJsonArray(String friendsCircleCommentJsonArray) {
        this.friendsCircleCommentJsonArray = friendsCircleCommentJsonArray;
    }
}
