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
import com.facebook.drawee.view.SimpleDraweeView;

public class ChatSingleSettingActivity extends FragmentActivity implements View.OnClickListener {

    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private RelativeLayout mAddUserToGroupRl;

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
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mAddUserToGroupRl = findViewById(R.id.rl_add_user_to_group);

        mNickNameTv.setText(userNickName);
        if (!TextUtils.isEmpty(userAvatar)) {
            mAvatarSdv.setImageURI(Uri.parse(userAvatar));
        }

        mAddUserToGroupRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_add_user_to_group:
                startActivity(new Intent(this, CreateChatRoomActivity.class));
                break;
        }
    }
}
