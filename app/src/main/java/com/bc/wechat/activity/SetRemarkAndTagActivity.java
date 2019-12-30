package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bc.wechat.R;
import com.bc.wechat.dao.FriendDao;
import com.bc.wechat.entity.Friend;

/**
 * 设置备注和标签
 *
 * @author zhou
 */
public class SetRemarkAndTagActivity extends BaseActivity {

    private EditText mRemarkEt;
    private FriendDao mFriendDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);
        mFriendDao = new FriendDao();
        initView();
    }

    private void initView() {
        String userId = getIntent().getStringExtra("userId");
        final Friend friend = mFriendDao.getFriendById(userId);

        mRemarkEt = findViewById(R.id.et_remark);
        mRemarkEt.setText(friend.getUserFriendRemark());
    }

    public void back(View view) {
        finish();
    }
}
