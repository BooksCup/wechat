package com.bc.wechat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.BlockedContactAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 通讯录黑名单
 *
 * @author zhou
 */
public class BlockedContactActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.lv_blocked_contact)
    ListView mBlockedContactLv;

    BlockedContactAdapter mBlockedContactAdapter;
    UserDao mUserDao;
    List<User> mBlockedContactList = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.activity_blocked_contact;
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
        mBlockedContactList = mUserDao.getAllBlockedUserList();

        mBlockedContactAdapter = new BlockedContactAdapter(this, mBlockedContactList);
        mBlockedContactLv.setAdapter(mBlockedContactAdapter);

        mBlockedContactLv.setOnItemClickListener((parent, view, position, id) -> {
            User blockedContact = mBlockedContactList.get(position);
            String isFriend = blockedContact.getIsFriend();
            if (Constant.IS_FRIEND.equals(isFriend)) {
                Intent intent = new Intent(BlockedContactActivity.this, UserInfoActivity.class);
                intent.putExtra("userId", blockedContact.getUserId());
                startActivity(intent);
            } else {
                Intent intent = new Intent(BlockedContactActivity.this, UserInfoStrangerActivity.class);
                intent.putExtra("contactId", blockedContact.getUserId());
                intent.putExtra("from", blockedContact.getUserContactFrom());
                startActivity(intent);
            }
        });

    }

    public void back(View view) {
        finish();
    }

}