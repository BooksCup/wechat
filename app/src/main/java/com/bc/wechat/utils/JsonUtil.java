package com.bc.wechat.utils;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * json工具类
 *
 * @author zhou
 */
public class JsonUtil {

    /**
     * json转list
     *
     * @param jsonString json字符串
     * @param clazz      list内对象类型
     * @param <T>        泛型
     * @return list
     */
    public static <T> List<T> jsonArrayToList(String jsonString, Class<T> clazz) {
        List<T> list;
        try {
            list = JSONArray.parseArray(jsonString, clazz);
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

}
