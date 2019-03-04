package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;

public class ChatSingleSettingActivity extends FragmentActivity {

    private TextView mNickNameTv;

    String userId;
    String userNickName;
    String userAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_setting);

        userId = getIntent().getStringExtra("userId");
        userNickName = getIntent().getStringExtra("userNickName");
        userAvatar = getIntent().getStringExtra("userAvatar");
        initView();
    }

    private void initView() {
        mNickNameTv = findViewById(R.id.tv_nick_name);

        mNickNameTv.setText(userNickName);
    }

    public void back(View view) {
        finish();
    }
}
