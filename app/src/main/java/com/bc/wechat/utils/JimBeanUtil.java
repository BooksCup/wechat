package com.bc.wechat.utils;

import com.bc.wechat.entity.User;

import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class JimBeanUtil {
    public static User transferGroupMemberInfoToUser(GroupMemberInfo groupMemberInfo) {
        User user = new User();
        // 极光用户信息(userInfo)
        UserInfo userInfo = groupMemberInfo.getUserInfo();
        user.setUserId(userInfo.getUserName());
        user.setUserNickName(userInfo.getNickname());
        user.setUserAvatar(userInfo.getAvatar());
        return user;
    }
}
