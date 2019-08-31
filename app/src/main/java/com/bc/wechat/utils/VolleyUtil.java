package com.bc.wechat.utils;


import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class VolleyUtil {
    private String url = "http://www.xxx.com";
    private static VolleyUtil volleyUtil;
    private RequestQueue requestQueue;

    private VolleyUtil(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * 单例模式
     *
     * @param context
     * @return
     */
    public static VolleyUtil getInstance(Context context) {
        if (volleyUtil == null) {
            volleyUtil = new VolleyUtil(context);
        }
        return volleyUtil;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * @param url           : get请求url
     * @param listener      : 请求监听
     * @param errorListener : 请求失败监听
     */
    public void httpGetRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener);
        if (requestQueue == null) {
            throw new RuntimeException("requestQueue未实例化");
        }
        requestQueue.add(stringRequest);
    }

    /**
     * @param url           : post请求url
     * @param params        : 请求参数
     * @param listener      : 请求监听
     * @param errorListener : 请求失败监听
     */
    public void httpPostRequest(String url, final Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
