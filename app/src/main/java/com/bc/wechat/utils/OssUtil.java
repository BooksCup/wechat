package com.bc.wechat.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * OSS工具类
 *
 * @author zhou
 */
public class OssUtil {

    /**
     * oss图片缩放
     *
     * @param url oss图片文件的原始url
     * @return 缩放后的图片url
     */
    public static String resize(String url) {
        Map<String, Object> data = new HashMap<>();
        data.put("x-oss-process", "image/resize,m_lfit,h_300,w_300");
        return appendUrl(url, data);
    }

    /**
     * 在指定url后追加参数
     *
     * @param url
     * @param data 参数集合 key = value
     * @return
     */
    private static String appendUrl(String url, Map<String, Object> data) {
        String newUrl = url;
        StringBuffer param = new StringBuffer();
        for (String key : data.keySet()) {
            param.append(key + "=" + data.get(key).toString() + "&");
        }
        String paramStr = param.toString();
        paramStr = paramStr.substring(0, paramStr.length() - 1);
        if (newUrl.indexOf("?") >= 0) {
            newUrl += "&" + paramStr;
        } else {
            newUrl += "?" + paramStr;
        }
        return newUrl;
    }
}
