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
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PinyinComparator;
import com.bc.wechat.utils.PreferencesUtil;

import java.util.Collections;
import java.util.List;


public class ContactsFragment extends Fragment {
    private FriendsAdapter mFriendsAdapter;

    private ListView mFriendsLv;
    private LayoutInflater mInflater;

    // 好友总数
    private TextView mFriendsCountTv;

    TextView mNewFriendsUnreadNumTv;

    // 好友列表
    private List<User> mFriendList;

    // 星标好友列表
    private List<User> mStarFriendList;

    private UserDao mUserDao;

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
        mUserDao = new UserDao();
        mFriendsLv = getView().findViewById(R.id.lv_friends);
        mInflater = LayoutInflater.from(getActivity());
        View headerView = mInflater.inflate(R.layout.item_friends_header, null);
        mFriendsLv.addHeaderView(headerView);
        View footerView = mInflater.inflate(R.layout.item_friends_footer, null);
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
            }
        });

        mNewFriendsUnreadNumTv = headerView.findViewById(R.id.tv_new_friends_unread);

        mStarFriendList = mUserDao.getAllStarFriendList();
        mFriendList = mUserDao.getAllFriendList();
        // 对list进行排序
        Collections.sort(mFriendList, new PinyinComparator() {
        });

        mStarFriendList.addAll(mFriendList);

        mFriendsAdapter = new FriendsAdapter(getActivity(), R.layout.item_friends, mStarFriendList);
        mFriendsLv.setAdapter(mFriendsAdapter);

        mFriendsCountTv.setText(mFriendList.size() + "位联系人");

        mFriendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != mStarFriendList.size() + 1) {
                    User friend = mStarFriendList.get(position - 1);
                    startActivity(new Intent(getActivity(), UserInfoActivity.class).
                            putExtra("userId", friend.getUserId()));
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
        mStarFriendList = mUserDao.getAllStarFriendList();
        mFriendList = mUserDao.getAllFriendList();

        // 对list进行排序
        Collections.sort(mFriendList, new PinyinComparator() {
        });
        mStarFriendList.addAll(mFriendList);
        mFriendsAdapter.setData(mStarFriendList);
        mFriendsAdapter.notifyDataSetChanged();
        mFriendsCountTv.setText(mFriendList.size() + "位联系人");
    }


}
