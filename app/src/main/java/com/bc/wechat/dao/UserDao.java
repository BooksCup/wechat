package com.bc.wechat.dao;

import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;

import java.util.List;

public class UserDao {

    public void saveUser(User user) {
        List<User> checkList = User.find(User.class, "user_id = ?", user.getUserId());
        if (null != checkList && checkList.size() > 0) {
            // 好友已存在，更新基本信息
            User existUser = checkList.get(0);
            user.setId(existUser.getId());
            User.save(user);
        } else {
            // 不存在,插入sqlite
            User.save(user);
        }
    }

    public List<User> getAllFriendList() {
        List<User> friendList = User.find(User.class, "is_friend = ?", Constant.IS_FRIEND);
        return friendList;
    }

}
