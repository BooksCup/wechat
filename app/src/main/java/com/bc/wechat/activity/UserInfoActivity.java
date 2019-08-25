package com.bc.wechat.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bc.wechat.R;

public class UserInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }
}
