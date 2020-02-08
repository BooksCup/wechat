package com.bc.wechat.dao;

import com.bc.wechat.entity.Address;

import java.util.List;

/**
 * 地址
 *
 * @author zhou
 */
public class AddressDao {
    public List<Address> getAddressList() {
        return Address.listAll(Address.class);
    }

    public void saveAddress(Address address) {
        Address.save(address);
    }

    public void clearAddress() {
        Address.deleteAll(Address.class);
    }
}
