package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bc.wechat.R;
import com.bc.wechat.adapter.GroupSettingGridAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.MessageDao;
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


/**
 * 群聊设置
 *
 * @author zhou
 */
public class ChatGroupSettingActivity extends FragmentActivity implements View.OnClickListener {
    private String groupId;

    private TextView mMemberNumTv;
    private ExpandGridView mAvatarsEgv;
    private GroupSettingGridAdapter mGroupSettingGridAdapter;
    private TextView mGroupNameTv;

    private RelativeLayout mExitGroupRl;

    private RelativeLayout mUpdateGroupNameRl;

    private boolean isTop = false;
    private LinearLayout mSwitchChatToTopLl;
    // 置顶
    private ImageView mSwitchChatToTopIv;
    // 非置顶
    private ImageView mSwitchUnChatToTopIv;


    private boolean isBlock = false;
    private LinearLayout mSwitchBlockGroupMsgLl;
    // 屏蔽
    private ImageView mSwitchBlockGroupMsgIv;
    // 解除屏蔽
    private ImageView mSwitchUnBlockGroupMsgIv;

    // 清空聊天记录
    private RelativeLayout mClearRl;

    private MessageDao messageDao;

    private static final int UPDATE_GROUP_NAME = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_setting);
        messageDao = new MessageDao();
        initView();
    }

    private void initView() {

        mMemberNumTv = findViewById(R.id.tv_member_num);
        mAvatarsEgv = findViewById(R.id.egv_avatars);
        mGroupNameTv = findViewById(R.id.tv_group_name);
        mExitGroupRl = findViewById(R.id.rl_exit_group);

        mUpdateGroupNameRl = findViewById(R.id.rl_change_group_name);
        mClearRl = findViewById(R.id.rl_clear);

        mSwitchChatToTopLl = findViewById(R.id.ll_switch_chat_to_top);
        mSwitchChatToTopIv = findViewById(R.id.iv_switch_chat_to_top);
        mSwitchUnChatToTopIv = findViewById(R.id.iv_switch_unchat_to_top);

        mSwitchBlockGroupMsgLl = findViewById(R.id.ll_switch_block_group_msg);
        mSwitchBlockGroupMsgIv = findViewById(R.id.iv_switch_block_group_msg);
        mSwitchUnBlockGroupMsgIv = findViewById(R.id.iv_switch_unblock_group_msg);

        groupId = getIntent().getStringExtra("groupId");
        Conversation conversation = JMessageClient.getGroupConversation(Long.valueOf(groupId));
        GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();

        final List<GroupMemberInfo> groupMemberInfoList = groupInfo.getGroupMemberInfos();
        final List<User> userList = new ArrayList<>();
        for (GroupMemberInfo groupMemberInfo : groupMemberInfoList) {
            User user = JimBeanUtil.transferGroupMemberInfoToUser(groupMemberInfo);
            userList.add(user);
        }

        mMemberNumTv.setText("(" + userList.size() + ")");
        // 通过groupDesc是否为空判断有没有设置过群名
        // 本来应该直接通过groupName去做这个逻辑，但是极光新建群组时groupName不能为空
        if (TextUtils.isEmpty(groupInfo.getGroupDescription())) {
            mGroupNameTv.setText("未命名");
        } else {
            mGroupNameTv.setText(groupInfo.getGroupName());
        }

        mGroupSettingGridAdapter = new GroupSettingGridAdapter(this, userList);
        mAvatarsEgv.setAdapter(mGroupSettingGridAdapter);

        mSwitchChatToTopLl.setOnClickListener(this);
        mSwitchBlockGroupMsgLl.setOnClickListener(this);
        mExitGroupRl.setOnClickListener(this);

        mUpdateGroupNameRl.setOnClickListener(this);
        mClearRl.setOnClickListener(this);

        mAvatarsEgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == groupMemberInfoList.size()) {
                    // 群组拉人
                    ArrayList<String> checkedUserIdList = new ArrayList<>();
                    ArrayList<String> checkedUserNickNameList = new ArrayList<>();
                    for (User user : userList) {
                        checkedUserIdList.add(user.getUserId());
                        checkedUserNickNameList.add(user.getUserNickName());
                    }
                    Intent intent = new Intent(ChatGroupSettingActivity.this, CreateGroupActivity.class);
                    intent.putExtra("createType", Constant.CREATE_GROUP_TYPE_FROM_GROUP);
                    intent.putExtra("groupId", groupId);
                    intent.putStringArrayListExtra("userIdList", checkedUserIdList);
                    intent.putStringArrayListExtra("userNickNameList", checkedUserNickNameList);
                    startActivity(intent);

                } else if (position == groupMemberInfoList.size() + 1) {
                    Toast.makeText(ChatGroupSettingActivity.this, "-", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_change_group_name:
                Intent intent = new Intent(this, UpdateGroupNameActivity.class);
                intent.putExtra("groupId", groupId);
                startActivityForResult(intent, UPDATE_GROUP_NAME);
                break;
            case R.id.rl_clear:
                final ConfirmDialog clearConfirmDialog = new ConfirmDialog(this, "",
                        "确定删除群的聊天记录吗?", "清空", "");
                clearConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        // 清除本地message
                        messageDao.deleteMessageByGroupId(groupId);
                        clearConfirmDialog.dismiss();
                    }

                    @Override
                    public void onCancelClick() {
                        clearConfirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                clearConfirmDialog.setCancelable(true);
                clearConfirmDialog.show();
                break;

            case R.id.rl_exit_group:
                final ConfirmDialog exitConfirmDialog = new ConfirmDialog(this, "",
                        "是否删除群聊并退出?", "删除", "");
                exitConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        exitConfirmDialog.dismiss();
                        exitGroup(groupId);
                    }

                    @Override
                    public void onCancelClick() {
                        exitConfirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                exitConfirmDialog.setCancelable(true);
                exitConfirmDialog.show();
                break;
            case R.id.ll_switch_chat_to_top:
                if (isTop) {
                    mSwitchChatToTopIv.setVisibility(View.GONE);
                    mSwitchUnChatToTopIv.setVisibility(View.VISIBLE);
                    isTop = false;
                } else {
                    mSwitchChatToTopIv.setVisibility(View.VISIBLE);
                    mSwitchUnChatToTopIv.setVisibility(View.GONE);
                    isTop = true;
                }
                break;

            case R.id.ll_switch_block_group_msg:
                if (isBlock) {
                    mSwitchBlockGroupMsgIv.setVisibility(View.GONE);
                    mSwitchUnBlockGroupMsgIv.setVisibility(View.VISIBLE);
                    isBlock = false;
                } else {
                    mSwitchBlockGroupMsgIv.setVisibility(View.VISIBLE);
                    mSwitchUnBlockGroupMsgIv.setVisibility(View.GONE);
                    isBlock = true;
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_GROUP_NAME:
                    String groupName = data.getStringExtra("groupName");
                    mGroupNameTv.setText(groupName);
                    break;
            }
        }
    }
}
