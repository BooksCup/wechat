package com.bc.wechat.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户设置
 *
 * @author zhou
 */
public class UserSettingActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_TO_HOME_SCREEN = 1;

    // 设置备注和标签
    @BindView(R.id.rl_edit_contact)
    RelativeLayout mEditContactRl;

    @BindView(R.id.tv_edit_contact)
    TextView mEditContactTv;

    // 朋友权限
    @BindView(R.id.rl_privacy)
    RelativeLayout mPrivacyRl;

    // 把他推荐给朋友
    @BindView(R.id.rl_share_contact)
    RelativeLayout mShareContactRl;

    // 添加到桌面
    @BindView(R.id.rl_add_to_home_screen)
    RelativeLayout mAddToHomeScreenRl;

    // 设为星标朋友
    @BindView(R.id.rl_add_star)
    RelativeLayout mAddStarRl;

    // 加入黑名单
    @BindView(R.id.rl_block)
    RelativeLayout mBlockRl;

    // 投诉
    @BindView(R.id.rl_report)
    RelativeLayout mReportRl;

    // 删除
    @BindView(R.id.rl_delete)
    RelativeLayout mDeleteRl;

    // 设为星标好友
    @BindView(R.id.iv_add_star)
    ImageView mAddStarIv;

    // 取消星标好友
    @BindView(R.id.iv_cancel_star)
    ImageView mCancelStarIv;

    UserDao mUserDao;
    String mContactId;
    String mIsFriend;
    User mContact;

    VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.bind(this);
        initStatusBar();

        mUserDao = new UserDao();
        mIsFriend = getIntent().getStringExtra("isFriend");
        mContactId = getIntent().getStringExtra("contactId");
        mContact = mUserDao.getUserById(mContactId);

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(UserSettingActivity.this);
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        if (Constant.IS_NOT_FRIEND.equals(mIsFriend)) {
            // 非好友
            mPrivacyRl.setVisibility(View.GONE);
            mShareContactRl.setVisibility(View.GONE);
            mAddToHomeScreenRl.setVisibility(View.GONE);
            mAddStarRl.setVisibility(View.GONE);
            mReportRl.setVisibility(View.GONE);
            mDeleteRl.setVisibility(View.GONE);
        } else {
            // 好友
        }
        loadData(mContact);
    }

    private void loadData(User user) {
        mEditContactTv.setText(user.getUserContactAlias());
    }

    @OnClick({R.id.rl_edit_contact, R.id.rl_privacy, R.id.rl_add_to_home_screen,
            R.id.iv_add_star, R.id.iv_cancel_star})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_edit_contact:
                // 设置备注和标签
                intent = new Intent(UserSettingActivity.this, EditContactActivity.class);
                intent.putExtra("contactId", mContactId);
                intent.putExtra("isFriend", mIsFriend);
                startActivity(intent);
                break;
            case R.id.rl_privacy:
                // 朋友权限
                intent = new Intent(UserSettingActivity.this, ContactPrivacyActivity.class);
                intent.putExtra("contactId", mContact.getUserId());
                startActivity(intent);
                break;
            case R.id.rl_add_to_home_screen:
                // 添加到桌面
                final ConfirmDialog confirmDialog = new ConfirmDialog(UserSettingActivity.this, "已尝试添加到桌面",
                        "若添加失败，请前往系统设置，为微信打开\"创建桌面快捷方式\"的权限。",
                        "了解详情", getString(R.string.cancel), getColor(R.color.navy_blue));
                confirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        confirmDialog.dismiss();
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                confirmDialog.setCancelable(false);
                confirmDialog.show();

                intent = new Intent(UserSettingActivity.this, ChatActivity.class);
                intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                intent.putExtra("contactId", mContact.getUserId());
                intent.putExtra("contactNickName", mContact.getUserNickName());
                intent.putExtra("contactAvatar", mContact.getUserAvatar());
                new Thread(() -> {
                    Bitmap bitmap = getBitmapFromAvatarUrl(mContact.getUserAvatar());
                    addShortcut(UserSettingActivity.this, mContact.getUserNickName(), bitmap, intent);
                }).start();
                break;
            case R.id.iv_add_star:
                setContactStarred(Constant.CONTACT_IS_STARRED);
                break;
            case R.id.iv_cancel_star:
                setContactStarred(Constant.CONTACT_IS_NOT_STARRED);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContact = mUserDao.getUserById(mContactId);
        loadData(mContact);
    }

    private static Bitmap getBitmapFromAvatarUrl(final String avatarUrl) {
        try {
            URL url = new URL(avatarUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 添加桌面图标快捷方式
     *
     * @param activity     Activity对象
     * @param name         快捷方式名称
     * @param icon         快捷方式图标
     * @param actionIntent 快捷方式图标点击动作
     */
    public void addShortcut(Activity activity, String name, Bitmap icon, Intent actionIntent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 创建快捷方式的intent广播
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 添加快捷名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            // 快捷图标是允许重复(不一定有效)
            shortcut.putExtra("duplicate", false);
            // 快捷图标
            // 使用Bitmap对象模式
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
            // 添加携带的下次启动要用的Intent信息
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            // 发送广播
            activity.sendBroadcast(shortcut);
        } else {
            ShortcutManager shortcutManager = (ShortcutManager) activity.getSystemService(Context.SHORTCUT_SERVICE);
            if (null == shortcutManager) {
                // 创建快捷方式失败
                return;
            }
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(activity, name)
                    .setShortLabel(name)
                    .setIcon(Icon.createWithBitmap(icon))
                    .setIntent(actionIntent)
                    .setLongLabel(name)
                    .build();
            shortcutManager.requestPinShortcut(shortcutInfo, PendingIntent.getActivity(activity,
                    REQUEST_CODE_ADD_TO_HOME_SCREEN, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT).getIntentSender());
        }
    }

    /**
     * 设置或取消星标朋友
     *
     * @param isStarred 是否星标好友
     */
    private void setContactStarred(final String isStarred) {
        mDialog.setMessage("正在处理...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String url = Constant.BASE_URL + "users/" + mUser.getUserId() + "/contacts/" + mContactId + "/star";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("isStarred", isStarred);

        mVolleyUtil.httpPutRequest(url, paramMap, (Response.Listener<String>) response -> {
            mDialog.dismiss();
            if (Constant.CONTACT_IS_STARRED.equals(isStarred)) {

                mAddStarIv.setVisibility(View.GONE);
                mCancelStarIv.setVisibility(View.VISIBLE);

                mContact.setIsStarFriend(Constant.CONTACT_IS_STARRED);
                Toast.makeText(UserSettingActivity.this, "已设为星标朋友", Toast.LENGTH_SHORT).show();
            } else {
                mAddStarIv.setVisibility(View.VISIBLE);
                mCancelStarIv.setVisibility(View.GONE);

                mContact.setIsStarFriend(Constant.CONTACT_IS_NOT_STARRED);
                Toast.makeText(UserSettingActivity.this, "已取消星标朋友", Toast.LENGTH_SHORT).show();
            }
            mUserDao.saveUser(mContact);
        }, (Response.ErrorListener) volleyError -> mDialog.dismiss());

    }
}
