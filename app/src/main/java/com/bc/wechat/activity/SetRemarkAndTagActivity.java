package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bc.wechat.R;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;

/**
 * 设置备注和标签
 *
 * @author zhou
 */
public class SetRemarkAndTagActivity extends BaseActivity {

    private EditText mRemarkEt;
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);
        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        String userId = getIntent().getStringExtra("userId");
        final User user = mUserDao.getUserById(userId);

        mRemarkEt = findViewById(R.id.et_remark);
        if (TextUtils.isEmpty(user.getUserFriendRemark())) {
            // 无备注，展示昵称
            mRemarkEt.setText(user.getUserNickName());
        } else {
            // 有备注，展示备注
            mRemarkEt.setText(user.getUserFriendRemark());
        }
    }

    public void back(View view) {
        finish();
    }
}
