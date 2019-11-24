package com.bc.wechat.service;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationService {
    private LocationClient client = null;
    private LocationClientOption mOption, customOption;
    private Object lock = new Object();

    public LocationService(Context locationContext) {
        synchronized (lock) {
            if (null == client) {
                client = new LocationClient(locationContext);
//                client.setLocOption();
            }
        }
    }

    public boolean registerListener(BDAbstractLocationListener listener) {
        boolean isSuccess = false;
        if (null != listener) {
            client.registerLocationListener(listener);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void unregisterListener(BDAbstractLocationListener listener) {
        if (null != listener) {
            client.unRegisterLocationListener(listener);
        }
    }

    public void setLocationOption(LocationClientOption option) {
        if (null != option) {
            if (client.isStarted()) {
                client.stop();
            }
            customOption = option;
            client.setLocOption(option);
        }
    }

    public LocationClientOption getOption() {
        return customOption;
    }

    public LocationClientOption getDefaultLocationClientOption() {
        if (null == mOption) {
            mOption = new LocationClientOption();
            // 可选，默认高精度，设置定位模式，（高精度/低功耗/仅设备）
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd0911
            mOption.setCoorType("bd09ll");
            // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才有效
//            mOption.setScanSpan(3000);
            // 可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedAddress(true);
            // 可选，设置是否需要地址描述
            mOption.setIsNeedLocationDescribe(true);
            // 可选，设置是否需要设备方向结果
            mOption.setNeedDeviceDirect(true);
            // 可选，默认false，设置是否当gps有效时按照1次/s频率输出gps结果
            mOption.setLocationNotify(false);
            // 可选，默认true，定位SDK内部是一个service，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIgnoreKillProcess(true);
            // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.setIsNeedLocationPoiList(true);
            // 可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.SetIgnoreCacheException(false);
        }
        return mOption;
    }

    public void start() {
        synchronized (lock) {
            if (null != client && !client.isStarted()) {
                client.start();
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            if (null != client && client.isStarted()) {
                client.stop();
            }
        }
    }
}
