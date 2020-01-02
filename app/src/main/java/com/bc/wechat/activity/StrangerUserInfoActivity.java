package com.bc.wechat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

public class StrangerUserInfoActivity extends BaseActivity {

    private SimpleDraweeView mAvatarSdv;
    private TextView mNameTv;
    private ImageView mSexIv;
    private TextView mSignTv;
    private TextView mSourceTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stranger_user_info);
        initView();
    }

    private void initView() {
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mNameTv = findViewById(R.id.tv_name);
        mSexIv = findViewById(R.id.iv_sex);
        mSignTv = findViewById(R.id.tv_sign);
        mSourceTv = findViewById(R.id.tv_source);

        String avatar = getIntent().getStringExtra("avatar");
        String nickName = getIntent().getStringExtra("nickName");
        String sex = getIntent().getStringExtra("sex");
        String sign = getIntent().getStringExtra("sign");
        String source = getIntent().getStringExtra("source");

        if (!TextUtils.isEmpty(avatar)) {
            mAvatarSdv.setImageURI(Uri.parse(avatar));
        }

        if (Constant.USER_SEX_MALE.equals(sex)) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(sex)) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        mNameTv.setText(nickName);
        mSignTv.setText(sign);

        if (Constant.FRIENDS_SOURCE_BY_PHONE.equals(source)) {
            mSourceTv.setText("来自手机号搜索");
        } else if (Constant.FRIENDS_SOURCE_BY_WX_ID.equals(source)) {
            mSourceTv.setText("来自微信号搜索");
        }
    }

    public void back(View view) {
        finish();
    }
}
