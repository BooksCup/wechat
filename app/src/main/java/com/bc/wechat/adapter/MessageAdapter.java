package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bc.wechat.R;
import com.bc.wechat.activity.BaiduMapActivity;
import com.bc.wechat.activity.MessageBigImageActivity;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendDao;
import com.bc.wechat.dao.MessageDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.Message;
import com.bc.wechat.entity.User;
import com.bc.wechat.entity.enums.MessageStatus;
import com.bc.wechat.utils.CalculateUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimestampUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageAdapter extends BaseAdapter {
    private static final int DEFAULT_WIDTH = 300;

    private static final int MESSAGE_TYPE_SENT_TEXT = 0;
    private static final int MESSAGE_TYPE_RECV_TEXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 5;

    private Context mContext;
    private LayoutInflater inflater;

    private List<Message> messageList;

    private User user;
    private VolleyUtil volleyUtil;
    private MessageDao messageDao;
    private FriendDao friendDao;

    private boolean isSender;

    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        PreferencesUtil.getInstance().init(context);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(mContext);
        messageDao = new MessageDao();
        friendDao = new FriendDao();
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
        isSender = user.getUserId().equals(message.getFromUserId());
        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
            return isSender ? MESSAGE_TYPE_SENT_TEXT : MESSAGE_TYPE_RECV_TEXT;
        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
            return isSender ? MESSAGE_TYPE_SENT_IMAGE : MESSAGE_TYPE_RECV_IMAGE;
        } else if (Constant.MSG_TYPE_LOCATION.equals(message.getMessageType())) {
            return isSender ? MESSAGE_TYPE_SENT_LOCATION : MESSAGE_TYPE_RECV_LOCATION;
        }
        // invalid
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
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
                // 文字消息
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.tv_timestamp);
                viewHolder.mContentTv = convertView.findViewById(R.id.tv_chat_content);
                viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
                viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
                viewHolder.mStatusIv = convertView.findViewById(R.id.iv_msg_status);

            } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
                // 图片消息
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.tv_timestamp);
                viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
                viewHolder.mImageContentSdv = convertView.findViewById(R.id.sdv_image_content);

            } else if (Constant.MSG_TYPE_LOCATION.equals(message.getMessageType())) {
                // 定位消息
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.tv_timestamp);
                viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
                viewHolder.mAddressTv = convertView.findViewById(R.id.tv_address);
                viewHolder.mAddressDetailTv = convertView.findViewById(R.id.tv_address_detail);
                viewHolder.mLocationImgSdv = convertView.findViewById(R.id.sdv_location_img);

                viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
                viewHolder.mStatusIv = convertView.findViewById(R.id.iv_msg_status);

            } else if (Constant.MSG_TYPE_SYSTEM.equals(message.getMessageType())) {
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.tv_timestamp);
                viewHolder.mSystemMessageTv = convertView.findViewById(R.id.tv_system_message);
                viewHolder.mMessageRl = convertView.findViewById(R.id.rl_message);
            } else {
                // 默认文字信息
                viewHolder.mTimeStampTv = convertView.findViewById(R.id.tv_timestamp);
                viewHolder.mContentTv = convertView.findViewById(R.id.tv_chat_content);
                viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
                viewHolder.mSendingPb = convertView.findViewById(R.id.pb_sending);
                viewHolder.mStatusIv = convertView.findViewById(R.id.iv_msg_status);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
            handleTextMessage(message, viewHolder, position);
        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
            handleImageMessage(message, viewHolder, position);
        } else if (Constant.MSG_TYPE_LOCATION.equals(message.getMessageType())) {
            handleLocationMessage(message, viewHolder, position);
        } else if (Constant.MSG_TYPE_SYSTEM.equals(message.getMessageType())) {
            handleEventNotificationMessage(message, viewHolder, position);
        } else {
            handleTextMessage(message, viewHolder, position);
        }

        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {

        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
            viewHolder.mImageContentSdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MessageBigImageActivity.class);
                    intent.putExtra("imgUrl", message.getImageUrl());
                    mContext.startActivity(intent);
                }
            });
        } else if (Constant.MSG_TYPE_LOCATION.equals(message.getMessageType())) {

        } else {

        }

        // 点击头像进入用户详情页
        // 做非空判断防止通知类消息
        if (null != viewHolder.mAvatarSdv) {
            viewHolder.mAvatarSdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 进入自己详情页
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    intent.putExtra("userId", message.getFromUserId());
                    mContext.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        // text
        TextView mTimeStampTv;
        TextView mContentTv;
        SimpleDraweeView mAvatarSdv;
        ProgressBar mSendingPb;
        ImageView mStatusIv;

        RelativeLayout mMessageRl;

        // sys
        TextView mSystemMessageTv;

        // image
        SimpleDraweeView mImageContentSdv;

        // location
//        TextView mLocationTv;
        // 地址
        TextView mAddressTv;
        // 详细地址
        TextView mAddressDetailTv;
        SimpleDraweeView mLocationImgSdv;

    }

    private View createViewByMessageType(String messageType, boolean isSender) {
        if (Constant.MSG_TYPE_TEXT.equals(messageType)) {
            return isSender ? inflater.inflate(R.layout.item_sent_text, null) :
                    inflater.inflate(R.layout.item_received_text, null);
        } else if (Constant.MSG_TYPE_IMAGE.equals(messageType)) {
            return isSender ? inflater.inflate(R.layout.item_sent_image, null) :
                    inflater.inflate(R.layout.item_received_image, null);
        } else if (Constant.MSG_TYPE_LOCATION.equals(messageType)) {
            return isSender ? inflater.inflate(R.layout.item_sent_location, null) :
                    inflater.inflate(R.layout.item_received_location, null);
        } else {
            return isSender ? inflater.inflate(R.layout.item_sent_text, null) :
                    inflater.inflate(R.layout.item_received_text, null);
        }
    }

    private void sendMessage(String targetType, String targetId, String fromId, String msgType, String body, final int messageIndex) {
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

    private void handleTextMessage(final Message message, ViewHolder viewHolder, final int position) {
        // 好友头像和昵称从sqlite中读取，防止脏数据
        Friend friend = friendDao.getFriendById(message.getFromUserId());

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
            if (!TextUtils.isEmpty(friend.getUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
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
                    String targetType = message.getTargetType();
                    if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
                        sendMessage(targetType, message.getToUserId(),
                                user.getUserId(), "text", JSON.toJSONString(body), messageList.size() - 1);
                    } else {
                        // 群聊
                        sendMessage(targetType, message.getGroupId(), user.getUserId(),
                                message.getMessageType(), JSON.toJSONString(body), messageList.size() - 1);
                    }
                }
            });
        }
    }

    private void handleImageMessage(final Message message, ViewHolder viewHolder, final int position) {
        viewHolder.mTimeStampTv.setText(TimestampUtil.getTimePoint(message.getTimestamp()));
        if (user.getUserId().equals(message.getFromUserId())) {
            if (!TextUtils.isEmpty(user.getUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
            }
        } else {
            if (!TextUtils.isEmpty(message.getFromUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(message.getFromUserAvatar()));
            }
        }
        Uri uri = Uri.fromFile(new File(message.getImageUrl()));

        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(message.getImageUrl(), options);

        //获取图片的宽高
        int height = options.outHeight;
        int width = options.outWidth;

        viewHolder.mImageContentSdv.setImageURI(uri);
        ViewGroup.LayoutParams params = viewHolder.mImageContentSdv.getLayoutParams();
        params.width = DEFAULT_WIDTH;
        Double resetHeight = CalculateUtil.mul(CalculateUtil.div(height, width, 5), DEFAULT_WIDTH);
        params.height = resetHeight.intValue();

        viewHolder.mImageContentSdv.setLayoutParams(params);

        if (position != 0) {
            Message lastMessage = messageList.get(position - 1);

            if (message.getTimestamp() - lastMessage.getTimestamp() < 10 * 60 * 1000) {
                viewHolder.mTimeStampTv.setVisibility(View.GONE);
            }
        }
    }

    private void handleEventNotificationMessage(final Message message, ViewHolder viewHolder, final int position) {
        viewHolder.mTimeStampTv.setText(TimestampUtil.getTimePoint(message.getTimestamp()));
        viewHolder.mSystemMessageTv.setVisibility(View.VISIBLE);
        viewHolder.mSystemMessageTv.setText(message.getContent());
        viewHolder.mMessageRl.setVisibility(View.GONE);

        if (position != 0) {
            Message lastMessage = messageList.get(position - 1);

            if (message.getTimestamp() - lastMessage.getTimestamp() < 10 * 60 * 1000) {
                viewHolder.mTimeStampTv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 处理位置消息
     */
    private void handleLocationMessage(final Message message, ViewHolder viewHolder, final int position) {
        // 好友头像和昵称从sqlite中读取，防止脏数据
        Friend friend = friendDao.getFriendById(message.getFromUserId());

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

        if (user.getUserId().equals(message.getFromUserId())) {
            if (!TextUtils.isEmpty(user.getUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
            }
        } else {
            if (!TextUtils.isEmpty(friend.getUserAvatar())) {
                viewHolder.mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
            }
        }

        if (position != 0) {
            Message lastMessage = messageList.get(position - 1);
            if (message.getTimestamp() - lastMessage.getTimestamp() < 10 * 60 * 1000) {
                viewHolder.mTimeStampTv.setVisibility(View.GONE);
            }
        }

        Map<String, Object> locationMap = JSON.parseObject(message.getMessageBody(), Map.class);
        final String address = locationMap.get("address") == null ? "" : String.valueOf(locationMap.get("address"));
        final double latitude = locationMap.get("latitude") == null ? 0L : Double.valueOf(String.valueOf(locationMap.get("latitude")));
        final double longitude = locationMap.get("longitude") == null ? 0L : Double.valueOf(String.valueOf(locationMap.get("longitude")));

        final String district = locationMap.get("district") == null ? "" : String.valueOf(locationMap.get("district"));
        final String street = locationMap.get("street") == null ? "" : String.valueOf(locationMap.get("street"));
        final String streetNumber = locationMap.get("streetNumber") == null ? "" : String.valueOf(locationMap.get("streetNumber"));

        viewHolder.mAddressTv.setText(address);
        viewHolder.mAddressDetailTv.setText(district + street + streetNumber);

        viewHolder.mAddressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(mContext, BaiduMapActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("address", address);
                mContext.startActivity(intent);
            }
        });
    }
}
