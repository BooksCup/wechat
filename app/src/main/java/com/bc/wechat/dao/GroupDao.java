package com.bc.wechat.dao;

import com.bc.wechat.entity.GroupInfo;

import java.util.List;

public class GroupDao {
    public GroupInfo getGroupByjId(String jId) {
        List<GroupInfo> groupList = GroupInfo.find(GroupInfo.class, "j_id = ?", jId);
        if (null != groupList && groupList.size() > 0) {
            return groupList.get(0);
        }
        return null;
    }
}
