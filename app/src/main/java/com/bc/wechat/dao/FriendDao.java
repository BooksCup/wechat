package com.bc.wechat.dao;

import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;

import java.util.List;

public class FriendDao {

    public void saveFriendByUserInfo(User user) {
        List<Friend> checkList = Friend.find(Friend.class, "user_id = ?", user.getUserId());
        if (null != checkList && checkList.size() > 0) {
            // 好友已存在，更新基本信息
            Friend friend = checkList.get(0);
            friend.setUserNickName(user.getUserNickName());
            friend.setUserAvatar(user.getUserAvatar());
            friend.setUserHeader(CommonUtil.setUserHeader(user.getUserNickName()));
            friend.setUserSex(user.getUserSex());
            friend.setUserLastestCirclePhotos(user.getUserLastestCirclePhotos());
            Friend.save(friend);
        } else {
            // 不存在,插入sqlite
            Friend friend = new Friend();
            friend.setUserId(user.getUserId());
            friend.setUserNickName(user.getUserNickName());
            friend.setUserAvatar(user.getUserAvatar());
            friend.setUserHeader(CommonUtil.setUserHeader(user.getUserNickName()));
            friend.setUserSex(user.getUserSex());
            friend.setUserLastestCirclePhotos(user.getUserLastestCirclePhotos());
            Friend.save(friend);
        }
    }

    public Friend getFriendById(String userId) {
        List<Friend> friendList = Friend.find(Friend.class, "user_id = ?", userId);
        if (null != friendList && friendList.size() > 0) {
            return friendList.get(0);
        } else {
            return new Friend();
        }
    }
}
