package com.bc.wechat.dao;

import com.bc.wechat.entity.FriendApply;

import java.util.List;

public class FriendApplyDao {
    public FriendApply getFriendApplyByApplyId(String applyId) {
        List<FriendApply> friendApplyList = FriendApply.find(FriendApply.class, "apply_id = ?", applyId);
        if (null != friendApplyList && friendApplyList.size() > 0) {
            return friendApplyList.get(0);
        }
        return null;
    }
}
