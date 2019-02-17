package com.bc.wechat;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarContext;

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

        JMessageClient.login("13770519290", "123456", new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
            }
        });
    }
}
