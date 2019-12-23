package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.UserFriendsCircleAdapter;
import com.bc.wechat.entity.FriendsCircle;

import java.util.ArrayList;
import java.util.List;

public class UserFriendsCircleActivity extends BaseActivity {

    private ListView mFriendsCircleLv;
    UserFriendsCircleAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends_circle);
        initView();
    }

    private void initView() {

        mFriendsCircleLv = findViewById(R.id.lv_friends_circle);
        List<FriendsCircle> mFriendsCircleList = new ArrayList<>();
        mFriendsCircleList.add(new FriendsCircle());
        mFriendsCircleList.add(new FriendsCircle());
        mFriendsCircleList.add(new FriendsCircle());
        mAdapter = new UserFriendsCircleAdapter(mFriendsCircleList, this);
        mFriendsCircleLv.setAdapter(mAdapter);
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);
        mFriendsCircleLv.addHeaderView(headerView, null, false);
    }

    public void back(View view) {
        finish();
    }
}
