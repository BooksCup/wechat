package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.MessageDao;
import com.bc.wechat.entity.Message;
import com.bc.wechat.entity.User;
import com.bc.wechat.entity.enums.MessageStatus;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimestampUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends BaseAdapter {
    private static final int MESSAGE_TYPE_SENT_TEXT = 0;
    private static final int MESSAGE_TYPE_RECV_TEXT = 1;

    private Context mContext;
    private LayoutInflater inflater;

    private List<Message> messageList;

    private User user;
    private VolleyUtil volleyUtil;
    private MessageDao messageDao;

    boolean isSender;

    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        PreferencesUtil.getInstance().init(context);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(mContext);
        messageDao = new MessageDao();
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
        } else {
            return MESSAGE_TYPE_RECV_TEXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message message = messageList.get(position);
        ViewHolder viewHolder;
        isSender = user.getUserId().equals(message.getFromUserId());
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = createViewByMessageType(message.getMessageType(), isSender);
            if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.timestamp);
                viewHolder.mContentTv = convertView.findViewById(R.id.tv_chatcontent);
                viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
                viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
                viewHolder.mStatusIv = convertView.findViewById(R.id.iv_msg_status);

            } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {

            } else {
                // 默认文字信息
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.timestamp);
                viewHolder.mContentTv = convertView.findViewById(R.id.tv_chatcontent);
                viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
                viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
                viewHolder.mStatusIv = convertView.findViewById(R.id.iv_msg_status);
            }
//            viewHolder.mTimeStampTv = convertView.findViewById(R.id.timestamp);
//            viewHolder.mContentTv = convertView.findViewById(R.id.tv_chatcontent);
//            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
//            viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
//            viewHolder.mStatusIv = convertView.findViewById(R.id.iv_msg_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
            handlerTextMessage(message, viewHolder, position);
        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {

        } else {
            handlerTextMessage(message, viewHolder, position);
        }
        return convertView;
    }

    class ViewHolder {
        TextView mTimeStampTv;
        TextView mContentTv;
        SimpleDraweeView mAvatarSdv;
        ProgressBar mSendingPb;
        ImageView mStatusIv;
    }

    private View createViewByMessageType(String messageType, boolean isSender) {
        if (Constant.MSG_TYPE_TEXT.equals(messageType)) {
            return isSender ? inflater.inflate(R.layout.item_sent_text, null) :
                    inflater.inflate(R.layout.item_received_text, null);
        } else if (Constant.MSG_TYPE_IMAGE.equals(messageType)) {
            return isSender ? inflater.inflate(R.layout.item_sent_image, null) :
                    inflater.inflate(R.layout.item_received_image, null);
        } else {
            return isSender ? inflater.inflate(R.layout.item_sent_text, null) :
                    inflater.inflate(R.layout.item_received_text, null);
        }
    }

    private void sendMessage(String targetType, String targetId, String fromId, String msgType, String body, final int messageIndex) {
        Toast.makeText(mContext, messageList.get(messageIndex).getContent(), Toast.LENGTH_SHORT).show();
        String url = Constant.BASE_URL + "messages";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("targetType", targetType);
        paramMap.put("targetId", targetId);
        paramMap.put("fromId", fromId);
        paramMap.put("msgType", msgType);
        paramMap.put("body", body);

        volleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Message message = messageList.get(messageIndex);
                message = messageDao.getMessageByMessageId(message.getMessageId());
                message.setStatus(MessageStatus.SEND_SUCCESS.value());
                message.setTimestamp(new Date().getTime());
                messageList.set(messageIndex, message);

                Message.delete(message);
                message.setId(null);
                Message.save(message);
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Message message = messageList.get(messageIndex);
                message = messageDao.getMessageByMessageId(message.getMessageId());
                message.setStatus(MessageStatus.SEND_FAIL.value());
                message.setTimestamp(new Date().getTime());
                messageList.set(messageIndex, message);

                Message.delete(message);
                message.setId(null);
                Message.save(message);
                notifyDataSetChanged();
            }
        });
    }

    private void handlerTextMessage(final Message message, ViewHolder viewHolder, final int position) {
        if (message.getStatus() == MessageStatus.SENDING.value()) {
            viewHolder.mSendingPb.setVisibility(View.VISIBLE);
            viewHolder.mStatusIv.setVisibility(View.GONE);
        } else if (message.getStatus() == MessageStatus.SEND_SUCCESS.value()) {
            viewHolder.mSendingPb.setVisibility(View.GONE);
            viewHolder.mStatusIv.setVisibility(View.GONE);
        } else if (message.getStatus() == MessageStatus.SEND_FAIL.value()) {
            viewHolder.mSendingPb.setVisibility(View.GONE);
            viewHolder.mStatusIv.setVisibility(View.VISIBLE);
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

        if (null != viewHolder.mStatusIv) {
            viewHolder.mStatusIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("extras", new HashMap<>());
                    body.put("text", message.getContent());
                    Message resendMessage = messageDao.getMessageByMessageId(message.getMessageId());
                    resendMessage.setStatus(MessageStatus.SENDING.value());
                    resendMessage.setTimestamp(new Date().getTime());
                    Message.delete(resendMessage);
                    resendMessage.setId(null);
                    Message.save(resendMessage);
                    // 重发消息移至最后
                    messageList.remove(position);
                    messageList.add(resendMessage);

                    notifyDataSetChanged();
                    sendMessage("single", message.getToUserId(),
                            user.getUserId(), "text", JSON.toJSONString(body), messageList.size() - 1);
                }
            });
        }
    }
}
