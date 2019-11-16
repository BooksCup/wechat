package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.MessageDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.utils.ConversationUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimeUtil;
import com.bc.wechat.utils.TimestampUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationAdapter extends BaseAdapter {


    private List<Conversation> conversationList;
    private Context mContext;
    private LayoutInflater inflater;
    private MessageDao messageDao;

    public ConversationAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.conversationList = conversationList;
        inflater = LayoutInflater.from(context);
        messageDao = new MessageDao();
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
            // 如果消息被清除
            // conversation.getLastestMessage() == null
            mLastMsgTv.setText(ConversationUtil.getLatestMessage(conversation));

            int unReadMsgCnt = conversation.getUnReadMsgCnt();
            if (unReadMsgCnt <= 0) {
                mUnreadTv.setVisibility(View.GONE);
            } else if (unReadMsgCnt > 99) {
                mUnreadTv.setText("99+");
            } else {
                mUnreadTv.setText(String.valueOf(conversation.getUnReadMsgCnt()));
            }
            // conversation由极光维护
            // 如果消息被清除，conversation.getLastestMessage() == null
            // 这个时间不好显示
            if (null == conversation.getLatestMessage()) {
                mCreateTimeTv.setText(TimestampUtil.getTimePoint(new Date().getTime()));
            } else {
                mCreateTimeTv.setText(TimestampUtil.getTimePoint(conversation.getLatestMessage().getCreateTime()));
            }


        } else {
            // 群聊
            GroupInfo jGroupInfo = (GroupInfo) conversation.getTargetInfo();
            String groupDesc = "";

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

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();

                if (!TextUtils.isEmpty(avatar1)) {
                    mAvatar1Sdv.setImageURI(avatar1);
                }
                if (!TextUtils.isEmpty(avatar2)) {
                    mAvatar2Sdv.setImageURI(avatar2);
                }
                if (!TextUtils.isEmpty(avatar3)) {
                    mAvatar3Sdv.setImageURI(avatar3);
                }
                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3);

            } else if (memberCount == 4) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();
                String userNickName4 = groupMemberInfoList.get(3).getUserInfo().getNickname();

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

                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3, userNickName4);
            } else if (memberCount == 5) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                SimpleDraweeView mAvatar5Sdv = convertView.findViewById(R.id.sdv_avatar5);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();
                String avatar5 = groupMemberInfoList.get(4).getUserInfo().getAvatar();

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();
                String userNickName4 = groupMemberInfoList.get(3).getUserInfo().getNickname();
                String userNickName5 = groupMemberInfoList.get(4).getUserInfo().getNickname();

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
                if (!TextUtils.isEmpty(avatar5)) {
                    mAvatar5Sdv.setImageURI(avatar5);
                }

                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3, userNickName4, userNickName5);
            } else if (memberCount == 6) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                SimpleDraweeView mAvatar5Sdv = convertView.findViewById(R.id.sdv_avatar5);
                SimpleDraweeView mAvatar6Sdv = convertView.findViewById(R.id.sdv_avatar6);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();
                String avatar5 = groupMemberInfoList.get(4).getUserInfo().getAvatar();
                String avatar6 = groupMemberInfoList.get(5).getUserInfo().getAvatar();

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();
                String userNickName4 = groupMemberInfoList.get(3).getUserInfo().getNickname();
                String userNickName5 = groupMemberInfoList.get(4).getUserInfo().getNickname();
                String userNickName6 = groupMemberInfoList.get(5).getUserInfo().getNickname();

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
                if (!TextUtils.isEmpty(avatar5)) {
                    mAvatar5Sdv.setImageURI(avatar5);
                }
                if (!TextUtils.isEmpty(avatar6)) {
                    mAvatar6Sdv.setImageURI(avatar6);
                }

                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3, userNickName4, userNickName5, userNickName6);
            } else if (memberCount == 7) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                SimpleDraweeView mAvatar5Sdv = convertView.findViewById(R.id.sdv_avatar5);
                SimpleDraweeView mAvatar6Sdv = convertView.findViewById(R.id.sdv_avatar6);
                SimpleDraweeView mAvatar7Sdv = convertView.findViewById(R.id.sdv_avatar7);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();
                String avatar5 = groupMemberInfoList.get(4).getUserInfo().getAvatar();
                String avatar6 = groupMemberInfoList.get(5).getUserInfo().getAvatar();
                String avatar7 = groupMemberInfoList.get(6).getUserInfo().getAvatar();

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();
                String userNickName4 = groupMemberInfoList.get(3).getUserInfo().getNickname();
                String userNickName5 = groupMemberInfoList.get(4).getUserInfo().getNickname();
                String userNickName6 = groupMemberInfoList.get(5).getUserInfo().getNickname();
                String userNickName7 = groupMemberInfoList.get(6).getUserInfo().getNickname();

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
                if (!TextUtils.isEmpty(avatar5)) {
                    mAvatar5Sdv.setImageURI(avatar5);
                }
                if (!TextUtils.isEmpty(avatar6)) {
                    mAvatar6Sdv.setImageURI(avatar6);
                }
                if (!TextUtils.isEmpty(avatar7)) {
                    mAvatar7Sdv.setImageURI(avatar7);
                }
                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3, userNickName4, userNickName5, userNickName6, userNickName7);
            } else if (memberCount == 8) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                SimpleDraweeView mAvatar5Sdv = convertView.findViewById(R.id.sdv_avatar5);
                SimpleDraweeView mAvatar6Sdv = convertView.findViewById(R.id.sdv_avatar6);
                SimpleDraweeView mAvatar7Sdv = convertView.findViewById(R.id.sdv_avatar7);
                SimpleDraweeView mAvatar8Sdv = convertView.findViewById(R.id.sdv_avatar8);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();
                String avatar5 = groupMemberInfoList.get(4).getUserInfo().getAvatar();
                String avatar6 = groupMemberInfoList.get(5).getUserInfo().getAvatar();
                String avatar7 = groupMemberInfoList.get(6).getUserInfo().getAvatar();
                String avatar8 = groupMemberInfoList.get(7).getUserInfo().getAvatar();

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();
                String userNickName4 = groupMemberInfoList.get(3).getUserInfo().getNickname();
                String userNickName5 = groupMemberInfoList.get(4).getUserInfo().getNickname();
                String userNickName6 = groupMemberInfoList.get(5).getUserInfo().getNickname();
                String userNickName7 = groupMemberInfoList.get(6).getUserInfo().getNickname();
                String userNickName8 = groupMemberInfoList.get(7).getUserInfo().getNickname();

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
                if (!TextUtils.isEmpty(avatar5)) {
                    mAvatar5Sdv.setImageURI(avatar5);
                }
                if (!TextUtils.isEmpty(avatar6)) {
                    mAvatar6Sdv.setImageURI(avatar6);
                }
                if (!TextUtils.isEmpty(avatar7)) {
                    mAvatar7Sdv.setImageURI(avatar7);
                }
                if (!TextUtils.isEmpty(avatar8)) {
                    mAvatar8Sdv.setImageURI(avatar8);
                }
                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3, userNickName4,
                        userNickName5, userNickName6, userNickName7, userNickName8);
            } else if (memberCount >= 9) {
                SimpleDraweeView mAvatar1Sdv = convertView.findViewById(R.id.sdv_avatar1);
                SimpleDraweeView mAvatar2Sdv = convertView.findViewById(R.id.sdv_avatar2);
                SimpleDraweeView mAvatar3Sdv = convertView.findViewById(R.id.sdv_avatar3);
                SimpleDraweeView mAvatar4Sdv = convertView.findViewById(R.id.sdv_avatar4);
                SimpleDraweeView mAvatar5Sdv = convertView.findViewById(R.id.sdv_avatar5);
                SimpleDraweeView mAvatar6Sdv = convertView.findViewById(R.id.sdv_avatar6);
                SimpleDraweeView mAvatar7Sdv = convertView.findViewById(R.id.sdv_avatar7);
                SimpleDraweeView mAvatar8Sdv = convertView.findViewById(R.id.sdv_avatar8);
                SimpleDraweeView mAvatar9Sdv = convertView.findViewById(R.id.sdv_avatar9);
                String avatar1 = groupMemberInfoList.get(0).getUserInfo().getAvatar();
                String avatar2 = groupMemberInfoList.get(1).getUserInfo().getAvatar();
                String avatar3 = groupMemberInfoList.get(2).getUserInfo().getAvatar();
                String avatar4 = groupMemberInfoList.get(3).getUserInfo().getAvatar();
                String avatar5 = groupMemberInfoList.get(4).getUserInfo().getAvatar();
                String avatar6 = groupMemberInfoList.get(5).getUserInfo().getAvatar();
                String avatar7 = groupMemberInfoList.get(6).getUserInfo().getAvatar();
                String avatar8 = groupMemberInfoList.get(7).getUserInfo().getAvatar();
                String avatar9 = groupMemberInfoList.get(8).getUserInfo().getAvatar();

                String userNickName1 = groupMemberInfoList.get(0).getUserInfo().getNickname();
                String userNickName2 = groupMemberInfoList.get(1).getUserInfo().getNickname();
                String userNickName3 = groupMemberInfoList.get(2).getUserInfo().getNickname();
                String userNickName4 = groupMemberInfoList.get(3).getUserInfo().getNickname();
                String userNickName5 = groupMemberInfoList.get(4).getUserInfo().getNickname();
                String userNickName6 = groupMemberInfoList.get(5).getUserInfo().getNickname();
                String userNickName7 = groupMemberInfoList.get(6).getUserInfo().getNickname();
                String userNickName8 = groupMemberInfoList.get(7).getUserInfo().getNickname();
                String userNickName9 = groupMemberInfoList.get(8).getUserInfo().getNickname();

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
                if (!TextUtils.isEmpty(avatar5)) {
                    mAvatar5Sdv.setImageURI(avatar5);
                }
                if (!TextUtils.isEmpty(avatar6)) {
                    mAvatar6Sdv.setImageURI(avatar6);
                }
                if (!TextUtils.isEmpty(avatar7)) {
                    mAvatar7Sdv.setImageURI(avatar7);
                }
                if (!TextUtils.isEmpty(avatar8)) {
                    mAvatar8Sdv.setImageURI(avatar8);
                }
                if (!TextUtils.isEmpty(avatar9)) {
                    mAvatar9Sdv.setImageURI(avatar9);
                }
                groupDesc = generateGroupDesc(PreferencesUtil.getInstance().getUser().getUserNickName(),
                        userNickName1, userNickName2, userNickName3, userNickName4,
                        userNickName5, userNickName6, userNickName7, userNickName8, userNickName9);
            }

            TextView mGroupNameTv = convertView.findViewById(R.id.tv_group_name);
            TextView mUnreadTv = convertView.findViewById(R.id.tv_unread);
            TextView mCreateTimeTv = convertView.findViewById(R.id.tv_create_time);
            TextView mLastMsgTv = convertView.findViewById(R.id.tv_last_msg);

            // 极光群组误删此处会有异常
            try {
                mGroupNameTv.setText(jGroupInfo.getGroupName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String messageType;
            try {
                messageType = conversation.getLatestMessage().getContentType().name();
            } catch (Exception e) {
                messageType = Constant.MSG_TYPE_TEXT;
            }
            long messageCount = messageDao.getMessageCountByGroupId(String.valueOf(jGroupInfo.getGroupID()));
            if (messageCount == 0) {
                mLastMsgTv.setText("");
            } else {
                String lastMsg = conversation.getLatestText();
                if (TextUtils.isEmpty(lastMsg)) {
                    mLastMsgTv.setText("你邀请" + groupDesc + "加入了群聊");
                } else {
                    UserInfo lastestFromUser = conversation.getLatestMessage().getFromUser();
                    String lastestFromUserName = lastestFromUser.getUserName().equals(PreferencesUtil.getInstance().getUser().getUserId()) ? "" :
                            lastestFromUser.getNickname() + ": ";
                    if (Constant.MSG_TYPE_TEXT.equals(messageType)) {
                        mLastMsgTv.setText(lastestFromUserName + conversation.getLatestText());
                    } else if (Constant.MSG_TYPE_IMAGE.equals(messageType)) {
                        mLastMsgTv.setText(lastestFromUserName + "[图片]");
                    } else {
                        mLastMsgTv.setText(lastestFromUserName + conversation.getLatestText());
                    }
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

        } else if (size == 5) {
            convertView = inflater.inflate(R.layout.item_conversation_group_5,
                    null, false);

        } else if (size == 6) {
            convertView = inflater.inflate(R.layout.item_conversation_group_6,
                    null, false);

        } else if (size == 7) {
            convertView = inflater.inflate(R.layout.item_conversation_group_7,
                    null, false);

        } else if (size == 8) {
            convertView = inflater.inflate(R.layout.item_conversation_group_8,
                    null, false);

        } else if (size >= 9) {
            convertView = inflater.inflate(R.layout.item_conversation_group_9,
                    null, false);

        } else {
            convertView = inflater.inflate(R.layout.item_conversation_group_5,
                    null, false);

        }
        return convertView;
    }

    private String generateGroupDesc(String myNickName, String... userNickNames) {
        StringBuffer groupDescBuffer = new StringBuffer();
        for (String userNickName : userNickNames) {
            if (!userNickName.equals(myNickName)) {
                groupDescBuffer.append(userNickName).append("、");
            }
        }
        if (groupDescBuffer.length() > 1) {
            groupDescBuffer.deleteCharAt(groupDescBuffer.length() - 1);
        }
        return groupDescBuffer.toString();
    }
}
