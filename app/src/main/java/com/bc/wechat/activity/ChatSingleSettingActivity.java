package com.bc.wechat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.MessageDao;
import com.bc.wechat.widget.ConfirmDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;

/**
 * 单聊设置
 *
 * @author zhou
 */
public class ChatSingleSettingActivity extends BaseActivity2 implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_nick_name)
    TextView mNickNameTv;

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.rl_add_user_to_group)
    RelativeLayout mAddUserToGroupRl;

    @BindView(R.id.rl_clear)
    RelativeLayout mClearRl;

    String userId;
    String userNickName;
    String userAvatar;

    MessageDao messageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_setting);
        ButterKnife.bind(this);
        initStatusBar();

        userId = getIntent().getStringExtra("userId");
        userNickName = getIntent().getStringExtra("userNickName");
        userAvatar = getIntent().getStringExtra("userAvatar");

        messageDao = new MessageDao();
        initView();
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);

        mNickNameTv.setText(userNickName);
        if (!TextUtils.isEmpty(userAvatar)) {
            mAvatarSdv.setImageURI(Uri.parse(userAvatar));
        }

        mAddUserToGroupRl.setOnClickListener(this);
        mClearRl.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_add_user_to_group:
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra("createType", Constant.CREATE_GROUP_TYPE_FROM_SINGLE);
                intent.putExtra("userId", userId);
                intent.putExtra("userNickName", userNickName);
                startActivity(intent);
                break;

            case R.id.rl_clear:
                final ConfirmDialog clearConfirmDialog = new ConfirmDialog(this, "",
                        "确定删除和" + userNickName + "的聊天记录吗?", "清空", "");
                clearConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        // 清除本地message
                        messageDao.deleteMessageByUserId(userId);
                        // 清除会话消息(极光)
                        JMessageClient.getSingleConversation(userId).deleteAllMessage();
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
        }
    }

}