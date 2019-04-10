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
    private static final int MESSAGE_TYPE_SENT_TEXT = 0;
    private static final int MESSAGE_TYPE_RECV_TEXT = 1;

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
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (user.getUserId().equals(message.getFromUserId())) {
            return MESSAGE_TYPE_SENT_TEXT;
        } else{
            return MESSAGE_TYPE_RECV_TEXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            if (getItemViewType(position) == 0) {
                convertView = inflater.inflate(R.layout.item_sent_message, null);
            } else {
                convertView = inflater.inflate(R.layout.item_received_message, null);
            }
            viewHolder.mTimeStampTv = convertView.findViewById(R.id.timestamp);
            viewHolder.mContentTv = convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if ("1".equals(message.getStatus())) {
            viewHolder.mSendingPb.setVisibility(View.INVISIBLE);
        }

        viewHolder.mTimeStampTv.setText(TimestampUtil.getTimePoint(message.getTimestamp()));
        viewHolder.mContentTv.setText(message.getContent());

        if (user.getUserId().equals(message.getFromUserId())) {
            if (!TextUtils.isEmpty(user.getUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
            }
        } else {
            if (!TextUtils.isEmpty(message.getFromUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(message.getFromUserAvatar()));
            }
        }

        if (position != 0) {
            Message lastMessage = messageList.get(position - 1);

            if (message.getTimestamp() - lastMessage.getTimestamp() < 10 * 60 * 1000) {
                viewHolder.mTimeStampTv.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTimeStampTv;
        TextView mContentTv;
        SimpleDraweeView mAvatarSdv;
        ProgressBar mSendingPb;
    }
}
