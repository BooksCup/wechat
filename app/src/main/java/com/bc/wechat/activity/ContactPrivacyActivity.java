package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 联系人权限
 *
 * @author zhou
 */
public class ContactPrivacyActivity extends BaseActivity {

    // 聊天、朋友圈、微信运动等
    @BindView(R.id.iv_chats_moments_werun_etc)
    ImageView mChatsMomentsWerunEtcIv;

    // 仅聊天
    @BindView(R.id.iv_chats_only)
    ImageView mChatsOnlyIv;

    UserDao mUserDao;
    String mContactId;
    User mContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_privacy);
        initStatusBar();
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        mUserDao = new UserDao();
        mContactId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactId);
    }

    private void initView() {
        if (Constant.PRIVACY_CHATS_MOMENTS_WERUN_ETC.equals(mContact.getUserContactPrivacy())) {
            // 所有权限
            mChatsMomentsWerunEtcIv.setVisibility(View.VISIBLE);
            mChatsOnlyIv.setVisibility(View.GONE);
        } else {
            // 仅聊天
            mChatsMomentsWerunEtcIv.setVisibility(View.GONE);
            mChatsOnlyIv.setVisibility(View.VISIBLE);
        }
    }

    public void back(View view) {
        finish();
    }
}
