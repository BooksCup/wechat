package com.bc.wechat.entity;

import com.orm.SugarRecord;

/**
 * 登录设备
 *
 * @author zhou
 */
public class DeviceInfo extends SugarRecord {
    /**
     * 手机品牌
     */
    private String phoneBrand;

    /**
     * 手机型号
     */
    private String phoneModel;

    /**
     * 操作系统版本
     */
    private String os;

    /**
     * 分辨率
     */
    private String resolution;

    /**
     * 运营商信息
     */
    private String operator;

    private String loginTime;

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }
}
