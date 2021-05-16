package com.bc.wechat.dao;

import com.bc.wechat.entity.Address;

import java.util.List;

/**
 * 地址
 *
 * @author zhou
 */
public class AddressDao {

    /**
     * 获取地址列表
     *
     * @return 地址列表
     */
    public List<Address> getAddressList() {
        return Address.listAll(Address.class);
    }

    /**
     * 保存地址
     *
     * @param address 地址
     */
    public void saveAddress(Address address) {
        Address.save(address);
    }

    /**
     * 清除地址
     */
    public void clearAddress() {
        Address.deleteAll(Address.class);
    }

    /**
     * 根据地址ID获取地址详情
     *
     * @param addressId 地址ID
     * @return 地址详情
     */
    public Address getAddressByAddressId(String addressId) {
        List<Address> addressList = Address.find(Address.class, "address_id = ?", addressId);
        if (null != addressList && addressList.size() > 0) {
            return addressList.get(0);
        }
        return null;
    }

    /**
     * 根据地址ID删除地址
     *
     * @param addressId 地址ID
     */
    public void deleteAddressByAddressId(String addressId) {
        String sql = "delete from address where address_id = ?";
        Address.executeQuery(sql, addressId);
    }

}