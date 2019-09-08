package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.bc.wechat.R;

public class MyUserInfoActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mNickNameRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        initView();
    }

    private void initView() {
        mNickNameRl = findViewById(R.id.rl_nick_name);

        mNickNameRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_nick_name:
                // 昵称
                startActivity(new Intent(this, UpdateNickNameActivity.class));
                break;
        }
    }
}
