package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bc.wechat.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;

public class UpdateGroupNameActivity extends FragmentActivity {
    String groupId;
    private EditText mGroupNameEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group_name);
        initView();
    }

    private void initView() {
        mGroupNameEt = findViewById(R.id.et_group_name);

        groupId = getIntent().getStringExtra("groupId");
        Conversation conversation = JMessageClient.getGroupConversation(Long.valueOf(groupId));
        GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();
        if (TextUtils.isEmpty(groupInfo.getGroupDescription())) {
            mGroupNameEt.setHint(groupInfo.getGroupName());
        } else {
            mGroupNameEt.setText(groupInfo.getGroupName());
            // 光标移至最后
            CharSequence charSequence = mGroupNameEt.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        }
    }

    public void back(View view) {
        finish();
    }
}
