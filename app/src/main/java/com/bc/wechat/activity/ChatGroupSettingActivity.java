package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.adapter.GroupSettingGridAdapter;
import com.bc.wechat.entity.User;
import com.bc.wechat.widget.ExpandGridView;


import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;


public class ChatGroupSettingActivity extends FragmentActivity {
    private String groupId;

    private ExpandGridView mAvatarsGv;
    private GroupSettingGridAdapter mGroupSettingGridAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_setting);
        initView();
    }

    private void initView() {
        mAvatarsGv = findViewById(R.id.egd_avatars);

        groupId = getIntent().getStringExtra("groupId");
        Conversation conversation = JMessageClient.getGroupConversation(Long.valueOf(groupId));
        GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();
        List<GroupMemberInfo> groupMemberInfoList = groupInfo.getGroupMemberInfos();
        for (GroupMemberInfo groupMemberInfo : groupMemberInfoList) {
        }

        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        userList.add(new User());
        userList.add(new User());
        userList.add(new User());
        userList.add(new User());
        userList.add(new User());

        mGroupSettingGridAdapter = new GroupSettingGridAdapter(this, userList);
        mAvatarsGv.setAdapter(mGroupSettingGridAdapter);
    }

    public void back(View view) {
        finish();
    }
}
