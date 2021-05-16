package com.bc.wechat.dao;

import com.bc.wechat.entity.FriendApply;

import java.util.List;

/**
 * 好友申请
 *
 * @author zhou
 */
public class FriendApplyDao {

    /**
     * 根据申请ID获取好友申请详情
     *
     * @param applyId 申请ID
     * @return 好友申请详情
     */
    public FriendApply getFriendApplyByApplyId(String applyId) {
        List<FriendApply> friendApplyList = FriendApply.find(FriendApply.class, "apply_id = ?", applyId);
        if (null != friendApplyList && friendApplyList.size() > 0) {
            return friendApplyList.get(0);
        }
        return null;
    }

}