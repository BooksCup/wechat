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
import com.bc.wechat.entity.Message;
import com.bc.wechat.utils.PreferencesUtil;

import java.util.Collections;
import java.util.Comparator;
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
                Message.deleteAll(Message.class);
            }
        });

        mNewFriendsUnreadNumTv = headerView.findViewById(R.id.tv_new_friends_unread);

        friendsList = Friend.listAll(Friend.class);
        // 对list进行排序
        Collections.sort(friendsList, new PinyinComparator() {
        });

        friendsAdapter = new FriendsAdapter(getActivity(), R.layout.item_friends, friendsList);
        mFriendsLv.setAdapter(friendsAdapter);

        mFriendsCountTv.setText(friendsList.size() + "位联系人");

        mFriendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != friendsList.size() + 1) {
                    Friend friend = friendsList.get(position - 1);
                    startActivity(new Intent(getActivity(), UserInfoActivity.class).
                            putExtra("userId", friend.getUserId()).
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
        // 对list进行排序
        Collections.sort(friendsList, new PinyinComparator() {
        });
        friendsAdapter.setData(friendsList);
        friendsAdapter.notifyDataSetChanged();
        mFriendsCountTv.setText(friendsList.size() + "位联系人");
    }

    public class PinyinComparator implements Comparator<Friend> {

        @Override
        public int compare(Friend o1, Friend o2) {
            String py1 = o1.getUserHeader();
            String py2 = o2.getUserHeader();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getUserHeader()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getUserHeader()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }
}
