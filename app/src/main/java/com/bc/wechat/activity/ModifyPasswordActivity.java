package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.utils.StatusBarUtil;

/**
 * 设置密码
 *
 * @author zhou
 */
public class ModifyPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initStatusBar();
        StatusBarUtil.setStatusBarColor(ModifyPasswordActivity.this, R.color.status_bar_color_white);
    }

    public void back(View view) {
        finish();
    }
}
