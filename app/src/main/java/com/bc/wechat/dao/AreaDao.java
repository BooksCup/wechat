package com.bc.wechat.dao;

import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Area;

import java.util.List;

/**
 * 地区
 *
 * @author zhou
 */
public class AreaDao {

    /**
     * 获取所有省份列表
     *
     * @return 所有省份列表
     */
    public List<Area> getProvinceList() {
        List<Area> provinceList = Area.findWithQuery(Area.class,
                "select * from area where type = ? order by seq asc",
                Constant.AREA_TYPE_PROVINCE);
        return provinceList;
    }

    /**
     * 获取某个省所有市列表
     *
     * @param provinceName 省
     * @return 某个省所有市列表
     */
    public List<Area> getCityListByProvinceName(String provinceName) {
        List<Area> cityList = Area.findWithQuery(Area.class,
                "select * from area where type = ? and parent_name = ? order by seq asc",
                Constant.AREA_TYPE_CITY, provinceName);
        return cityList;
    }

    /**
     * 获取某个市所有区县列表
     *
     * @param cityName 市
     * @return 某个市所有区县列表
     */
    public List<Area> getDistrictListByCityName(String cityName) {
        List<Area> districtList = Area.findWithQuery(Area.class,
                "select * from area where type = ? and parent_name = ? order by seq asc",
                Constant.AREA_TYPE_DISTRICT, cityName);
        return districtList;
    }

    /**
     * 根据市和区获取某个区
     * 某些区会重复
     *
     * @param cityName     市
     * @param districtName 区
     * @return 区
     */
    public Area getDistrictByCityNameAndDistrictName(String cityName, String districtName) {
        List<Area> districtList = Area.findWithQuery(Area.class,
                "select * from area where name = ? and type = ? and parent_name = ?",
                districtName, Constant.AREA_TYPE_DISTRICT, cityName);
        if (null != districtList && districtList.size() > 0) {
            return districtList.get(0);
        } else {
            return new Area();
        }
    }

}