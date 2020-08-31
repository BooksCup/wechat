package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.utils.StatusBarUtil;

import androidx.annotation.Nullable;

/**
 * 用户详情
 *
 * @author zhou
 */
public class UserInfoFileHelperActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_file_helper);
        initStatusBar();
        StatusBarUtil.setStatusBarColor(UserInfoFileHelperActivity.this, R.color.status_bar_color_white);
        initView();
    }

    private void initView() {
    }

    public void back(View view) {
        finish();
    }
}
