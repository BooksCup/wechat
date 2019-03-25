package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.bc.wechat.R;
import com.bc.wechat.utils.PreferencesUtil;

import me.leolin.shortcutbadger.ShortcutBadger;


public class SplashActivity extends FragmentActivity {

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        super.onCreate(savedInstanceState);

        PreferencesUtil.getInstance().init(this);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        view.setAnimation(animation);

        ShortcutBadger.removeCount(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (PreferencesUtil.getInstance().isLogin()) {
                    // 已登录，跳至主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    // 未登录，跳至登录页
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }
}
