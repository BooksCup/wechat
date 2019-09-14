package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.utils.PreferencesUtil;

public class MyUserInfoActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mNickNameRl;
    private TextView mNickNameTv;
    private RelativeLayout mWxIdRl;

    private static final int UPDATE_USER_NICK_NAME = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        PreferencesUtil.getInstance().init(this);
        initView();
    }

    private void initView() {
        mNickNameRl = findViewById(R.id.rl_nick_name);
        mNickNameTv = findViewById(R.id.tv_nick_name);

        mWxIdRl = findViewById(R.id.rl_wx_id);

        mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
        mNickNameRl.setOnClickListener(this);
        mWxIdRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_nick_name:
                // 昵称
                startActivityForResult(new Intent(this, UpdateNickNameActivity.class), UPDATE_USER_NICK_NAME);
                break;
            case R.id.rl_wx_id:
                startActivity(new Intent(this, UpdateWxIdActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_USER_NICK_NAME:
                    mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
                    break;
            }
        }
    }
}
