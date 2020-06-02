package com.bc.wechat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Message;
import com.bc.wechat.entity.QrCodeContent;
import com.bc.wechat.entity.User;
import com.bc.wechat.fragment.ChatsFragment;
import com.bc.wechat.fragment.DiscoverFragment;
import com.bc.wechat.fragment.ContactsFragment;
import com.bc.wechat.fragment.MeFragment;
import com.bc.wechat.utils.ExampleUtil;
import com.bc.wechat.utils.JimUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.TimeUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.google.zxing.client.android.CaptureActivity2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * 主activity
 *
 * @author zhou
 */
public class MainActivity extends BaseActivity {
    public static final int REQUEST_CODE_SCAN = 0;
    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_LOCATION = 2;

    public static boolean isForeground = false;

    private Fragment[] mFragments;
    private ChatsFragment mChatsFragment;
    private ContactsFragment mContactsFragment;
    private DiscoverFragment mDiscoverFragment;
    private MeFragment mMeFragment;

    private ImageView[] mMainButtonIvs;
    private TextView[] mMainButtonTvs;
    private int mIndex;
    // 当前fragment的index
    private int mCurrentTabIndex;

    private TextView mUnreadNewMsgsNumTv;
    private TextView mUnreadNewFriendsNumTv;

    private RelativeLayout mTitleRl;
    private TextView mTitleTv;
    private ImageView mAddIv;

    User mUser;

    // 首页弹出框
    private PopupWindow mPopupWindow;
    private View mPopupView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initStatusBar();
        initView();
        JMessageClient.registerEventReceiver(this);
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        registerMessageReceiver();
        refreshNewMsgsUnreadNum();
        refreshNewFriendsUnreadNum();
        // 进入强制刷新，防止离线消息
        mChatsFragment.refreshConversationList();
    }

    private void initView() {
        mChatsFragment = new ChatsFragment();
        mContactsFragment = new ContactsFragment();
        mDiscoverFragment = new DiscoverFragment();
        mMeFragment = new MeFragment();

        mFragments = new Fragment[]{mChatsFragment, mContactsFragment,
                mDiscoverFragment, mMeFragment};

        mMainButtonIvs = new ImageView[4];
        mMainButtonIvs[0] = findViewById(R.id.iv_chats);
        mMainButtonIvs[1] = findViewById(R.id.iv_contacts);
        mMainButtonIvs[2] = findViewById(R.id.iv_discover);
        mMainButtonIvs[3] = findViewById(R.id.iv_me);

        mMainButtonIvs[0].setSelected(true);
        mMainButtonTvs = new TextView[4];
        mMainButtonTvs[0] = findViewById(R.id.tv_chats);
        mMainButtonTvs[1] = findViewById(R.id.tv_contacts);
        mMainButtonTvs[2] = findViewById(R.id.tv_discover);
        mMainButtonTvs[3] = findViewById(R.id.tv_me);
        mMainButtonTvs[0].setTextColor(0xFF45C01A);

        mTitleRl = findViewById(R.id.rl_title);
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mAddIv = findViewById(R.id.iv_add);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rl_fragment_container, mChatsFragment)
                .add(R.id.rl_fragment_container, mContactsFragment)
                .add(R.id.rl_fragment_container, mDiscoverFragment)
                .add(R.id.rl_fragment_container, mMeFragment)
                .hide(mContactsFragment).hide(mDiscoverFragment).hide(mMeFragment)
                .show(mChatsFragment).commit();

        mUnreadNewMsgsNumTv = findViewById(R.id.unread_msg_number);
        mUnreadNewFriendsNumTv = findViewById(R.id.unread_address_number);

        mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopupWindow();
                if (!mPopupWindow.isShowing()) {
                    // 以下拉方式显示popupwindow
                    mPopupWindow.showAsDropDown(mAddIv, 0, 0);
                } else {
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_chats:
                // 会话列表
                // 主动加载一次会话
                mChatsFragment.refreshConversationList();
                mIndex = 0;
                mTitleTv.setText(getString(R.string.tab_chats));
                mTitleRl.setVisibility(View.VISIBLE);
                StatusBarUtil.setStatusBarColor(MainActivity.this, R.color.picker_list_divider);
                break;
            case R.id.rl_contacts:
                mIndex = 1;
                mTitleTv.setText(getString(R.string.tab_contacts));
                mTitleRl.setVisibility(View.VISIBLE);
                StatusBarUtil.setStatusBarColor(MainActivity.this, R.color.picker_list_divider);
                break;
            case R.id.rl_discover:
                mIndex = 2;
                mTitleTv.setText(getString(R.string.tab_discover));
                mTitleRl.setVisibility(View.VISIBLE);
                StatusBarUtil.setStatusBarColor(MainActivity.this, R.color.picker_list_divider);
                break;
            case R.id.rl_me:
                mIndex = 3;
                mTitleTv.setText(getString(R.string.tab_me));
                mTitleRl.setVisibility(View.GONE);
                StatusBarUtil.setStatusBarColor(MainActivity.this, R.color.bottom_text_color_normal);
                break;
        }

        if (mCurrentTabIndex != mIndex) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(mFragments[mCurrentTabIndex]);
            if (!mFragments[mIndex].isAdded()) {
                trx.add(R.id.rl_fragment_container, mFragments[mIndex]);
            }
            trx.show(mFragments[mIndex]).commit();
        }
        mMainButtonIvs[mCurrentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mMainButtonIvs[mIndex].setSelected(true);
        mMainButtonTvs[mCurrentTabIndex].setTextColor(getColor(R.color.black_deep));
        mMainButtonTvs[mIndex].setTextColor(0xFF45C01A);
        mCurrentTabIndex = mIndex;
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
        mContactsFragment.refreshNewFriendsUnreadNum();
        mContactsFragment.refreshFriendsList();

        // 会话
        if (mCurrentTabIndex == 0) {
            mChatsFragment.refreshConversationList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        JMessageClient.unRegisterEventReceiver(this);
    }

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_MAIN =
            "com.bc.wechat.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_MAIN";
    public static final String MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_NEW_FRIENDS_MSG =
            "com.bc.wechat.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_NEW_FRIENDS_MSG";

    public static final String MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_ACCEPT_MAIN =
            "com.bc.wechat.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_ACCEPT_MAIN";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_MAIN);
        filter.addAction(MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_ACCEPT_MAIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_MAIN.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    refreshNewFriendsUnreadNum();
                    mContactsFragment.refreshNewFriendsUnreadNum();
                }
                if (MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_ACCEPT_MAIN.equals(intent.getAction())) {
                    mContactsFragment.refreshFriendsList();
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
        message.setToUserId(mUser.getUserId());
        message.setTimestamp(new Date().getTime());
        message.setMessageType(messageType);

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

        List<User> friendList = User.find(User.class, "user_id = ?", message.getFromUserId());
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
        mChatsFragment.refreshConversationList();

        if (fromUserInfo.getUserName().equals(mUser.getUserId())) {
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
        mPopupView = View.inflate(this, R.layout.popup_window_add, null);
        mPopupWindow = new PopupWindow();
        // 设置SelectPicPopupWindow的View
        mPopupWindow.setContentView(mPopupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 刷新状态
        mPopupWindow.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);

        // 发起群聊
        RelativeLayout mCreateGroupRl = mPopupView.findViewById(R.id.rl_create_group);

        // 添加朋友
        RelativeLayout mAddFriendsRl = mPopupView.findViewById(R.id.rl_add_friends);
        mAddFriendsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                startActivity(new Intent(MainActivity.this, AddContactsActivity.class));
            }
        });

        // 扫一扫
        RelativeLayout mScanQrCodeRl = mPopupView.findViewById(R.id.rl_scan_qr_code);
        mScanQrCodeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                String[] permissions = new String[]{"android.permission.CAMERA"};
                requestPermissions(MainActivity.this, permissions, REQUEST_CODE_CAMERA);
            }
        });

        // 帮助和反馈
        RelativeLayout mHelpRl = mPopupView.findViewById(R.id.rl_help);

    }

    @Override
    public int checkSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN) {
                String isbn = data.getStringExtra("CaptureIsbn");
                if (!TextUtils.isEmpty(isbn)) {
                    if (isbn.contains("http")) {
                        Intent intent = new Intent(this, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.RESULT, isbn);
                        startActivity(intent);
                    } else {
                        try {
                            QrCodeContent qrCodeContent = JSON.parseObject(isbn, QrCodeContent.class);
                            if (QrCodeContent.QR_CODE_TYPE_USER.equals(qrCodeContent.getType())) {
                                startActivity(new Intent(this, UserInfoActivity.class).
                                        putExtra("userId", String.valueOf(qrCodeContent.getContentMap().get("userId"))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0开始的动态权限，这里进行版本判断
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
                    case REQUEST_CODE_CAMERA:
                        startScanActivity();
                        break;
                }
            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(this, requestPermissions, requestCode);
            }
        }
    }

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
            // 同意权限做的处理,开启服务提交通讯录
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    startScanActivity();
                    break;
            }
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission,
                                       int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            String content = "";
            // 非初次进入App且已授权
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    content = getString(R.string.request_permission_camera);
                    break;
            }
            if (!TextUtils.isEmpty(content)) {
                final ConfirmDialog mConfirmDialog = new ConfirmDialog(MainActivity.this, "权限申请",
                        content,
                        "去设置", "取消", context.getColor(R.color.navy_blue));
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
    }

    /**
     * 进入扫一扫页面
     */
    private void startScanActivity() {
        Intent intent = new Intent(this, CaptureActivity2.class);
        intent.putExtra(CaptureActivity2.USE_DEFUALT_ISBN_ACTIVITY, true);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }
}
