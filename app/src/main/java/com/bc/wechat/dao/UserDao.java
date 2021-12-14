package com.bc.wechat.dao;

import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PinyinComparator;

import java.util.Collections;
import java.util.List;

/**
 * 用户DAO
 *
 * @author zhou
 */
public class UserDao {

    /**
     * 保存用户
     * 不存在则新建，存在则更新
     * 唯一标识(userId)
     *
     * @param user 用户
     */
    public void saveUser(User user) {
        List<User> checkList = User.find(User.class, "user_id = ?", user.getUserId());
        user.setUserHeader(CommonUtil.setUserHeader(user.getUserNickName()));
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

    /**
     * 通过用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserById(String userId) {
        List<User> userList = User.find(User.class, "user_id = ?", userId);
        if (null != userList && userList.size() > 0) {
            return userList.get(0);
        } else {
            return new User();
        }
    }

    /**
     * 获取所有的好友列表
     *
     * @return 所有的好友列表
     */
    public List<User> getAllFriendList() {
        return User.findWithQuery(User.class, "select * from user where is_friend = ? and is_blocked = ?",
                Constant.IS_FRIEND, Constant.CONTACT_IS_NOT_BLOCKED);
    }

    /**
     * 获取所有的黑名单用户列表
     *
     * @return 所有的好友列表
     */
    public List<User> getAllBlockedUserList() {
        return User.find(User.class, "is_blocked = ?", Constant.CONTACT_IS_BLOCKED);
    }

    /**
     * 获取联系人数量,进注册好友数量,不包括系统用户
     *
     * @return 联系人数量
     */
    public long getContactsCount() {
        return User.count(User.class, "is_friend = ? and user_type = ? and is_blocked = ?",
                new String[]{Constant.IS_FRIEND, Constant.USER_TYPE_REG, Constant.CONTACT_IS_NOT_BLOCKED});
    }

    /**
     * 获取所有的星标好友列表
     * 按好友昵称或备注首字母排序并设置特殊header
     *
     * @return 所有的星标好友列表
     */
    public List<User> getAllStarredContactList() {
        List<User> starredContactList = User.findWithQuery(User.class,
                "select * from user where is_friend = ? and is_starred = ? and is_blocked = ?",
                Constant.IS_FRIEND, Constant.CONTACT_IS_STARRED, Constant.CONTACT_IS_NOT_BLOCKED);
        Collections.sort(starredContactList, new PinyinComparator() {
        });
        for (User starredContact : starredContactList) {
            starredContact.setUserHeader(Constant.STAR_FRIEND);
        }
        return starredContactList;
    }

    /**
     * 检查是否为好友
     *
     * @param phone 手机号
     * @return true:是 false:否
     */
    public boolean checkIsFriend(String phone) {
        List<User> friendList = User.findWithQuery(User.class,
                "select * from user where is_friend = ? and user_phone = ?", Constant.IS_FRIEND, phone);
        if (null != friendList && friendList.size() > 0) {
            return true;
        }
        return false;
    }

}