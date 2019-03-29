package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.bc.wechat.R;
import com.bc.wechat.utils.PreferencesUtil;

public class SettingActivity extends FragmentActivity implements View.OnClickListener {

    private Button mLogoutBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        PreferencesUtil.getInstance().init(this);
        initView();
    }

    private void initView() {
        mLogoutBtn = findViewById(R.id.btn_logout);

        mLogoutBtn.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                // 清除sharedpreferences中存储信息
                PreferencesUtil.getInstance().setLogin(false);
                PreferencesUtil.getInstance().setUserId("");
                PreferencesUtil.getInstance().setUserNickName("");
                PreferencesUtil.getInstance().setUserWxId("");

                // 跳转至登录页
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

                break;
        }
    }
}
