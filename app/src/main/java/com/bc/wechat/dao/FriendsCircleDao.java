package com.bc.wechat.dao;

import com.bc.wechat.entity.FriendsCircle;

import java.util.List;

public class FriendsCircleDao {
    public List<FriendsCircle> getFriendsCircleList() {
        return FriendsCircle.findWithQuery(FriendsCircle.class,
                "select * from friends_circle order by timestamp desc");
    }

    public void addFriendsCircle(FriendsCircle friendsCircle) {
        FriendsCircle.save(friendsCircle);
    }

    public FriendsCircle getFriendsCircleByCircleId(String circleId) {
        List<FriendsCircle> friendsCircleList = FriendsCircle.find(FriendsCircle.class, "circle_id = ?", circleId);
        if (null != friendsCircleList && friendsCircleList.size() > 0) {
            return friendsCircleList.get(0);
        }
        return null;
    }
}
