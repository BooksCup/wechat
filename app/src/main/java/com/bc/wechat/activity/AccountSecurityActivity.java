package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * 账号与安全
 *
 * @author zhou
 */
public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 标题
     */
    private TextView mTitleTv;

    private TextView mWeChatIdTv;
    private TextView mPhoneTv;

    /**
     * 登录设备管理
     */
    private RelativeLayout mManageDevicesRl;

    /**
     * 更多安全设置
     */
    private RelativeLayout mMoreSettingRl;

    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mWeChatIdTv = findViewById(R.id.tv_wechat_id);
        mPhoneTv = findViewById(R.id.tv_phone);

        mWeChatIdTv.setText(mUser.getUserWxId());
        mPhoneTv.setText(mUser.getUserPhone());

        mManageDevicesRl = findViewById(R.id.rl_manage_devices);
        mManageDevicesRl.setOnClickListener(this);

        mMoreSettingRl = findViewById(R.id.rl_more_settings);
        mMoreSettingRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_manage_devices:
                // 登录设备管理
                startActivity(new Intent(AccountSecurityActivity.this, ManageDevicesActivity.class));
                break;

            case R.id.rl_more_settings:
                // 更多安全设置
                startActivity(new Intent(AccountSecurityActivity.this, MoreSecuritySettingActivity.class));
                break;
        }
    }
}
