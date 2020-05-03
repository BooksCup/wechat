package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.bc.wechat.utils.FileUtil;
import com.bc.wechat.utils.JimUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimeUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * 聊天
 *
 * @author zhou
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_EMPTY_HISTORY = 1;
    public static final int REQUEST_CODE_CONTEXT_MENU = 2;
    private static final int REQUEST_CODE_MAP = 3;
    public static final int REQUEST_CODE_TEXT = 4;
    public static final int REQUEST_CODE_VOICE = 5;
    public static final int REQUEST_CODE_IMAGE_ALBUM = 6;
    public static final int REQUEST_CODE_IMAGE_CAMERA = 7;
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
    private LinearLayout mEmojiContainerLl;

    // 各种消息类型容器
    private LinearLayout mBtnContainerLl;
    // 发送图片-"相册"
    private LinearLayout mImageAlbumLl;
    // 发送图片-"拍照"
    private LinearLayout mImageCameraLl;

    private ImageView mEmojiNormalIv;
    private ImageView mEmojiCheckedIv;

    private Button mMoreBtn;
    private Button mSendBtn;

    private View buttonSetModeVoice;
    private View buttonPressToSpeak;

    private EditText mTextMsgEt;
    private RelativeLayout mTextMsgRl;

    private ImageView mSingleChatSettingIv;

    private ListView mMessageLv;
    private MessageAdapter mMessageAdapter;
    List<Message> mMessageList;

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

    User mUser;
    private VolleyUtil mVolleyUtil;

    private int mMessageIndex;

    MessageDao mMessageDao;

    private String mImageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mVolleyUtil = VolleyUtil.getInstance(this);
        JMessageClient.registerEventReceiver(this);
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mMessageDao = new MessageDao();
        initView();
        setUpView();
        initCamera();
    }

    private void initView() {
        mFromNickNameTv = findViewById(R.id.tv_from_nick_name);

        mMessageLv = findViewById(R.id.list);

        mMoreLl = findViewById(R.id.ll_more);
        mEmojiContainerLl = findViewById(R.id.ll_emoji_container);
        mBtnContainerLl = findViewById(R.id.ll_btn_container);

        mImageAlbumLl = findViewById(R.id.ll_image_album);
        mImageCameraLl = findViewById(R.id.ll_image_camera);

        mEmojiNormalIv = findViewById(R.id.iv_emoji_normal);
        mEmojiCheckedIv = findViewById(R.id.iv_emoji_checked);

        mMoreBtn = findViewById(R.id.btn_more);
        mSendBtn = findViewById(R.id.btn_send);

        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        mTextMsgEt = findViewById(R.id.et_text_msg);
        mTextMsgRl = findViewById(R.id.rl_text_msg);

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

        mTextMsgEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mEditTextRl.setBackgroundResource(R.mipmap.input_bar_bg_active);
                mMoreLl.setVisibility(View.GONE);
                mEmojiNormalIv.setVisibility(View.VISIBLE);
                mEmojiCheckedIv.setVisibility(View.GONE);
                mEmojiContainerLl.setVisibility(View.GONE);
                mBtnContainerLl.setVisibility(View.GONE);
            }
        });

        mTextMsgEt.addTextChangedListener(new TextWatcher() {

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
                if (mEmojiContainerLl.getVisibility() == View.VISIBLE) {
                    mEmojiContainerLl.setVisibility(View.GONE);
                    mBtnContainerLl.setVisibility(View.VISIBLE);
                    mEmojiNormalIv.setVisibility(View.VISIBLE);
                    mEmojiCheckedIv.setVisibility(View.INVISIBLE);
                } else {
                    mMoreLl.setVisibility(View.GONE);
                }

                return false;
            }
        });

        mSingleChatSettingIv.setOnClickListener(this);
        mImageAlbumLl.setOnClickListener(this);
        mImageCameraLl.setOnClickListener(this);
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
            mMessageList = mMessageDao.getMessageListByUserId(fromUserId);
        } else if (Constant.TARGET_TYPE_GROUP.equals(targetType)) {
            groupId = getIntent().getStringExtra("groupId");
            groupDesc = getIntent().getStringExtra("groupDesc");
            memberNum = getIntent().getStringExtra("memberNum");
            if (TextUtils.isEmpty(groupDesc)) {
                mFromNickNameTv.setText("群聊(" + memberNum + ")");
            } else {
                mFromNickNameTv.setText(groupDesc + "(" + memberNum + ")");
            }

            mMessageList = mMessageDao.getMessageListByGroupId(groupId);
        }
        mMessageAdapter = new MessageAdapter(this, mMessageList);
        mMessageLv.setAdapter(mMessageAdapter);

        mMessageLv.setSelection(mMessageLv.getCount() - 1);
    }

    @Override
    public void onClick(View view) {
        String[] permissions;
        switch (view.getId()) {
            case R.id.btn_send:
                String content = mTextMsgEt.getText().toString();
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
                // 动态申请定位权限
                permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
                requestPermissions(ChatActivity.this, permissions, REQUEST_CODE_LOCATION);
                break;

            case R.id.ll_image_album:
                // 通过相册发送图片
                // 动态申请相册权限
                permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
                requestPermissions(ChatActivity.this, permissions, REQUEST_CODE_IMAGE_ALBUM);
                break;
            case R.id.ll_image_camera:
                // 通过拍照发送图片
                // 动态申请相机权限
                permissions = new String[]{"android.permission.CAMERA"};
                requestPermissions(ChatActivity.this, permissions, REQUEST_CODE_IMAGE_CAMERA);
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
        mTextMsgEt.setVisibility(View.VISIBLE);
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
            mEmojiContainerLl.setVisibility(View.GONE);
        } else {
            if (mEmojiContainerLl.getVisibility() == View.VISIBLE) {
                mEmojiContainerLl.setVisibility(View.GONE);
                mBtnContainerLl.setVisibility(View.VISIBLE);
                mEmojiNormalIv.setVisibility(View.VISIBLE);
                mEmojiCheckedIv.setVisibility(View.INVISIBLE);
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
            mEmojiNormalIv.setVisibility(View.VISIBLE);
            mEmojiCheckedIv.setVisibility(View.INVISIBLE);
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
        message.setFromUserId(mUser.getUserId());
        message.setToUserId(fromUserId);
        message.setToUserName(fromUserNickName);
        message.setToUserAvatar(fromUserAvatar);
        message.setTimestamp(new Date().getTime());
        message.setStatus(MessageStatus.SENDING.value());

        // 群组
        message.setGroupId(groupId);

        mMessageList.add(message);
        mMessageIndex = mMessageList.size() - 1;
        message.setMessageType(Constant.MSG_TYPE_TEXT);

        Message.save(message);
        Map<String, Object> body = new HashMap<>();
        body.put("extras", new HashMap<>());
        body.put("text", content);
        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            // 单聊
            sendMessage(targetType, fromUserId, mUser.getUserId(),
                    message.getMessageType(), JSON.toJSONString(body), mMessageIndex);
        } else {
            // 群聊
            sendMessage(targetType, groupId, mUser.getUserId(),
                    message.getMessageType(), JSON.toJSONString(body), mMessageIndex);
        }
        mMessageAdapter.notifyDataSetChanged();
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        mTextMsgEt.setText("");
    }

    /**
     * 预发送图片消息
     */
    private String preSendImageMsg(String localPath) {
        String messageId = CommonUtil.generateId();
        Message message = new Message();
        message.setMessageId(messageId);
        message.setTargetType(targetType);
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));
        message.setFromUserId(mUser.getUserId());
        message.setToUserId(fromUserId);
        message.setToUserName(fromUserNickName);
        message.setToUserAvatar(fromUserAvatar);
        message.setTimestamp(new Date().getTime());
        message.setStatus(MessageStatus.SENDING.value());

        // 群组
        message.setGroupId(groupId);

        mMessageList.add(message);
        mMessageIndex = mMessageList.size() - 1;
        message.setMessageType(Constant.MSG_TYPE_IMAGE);

        Map<String, Object> body = new HashMap<>();
        body.put("extras", new HashMap<>());
        body.put("localPath", localPath);
        String messageBody = JSON.toJSONString(body);
        message.setMessageBody(messageBody);

        Message.save(message);

        mMessageAdapter.notifyDataSetChanged();
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        mTextMsgEt.setText("");

        // 隐藏底部功能栏
        mBtnContainerLl.setVisibility(View.GONE);

        return messageId;
    }

    /**
     * 发送图片消息
     *
     * @param imgUrl 消息内容
     */
    private void sendImageMsg(String imgUrl, String messageId, String localPath) {
        Message message = mMessageDao.getMessageByMessageId(messageId);
        Map<String, Object> body = new HashMap<>();
        body.put("type", Constant.MSG_TYPE_IMAGE);
        body.put("extras", new HashMap<>());
        body.put("imgUrl", imgUrl);
        body.put("localPath", localPath);
        String messageBody = JSON.toJSONString(body);
        message.setMessageBody(messageBody);
        Message.save(message);

        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            // 单聊
            sendMessage(targetType, fromUserId, mUser.getUserId(),
                    message.getMessageType(), JSON.toJSONString(body), mMessageIndex);
        } else {
            // 群聊
            sendMessage(targetType, groupId, mUser.getUserId(),
                    message.getMessageType(), JSON.toJSONString(body), mMessageIndex);
        }
        mMessageAdapter.notifyDataSetChanged();
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        mTextMsgEt.setText("");
    }

    /**
     * 发送位置消息
     *
     * @param latitude      纬度
     * @param longitude     经度
     * @param address       地址
     * @param addressDetail 详细地址
     * @param path          地图截图http地址
     */
    private void sendLocationMsg(double latitude, double longitude, String address, String addressDetail, String path) {
        Message message = new Message();
        message.setMessageId(CommonUtil.generateId());
        message.setTargetType(targetType);
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));
        message.setFromUserId(mUser.getUserId());
        message.setToUserId(fromUserId);
        message.setToUserName(fromUserNickName);
        message.setToUserAvatar(fromUserAvatar);
        message.setTimestamp(new Date().getTime());
        message.setStatus(MessageStatus.SENDING.value());

        // 群组
        message.setGroupId(groupId);

        mMessageList.add(message);
        mMessageIndex = mMessageList.size() - 1;
        message.setMessageType(Constant.MSG_TYPE_LOCATION);
        Map<String, Object> body = new HashMap<>();
        body.put("type", Constant.MSG_TYPE_LOCATION);
        body.put("latitude", latitude);
        body.put("longitude", longitude);
        body.put("address", address);
        body.put("addressDetail", addressDetail);
        body.put("path", path);
        String messageBody = JSON.toJSONString(body);
        message.setMessageBody(messageBody);

        Message.save(message);

        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            // 单聊
            sendMessage(targetType, fromUserId, mUser.getUserId(),
                    message.getMessageType(), messageBody, mMessageIndex);
        } else {
            // 群聊
            sendMessage(targetType, groupId, mUser.getUserId(),
                    message.getMessageType(), messageBody, mMessageIndex);
        }
        mMessageAdapter.notifyDataSetChanged();
        mMessageLv.setSelection(mMessageLv.getCount() - 1);
        mTextMsgEt.setText("");
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

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Message message = mMessageList.get(messageIndex);
                message = mMessageDao.getMessageByMessageId(message.getMessageId());
                message.setStatus(MessageStatus.SEND_SUCCESS.value());
                mMessageList.set(messageIndex, message);
                Message.save(message);
                mMessageAdapter.setData(mMessageList);
                mMessageAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Message message = mMessageList.get(messageIndex);
                message = mMessageDao.getMessageByMessageId(message.getMessageId());
                message.setStatus(MessageStatus.SEND_FAIL.value());
                mMessageList.set(messageIndex, message);
                Message.save(message);
                mMessageAdapter.setData(mMessageList);
                mMessageAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.TARGET_TYPE_SINGLE.equals(targetType)) {
            mMessageList = mMessageDao.getMessageListByUserId(fromUserId);
        } else if (Constant.TARGET_TYPE_GROUP.equals(targetType)) {
            mMessageList = mMessageDao.getMessageListByGroupId(groupId);
        }

        mMessageAdapter = new MessageAdapter(this, mMessageList);
        mMessageLv.setAdapter(mMessageAdapter);

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
        if (fromUserInfo.getUserName().equals(mUser.getUserId())) {
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
        message.setToUserId(mUser.getUserId());

        // 消息类型
        message.setMessageType(JimUtil.getMessageType(msg));
        message.setTimestamp(new Date().getTime());

        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
            // 文字
            TextContent messageContent = (TextContent) msg.getContent();
            message.setContent(messageContent.getText());
        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
            // 图片
            Map<String, String> messageMap = JSON.parseObject(msg.getContent().toJson(), Map.class);
            Map<String, Object> messageBodyMap = JSON.parseObject(messageMap.get("text"), Map.class);
            message.setMessageBody(JSON.toJSONString(messageBodyMap));

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
                mMessageList.add(message);
                mMessageLv.post(new Runnable() {
                    @Override
                    public void run() {
                        mMessageAdapter.setData(mMessageList);
                        mMessageAdapter.notifyDataSetChanged();
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
            if (String.valueOf(groupInfo.getGroupID()).equals(groupId) && !fromUserInfo.getUserName().equals(mUser.getUserId())) {
                mMessageList.add(message);
                mMessageLv.post(new Runnable() {
                    @Override
                    public void run() {
                        mMessageAdapter.setData(mMessageList);
                        mMessageAdapter.notifyDataSetChanged();
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
            switch (requestCode) {
                case REQUEST_CODE_IMAGE_ALBUM:
                    if (data != null) {
                        Uri uri = data.getData();
                        final String filePath = FileUtil.getFilePathByUri(this, uri);
                        final String messageId = preSendImageMsg(filePath);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", filePath);
                                if (null != imageList && imageList.size() > 0) {
                                    android.os.Message msg = new android.os.Message();
                                    msg.what = REQUEST_CODE_IMAGE_ALBUM;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("imgUrl", imageList.get(0));
                                    bundle.putString("messageId", messageId);
                                    bundle.putString("localPath", filePath);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }
                        }).start();
                    }
                    break;
                case REQUEST_CODE_IMAGE_CAMERA:
                    final File file = new File(Environment.getExternalStorageDirectory(), mImageName);
                    final String messageId = preSendImageMsg(file.getPath());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", file.getPath());
                            if (null != imageList && imageList.size() > 0) {
                                android.os.Message msg = new android.os.Message();
                                msg.what = REQUEST_CODE_IMAGE_ALBUM;
                                Bundle bundle = new Bundle();
                                bundle.putString("imgUrl", imageList.get(0));
                                bundle.putString("messageId", messageId);
                                bundle.putString("localPath", file.getPath());
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }
                    }).start();
                    break;

                case REQUEST_CODE_LOCATION:
                    // 获取经纬度，发送位置消息
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String address = data.getStringExtra("address");
                    String addressDetail = data.getStringExtra("addressDetail");
                    String path = data.getStringExtra("path");
                    sendLocationMsg(latitude, longitude, address, addressDetail, path);
                    break;
            }
        }
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        // Android 6.0开始的动态权限，这里进行版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                switch (requestCode) {
                    case REQUEST_CODE_LOCATION:
                        showMapPicker();
                        break;
                    case REQUEST_CODE_IMAGE_ALBUM:
                        showAlbum();
                        break;
                    case REQUEST_CODE_IMAGE_CAMERA:
                        showCamera();
                        break;
                }
            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
            }
        }
    }

    /**
     * requestPermissions的回调
     * 一个或多个权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // 判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            switch (requestCode) {
                case REQUEST_CODE_LOCATION:
                    showMapPicker();
                    break;
                case REQUEST_CODE_IMAGE_ALBUM:
                    showAlbum();
                    break;
                case REQUEST_CODE_IMAGE_CAMERA:
                    showCamera();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(ChatActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            // 非初次进入App且已授权
            switch (requestCode) {
                case REQUEST_CODE_LOCATION:
                    content = getString(R.string.request_permission_location);
                    break;
                case REQUEST_CODE_IMAGE_ALBUM:
                    content = getString(R.string.request_permission_storage);
                    break;
                case REQUEST_CODE_IMAGE_CAMERA:
                    content = getString(R.string.request_permission_camera);
                    break;
            }

            final ConfirmDialog mConfirmDialog = new ConfirmDialog(ChatActivity.this, getString(R.string.request_permission),
                    content,
                    getString(R.string.go_setting), getString(R.string.cancel), getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }

    /**
     * 进入地图选择页面
     */
    private void showMapPicker() {
        Intent intent = new Intent(ChatActivity.this, MapPickerActivity.class);
        intent.putExtra("sendLocation", true);
        startActivityForResult(intent, REQUEST_CODE_LOCATION);
    }

    /**
     * 跳转到相机
     */
    private void showCamera() {
        mImageName = CommonUtil.generateId() + ".png";
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                new File(Environment.getExternalStorageDirectory(), mImageName)));
        startActivityForResult(cameraIntent, REQUEST_CODE_IMAGE_CAMERA);
    }

    /**
     * 跳转到相册
     */
    private void showAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE_ALBUM);
    }

    /**
     * android 7.0系统解决拍照的问题
     */
    private void initCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_CODE_IMAGE_ALBUM:
                    String imgUrl = msg.getData().getString("imgUrl");
                    String messageId = msg.getData().getString("messageId");
                    String localPath = msg.getData().getString("localPath");
                    sendImageMsg(imgUrl, messageId, localPath);
                    break;
            }
        }
    };
}
