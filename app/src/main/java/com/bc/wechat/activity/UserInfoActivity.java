package com.bc.wechat.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import com.bc.wechat.dao.FriendDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.utils.WechatBeanUtil;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends Activity {

    private LinearLayout mRootLl;
    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private ImageView mSexIv;
    private TextView mWxIdTv;
    private ImageView mSettingIv;

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

    private VolleyUtil volleyUtil;
    private FriendDao friendDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        volleyUtil = VolleyUtil.getInstance(this);
        friendDao = new FriendDao();
        initView();
    }

    private void initView() {
        mRootLl = findViewById(R.id.ll_root);

        mNickNameTv = findViewById(R.id.tv_name);
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mSexIv = findViewById(R.id.iv_sex);
        mWxIdTv = findViewById(R.id.tv_wx_id);
        mSettingIv = findViewById(R.id.iv_setting);

        mOperateRl = findViewById(R.id.rl_operate);

        mFriendsCircleRl = findViewById(R.id.rl_friends_circle);
        mCirclePhoto1Sdv = findViewById(R.id.sdv_circle_photo_1);
        mCirclePhoto2Sdv = findViewById(R.id.sdv_circle_photo_2);
        mCirclePhoto3Sdv = findViewById(R.id.sdv_circle_photo_3);
        mCirclePhoto4Sdv = findViewById(R.id.sdv_circle_photo_4);

        final String userId = getIntent().getStringExtra("userId");

        final Friend friend = friendDao.getFriendById(userId);
        loadData(friend);

        getUserFromServer(userId);

        mSettingIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.popup_window_user_setting, null);
                // 给popwindow加上动画效果
                LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                mPopRootLl.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
                // 设置popwindow的宽高，这里我直接获取了手机屏幕的宽，高设置了600DP
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                PopupWindow popupWindow = new PopupWindow(view, dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

                // 使其聚集
                popupWindow.setFocusable(true);
                // 设置允许在外点击消失
                popupWindow.setOutsideTouchable(true);

                // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                backgroundAlpha(0.5f);  //透明度

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                    }
                });
                //弹出的位置
                popupWindow.showAtLocation(mRootLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void back(View view) {
        finish();
    }

    // 渲染数据
    private void loadData(Friend friend) {
        if (!TextUtils.isEmpty(friend.getUserLastestCirclePhotos())) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(friend.getUserLastestCirclePhotos(), String.class);
                if (null == circlePhotoList) {
                    circlePhotoList = new ArrayList<>();
                }
            } catch (Exception e) {
                circlePhotoList = new ArrayList<>();
            }
            switch (circlePhotoList.size()) {
                case 1:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    break;
                case 2:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    break;
                case 3:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    mCirclePhoto3Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                    break;
                case 4:
                    mCirclePhoto1Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mCirclePhoto2Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    mCirclePhoto3Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                    mCirclePhoto4Sdv.setVisibility(View.VISIBLE);
                    mCirclePhoto4Sdv.setImageURI(Uri.parse(circlePhotoList.get(3)));
                    break;
                default:
                    break;
            }
        }

        mNickNameTv.setText(friend.getUserNickName());
        if (null != friend.getUserAvatar() && !"".equals(friend.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(friend.getUserAvatar()));
        }
        if (Constant.USER_SEX_MALE.equals(friend.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(friend.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.ic_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(friend.getUserWxId())) {
            mWxIdTv.setText("微信号：" + friend.getUserWxId());
        }
    }

    public void getUserFromServer(final String userId) {
        String url = Constant.BASE_URL + "users/" + userId;

        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = JSON.parseObject(response, User.class);
                // 检查此人是否好友，
                // 如果是好友则更新用户信息，非好友则不做任何操作
                boolean isFriend = friendDao.checkFriendExists(userId);
                if (isFriend) {
                    friendDao.saveFriendByUserInfo(user);
                }
                Friend friend = WechatBeanUtil.transferUserToFriend(user);
                loadData(friend);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

}
