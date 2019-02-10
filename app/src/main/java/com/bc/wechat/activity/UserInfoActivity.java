package com.bc.wechat.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

public class UserInfoActivity extends Activity {

    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private ImageView mSexIv;

    // 操作按钮  根据是否好友关系分为如下两种
    // 是好友: 发送消息
    // 非好友: 添加到通讯录
    private Button mSendMsgBtn;

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
        mSendMsgBtn = findViewById(R.id.btn_operate);

        final String userId = getIntent().getStringExtra("userId");
        final String nickName = getIntent().getStringExtra("nickName");
        final String avatar = getIntent().getStringExtra("avatar");
        final String sex = getIntent().getStringExtra("sex");
        final String isFriend = getIntent().getStringExtra("isFriend");

        mNickNameTv.setText(nickName);
        if (null != avatar && !"".equals(avatar)) {
            mAvatarSdv.setImageURI(Uri.parse(avatar));
        }
        if (Constant.USER_SEX_MALE.equals(sex)) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(sex)) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        if (Constant.IS_FRIEND.equals(isFriend)) {
            mSendMsgBtn.setText("发消息");
        } else {
            mSendMsgBtn.setText("添加到通讯录");
        }

        mSendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.IS_FRIEND.equals(isFriend)) {
                    // 是好友，发消息
                    Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                    startActivity(intent);
                } else {
                    // 非好友，添加到通讯录
                    Intent intent = new Intent(UserInfoActivity.this, AddFriendsFinalActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }

            }
        });
    }

    public void back(View view) {
        finish();
    }
}
