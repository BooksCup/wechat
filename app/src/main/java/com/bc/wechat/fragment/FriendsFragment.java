package com.bc.wechat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.FriendsAdapter;
import com.bc.wechat.entity.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private FriendsAdapter friendsAdapter;

    private ListView mFriendsLv;
    private LayoutInflater inflater;

    // 联系人列表
    private List<User> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFriendsLv = getView().findViewById(R.id.lv_friends);
        inflater = LayoutInflater.from(getActivity());
        View headerView = inflater.inflate(R.layout.item_friends_header, null);
        mFriendsLv.addHeaderView(headerView);
        View footerView = inflater.inflate(R.layout.item_friends_footer, null);
        mFriendsLv.addFooterView(footerView);

        friendsList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getActivity(), R.layout.item_friends, friendsList);
        mFriendsLv.setAdapter(friendsAdapter);
    }
}
