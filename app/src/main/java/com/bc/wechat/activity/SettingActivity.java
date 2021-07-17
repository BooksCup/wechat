package com.bc.wechat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 *
 * @author zhou
 */
public class SettingActivity extends BaseActivity {

    /**
     * 标题
     */
    @BindView(R.id.tv_title)
    TextView mTitleTv;

    /**
     * 账号与安全
     */
    @BindView(R.id.rl_account_security)
    RelativeLayout mAccountSecurityRl;

    /**
     * 关于微信
     */
    @BindView(R.id.rl_about)
    RelativeLayout mAboutRl;

    /**
     * 退出
     */
    @BindView(R.id.rl_log_out)
    RelativeLayout mLogOutRl;

    @Override
    public int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        PreferencesUtil.getInstance().init(this);
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_account_security, R.id.rl_about, R.id.rl_log_out})
    public void onClick(View view) {
        switch (view.getId()) {
            // 账号与安全
            case R.id.rl_account_security:
                startActivity(new Intent(SettingActivity.this, AccountSecurityActivity.class));
                break;

            // 关于微信
            case R.id.rl_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
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
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}