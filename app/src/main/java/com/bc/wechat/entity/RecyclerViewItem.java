package com.bc.wechat.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerViewItem implements Serializable {

    private String content;                  // 内容信息
    private String createTime;               // 时间
    private String channelId;                // id
    private String nickName;                 // 名字
    private Location location;               // location
    private String headImageUrl;             // 头像
    private ArrayList<MyMedia> mediaList;   // 九宫格数据

    public RecyclerViewItem() {
    }

    public RecyclerViewItem(ArrayList<MyMedia> mediaList, String content, String createTime, String channelId, String nickName, Location location, String headImageUrl) {
        this.mediaList = mediaList;
        this.content = content;
        this.createTime = createTime;
        this.channelId = channelId;
        this.nickName = nickName;
        this.location = location;
        this.headImageUrl = headImageUrl;
    }

    public ArrayList<MyMedia> getMediaList() {
        return mediaList;
    }

    public void setMediaList(ArrayList<MyMedia> mediaList) {
        this.mediaList = mediaList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }
}