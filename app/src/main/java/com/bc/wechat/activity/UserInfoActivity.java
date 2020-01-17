package com.bc.wechat.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendsCircleDao;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;
import com.facebook.drawee.view.SimpleDraweeView;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;

/**
 * 用户详情
 *
 * @author zhou
 */
public class UserInfoActivity extends Activity {

    private static final int REQUEST_CODE_ADD_FRIEND_TO_DESKTOP = 1;

    private LinearLayout mRootLl;
    private LinearLayout mNickNameLl;
    private TextView mNickNameTv;
    private TextView mNameTv;
    private SimpleDraweeView mAvatarSdv;
    private ImageView mSexIv;
    private TextView mWxIdTv;
    private ImageView mSettingIv;
    private TextView mDescTv;
    private TextView mPhoneTempTv;
    private TextView mPhoneTv;

    private RelativeLayout mSetRemarkAndTagRl;
    private RelativeLayout mDescRl;
    private RelativeLayout mPhoneRl;

    // 星标好友
    private ImageView mStarFriendsIv;

    // 操作按钮  根据是否好友关系分为如下两种
    // 是好友: 发送消息
    // 非好友: 添加到通讯录
    private RelativeLayout mOperateRl;

    // 朋友圈图片
    private SimpleDraweeView mCirclePhoto1Sdv;
    private SimpleDraweeView mCirclePhoto2Sdv;
    private SimpleDraweeView mCirclePhoto3Sdv;
    private SimpleDraweeView mCirclePhoto4Sdv;

    private RelativeLayout mFriendsCircleRl;

    private User mUser;
    private VolleyUtil mVolleyUtil;
    private UserDao mUserDao;
    private String userId;

    private FriendsCircleDao mFriendsCircleDao;

    // 弹窗
    private PopupWindow mPopupWindow;

    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUserDao = new UserDao();

        mFriendsCircleDao = new FriendsCircleDao();
        mDialog = new LoadingDialog(UserInfoActivity.this);
        initView();
    }

    private void initView() {
        mRootLl = findViewById(R.id.ll_root);

        mSetRemarkAndTagRl = findViewById(R.id.rl_set_remark_and_tag);
        mDescRl = findViewById(R.id.rl_desc);
        mPhoneRl = findViewById(R.id.rl_phone);

        mNickNameLl = findViewById(R.id.ll_nick_name);
        mNameTv = findViewById(R.id.tv_name);
        mNickNameTv = findViewById(R.id.tv_nick_name);
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mSexIv = findViewById(R.id.iv_sex);
        mWxIdTv = findViewById(R.id.tv_wx_id);
        mSettingIv = findViewById(R.id.iv_setting);
        mDescTv = findViewById(R.id.tv_desc);
        mPhoneTempTv = findViewById(R.id.tv_phone_temp);
        mPhoneTv = findViewById(R.id.tv_phone);

        mStarFriendsIv = findViewById(R.id.iv_star_friends);

        mOperateRl = findViewById(R.id.rl_operate);

        mFriendsCircleRl = findViewById(R.id.rl_friends_circle);
        mCirclePhoto1Sdv = findViewById(R.id.sdv_circle_photo_1);
        mCirclePhoto2Sdv = findViewById(R.id.sdv_circle_photo_2);
        mCirclePhoto3Sdv = findViewById(R.id.sdv_circle_photo_3);
        mCirclePhoto4Sdv = findViewById(R.id.sdv_circle_photo_4);

        userId = getIntent().getStringExtra("userId");

        final User friend = mUserDao.getUserById(userId);
        loadData(friend);

        getFriendFromServer(mUser.getUserId(), userId);

        mSettingIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.popup_window_user_setting, null);
                // 给popwindow加上动画效果
                LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                mPopRootLl.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
                // 设置popwindow的宽高
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                mPopupWindow = new PopupWindow(view, dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

                // 使其聚集
                mPopupWindow.setFocusable(true);
                // 设置允许在外点击消失
                mPopupWindow.setOutsideTouchable(true);

                // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                backgroundAlpha(0.5f);  //透明度

                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                    }
                });
                // 弹出的位置
                mPopupWindow.showAtLocation(mRootLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                // 设置备注和标签
                RelativeLayout mSetRemarkAndTagRl = view.findViewById(R.id.rl_set_remark_and_tag);
                mSetRemarkAndTagRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mPopupWindow.dismiss();
                        Intent intent = new Intent(UserInfoActivity.this, SetRemarkAndTagActivity.class);
                        intent.putExtra("userId", friend.getUserId());
                        intent.putExtra("isFriend", Constant.IS_FRIEND);
                        startActivity(intent);
                    }
                });

                // 设为星标好友
                RelativeLayout mSetStarFriendsRl = view.findViewById(R.id.rl_set_star_friends);
                mSetStarFriendsRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        mStarFriendsIv.setVisibility(View.VISIBLE);
                    }
                });

                // 添加好友至桌面
                RelativeLayout mAddFriendToDesktopRl = view.findViewById(R.id.rl_add_friend_to_desktop);
                mAddFriendToDesktopRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();

                        final ConfirmDialog confirmDialog = new ConfirmDialog(UserInfoActivity.this, "已尝试添加到桌面",
                                "若添加失败，请前往系统设置，为微信打开\"创建桌面快捷方式\"的权限。",
                                "了解详情", "返回");
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
                        confirmDialog.setCancelable(true);
                        confirmDialog.show();

                        final Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                        intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                        intent.putExtra("fromUserId", friend.getUserId());
                        intent.putExtra("fromUserNickName", friend.getUserNickName());
                        intent.putExtra("fromUserAvatar", friend.getUserAvatar());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = getBitmapFromAvatarUrl(friend.getUserAvatar());
                                addShortcut(UserInfoActivity.this, friend.getUserNickName(), bitmap, intent);
                            }
                        }).start();
                    }
                });

                // 删除好友
                RelativeLayout mDeleteFriendRl = view.findViewById(R.id.rl_delete_friend);
                mDeleteFriendRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        final ConfirmDialog confirmDialog = new ConfirmDialog(UserInfoActivity.this, "删除联系人",
                                "将联系人\"" + friend.getUserNickName() + "\"删除，将同时删除与该联系人的聊天记录",
                                getString(R.string.delete), getString(R.string.cancel));
                        confirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                            @Override
                            public void onOkClick() {
                                confirmDialog.dismiss();
                                mDialog.setMessage(getString(R.string.please_wait));
                                mDialog.show();
                                deleteFriend(mUser.getUserId(), friend.getUserId());
                            }

                            @Override
                            public void onCancelClick() {
                                confirmDialog.dismiss();
                            }
                        });
                        // 点击空白处消失
                        confirmDialog.setCancelable(true);
                        confirmDialog.show();
                    }
                });

                // 取消
                RelativeLayout mCancelRl = view.findViewById(R.id.rl_cancel);
                mCancelRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
            }
        });

        mSetRemarkAndTagRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, SetRemarkAndTagActivity.class);
                intent.putExtra("userId", friend.getUserId());
                intent.putExtra("isFriend", friend.getIsFriend());
                startActivity(intent);
            }
        });

        mDescRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, SetRemarkAndTagActivity.class);
                intent.putExtra("userId", friend.getUserId());
                intent.putExtra("isFriend", friend.getIsFriend());
                startActivity(intent);
            }
        });

        mPhoneTempTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, SetRemarkAndTagActivity.class);
                intent.putExtra("userId", friend.getUserId());
                intent.putExtra("isFriend", friend.getIsFriend());
                startActivity(intent);
            }
        });

        mOperateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                intent.putExtra("fromUserId", userId);
                intent.putExtra("fromUserNickName", friend.getUserNickName());
                intent.putExtra("fromUserAvatar", friend.getUserAvatar());
                startActivity(intent);
            }
        });

        mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", friend.getUserAvatar());
                startActivity(intent);
            }
        });

        mFriendsCircleRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, UserFriendsCircleActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     * 1.0完全不透明，0.0f完全透明
     *
     * @param bgAlpha 透明度值
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
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
                    REQUEST_CODE_ADD_FRIEND_TO_DESKTOP, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT).getIntentSender());
        }
    }

    public void back(View view) {
        finish();
    }

    // 渲染数据
    private void loadData(User user) {
        if (!TextUtils.isEmpty(user.getUserLastestCirclePhotos())) {
            // 渲染朋友圈图片
            List<String> circlePhotoList = CommonUtil.getListFromJson(user.getUserLastestCirclePhotos(), String.class);
            if (circlePhotoList.size() == 1) {
                mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
            } else if (circlePhotoList.size() == 2) {
                mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
            } else if (circlePhotoList.size() == 3) {
                mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                mCirclePhoto3Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
            } else if (circlePhotoList.size() >= 4) {
                mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                mCirclePhoto3Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                mCirclePhoto4Sdv.setVisibility(View.VISIBLE);
                mCirclePhoto4Sdv.setImageURI(Uri.parse(circlePhotoList.get(3)));
            }
        }

        if (!TextUtils.isEmpty(user.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
        }
        if (Constant.USER_SEX_MALE.equals(user.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(user.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(user.getUserWxId())) {
            mWxIdTv.setText("微信号：" + user.getUserWxId());
        }

        // 电话号码
        if (!TextUtils.isEmpty(user.getUserFriendPhone())) {
            mPhoneRl.setVisibility(View.VISIBLE);
            mPhoneTv.setText(user.getUserFriendPhone());
        } else {
            mPhoneRl.setVisibility(View.GONE);
        }

        // 描述
        if (!TextUtils.isEmpty(user.getUserFriendDesc())) {
            mDescRl.setVisibility(View.VISIBLE);
            mDescTv.setText(user.getUserFriendDesc());
        } else {
            mDescRl.setVisibility(View.GONE);
        }

        // 备注
        if (!TextUtils.isEmpty(user.getUserFriendRemark())) {
            mNameTv.setText(user.getUserFriendRemark());
            mNickNameLl.setVisibility(View.VISIBLE);
            mNickNameTv.setText("昵称：" + user.getUserNickName());
        } else {
            mNickNameLl.setVisibility(View.GONE);
            mNameTv.setText(user.getUserNickName());
        }

        if (TextUtils.isEmpty(user.getUserFriendDesc())) {
            mSetRemarkAndTagRl.setVisibility(View.VISIBLE);
        } else {
            mSetRemarkAndTagRl.setVisibility(View.GONE);
        }
    }

    /**
     * 从服务器获取用户最新信息
     *
     * @param userId 用户ID
     */
    public void getFriendFromServer(final String userId, final String friendId) {
        String url = Constant.BASE_URL + "users/" + userId + "/friends/" + friendId;

        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = JSON.parseObject(response, User.class);

                mUserDao.saveUser(user);
                loadData(user);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    /**
     * 删除好友
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     */
    private void deleteFriend(final String userId, final String friendId) {
        String url = Constant.BASE_URL + "users/" + userId + "/friends/" + friendId;

        mVolleyUtil.httpDeleteRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                // 清除本地记录
                // 通讯录删除
                User user = mUserDao.getUserById(friendId);
                User.delete(user);

                // 朋友圈清除记录
                mFriendsCircleDao.deleteFriendsCircleByUserId(friendId);

                // 删除会话
                JMessageClient.deleteSingleConversation(friendId);

                finish();
                // TODO
                // 跳转到首页第二个tab并refresh
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        User user = mUserDao.getUserById(userId);
        loadData(user);
        getFriendFromServer(mUser.getUserId(), userId);
    }

}
