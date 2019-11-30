package com.bc.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.MessageAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.MessageDao;
import com.bc.wechat.entity.Message;
import com.bc.wechat.entity.User;
import com.bc.wechat.entity.enums.MessageStatus;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.JimUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimeUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class ChatActivity extends FragmentActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final String COPY_IMAGE = "EASEMOBIMG";

    private InputMethodManager manager;

    private TextView mFromNickNameTv;

    private LinearLayout mMoreLl;
    private LinearLayout mEmojiIconContainerLl;
    private LinearLayout mBtnContainerLl;
    private ImageView mEmoticonsNormalIv;
    private ImageView mEmoticonsCheckedIv;

    private Button mMoreBtn;
    private Button mSendBtn;

    private View buttonSetModeVoice;
    private View buttonPressToSpeak;

    private EditText mEditTextContent;
    private RelativeLayout mEditTextRl;

    private ImageView mSingleChatSettingIv;

    private ListView mMessageLv;
    private MessageAdapter messageAdapter;
    List<Message> messageList;

    // intent传值
    // 单聊
    private String targetType;
    private String fromUserId;
    private String fromUserNickName;
    private String fromUserAvatar;

    // 群聊
    private String groupId;
    private String groupDesc;
    private String memberNum;

    User user;
    private VolleyUtil volleyUtil;

    private int messageIndex;

    MessageDao messageDao;
    String imageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        volleyUtil = VolleyUtil.getInstance(this);
        JMessageClient.registerEventReceiver(this);
        PreferencesUtil.getInstance().init(this);
        user = PreferencesUtil.getInstance().getUser();
        messageDao = new MessageDao();
        initView();
        setUpView();
    }

    private void initView() {
        mFromNickNameTv = findViewById(R.id.tv_from_nick_name);

        mMessageLv = findViewById(R.id.list);

        mMoreLl = findViewById(R.id.more);
        mEmojiIconContainerLl = findViewById(R.id.ll_face_container);
        mBtnContainerLl = findViewById(R.id.ll_btn_container);
        mEmoticonsNormalIv = findViewById(R.id.iv_emoticons_normal);
        mEmoticonsCheckedIv = findViewById(R.id.iv_emoticons_checked);

        mMoreBtn = findViewById(R.id.btn_more);
        mSendBtn = findViewById(R.id.btn_send);


        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        mEditTextContent = findViewById(R.id.et_sendmessage);
        mEditTextRl = findViewById(R.id.edittext_layout);

        mSingleChatSettingIv = findViewById(R.id.iv_setting);

//        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    mEditTextRl.setBackgroundResource(R.mipmap.input_bar_bg_active);
//                } else {
//                    mEditTextRl.setBackgroundResource(R.mipmap.input_bar_bg_normal);
//                }
//            }
//        });

        mEditTextContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mEditTextRl.setBackgroundResource(R.mipmap.input_bar_bg_active);
                mMoreLl.setVisibility(View.GONE);
                mEmoticonsNormalIv.setVisibility(View.VISIBLE);
                mEmoticonsCheckedIv.setVisibility(View.GONE);
                mEmojiIconContainerLl.setVisibility(View.GONE);
                mBtnContainerLl.setVisibility(View.GONE);
            }
        });

        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (TextUtils.isEmpty(charSequence)) {
                    mMoreBtn.setVisibility(View.VISIBLE);
                    mSendBtn.setVisibility(View.GONE);
                } else {
                    mMoreBtn.setVisibility(View.GONE);
                    mSendBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mMessageLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();

                // 隐藏更多
                if (mEmojiIconContainerLl.getVisibility() == View.VISIBLE) {
                    mEmojiIconContainerLl.setVisibility(View.GONE);
                    mBtnContainerLl.setVisibility(View.VISIBLE);
                    mEmoticonsNormalIv.setVisibility(View.VISIBLE);
                    mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
                } else {
                    mMoreLl.setVisibility(View.GONE);
                }

                return false;
            }
        });

        mSingleChatSettingIv.setOnClickListener(this);
    }

    private void setUpView() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        targetType = getIntent().getStringExtra("targetType");
        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            fromUserId = getIntent().getStringExtra("fromUserId");
            fromUserNickName = getIntent().getStringExtra("fromUserNickName");
            fromUserAvatar = getIntent().getStringExtra("fromUserAvatar");
            mFromNickNameTv.setText(fromUserNickName);
            messageList = Message.findWithQuery(Message.class,
                    "select * from message where (from_user_id = ? or to_user_id = ?) and target_type = ? order by timestamp asc", fromUserId, fromUserId, Constant.TARGET_TYPE_SINGLE);
        } else if (Constant.TARGET_TYPE_GROUP.equals(targetType)) {
            groupId = getIntent().getStringExtra("groupId");
            groupDesc = getIntent().getStringExtra("groupDesc");
            memberNum = getIntent().getStringExtra("memberNum");
            if (TextUtils.isEmpty(groupDesc)) {
                mFromNickNameTv.setText("群聊(" + memberNum + ")");
            } else {
                mFromNickNameTv.setText(groupDesc + "(" + memberNum + ")");
            }

            // and message_type <> 'eventNotification'
            messageList = Message.findWithQuery(Message.class, "select * from message where group_id = ? order by timestamp asc", groupId);
        }
        messageAdapter = new MessageAdapter(this, messageList);
        mMessageLv.setAdapter(messageAdapter);

        mMessageLv.setSelection(mMessageLv.getCount() - 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String content = mEditTextContent.getText().toString();
                sendTextMsg(content);
                break;
            case R.id.iv_setting:
                if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
                    // 单聊设置
                    Intent intent = new Intent(ChatActivity.this, ChatSingleSettingActivity.class);
                    intent.putExtra("userId", fromUserId);
                    intent.putExtra("userNickName", fromUserNickName);
                    intent.putExtra("userAvatar", fromUserAvatar);
                    startActivity(intent);
                } else {
                    // 群聊设置
                    Intent intent = new Intent(ChatActivity.this, ChatGroupSettingActivity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
                break;
            case R.id.iv_chat_location:
                Intent intent = new Intent(ChatActivity.this, MapPickerActivity.class);
                intent.putExtra("sendLocation", true);
                startActivityForResult(intent, REQUEST_CODE_LOCATION);
                break;
        }
    }


    public void back(View view) {
        finish();
    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        mEditTextRl.setVisibility(View.VISIBLE);
        mMoreLl.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);

    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void more(View view) {
        if (mMoreLl.getVisibility() == View.GONE) {
            hideKeyboard();
            mMoreLl.setVisibility(View.VISIBLE);
            mBtnContainerLl.setVisibility(View.VISIBLE);
            mEmojiIconContainerLl.setVisibility(View.GONE);
        } else {
            if (mEmojiIconContainerLl.getVisibility() == View.VISIBLE) {
                mEmojiIconContainerLl.setVisibility(View.GONE);
                mBtnContainerLl.setVisibility(View.VISIBLE);
                mEmoticonsNormalIv.setVisibility(View.VISIBLE);
                mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
            } else {
                mMoreLl.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 点击文字输入框
     *
     * @param v
     */
    public void editClick(View v) {
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        if (mMoreLl.getVisibility() == View.VISIBLE) {
            mMoreLl.setVisibility(View.GONE);
            mEmoticonsNormalIv.setVisibility(View.VISIBLE);
            mEmoticonsCheckedIv.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 发送文字消息
     *
     * @param content 消息内容
     */
    private void sendTextMsg(String content) {
        Message message = new Message();
        message.setMessageId(CommonUtil.generateId());
        message.setTargetType(targetType);
        message.setContent(content);
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));
        message.setFromUserId(user.getUserId());
        message.setToUserId(fromUserId);
        message.setToUserName(fromUserNickName);
        message.setToUserAvatar(fromUserAvatar);
        message.setTimestamp(new Date().getTime());
        message.setStatus(MessageStatus.SENDING.value());

        // 群组
        message.setGroupId(groupId);

        messageList.add(message);
        messageIndex = messageList.size() - 1;
        message.setMessageType(Constant.MSG_TYPE_TEXT);

        Message.save(message);
        Map<String, Object> body = new HashMap<>();
        body.put("extras", new HashMap<>());
        body.put("text", content);
        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            // 单聊
            sendMessage(targetType, fromUserId, user.getUserId(),
                    message.getMessageType(), JSON.toJSONString(body), messageIndex);
        } else {
            // 群聊
            sendMessage(targetType, groupId, user.getUserId(),
                    message.getMessageType(), JSON.toJSONString(body), messageIndex);
        }
        messageAdapter.notifyDataSetChanged();
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        mEditTextContent.setText("");
    }

    /**
     * 发送位置消息
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param path      地图截图http地址
     */
    private void sendLocationMsg(double latitude, double longitude, String path) {
        Message message = new Message();
        message.setMessageId(CommonUtil.generateId());
        message.setTargetType(targetType);
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));
        message.setFromUserId(user.getUserId());
        message.setToUserId(fromUserId);
        message.setToUserName(fromUserNickName);
        message.setToUserAvatar(fromUserAvatar);
        message.setTimestamp(new Date().getTime());
        message.setStatus(MessageStatus.SENDING.value());

        // 群组
        message.setGroupId(groupId);

        messageList.add(message);
        messageIndex = messageList.size() - 1;
        message.setMessageType(Constant.MSG_TYPE_LOCATION);
        Map<String, Object> body = new HashMap<>();
        body.put("type", Constant.MSG_TYPE_LOCATION);
        body.put("latitude", latitude);
        body.put("longitude", longitude);
        body.put("path", path);
        String messageBody = JSON.toJSONString(body);
        message.setMessageBody(messageBody);

        Message.save(message);

        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            // 单聊
            sendMessage(targetType, fromUserId, user.getUserId(),
                    message.getMessageType(), messageBody, messageIndex);
        } else {
            // 群聊
            sendMessage(targetType, groupId, user.getUserId(),
                    message.getMessageType(), messageBody, messageIndex);
        }
        messageAdapter.notifyDataSetChanged();
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        mEditTextContent.setText("");
    }


    /*接收到的消息*/
    public void onEvent(MessageEvent event) {
        handleReceivedMessage(event.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
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
                messageList.set(messageIndex, message);
                Message.save(message);
                messageAdapter.setData(messageList);
                messageAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Message message = messageList.get(messageIndex);
                message = messageDao.getMessageByMessageId(message.getMessageId());
                message.setStatus(MessageStatus.SEND_FAIL.value());
                messageList.set(messageIndex, message);
                Message.save(message);
                messageAdapter.setData(messageList);
                messageAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            messageList = Message.findWithQuery(Message.class,
                    "select * from message where (from_user_id = ? or to_user_id = ?) and target_type = ? order by timestamp asc", fromUserId, fromUserId, Constant.TARGET_TYPE_SINGLE);
        } else if (Constant.TARGET_TYPE_GROUP.equals(targetType)) {
            // and message_type <> 'eventNotification'
            messageList = Message.findWithQuery(Message.class, "select * from message where group_id = ? order by timestamp asc", groupId);
        }
        messageAdapter = new MessageAdapter(this, messageList);
        mMessageLv.setAdapter(messageAdapter);

        mMessageLv.setSelection(mMessageLv.getCount() - 1);
    }

    /**
     * 监听极光收到的消息
     *
     * @param msg
     */
    private void handleReceivedMessage(cn.jpush.im.android.api.model.Message msg) {
        // 自己发送出的消息(接收方非自己)自己也能收到
        // 如果是自己发送的消息 则无需处理此条消息
        // 发送者
        UserInfo fromUserInfo = msg.getFromUser();
        if (fromUserInfo.getUserName().equals(user.getUserId())) {
            return;
        }

        Message message = new Message();
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));

        // 消息发送者信息
        message.setFromUserId(fromUserInfo.getUserName());
        message.setFromUserName(fromUserInfo.getNickname());
        message.setFromUserAvatar(fromUserInfo.getAvatar());

        // 群发 or 单发
        if (msg.getTargetType().equals(ConversationType.single)) {
            message.setTargetType(Constant.TARGET_TYPE_SINGLE);

        } else {
            message.setTargetType(Constant.TARGET_TYPE_GROUP);
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            message.setGroupId(String.valueOf(groupInfo.getGroupID()));
        }

        // 消息接收者信息
        message.setToUserId(user.getUserId());

        // 消息类型
        message.setMessageType(JimUtil.getMessageType(msg));
        message.setTimestamp(new Date().getTime());

        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
            // 文字
            TextContent messageContent = (TextContent) msg.getContent();
            message.setContent(messageContent.getText());
        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
            // 图片
            ImageContent imageContent = ((ImageContent) msg.getContent());
            String imageUrl = imageContent.getLocalThumbnailPath();
            message.setImageUrl(imageUrl);
        } else if (Constant.MSG_TYPE_LOCATION.equals(message.getMessageType())) {
            // 位置
            Map<String, String> messageMap = JSON.parseObject(msg.getContent().toJson(), Map.class);
            Map<String, Object> messageBodyMap = JSON.parseObject(messageMap.get("text"), Map.class);
            message.setMessageBody(JSON.toJSONString(messageBodyMap));
        }

        Message.save(message);

        if (msg.getTargetType().equals(ConversationType.single)) {
            // 单聊
            // 如果是当前会话
            Conversation conversation = JMessageClient.getSingleConversation(fromUserInfo.getUserName());
            if (fromUserInfo.getUserName().equals(fromUserId)) {
                messageList.add(message);
                mMessageLv.post(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.setData(messageList);
                        messageAdapter.notifyDataSetChanged();
                        mMessageLv.setSelection(mMessageLv.getBottom());
                    }
                });
                // 清除未读数
                conversation.resetUnreadCount();
            } else {
                // 未读数++
                PreferencesUtil.getInstance().setNewMsgsUnreadNumber(PreferencesUtil.getInstance().getNewMsgsUnreadNumber() + 1);
            }
        } else {
            Conversation conversation = JMessageClient.getGroupConversation(Long.valueOf(groupId));

            // 群聊
            // 如果是当前会话
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            if (String.valueOf(groupInfo.getGroupID()).equals(groupId) && !fromUserInfo.getUserName().equals(user.getUserId())) {
                messageList.add(message);
                mMessageLv.post(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.setData(messageList);
                        messageAdapter.notifyDataSetChanged();
                        mMessageLv.setSelection(mMessageLv.getBottom());
                    }
                });
                // 清除未读数
                conversation.resetUnreadCount();
            } else {
                // 未读数++
                PreferencesUtil.getInstance().setNewMsgsUnreadNumber(PreferencesUtil.getInstance().getNewMsgsUnreadNumber() + 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCATION) {
                // 获取经纬度，发送位置消息
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String path = data.getStringExtra("path");
                sendLocationMsg(latitude, longitude, path);
            }
        }
    }
}
