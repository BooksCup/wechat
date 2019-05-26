package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.GroupSettingGridAdapter;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.JimBeanUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.ExpandGridView;


import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.api.BasicCallback;


public class ChatGroupSettingActivity extends FragmentActivity implements View.OnClickListener {
    private String groupId;

    private TextView mMemberNumTv;
    private ExpandGridView mAvatarsEgv;
    private GroupSettingGridAdapter mGroupSettingGridAdapter;
    private TextView mGroupNameTv;

    private RelativeLayout mExitGroupRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_setting);
        initView();
    }

    private void initView() {

        mMemberNumTv = findViewById(R.id.tv_member_num);
        mAvatarsEgv = findViewById(R.id.egv_avatars);
        mGroupNameTv = findViewById(R.id.tv_group_name);
        mExitGroupRl = findViewById(R.id.rl_exit_group);


        groupId = getIntent().getStringExtra("groupId");
        Conversation conversation = JMessageClient.getGroupConversation(Long.valueOf(groupId));
        GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();
        List<GroupMemberInfo> groupMemberInfoList = groupInfo.getGroupMemberInfos();
        List<User> userList = new ArrayList<>();
        for (GroupMemberInfo groupMemberInfo : groupMemberInfoList) {
            User user = JimBeanUtil.transferGroupMemberInfoToUser(groupMemberInfo);
            userList.add(user);
        }

        mMemberNumTv.setText("(" + userList.size() + ")");
        mGroupNameTv.setText(groupInfo.getGroupName());
        mGroupSettingGridAdapter = new GroupSettingGridAdapter(this, userList);
        mAvatarsEgv.setAdapter(mGroupSettingGridAdapter);

        mExitGroupRl.setOnClickListener(this);

    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_exit_group:
                final ConfirmDialog confirmDialog = new ConfirmDialog(this,
                        "是否删除群聊并退出?", "删除", "");
                confirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOKClick() {
                        confirmDialog.dismiss();
                        exitGroup(groupId);
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                confirmDialog.setCancelable(true);
                confirmDialog.show();
                break;
        }
    }

    private void exitGroup(String groupId) {
        JMessageClient.exitGroup(Long.valueOf(groupId), new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseDesc) {
                if (responseCode == 0) {
                    // 退群成功
                } else {
                    // 退群失败
                }
            }
        });
        JMessageClient.deleteGroupConversation(Long.valueOf(groupId));

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
