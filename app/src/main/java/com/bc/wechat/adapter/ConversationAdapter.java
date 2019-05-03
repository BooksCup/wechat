package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.utils.TimestampUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationAdapter extends BaseAdapter {


    private List<Conversation> conversationList;
    private Context mContext;
    private LayoutInflater inflater;

    public ConversationAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.conversationList = conversationList;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Conversation> conversationList) {
        if (null != conversationList) {
            this.conversationList.clear();
            this.conversationList.addAll(conversationList);
        }
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Conversation getItem(int position) {
        return conversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Conversation conversation = conversationList.get(position);

        if (conversation.getType().equals(ConversationType.single)) {
            // 单聊
            convertView = creatConvertView(0);
            TextView mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
            TextView mLastMsgTv = convertView.findViewById(R.id.tv_last_msg);
            SimpleDraweeView mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            TextView mUnreadTv = convertView.findViewById(R.id.tv_unread);
            TextView mCreateTimeTv = convertView.findViewById(R.id.tv_create_time);

            UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
            List<Friend> friendList = Friend.find(Friend.class, "user_id = ?", userInfo.getUserName());
            if (null != friendList && friendList.size() > 0) {
                Friend friend = friendList.get(0);
                mNickNameTv.setText(friend.getUserNickName());
                if (!TextUtils.isEmpty(friend.getUserAvatar())) {
                    mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
                }
            }
            String messageType = conversation.getLatestMessage().getContentType().name();
            if (Constant.MSG_TYPE_TEXT.equals(messageType)) {
                mLastMsgTv.setText(conversation.getLatestText());
            } else if (Constant.MSG_TYPE_IMAGE.equals(messageType)) {
                mLastMsgTv.setText("[图片]");
            } else {
                mLastMsgTv.setText(conversation.getLatestText());
            }
            int unReadMsgCnt = conversation.getUnReadMsgCnt();
            if (unReadMsgCnt <= 0) {
                mUnreadTv.setVisibility(View.GONE);
            } else if (unReadMsgCnt > 99) {
                mUnreadTv.setText("99+");
            } else {
                mUnreadTv.setText(String.valueOf(conversation.getUnReadMsgCnt()));
            }
            mCreateTimeTv.setText(TimestampUtil.getTimePoint(conversation.getLatestMessage().getCreateTime()));

        } else {
            // 群聊
            GroupInfo jGroupInfo = (GroupInfo) conversation.getTargetInfo();

            List<GroupMemberInfo> groupMemberInfoList = jGroupInfo.getGroupMemberInfos();
            int memberCount = groupMemberInfoList.size();
            convertView = creatConvertView(memberCount);
            if (memberCount == 3) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                if (!TextUtils.isEmpty(avatar1)) {
                    mAvatar1Sdv.setImageURI(avatar1);
                }
                if (!TextUtils.isEmpty(avatar2)) {
                    mAvatar2Sdv.setImageURI(avatar2);
                }
                if (!TextUtils.isEmpty(avatar3)) {
                    mAvatar3Sdv.setImageURI(avatar3);
                }
            } else if (memberCount == 4) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();
                if (!TextUtils.isEmpty(avatar1)) {
                    mAvatar1Sdv.setImageURI(avatar1);
                }
                if (!TextUtils.isEmpty(avatar2)) {
                    mAvatar2Sdv.setImageURI(avatar2);
                }
                if (!TextUtils.isEmpty(avatar3)) {
                    mAvatar3Sdv.setImageURI(avatar3);
                }
                if (!TextUtils.isEmpty(avatar4)) {
                    mAvatar4Sdv.setImageURI(avatar4);
                }
            }

            TextView mGroupNameTv = convertView.findViewById(R.id.tv_group_name);
            TextView mUnreadTv = convertView.findViewById(R.id.tv_unread);
            TextView mCreateTimeTv = convertView.findViewById(R.id.tv_create_time);
            TextView mLastMsgTv = convertView.findViewById(R.id.tv_last_msg);

            mGroupNameTv.setText(jGroupInfo.getGroupName());
            String messageType = conversation.getLatestMessage().getContentType().name();
            String lastMsg = conversation.getLatestText();
            if (TextUtils.isEmpty(lastMsg)) {
                mLastMsgTv.setText("你邀请" + jGroupInfo.getGroupName() + "加入了群聊");
            } else {
                UserInfo lastestFromUser = conversation.getLatestMessage().getFromUser();
                if (Constant.MSG_TYPE_TEXT.equals(messageType)) {
                    mLastMsgTv.setText(lastestFromUser.getNickname() + ": " + conversation.getLatestText());
                } else if (Constant.MSG_TYPE_IMAGE.equals(messageType)) {
                    mLastMsgTv.setText(lastestFromUser.getNickname() + ": [图片]");
                } else {
                    mLastMsgTv.setText(lastestFromUser.getNickname() + ": " + conversation.getLatestText());
                }
            }

            int unReadMsgCnt = conversation.getUnReadMsgCnt();
            if (unReadMsgCnt <= 0) {
                mUnreadTv.setVisibility(View.GONE);
            } else if (unReadMsgCnt > 99) {
                mUnreadTv.setText("99+");
            } else {
                mUnreadTv.setText(String.valueOf(conversation.getUnReadMsgCnt()));
            }
            mCreateTimeTv.setText(TimestampUtil.getTimePoint(conversation.getLastMsgDate()));
        }

        return convertView;
    }

    private View creatConvertView(int size) {
        View convertView;

        if (size == 0) {
            convertView = inflater.inflate(R.layout.item_conversation_single,
                    null, false);

        } else if (size == 1) {
            convertView = inflater.inflate(R.layout.item_conversation_group_1,
                    null, false);

        } else if (size == 2) {
            convertView = inflater.inflate(R.layout.item_conversation_group_2,
                    null, false);

        } else if (size == 3) {
            convertView = inflater.inflate(R.layout.item_conversation_group_3,
                    null, false);

        } else if (size == 4) {
            convertView = inflater.inflate(R.layout.item_conversation_group_4,
                    null, false);

        } else if (size > 4) {
            convertView = inflater.inflate(R.layout.item_conversation_group_5,
                    null, false);

        } else {
            convertView = inflater.inflate(R.layout.item_conversation_group_5,
                    null, false);

        }
        return convertView;
    }
}
