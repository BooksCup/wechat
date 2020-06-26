package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 账号与安全
 *
 * @author zhou
 */
public class AccountSecurityActivity extends BaseActivity {

    /**
     * 标题
     */
    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_wechat_id)
    TextView mWeChatIdTv;

    @BindView(R.id.tv_phone)
    TextView mPhoneTv;

    /**
     * 手机号
     */
    @BindView(R.id.rl_phone)
    RelativeLayout mPhoneRl;

    /**
     * 微信密码
     */
    @BindView(R.id.rl_password)
    RelativeLayout mPasswordRl;

    /**
     * 登录设备管理
     */
    @BindView(R.id.rl_manage_devices)
    RelativeLayout mManageDevicesRl;

    /**
     * 更多安全设置
     */
    @BindView(R.id.rl_more_settings)
    RelativeLayout mMoreSettingRl;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        ButterKnife.bind(this);
        initStatusBar();

        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mWeChatIdTv.setText(mUser.getUserWxId());
        mPhoneTv.setText(mUser.getUserPhone());
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_phone, R.id.rl_password, R.id.rl_manage_devices, R.id.rl_more_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_phone:
                // 绑定手机号
                startActivity(new Intent(AccountSecurityActivity.this, PhoneLinkActivity.class));
                break;

            case R.id.rl_password:
                // 设置密码
                startActivity(new Intent(AccountSecurityActivity.this, ModifyPasswordActivity.class));
                break;

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
