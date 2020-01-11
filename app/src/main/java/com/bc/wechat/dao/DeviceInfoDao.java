package com.bc.wechat.dao;

import com.bc.wechat.entity.DeviceInfo;

import java.util.List;

/**
 * 登录设备信息
 *
 * @author zhou
 */
public class DeviceInfoDao {
    public List<DeviceInfo> getDeviceInfoList() {
        return DeviceInfo.listAll(DeviceInfo.class);
    }

    public void clearDeviceInfo() {
        DeviceInfo.deleteAll(DeviceInfo.class);
    }

    public void saveDeviceInfo(DeviceInfo deviceInfo) {
        DeviceInfo.save(deviceInfo);
    }
}
