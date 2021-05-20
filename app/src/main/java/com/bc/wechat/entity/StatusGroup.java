package com.bc.wechat.entity;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

/**
 * 用户状态组
 *
 * @author zhou
 */
public class StatusGroup extends SugarRecord implements Serializable {

    private String name;
    private List<Status> statusList;
    private String statusListJson;

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

    public String getStatusListJson() {
        return statusListJson;
    }

    public void setStatusListJson(String statusListJson) {
        this.statusListJson = statusListJson;
    }

}