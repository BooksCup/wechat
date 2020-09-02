package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 文件传输助手
 *
 * @author zhou
 */
public class UserInfoFileHelperActivity extends BaseActivity {

    @BindView(R.id.rl_send_message)
    RelativeLayout mSendMessageRl;

    private UserDao mUserDao;
    private String mContactId;
    private User contact;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_file_helper);
        ButterKnife.bind(this);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(UserInfoFileHelperActivity.this, R.color.status_bar_color_white);

        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        mContactId = getIntent().getStringExtra("userId");
        contact = mUserDao.getUserById(mContactId);
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.rl_send_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_send_message:
                Intent intent = new Intent(UserInfoFileHelperActivity.this, ChatActivity.class);
                intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                intent.putExtra("contactId", mContactId);
                intent.putExtra("contactNickName", contact.getUserNickName());
                intent.putExtra("contactAvatar", contact.getUserAvatar());
                startActivity(intent);
                break;
        }
    }
}
