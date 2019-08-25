package com.bc.wechat;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarContext;

public class WechatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        SugarContext.init(this);
    }
}
