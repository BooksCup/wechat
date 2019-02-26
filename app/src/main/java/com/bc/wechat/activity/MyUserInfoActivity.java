package com.bc.wechat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.utils.PreferencesUtil;
import com.facebook.drawee.view.SimpleDraweeView;

public class MyUserInfoActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mNickNameRl;
    private TextView mNickNameTv;
    private RelativeLayout mWxIdRl;
    private TextView mWxIdTv;
    private SimpleDraweeView mAvatarSdv;

    private static final int UPDATE_USER_NICK_NAME = 3;
    private static final int UPDATE_USER_WX_ID = 4;

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
        mWxIdTv = findViewById(R.id.tv_wx_id);

        mAvatarSdv = findViewById(R.id.sdv_avatar);

        mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
        mWxIdTv.setText(PreferencesUtil.getInstance().getUserWxId());
        String userAvatar = PreferencesUtil.getInstance().getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            mAvatarSdv.setImageURI(Uri.parse(userAvatar));
        }

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
                startActivityForResult(new Intent(this, UpdateWxIdActivity.class), UPDATE_USER_WX_ID);
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
                case UPDATE_USER_WX_ID:
                    mWxIdTv.setText(PreferencesUtil.getInstance().getUserWxId());
                    break;
            }
        }
    }
}
