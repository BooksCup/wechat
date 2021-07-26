package com.bc.wechat.activity;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.BlockedContactAdapter;
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
        mBlockedContactList.add(new User());
        mBlockedContactList.add(new User());
        mBlockedContactAdapter = new BlockedContactAdapter(this, mBlockedContactList);
        mBlockedContactLv.setAdapter(mBlockedContactAdapter);
    }

    public void back(View view) {
        finish();
    }

}