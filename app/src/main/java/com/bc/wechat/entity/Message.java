package com.bc.wechat.entity;

import com.orm.SugarRecord;

public class Message extends SugarRecord{
    private String content;
    private String createTime;

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
}
