package com.bc.wechat.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Area;
import com.bc.wechat.entity.area.City;
import com.bc.wechat.entity.area.District;
import com.bc.wechat.entity.area.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * 地区工具类
 */
public class AreaUtil {
    /**
     * 初始化地区信息
     */
    public static void initArea(Context context) {
        if (Area.count(Area.class) == 0) {
            initData(context);
        }
    }

    private static void initData(Context context) {
        String jsonData = new GetJsonDataUtil().getJson(context, "area-wx.json");//获取assets目录下的json文件数据
        List<Province> provinceList = JSONArray.parseArray(jsonData, Province.class);
        int provinceSeq = 0;
        List<Area> areaList = new ArrayList<>();
        for (Province province : provinceList) {
            provinceSeq++;
            // 省
            Area provinceArea = new Area(province.getName(), "", Constant.AREA_TYPE_PROVINCE, provinceSeq);
            areaList.add(provinceArea);
            int citySeq = 0;
            List<City> cityList = province.getCity();
            for (City city : cityList) {
                citySeq++;
                Area cityArea = new Area(city.getName(), province.getName(), Constant.AREA_TYPE_CITY, citySeq);
                areaList.add(cityArea);
                List<District> districtList = city.getDistrict();
                int districtSeq = 0;
                for (District district : districtList) {
                    districtSeq++;
                    Area districtArea = new Area(district.getName(), city.getName(),
                            Constant.AREA_TYPE_DISTRICT, districtSeq, district.getPostCode());
                    areaList.add(districtArea);
                }
            }
        }
        Area.saveInTx(areaList);
    }
}
