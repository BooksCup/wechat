package com.bc.wechat.utils;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author zhou
 */
public class CollectionUtils {

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return true:空 false:非空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

}