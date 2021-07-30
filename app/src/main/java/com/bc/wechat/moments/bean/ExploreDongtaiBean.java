package com.bc.wechat.moments.bean;

import java.util.List;

/**
 * Author     wildma
 * DATE       2017/8/3
 * Des	      ${TODO}
 */
public class ExploreDongtaiBean {
    private int id;//该条数据的id
    private int type = 1;//消息类型 1,文本消息，2，图片消息，3，视频消息
    private int userid;//发布人id,
    private String handimg;//发布人头像地址
    private String nickname;//发布人昵称
    private String writtenwords;//内容
    private long creattime;//创建时间
    private String imgs;//图片，逗号隔开
    private String videos;//视频，逗号隔开
    private String thumbnail;//缩略图，逗号隔开
    private int likeuself;//自己是否点了赞
    private List<ExplorePostPinglunBean> evea;//评论list
    private List<ExplorePostDianzanBean> fabulous;//点赞list

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getHandimg() {
        return handimg;
    }

    public void setHandimg(String handimg) {
        this.handimg = handimg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWrittenwords() {
        return writtenwords;
    }

    public void setWrittenwords(String writtenwords) {
        this.writtenwords = writtenwords;
    }

    public long getCreattime() {
        return creattime;
    }

    public void setCreattime(long creattime) {
        this.creattime = creattime;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<ExplorePostPinglunBean> getEvea() {
        return evea;
    }

    public void setEvea(List<ExplorePostPinglunBean> evea) {
        this.evea = evea;
    }

    public List<ExplorePostDianzanBean> getFabulous() {
        return fabulous;
    }

    public void setFabulous(List<ExplorePostDianzanBean> fabulous) {
        this.fabulous = fabulous;
    }

    public int getLikeuself() {
        return likeuself;
    }

    public void setLikeuself(int likeuself) {
        this.likeuself = likeuself;
    }
}
