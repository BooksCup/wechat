package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 退出
     */
    private RelativeLayout mLogOutRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        PreferencesUtil.getInstance().init(this);
        initView();
    }

    private void initView() {
        mLogOutRl = findViewById(R.id.rl_log_out);

        mLogOutRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_log_out:
                // 清除sharedpreferences中存储信息
                PreferencesUtil.getInstance().setLogin(false);
                PreferencesUtil.getInstance().setUser(null);

                // 清除通讯录
                User.deleteAll(User.class);
                // 清除朋友圈
                FriendsCircle.deleteAll(FriendsCircle.class);

                // 跳转至登录页
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

                break;
        }
    }
}
