package com.bc.wechat.dao;

import com.bc.wechat.entity.UserFriendsCircle;

import java.util.List;

public class UserFriendsCircleDao {

    /**
     * 获取朋友圈列表
     *
     * @param userId    用户ID
     * @param pageSize  分页大小
     * @param timeStamp 时间戳
     * @return 朋友圈列表
     */
    public List<UserFriendsCircle> getUserFriendsCircleList(String userId, int pageSize, long timeStamp) {
        if (0L == timeStamp) {
            return UserFriendsCircle.findWithQuery(UserFriendsCircle.class,
                    "select * from user_friends_circle where user_id = ? order by timestamp desc limit ? offset 0",
                    userId, String.valueOf(pageSize));
        } else {
            return UserFriendsCircle.findWithQuery(UserFriendsCircle.class,
                    "select * from user_friends_circle where user_id = ? and timestamp < ? order by timestamp desc limit ?",
                    userId, String.valueOf(timeStamp), String.valueOf(pageSize));
        }
    }

    /**
     * 新增朋友圈实体
     *
     * @param userFriendsCircle 朋友圈实体
     */
    public void addUserFriendsCircle(UserFriendsCircle userFriendsCircle) {
        UserFriendsCircle.save(userFriendsCircle);
    }

    /**
     * 获取朋友圈详情
     *
     * @param circleId 朋友圈ID
     * @return 朋友圈详情
     */
    public UserFriendsCircle getUserFriendsCircleByCircleId(String circleId) {
        List<UserFriendsCircle> userFriendsCircleList = UserFriendsCircle.find(UserFriendsCircle.class, "circle_id = ?", circleId);
        if (null != userFriendsCircleList && userFriendsCircleList.size() > 0) {
            return userFriendsCircleList.get(0);
        }
        return null;
    }
}
