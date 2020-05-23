package com.bc.wechat.dao;

import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;

import java.util.List;

/**
 * 通讯录
 *
 * @author zhou
 */
public class ContactsDao {
    /**
     * 检查是否为好友
     *
     * @param contactsId 联系人用户ID
     * @return true:是 false:否
     */
    public boolean checkIsFriend(String contactsId) {
        List<User> userList = User.findWithQuery(User.class,
                "select * from user where is_friend = ? and user_id = ?",
                Constant.IS_FRIEND, contactsId);
        if (null != userList && userList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
