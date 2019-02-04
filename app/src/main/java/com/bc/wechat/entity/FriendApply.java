package com.bc.wechat.entity;

import com.orm.SugarRecord;

public class FriendApply extends SugarRecord {
    private String fromNickName;
    private long timeStamp;

    public String getFromNickName() {
        return fromNickName;
    }

    public void setFromNickName(String fromNickName) {
        this.fromNickName = fromNickName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
