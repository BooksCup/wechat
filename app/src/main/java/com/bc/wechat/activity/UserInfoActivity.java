package com.bc.wechat.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;

public class UserInfoActivity extends Activity {

    private TextView mNickNameTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();
    }

    private void initView() {
        mNickNameTv = findViewById(R.id.tv_name);

        final String nickName = getIntent().getStringExtra("nickName");
        final String avatar = getIntent().getStringExtra("avatar");
        final String sex = getIntent().getStringExtra("sex");

        mNickNameTv.setText(nickName);
    }

    public void back(View view) {
        finish();
    }
}
