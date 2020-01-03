package com.bc.wechat.activity;

import android.content.Intent;
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

        final String avatar = getIntent().getStringExtra("avatar");
        final String nickName = getIntent().getStringExtra("nickName");
        final String sex = getIntent().getStringExtra("sex");
        final String sign = getIntent().getStringExtra("sign");
        final String source = getIntent().getStringExtra("source");

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
            mSourceTv.setText(getString(R.string.search_friend_by_phone));
        } else if (Constant.FRIENDS_SOURCE_BY_WX_ID.equals(source)) {
            mSourceTv.setText(getString(R.string.search_friend_by_wx_id));
        }

        mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrangerUserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", avatar);
                startActivity(intent);
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
