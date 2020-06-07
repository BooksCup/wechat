package com.bc.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.bc.wechat.dao.MessageDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.WarningDialog;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

public class BaseActivity extends FragmentActivity {
    private MessageDao mMessageDao;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageDao = new MessageDao();
        mUser = PreferencesUtil.getInstance().getUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 监听登录状态
     *
     * @param event 登录状态改变事件
     */
    public void onEventMainThread(LoginStateChangeEvent event) {
        final LoginStateChangeEvent.Reason reason = event.getReason();
        switch (reason) {
            // 被挤掉线
            case user_logout:
                // 去除登录标识
                PreferencesUtil.getInstance().setLogin(false);

                final ConfirmDialog logoutConfirmDialog = new ConfirmDialog(this, "",
                        "您的账号在其他设备上登陆", "重新登录", "退出");
                logoutConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        // 重新登录
                        logoutConfirmDialog.dismiss();
                        User user = PreferencesUtil.getInstance().getUser();
                        JMessageClient.login(user.getUserId(), user.getUserImPassword(), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                            }
                        });
                    }

                    @Override
                    public void onCancelClick() {
                        // 退出
                        logoutConfirmDialog.dismiss();
                        // 清除本地记录
                        PreferencesUtil.getInstance().setUser(null);
                        User.deleteAll(User.class);

                        // 跳转至登录页
                        Intent intent = new Intent(BaseActivity.this, PhoneLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                // 点击空白处消失
                logoutConfirmDialog.setCancelable(false);
                logoutConfirmDialog.show();
                break;
        }
    }

    public void onEvent(OfflineMessageEvent event) {
        List<Message> offlineMessageList = event.getOfflineMessageList();
        for (Message message : offlineMessageList) {
            mMessageDao.saveMessageByImMessage(message, mUser.getUserId());
        }
    }

    protected void initStatusBar() {
        Window win = getWindow();
        // KITKAT也能满足，只是SYSTEM_UI_FLAG_LIGHT_STATUS_BAR（状态栏字体颜色反转）只有在6.0才有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 显示警告弹窗
     *
     * @param context context
     * @param title   标题
     * @param content 内容
     * @param confirm 确认键
     */
    protected void showWarningDialog(Context context, String title, String content, String confirm) {
        final WarningDialog mWarningDialog = new WarningDialog(context, title, content, confirm);
        mWarningDialog.setOnDialogClickListener(new WarningDialog.OnDialogClickListener() {
            @Override
            public void onOkClick() {
                mWarningDialog.dismiss();
            }

        });
        // 点击空白处消失
        mWarningDialog.setCancelable(true);
        mWarningDialog.show();
    }

}
