package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bc.wechat.R;

/**
 * 修改手机号
 *
 * @author zhou
 */
public class ModifyPhoneActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);

        initStatusBar();
    }
}
