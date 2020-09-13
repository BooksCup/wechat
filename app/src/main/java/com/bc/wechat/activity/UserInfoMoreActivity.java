package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * "用户信息"-"更多信息"
 *
 * @author zhou
 */
public class UserInfoMoreActivity extends BaseActivity {
    @BindView(R.id.tv_whats_up)
    TextView mWhatsUpTv;

    UserDao mUserDao;
    private String mContactId;
    private User mContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_more);
        ButterKnife.bind(this);
        initStatusBar();

        initData();
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initData() {
        mUserDao = new UserDao();
        mContactId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactId);
    }

    private void initView() {
        mWhatsUpTv.setText(mContact.getUserSign());
    }
}
