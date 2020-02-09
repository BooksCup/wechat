package com.bc.wechat.utils;

import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Area;

/**
 * 地区工具类
 */
public class AreaUtil {
    /**
     * 初始化地区信息
     */
    public static void initArea() {
        try {
            Area.deleteAll(Area.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initProvince();
    }

    private static void initProvince() {
        Area.save(new Area("北京市", "", Constant.AREA_TYPE_PROVINCE, "1"));
        Area.save(new Area("天津市", "", Constant.AREA_TYPE_PROVINCE, "2"));
        Area.save(new Area("河北省", "", Constant.AREA_TYPE_PROVINCE, "3"));
        Area.save(new Area("山西省", "", Constant.AREA_TYPE_PROVINCE, "4"));
        Area.save(new Area("内蒙古自治区", "", Constant.AREA_TYPE_PROVINCE, "5"));
        Area.save(new Area("辽宁省", "", Constant.AREA_TYPE_PROVINCE, "6"));
        Area.save(new Area("吉林省", "", Constant.AREA_TYPE_PROVINCE, "7"));
        Area.save(new Area("黑龙江省", "", Constant.AREA_TYPE_PROVINCE, "8"));
        Area.save(new Area("上海市", "", Constant.AREA_TYPE_PROVINCE, "9"));
        Area.save(new Area("江苏省", "", Constant.AREA_TYPE_PROVINCE, "10"));
        Area.save(new Area("浙江省", "", Constant.AREA_TYPE_PROVINCE, "11"));
        Area.save(new Area("安徽省", "", Constant.AREA_TYPE_PROVINCE, "12"));
        Area.save(new Area("江西省", "", Constant.AREA_TYPE_PROVINCE, "13"));
        Area.save(new Area("山东省", "", Constant.AREA_TYPE_PROVINCE, "14"));
        Area.save(new Area("河南省", "", Constant.AREA_TYPE_PROVINCE, "15"));
        Area.save(new Area("湖北省", "", Constant.AREA_TYPE_PROVINCE, "16"));
        Area.save(new Area("湖南省", "", Constant.AREA_TYPE_PROVINCE, "17"));
        Area.save(new Area("广东省", "", Constant.AREA_TYPE_PROVINCE, "18"));
        Area.save(new Area("广西壮族自治区", "", Constant.AREA_TYPE_PROVINCE, "19"));
        Area.save(new Area("海南省", "", Constant.AREA_TYPE_PROVINCE, "20"));
        Area.save(new Area("重庆市", "", Constant.AREA_TYPE_PROVINCE, "21"));
        Area.save(new Area("四川省", "", Constant.AREA_TYPE_PROVINCE, "22"));
        Area.save(new Area("贵州省", "", Constant.AREA_TYPE_PROVINCE, "23"));
        Area.save(new Area("云南省", "", Constant.AREA_TYPE_PROVINCE, "24"));
        Area.save(new Area("西藏自治区", "", Constant.AREA_TYPE_PROVINCE, "25"));
        Area.save(new Area("陕西省", "", Constant.AREA_TYPE_PROVINCE, "26"));
        Area.save(new Area("甘肃省", "", Constant.AREA_TYPE_PROVINCE, "27"));
        Area.save(new Area("青海省", "", Constant.AREA_TYPE_PROVINCE, "28"));
        Area.save(new Area("宁夏回族自治区", "", Constant.AREA_TYPE_PROVINCE, "29"));
        Area.save(new Area("新疆维吾尔自治区", "", Constant.AREA_TYPE_PROVINCE, "30"));
        Area.save(new Area("台湾省", "", Constant.AREA_TYPE_PROVINCE, "31"));
        Area.save(new Area("香港特别行政区", "", Constant.AREA_TYPE_PROVINCE, "32"));
        Area.save(new Area("澳门特别行政区", "", Constant.AREA_TYPE_PROVINCE, "33"));
    }
}
