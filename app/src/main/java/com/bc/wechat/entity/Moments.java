package com.bc.wechat.entity;


import java.util.List;

/**
 * 朋友圈
 *
 * @author zhou
 */
public class Moments {

    private String momentsId;
    private String userId;
    private String type;
    private String userNickName;
    private String userAvatar;
    private String content;
    private String photos;
    private String createTime;
    private Long timestamp;

    /**
     * 点赞用户列表
     */
    private List<User> likeUserList;

    /**
     * 评论列表
     */
    private List<MomentsComment> momentsCommentList;

    public String getMomentsId() {
        return momentsId;
    }

    public void setMomentsId(String momentsId) {
        this.momentsId = momentsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
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

    public List<MomentsComment> getMomentsCommentList() {
        return momentsCommentList;
    }

    public void setMomentsCommentList(List<MomentsComment> momentsCommentList) {
        this.momentsCommentList = momentsCommentList;
    }
}
