package com.bc.wechat.entity;

import com.orm.SugarRecord;

public class GroupInfo extends SugarRecord {

    private String groupId;

    /**
     * 群主userId
     */
    private String owner;

    /**
     * 群组名字
     * 支持的字符：全部，包括 Emoji。
     */
    private String name;

    /**
     * 群描述
     * 支持的字符：全部，包括 Emoji。
     */
    private String desc;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 极光
     */
    private Long jId;

    private String groupAvatars;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Long getjId() {
        return jId;
    }

    public void setjId(Long jId) {
        this.jId = jId;
    }

    public String getGroupAvatars() {
        return groupAvatars;
    }

    public void setGroupAvatars(String groupAvatars) {
        this.groupAvatars = groupAvatars;
    }
}
