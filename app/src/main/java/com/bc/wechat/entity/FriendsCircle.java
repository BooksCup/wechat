package com.bc.wechat.entity;

public class FriendsCircle {
    private int otherlayout;  //0代表一种 1代表另一种
    private String name;
    private int images;  //头像
    private int contentimages;  //头像
    private String content; //内容
    private String date;   //发表时间
    private String praise;  //点赞人
    private String commentary; //评论
    public int getImages() {
        return images;
    }
    public void setImages(int images) {
        this.images = images;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getPraise() {
        return praise;
    }
    public void setPraise(String praise) {
        this.praise = praise;
    }
    public String getCommentary() {
        return commentary;
    }
    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getOtherlayout() {
        return otherlayout;
    }
    public void setOtherlayout(int otherlayout) {
        this.otherlayout = otherlayout;
    }
    public int getContentimages() {
        return contentimages;
    }
    public void setContentimages(int contentimages) {
        this.contentimages = contentimages;
    }

}
