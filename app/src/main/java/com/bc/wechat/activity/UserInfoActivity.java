package com.bc.wechat.activity;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

public class UserInfoActivity extends Activity {

    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private ImageView mSexIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();
    }

    private void initView() {
        mNickNameTv = findViewById(R.id.tv_name);
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mSexIv = findViewById(R.id.iv_sex);

        final String nickName = getIntent().getStringExtra("nickName");
        final String avatar = getIntent().getStringExtra("avatar");
        final String sex = getIntent().getStringExtra("sex");

        mNickNameTv.setText(nickName);
        mAvatarSdv.setImageURI(Uri.parse(avatar));
    }

    public void back(View view) {
        finish();
    }
}
