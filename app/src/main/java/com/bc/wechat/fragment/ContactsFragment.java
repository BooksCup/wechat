package com.bc.wechat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.NewFriendsActivity;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.activity.UserInfoFileHelperActivity;
import com.bc.wechat.adapter.FriendsAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PinyinComparator;
import com.bc.wechat.utils.PreferencesUtil;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通讯录
 *
 * @author zhou
 */
public class ContactsFragment extends BaseFragment {

    FriendsAdapter mFriendsAdapter;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.lv_friends)
    ListView mFriendsLv;

    LayoutInflater mInflater;

    // 好友总数
    TextView mFriendsCountTv;

    TextView mNewFriendsUnreadNumTv;

    // 好友列表
    List<User> mFriendList;

    // 星标好友列表
    List<User> mStarFriendList;

    UserDao mUserDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PreferencesUtil.getInstance().init(getActivity());
        mUserDao = new UserDao();

        setTitleStrokeWidth(mTitleTv);

        mInflater = LayoutInflater.from(getActivity());
        View headerView = mInflater.inflate(R.layout.item_contacts_header, null);
        mFriendsLv.addHeaderView(headerView);
        View footerView = mInflater.inflate(R.layout.item_contacts_footer, null);
        mFriendsLv.addFooterView(footerView);

        mFriendsCountTv = footerView.findViewById(R.id.tv_total);

        RelativeLayout mNewFriendsRl = headerView.findViewById(R.id.rl_new_friends);
        mNewFriendsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewFriendsActivity.class));
                PreferencesUtil.getInstance().setNewFriendsUnreadNumber(0);
            }
        });

        RelativeLayout mGroupChatsRl = headerView.findViewById(R.id.rl_group_chats);
        mGroupChatsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mNewFriendsUnreadNumTv = headerView.findViewById(R.id.tv_new_friends_unread);

        mStarFriendList = mUserDao.getAllStarredContactList();
        mFriendList = mUserDao.getAllFriendList();
        // 对list进行排序
        Collections.sort(mFriendList, new PinyinComparator() {
        });

        mStarFriendList.addAll(mFriendList);

        mFriendsAdapter = new FriendsAdapter(getActivity(), R.layout.item_contacts, mStarFriendList);
        mFriendsLv.setAdapter(mFriendsAdapter);

        mFriendsCountTv.setText(mUserDao.getContactsCount() + "位联系人");

        mFriendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != mStarFriendList.size() + 1) {
                    User friend = mStarFriendList.get(position - 1);
                    String userType = friend.getUserType();
                    if (Constant.USER_TYPE_REG.equals(userType)) {
                        startActivity(new Intent(getActivity(), UserInfoActivity.class).
                                putExtra("userId", friend.getUserId()));
                    } else if (Constant.USER_TYPE_WEIXIN.equals(userType)) {
                        startActivity(new Intent(getActivity(), UserInfoActivity.class).
                                putExtra("userId", friend.getUserId()));
                    } else if (Constant.USER_TYPE_FILEHELPER.equals(userType)) {
                        startActivity(new Intent(getActivity(), UserInfoFileHelperActivity.class).
                                putExtra("userId", friend.getUserId()));
                    }
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
        mStarFriendList = mUserDao.getAllStarredContactList();
        mFriendList = mUserDao.getAllFriendList();

        // 对list进行排序
        Collections.sort(mFriendList, new PinyinComparator() {
        });
        mStarFriendList.addAll(mFriendList);
        mFriendsAdapter.setData(mStarFriendList);
        mFriendsAdapter.notifyDataSetChanged();
        mFriendsCountTv.setText(mUserDao.getContactsCount() + "位联系人");
    }

}