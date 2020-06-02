package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * 设置
 *
 * @author zhou
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 标题
     */
    private TextView mTitleTv;

    /**
     * 账号与安全
     */
    private RelativeLayout mAccountSecurityRl;

    /**
     * 退出
     */
    private RelativeLayout mLogOutRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initStatusBar();
        PreferencesUtil.getInstance().init(this);
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);
        mAccountSecurityRl = findViewById(R.id.rl_account_security);
        mLogOutRl = findViewById(R.id.rl_log_out);

        mAccountSecurityRl.setOnClickListener(this);
        mLogOutRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_account_security:
                startActivity(new Intent(SettingActivity.this, AccountSecurityActivity.class));
                break;

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
