package com.bc.wechat.activity;

import android.view.View;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;

import butterknife.BindView;

/**
 * "用户信息"-"更多信息"
 *
 * @author zhou
 */
public class UserInfoMoreActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_whats_up)
    TextView mWhatsUpTv;

    @BindView(R.id.tv_from)
    TextView mFromTv;

    UserDao mUserDao;
    String mContactId;
    User mContact;

    @Override
    public int getContentView() {
        return R.layout.activity_user_info_more;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        mUserDao = new UserDao();
        mContactId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactId);
        mWhatsUpTv.setText(mContact.getUserSign());
        // 来源
        if (Constant.CONTACTS_FROM_PHONE.equals(mContact.getUserContactFrom())) {
            mFromTv.setText("通过搜索手机号添加");
        } else if (Constant.CONTACTS_FROM_WX_ID.equals(mContact.getUserContactFrom())) {
            mFromTv.setText("通过搜索微信号添加");
        } else if (Constant.CONTACTS_FROM_PEOPLE_NEARBY.equals(mContact.getUserContactFrom())) {

        } else if (Constant.CONTACTS_FROM_CONTACT.equals(mContact.getUserContactFrom())) {

        }
    }

    public void back(View view) {
        finish();
    }

}