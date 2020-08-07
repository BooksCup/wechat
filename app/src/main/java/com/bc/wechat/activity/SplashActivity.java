package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.bc.wechat.R;
import com.bc.wechat.utils.AreaUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.RegionUtil;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import me.leolin.shortcutbadger.ShortcutBadger;


public class SplashActivity extends FragmentActivity implements View.OnClickListener {

    private static final int sleepTime = 2000;

    Button mLoginBtn;
    Button mRegisterBtn;

    private static final int SHOW_OPERATE_BTN = 0x3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        super.onCreate(savedInstanceState);
        initView();

        PreferencesUtil.getInstance().init(this);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        view.setAnimation(animation);

        ShortcutBadger.removeCount(this);
    }

    private void initView() {
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterBtn = findViewById(R.id.btn_register);

        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 预置地址信息
                    // 初始化省市区
                    AreaUtil.initArea(SplashActivity.this);
                    RegionUtil.initRegion(SplashActivity.this);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (PreferencesUtil.getInstance().isLogin()) {
                    // 已登录，跳至主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(SHOW_OPERATE_BTN));
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(SplashActivity.this, PhoneLoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_OPERATE_BTN) {
                mLoginBtn.setVisibility(View.VISIBLE);
                mRegisterBtn.setVisibility(View.VISIBLE);
            }
        }
    };
}
