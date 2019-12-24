package com.bc.wechat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.UserFriendsCircleAdapter;
import com.bc.wechat.dao.FriendDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.FriendsCircle;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class UserFriendsCircleActivity extends BaseActivity {

    private ListView mFriendsCircleLv;
    UserFriendsCircleAdapter mAdapter;
    private FriendDao mFriendDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends_circle);
        mFriendDao = new FriendDao();
        initView();
    }

    private void initView() {
        final String userId = getIntent().getStringExtra("userId");
        final Friend friend = mFriendDao.getFriendById(userId);
        mFriendsCircleLv = findViewById(R.id.lv_friends_circle);
        List<FriendsCircle> mFriendsCircleList = new ArrayList<>();
        mFriendsCircleList.add(new FriendsCircle());
        mFriendsCircleList.add(new FriendsCircle());
        mFriendsCircleList.add(new FriendsCircle());
        mAdapter = new UserFriendsCircleAdapter(mFriendsCircleList, this);
        mFriendsCircleLv.setAdapter(mAdapter);
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);
        mFriendsCircleLv.addHeaderView(headerView, null, false);
        mFriendsCircleLv.setHeaderDividersEnabled(false);

        // headerView
        ImageView mCoverIv = headerView.findViewById(R.id.iv_cover);
        mCoverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        TextView mNickNameTv = headerView.findViewById(R.id.tv_nick_name);
        mNickNameTv.setText(friend.getUserNickName());

        SimpleDraweeView mAvatarSdv = headerView.findViewById(R.id.sdv_avatar);
        if (!TextUtils.isEmpty(friend.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
        }
    }

    public void back(View view) {
        finish();
    }
}
