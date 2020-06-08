package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bc.wechat.R;

/**
 * 更多安全设置
 *
 * @author zhou
 */
public class MoreSecuritySettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_security_setting);
        initStatusBar();
    }

    public void back(View view) {
        finish();
    }
}
