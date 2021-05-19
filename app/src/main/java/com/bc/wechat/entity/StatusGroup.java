package com.bc.wechat.entity;

import java.util.List;

/**
 * 用户状态组
 *
 * @author zhou
 */
public class StatusGroup {

    private String name;
    private List<Status> statusList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }

}