package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.Message;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimestampUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Message> messageList;

    User user;

    public MessageAdapter(Context context, List<Message> messageList) {
        inflater = LayoutInflater.from(context);
        PreferencesUtil.getInstance().init(context);
        user = PreferencesUtil.getInstance().getUser();
        this.messageList = messageList;
    }

    public void setData(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);

        if (user.getUserId().equals(message.getFromUserId())) {
            convertView = inflater.inflate(R.layout.item_sent_message, null);
        } else {
            convertView = inflater.inflate(R.layout.item_received_message, null);
        }
        TextView mTimeStampTv = convertView.findViewById(R.id.timestamp);
        TextView mContentTv = convertView.findViewById(R.id.tv_chatcontent);
        SimpleDraweeView mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
        ProgressBar mSendingPb = convertView.findViewById(R.id.pb_sending);

        if ("1".equals(message.getStatus())) {
            mSendingPb.setVisibility(View.INVISIBLE);
        }


        mTimeStampTv.setText(TimestampUtil.getTimePoint(message.getTimestamp()));
        mContentTv.setText(message.getContent());

        if (user.getUserId().equals(message.getFromUserId())) {
            if (!TextUtils.isEmpty(user.getUserAvatar())) {
                mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
            }
        } else {
            if (!TextUtils.isEmpty(message.getFromUserAvatar())) {
                mAvatarSdv.setImageURI(Uri.parse(message.getFromUserAvatar()));
            }
        }

        if (position != 0) {
            Message lastMessage = messageList.get(position - 1);

            if (message.getTimestamp() - lastMessage.getTimestamp() < 10 * 60 * 1000) {
                mTimeStampTv.setVisibility(View.GONE);
            }

        }

        return convertView;
    }

    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView head_iv;
        TextView tv_userId;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }
}
