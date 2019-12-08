package com.bc.wechat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.Message;
import com.bc.wechat.entity.User;
import com.bc.wechat.fragment.ConversationFragment;
import com.bc.wechat.fragment.FindFragment;
import com.bc.wechat.fragment.FriendsFragment;
import com.bc.wechat.fragment.ProfileFragment;
import com.bc.wechat.utils.ExampleUtil;
import com.bc.wechat.utils.JimUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimeUtil;
import com.google.zxing.client.android.CaptureActivity2;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private static final int SCAN_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION = 110;

    public static boolean isForeground = false;

    private Fragment[] fragments;
    private ConversationFragment conversationFragment;
    private FriendsFragment friendsFragment;
    private FindFragment findFragment;
    private ProfileFragment profileFragment;

    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    // 当前fragment的index
    private int currentTabIndex;

    private TextView mUnreadNewMsgsNumTv;
    private TextView mUnreadNewFriendsNumTv;

    private ImageView mAddIv;

    User user;

    // 首页弹出框
    private PopupWindow popupWindow;
    private View popupView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        JMessageClient.registerEventReceiver(this);
        PreferencesUtil.getInstance().init(this);
        user = PreferencesUtil.getInstance().getUser();
        registerMessageReceiver();
        refreshNewMsgsUnreadNum();
        refreshNewFriendsUnreadNum();
        // 进入强制刷新，防止离线消息
        conversationFragment.refreshConversationList();
    }

    private void initView() {
        conversationFragment = new ConversationFragment();
        friendsFragment = new FriendsFragment();
        findFragment = new FindFragment();
        profileFragment = new ProfileFragment();

        fragments = new Fragment[]{conversationFragment, friendsFragment, findFragment, profileFragment};

        imagebuttons = new ImageView[4];
        imagebuttons[0] = findViewById(R.id.ib_weixin);
        imagebuttons[1] = findViewById(R.id.ib_contact_list);
        imagebuttons[2] = findViewById(R.id.ib_find);
        imagebuttons[3] = findViewById(R.id.ib_profile);

        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = findViewById(R.id.tv_weixin);
        textviews[1] = findViewById(R.id.tv_contact_list);
        textviews[2] = findViewById(R.id.tv_find);
        textviews[3] = findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);

        mAddIv = findViewById(R.id.iv_add);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationFragment)
                .add(R.id.fragment_container, friendsFragment)
                .add(R.id.fragment_container, findFragment)
                .add(R.id.fragment_container, profileFragment)
                .hide(friendsFragment).hide(findFragment).hide(profileFragment)
                .show(conversationFragment).commit();

        mUnreadNewMsgsNumTv = findViewById(R.id.unread_msg_number);
        mUnreadNewFriendsNumTv = findViewById(R.id.unread_address_number);

        mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopupWindow();
                if (!popupWindow.isShowing()) {
                    // 以下拉方式显示popupwindow
                    popupWindow.showAsDropDown(mAddIv, 0, 0);
                } else {
                    popupWindow.dismiss();
                }
            }
        });
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_weixin:
                // 会话列表
                // 主动加载一次会话
                conversationFragment.refreshConversationList();
                index = 0;
                break;
            case R.id.re_contact_list:
                index = 1;
                break;
            case R.id.re_find:
                index = 2;
                break;
            case R.id.re_profile:
                index = 3;
                break;
        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        JMessageClient.registerEventReceiver(this);
        // 消息
        refreshNewMsgsUnreadNum();
        // 通讯录
        refreshNewFriendsUnreadNum();
        friendsFragment.refreshNewFriendsUnreadNum();
        friendsFragment.refreshFriendsList();

        // 会话
        if (currentTabIndex == 0) {
            conversationFragment.refreshConversationList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        JMessageClient.unRegisterEventReceiver(this);
    }

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_MAIN = "com.bc.wechat.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_MAIN";
    public static final String MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_NEW_FRIENDS_MSG = "com.bc.wechat.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_NEW_FRIENDS_MSG";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_MAIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_MAIN.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    refreshNewFriendsUnreadNum();
                    friendsFragment.refreshNewFriendsUnreadNum();
                }
            } catch (Exception e) {
            }
        }
    }

    public void refreshNewMsgsUnreadNum() {
        android.os.Message message = new android.os.Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    private void refreshNewFriendsUnreadNum() {
        int newFriendsUnreadNum = PreferencesUtil.getInstance().getNewFriendsUnreadNumber();
        if (newFriendsUnreadNum > 0) {
            mUnreadNewFriendsNumTv.setVisibility(View.VISIBLE);
            mUnreadNewFriendsNumTv.setText(String.valueOf(newFriendsUnreadNum));
        } else {
            mUnreadNewFriendsNumTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    /*接收到的消息*/
    public void onEvent(MessageEvent event) {
        final cn.jpush.im.android.api.model.Message msg = event.getMessage();
        Message message = new Message();
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));
        String messageType = JimUtil.getMessageType(msg);
        message.setToUserId(user.getUserId());
        message.setTimestamp(new Date().getTime());
        message.setMessageType(messageType);

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
        } else if (Constant.MSG_TYPE_SYSTEM.equals(message.getMessageType())) {
            EventNotificationContent eventNotificationContent = (EventNotificationContent) msg.getContent();

            // 群加人
            if (EventNotificationContent.EventNotificationType.group_member_added == eventNotificationContent.getEventNotificationType()) {
                List<String> userNickNameList = eventNotificationContent.getUserDisplayNames();
                StringBuffer stringBuffer = new StringBuffer();
                for (String userNickName : userNickNameList) {
                    stringBuffer.append(userNickName).append("、");
                }
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                message.setContent("你邀请" + stringBuffer.toString() + "加入了群聊");
            }
        }
        UserInfo fromUserInfo = msg.getFromUser();
        message.setFromUserId(fromUserInfo.getUserName());

        List<Friend> friendList = Friend.find(Friend.class, "user_id = ?", message.getFromUserId());
        if (null != friendList && friendList.size() > 0) {
            message.setFromUserAvatar(friendList.get(0).getUserAvatar());
        }

        if (msg.getTargetType().equals(ConversationType.single)) {
            message.setTargetType(Constant.TARGET_TYPE_SINGLE);

        } else {
            message.setTargetType(Constant.TARGET_TYPE_GROUP);
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            message.setGroupId(String.valueOf(groupInfo.getGroupID()));
        }

        Message.save(message);
        conversationFragment.refreshConversationList();

        if (fromUserInfo.getUserName().equals(user.getUserId())) {
            // 如果发送者是自己，不更新
        } else if (fromUserInfo.getUserID() == 0) {
            // 系统管理员，通知类消息，不更新
        } else {
            // 未读数++
            PreferencesUtil.getInstance().setNewMsgsUnreadNumber(PreferencesUtil.getInstance().getNewMsgsUnreadNumber() + 1);
            refreshNewMsgsUnreadNum();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                int newMsgsUnreadNum = PreferencesUtil.getInstance().getNewMsgsUnreadNumber();
                if (newMsgsUnreadNum > 0) {
                    mUnreadNewMsgsNumTv.setVisibility(View.VISIBLE);
                    mUnreadNewMsgsNumTv.setText(String.valueOf(newMsgsUnreadNum));
                } else {
                    mUnreadNewMsgsNumTv.setVisibility(View.GONE);
                }
            }
        }
    };

    /**
     * 初始化首页弹出框
     */
    private void initPopupWindow() {
        popupView = View.inflate(this, R.layout.popupwindow_add, null);
        popupWindow = new PopupWindow();
        // 设置SelectPicPopupWindow的View
        popupWindow.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        popupWindow.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        popupWindow.setAnimationStyle(R.style.AnimationPreview);

        // 发起群聊
        RelativeLayout mCreateGroupRl = popupView.findViewById(R.id.rl_create_group);

        // 添加朋友
        RelativeLayout mAddFriendsRl = popupView.findViewById(R.id.rl_add_friends);
        mAddFriendsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                startActivity(new Intent(MainActivity.this, AddFriendsActivity.class));
            }
        });

        // 扫一扫
        RelativeLayout mScanQrCodeRl = popupView.findViewById(R.id.rl_scan_qr_code);
        mScanQrCodeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                    } else {
                        startScanActivity();
                    }
                } else {
                    startScanActivity();
                }
            }
        });

        // 帮助和反馈
        RelativeLayout mHelpRl = popupView.findViewById(R.id.rl_help);

    }

    private void startScanActivity() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity2.class);
        intent.putExtra(CaptureActivity2.USE_DEFUALT_ISBN_ACTIVITY, true);
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanActivity();
                } else {
                    Toast.makeText(MainActivity.this, "请手动打开摄像头权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SCAN_REQUEST_CODE) {
                String isbn = data.getStringExtra("CaptureIsbn");
                if (!TextUtils.isEmpty(isbn)) {
                    if (isbn.contains("http")) {
                        Intent intent = new Intent(this, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.RESULT, isbn);
                        startActivity(intent);
                    }
                }
            }
        }
    }
}
