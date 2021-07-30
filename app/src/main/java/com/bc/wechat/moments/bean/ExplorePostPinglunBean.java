package com.bc.wechat.moments.bean;

public class ExplorePostPinglunBean {

    private String altnickname;
    private String altuserid;
    private String content;
    private long creattime;
    private int friendid;
    private int id;
    private int userid;
    private String usernickname;
    private UserBean replyUser; //@的人 回复人信息
    private UserBean commentsUser;  // 评论人信息，，commentuser回复repleyuser

    public String getAltnickname() {
        return altnickname;
    }

    public void setAltnickname(String altnickname) {
        this.altnickname = altnickname;
    }

    public String getAltuserid() {
        return altuserid;
    }

    public void setAltuserid(String altuserid) {
        this.altuserid = altuserid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreattime() {
        return creattime;
    }

    public void setCreattime(long creattime) {
        this.creattime = creattime;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsernickname() {
        return usernickname;
    }

    public void setUsernickname(String usernickname) {
        this.usernickname = usernickname;
    }

    public UserBean getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(UserBean replyUser) {
        this.replyUser = replyUser;
    }

    public UserBean getCommentsUser() {
        return commentsUser;
    }

    public void setCommentsUser(UserBean commentsUser) {
        this.commentsUser = commentsUser;
    }
}
