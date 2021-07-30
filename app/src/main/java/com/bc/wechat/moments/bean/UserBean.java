package com.bc.wechat.moments.bean;

import java.io.Serializable;

/**
 * @作者: njb
 * @时间: 2019/7/22 19:21
 * @描述:
 */
public class UserBean implements Serializable {
    private String userId;
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
