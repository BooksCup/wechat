package com.bc.wechat.entity;

import java.util.List;

/**
 * 用户状态组
 *
 * @author zhou
 */
public class UserStatusGroup {

    private String name;
    private List<UserStatus> userStatusList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserStatus> getUserStatusList() {
        return userStatusList;
    }

    public void setUserStatusList(List<UserStatus> userStatusList) {
        this.userStatusList = userStatusList;
    }

}