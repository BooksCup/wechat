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

    /**
     * 根据设备ID获取设备信息
     *
     * @param deviceId 设备ID
     * @return 设备信息
     */
    public DeviceInfo getDeviceInfoByDeviceId(String deviceId) {
        List<DeviceInfo> deviceInfoList = DeviceInfo.find(DeviceInfo.class, "device_id = ?", deviceId);
        if (null != deviceInfoList && deviceInfoList.size() > 0) {
            return deviceInfoList.get(0);
        }
        return null;
    }
}
