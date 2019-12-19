package com.bc.wechat.entity;

public class FriendsCircleComment {
    private String commentId;
    private String commentCircleId;
    private String commentUserId;
    private String commentUserNickName;

    private String commentReplyToUserId;
    private String commentReplyToUserNickName;

    private String commentContent;
    private String commentCreateTime;
    private String commentDeleteFlag;
    private String commentDeleteTime;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentCircleId() {
        return commentCircleId;
    }

    public void setCommentCircleId(String commentCircleId) {
        this.commentCircleId = commentCircleId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentUserNickName() {
        return commentUserNickName;
    }

    public void setCommentUserNickName(String commentUserNickName) {
        this.commentUserNickName = commentUserNickName;
    }

    public String getCommentReplyToUserId() {
        return commentReplyToUserId;
    }

    public void setCommentReplyToUserId(String commentReplyToUserId) {
        this.commentReplyToUserId = commentReplyToUserId;
    }

    public String getCommentReplyToUserNickName() {
        return commentReplyToUserNickName;
    }

    public void setCommentReplyToUserNickName(String commentReplyToUserNickName) {
        this.commentReplyToUserNickName = commentReplyToUserNickName;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentCreateTime() {
        return commentCreateTime;
    }

    public void setCommentCreateTime(String commentCreateTime) {
        this.commentCreateTime = commentCreateTime;
    }

    public String getCommentDeleteFlag() {
        return commentDeleteFlag;
    }

    public void setCommentDeleteFlag(String commentDeleteFlag) {
        this.commentDeleteFlag = commentDeleteFlag;
    }

    public String getCommentDeleteTime() {
        return commentDeleteTime;
    }

    public void setCommentDeleteTime(String commentDeleteTime) {
        this.commentDeleteTime = commentDeleteTime;
    }
}
