package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户设置
 *
 * @author zhou
 */
public class UserSettingActivity extends BaseActivity {

    // 设置备注和标签
    @BindView(R.id.rl_edit_contact)
    RelativeLayout mEditContactRl;

    @BindView(R.id.tv_edit_contact)
    TextView mEditContactTv;

    // 朋友权限
    @BindView(R.id.rl_privacy)
    RelativeLayout mPrivacyRl;

    // 把他推荐给朋友
    @BindView(R.id.rl_share_contact)
    RelativeLayout mShareContactRl;

    // 添加到桌面
    @BindView(R.id.rl_add_to_home_screen)
    RelativeLayout mAddToHomeScreenRl;

    // 设为星标朋友
    @BindView(R.id.rl_add_star)
    RelativeLayout mAddStarRl;

    // 加入黑名单
    @BindView(R.id.rl_block)
    RelativeLayout mBlockRl;

    // 投诉
    @BindView(R.id.rl_report)
    RelativeLayout mReportRl;

    // 删除
    @BindView(R.id.rl_delete)
    RelativeLayout mDeleteRl;

    UserDao mUserDao;
    String mContactId;
    String mIsFriend;
    User mContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.bind(this);
        initStatusBar();

        mUserDao = new UserDao();
        mIsFriend = getIntent().getStringExtra("isFriend");
        mContactId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactId);
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        if (Constant.IS_NOT_FRIEND.equals(mIsFriend)) {
            // 非好友
            mPrivacyRl.setVisibility(View.GONE);
            mShareContactRl.setVisibility(View.GONE);
            mAddToHomeScreenRl.setVisibility(View.GONE);
            mAddStarRl.setVisibility(View.GONE);
            mReportRl.setVisibility(View.GONE);
            mDeleteRl.setVisibility(View.GONE);
        } else {
            // 好友
        }
        loadData(mContact);
    }

    private void loadData(User user) {
        mEditContactTv.setText(user.getUserContactAlias());
    }

    @OnClick({R.id.rl_edit_contact})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_edit_contact:
                intent = new Intent(UserSettingActivity.this, EditContactActivity.class);
                intent.putExtra("contactId", mContactId);
                intent.putExtra("isFriend", mIsFriend);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContact = mUserDao.getUserById(mContactId);
        loadData(mContact);
    }
}
