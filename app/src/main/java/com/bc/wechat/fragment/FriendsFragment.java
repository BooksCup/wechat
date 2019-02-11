package com.bc.wechat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.NewFriendsMsgActivity;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.adapter.FriendsAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private FriendsAdapter friendsAdapter;

    private ListView mFriendsLv;
    private LayoutInflater inflater;

    // 好友总数
    private TextView mFriendsCountTv;

    TextView mNewFriendsUnreadNumTv;

    // 好友列表
    private List<Friend> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PreferencesUtil.getInstance().init(getActivity());
        mFriendsLv = getView().findViewById(R.id.lv_friends);
        inflater = LayoutInflater.from(getActivity());
        View headerView = inflater.inflate(R.layout.item_friends_header, null);
        mFriendsLv.addHeaderView(headerView);
        View footerView = inflater.inflate(R.layout.item_friends_footer, null);
        mFriendsLv.addFooterView(footerView);

        mFriendsCountTv = footerView.findViewById(R.id.tv_total);

        RelativeLayout mNewFriendsRl = headerView.findViewById(R.id.rl_new_friends);
        mNewFriendsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                PreferencesUtil.getInstance().setNewFriendsUnreadNumber(0);
            }
        });

        RelativeLayout mChatRoomRl = headerView.findViewById(R.id.re_chatroom);
        mChatRoomRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendApply.deleteAll(FriendApply.class);
                Friend.deleteAll(Friend.class);
            }
        });

        mNewFriendsUnreadNumTv = headerView.findViewById(R.id.tv_new_friends_unread);

//        friendsList = new ArrayList<>();
//        User user1 = new User();
//        user1.setUserNickName("张三");
//        user1.setUserHeader("Z");
//        user1.setUserAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/6971f1ff7cab4daca6fe5011406cec3e.jpg");
//        user1.setUserSex("1");
//
//        User user2 = new User();
//        user2.setUserNickName("杖撸");
//        user2.setUserHeader("Z");
//        user2.setUserAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/6971f1ff7cab4daca6fe5011406cec3e.jpg");
//        user2.setUserSex("2");
//
//        User user3 = new User();
//        user3.setUserNickName("李四");
//        user3.setUserHeader("L");
//        user3.setUserAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/a7d913e78cd04682bad50ad90e81ae29.png");
//        user3.setUserSex("1");
//
//        User user4 = new User();
//        user4.setUserNickName("力王");
//        user4.setUserHeader("L");
//        user4.setUserAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/a7d913e78cd04682bad50ad90e81ae29.png");
//        user4.setUserSex("2");
//
//        friendsList.add(user1);
//        friendsList.add(user2);
//        friendsList.add(user3);
//        friendsList.add(user4);

        friendsList = Friend.listAll(Friend.class);

        friendsAdapter = new FriendsAdapter(getActivity(), R.layout.item_friends, friendsList);
        mFriendsLv.setAdapter(friendsAdapter);

        mFriendsCountTv.setText(friendsList.size() + "位联系人");

        mFriendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != friendsList.size() + 1) {
                    Friend friend = friendsList.get(position - 1);
                    startActivity(new Intent(getActivity(), UserInfoActivity.class).
                            putExtra("nickName", friend.getUserNickName()).
                            putExtra("avatar", friend.getUserAvatar()).
                            putExtra("sex", friend.getUserSex()).
                            putExtra("isFriend", Constant.IS_FRIEND));
                }
            }
        });
    }

    public void refreshNewFriendsUnreadNum() {
        int newFriendsUnreadNum = PreferencesUtil.getInstance().getNewFriendsUnreadNumber();
        if (newFriendsUnreadNum > 0) {
            mNewFriendsUnreadNumTv.setVisibility(View.VISIBLE);
            mNewFriendsUnreadNumTv.setText(String.valueOf(newFriendsUnreadNum));
        } else {
            mNewFriendsUnreadNumTv.setVisibility(View.GONE);
        }
    }

    public void refreshFriendsList() {
        friendsList = Friend.listAll(Friend.class);
        friendsAdapter.setData(friendsList);
        friendsAdapter.notifyDataSetChanged();
    }
}
