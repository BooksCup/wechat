package com.bc.wechat.dao;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.entity.Status;
import com.bc.wechat.entity.StatusGroup;
import com.bc.wechat.utils.JsonUtil;

import java.util.List;

/**
 * 状态组
 *
 * @author zhou
 */
public class StatusGroupDao {

    /**
     * 获取状态组列表
     *
     * @return 状态组列表
     */
    public List<StatusGroup> getStatusGroupList() {
        List<StatusGroup> statusGroupList = StatusGroup.listAll(StatusGroup.class);
        for (StatusGroup statusGroup : statusGroupList) {
            List<Status> statusList = JsonUtil.jsonArrayToList(statusGroup.getStatusListJson(), Status.class);
            statusGroup.setStatusList(statusList);
        }
        return statusGroupList;
    }

    /**
     * 保存状态组
     *
     * @param statusGroup 状态组
     */
    public void saveStatusGroup(StatusGroup statusGroup) {
        statusGroup.setStatusListJson(JSON.toJSONString(statusGroup.getStatusList()));
        StatusGroup.save(statusGroup);
    }

    /**
     * 清除状态组
     */
    public void clearStatusGroup() {
        StatusGroup.deleteAll(StatusGroup.class);
    }

}