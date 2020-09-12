package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;

import com.bc.wechat.R;

import androidx.annotation.Nullable;

/**
 * "用户信息"-"更多信息"
 *
 * @author zhou
 */
public class UserInfoMoreActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_more);
        initStatusBar();
    }

    public void back(View view) {
        finish();
    }
}
