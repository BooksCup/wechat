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

import com.bc.wechat.R;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.adapter.ConversationAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationFragment extends Fragment {

    private ListView listView;
    private ConversationAdapter conversationAdapter;
    List<Conversation> conversationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = getView().findViewById(R.id.list);
        conversationList = JMessageClient.getConversationList();
        conversationAdapter = new ConversationAdapter(getActivity(), conversationList);
        listView.setAdapter(conversationAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = conversationList.get(position);
                UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                List<Friend> friendList = Friend.find(Friend.class, "user_id = ?", userInfo.getUserName());
                if (null != friendList && friendList.size() > 0) {
                    Friend friend = friendList.get(0);
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
}
