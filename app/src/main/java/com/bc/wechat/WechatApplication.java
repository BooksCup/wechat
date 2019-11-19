package com.bc.wechat;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarContext;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class WechatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        SugarContext.init(this);

        // 极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        // 极光IM
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);

        // 上拉下拉控件
        ClassicsHeader.REFRESH_HEADER_PULLING = "";//"下拉可以刷新";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "";//"释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "";//"正在刷新...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "";//"释放立即刷新";
        ClassicsHeader.REFRESH_HEADER_FINISH = "";//"刷新完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = "";//"刷新失败";
        ClassicsHeader.REFRESH_HEADER_UPDATE = "";//"上次更新 M-d HH:mm";

    }
}
