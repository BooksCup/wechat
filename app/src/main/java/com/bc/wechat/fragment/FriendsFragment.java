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
import android.widget.TextView;
import android.widget.Toast;

import com.bc.wechat.R;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.adapter.FriendsAdapter;
import com.bc.wechat.entity.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private FriendsAdapter friendsAdapter;

    private ListView mFriendsLv;
    private LayoutInflater inflater;

    // 好友总数
    private TextView mFriendsCountTv;

    // 好友列表
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

        mFriendsCountTv = footerView.findViewById(R.id.tv_total);

        friendsList = new ArrayList<>();
        User user1 = new User();
        user1.setNickName("张三");
        user1.setHeader("Z");
        user1.setAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/6971f1ff7cab4daca6fe5011406cec3e.jpg");
        user1.setSex("1");

        User user2 = new User();
        user2.setNickName("杖撸");
        user2.setHeader("Z");
        user2.setAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/6971f1ff7cab4daca6fe5011406cec3e.jpg");
        user2.setSex("2");

        User user3 = new User();
        user3.setNickName("李四");
        user3.setHeader("L");
        user3.setAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/a7d913e78cd04682bad50ad90e81ae29.png");
        user3.setSex("1");

        User user4 = new User();
        user4.setNickName("力王");
        user4.setHeader("L");
        user4.setAvatar("http://erp-cfpu-com.oss-cn-hangzhou.aliyuncs.com/a7d913e78cd04682bad50ad90e81ae29.png");
        user4.setSex("2");

        friendsList.add(user1);
        friendsList.add(user2);
        friendsList.add(user3);
        friendsList.add(user4);

        friendsAdapter = new FriendsAdapter(getActivity(), R.layout.item_friends, friendsList);
        mFriendsLv.setAdapter(friendsAdapter);

        mFriendsCountTv.setText(friendsList.size() + "位联系人");

        mFriendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != friendsList.size() + 1) {
                    User user = friendsList.get(position - 1);
                    startActivity(new Intent(getActivity(), UserInfoActivity.class).
                            putExtra("nickName", user.getNickName()).
                            putExtra("avatar", user.getAvatar()).
                            putExtra("sex", user.getSex()));
                }
            }
        });
    }
}
