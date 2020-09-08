package com.bc.wechat.activity;

import android.os.Bundle;

import com.bc.wechat.R;

import androidx.annotation.Nullable;

/**
 * 联系人权限
 *
 * @author zhou
 */
public class ContactPrivacyActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_privacy);
        initStatusBar();
    }
}
