package com.bc.wechat.entity.area;

import java.util.List;

/**
 * 市
 *
 * @author zhou
 */
public class City {
    private String name;
    private List<District> district;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<District> getDistrict() {
        return district;
    }

    public void setDistrict(List<District> district) {
        this.district = district;
    }
}
