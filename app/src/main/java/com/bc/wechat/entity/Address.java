package com.bc.wechat.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * 地址
 *
 * @author zhou
 */
public class Address extends SugarRecord implements Serializable {

    private String addressId;
    private String userId;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String info;
    private String detail;
    private String postCode;
    private String createTime;
    private String modifyTime;

    public Address() {

    }

    public Address(String name,
                   String phone,
                   String province,
                   String city,
                   String district,
                   String detail,
                   String postCode) {
        this.name = name;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.postCode = postCode;
    }

    public Address(String userId,
                   String addressId,
                   String name,
                   String phone,
                   String province,
                   String city,
                   String district,
                   String detail,
                   String postCode) {
        this.userId = userId;
        this.addressId = addressId;
        this.name = name;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.postCode = postCode;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

}