package com.bc.wechat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户详情
 *
 * @author zhou
 */
public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.ll_root)
    LinearLayout mRootLl;

    @BindView(R.id.ll_nick_name)
    LinearLayout mNickNameLl;

    @BindView(R.id.tv_nick_name)
    TextView mNickNameTv;

    @BindView(R.id.tv_name)
    TextView mNameTv;

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.iv_sex)
    ImageView mSexIv;

    @BindView(R.id.tv_wx_id)
    TextView mWxIdTv;

    @BindView(R.id.iv_setting)
    ImageView mSettingIv;

    @BindView(R.id.tv_desc)
    TextView mDescTv;

    @BindView(R.id.tv_phone_temp)
    TextView mPhoneTempTv;

    @BindView(R.id.tv_phone)
    TextView mPhoneTv;

    @BindView(R.id.rl_edit_contact)
    RelativeLayout mEditContactRl;

    @BindView(R.id.rl_desc)
    RelativeLayout mDescRl;

    @BindView(R.id.rl_phone)
    RelativeLayout mPhoneRl;

    // 星标好友
    @BindView(R.id.iv_star_friends)
    ImageView mStarFriendsIv;

    // 操作按钮  根据是否好友关系分为如下两种
    // 是好友: 发送消息
    // 非好友: 添加到通讯录
    @BindView(R.id.rl_operate)
    RelativeLayout mOperateRl;

    // 朋友圈图片
    @BindView(R.id.sdv_moments_photo_1)
    SimpleDraweeView mMomentsPhoto1Sdv;

    @BindView(R.id.sdv_moments_photo_2)
    SimpleDraweeView mMomentsPhoto2Sdv;

    @BindView(R.id.sdv_moments_photo_3)
    SimpleDraweeView mMomentsPhoto3Sdv;

    @BindView(R.id.sdv_moments_photo_4)
    SimpleDraweeView mMomentsPhoto4Sdv;

    @BindView(R.id.rl_moments)
    RelativeLayout mMomentsRl;

    private User mUser;
    private VolleyUtil mVolleyUtil;
    private UserDao mUserDao;
    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(UserInfoActivity.this, R.color.status_bar_color_white);

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUserDao = new UserDao();

        initView();
    }

    private void initView() {
        userId = getIntent().getStringExtra("userId");

        final User contact = mUserDao.getUserById(userId);
        loadData(contact);

        getContactFromServer(mUser.getUserId(), userId);

        mEditContactRl.setOnClickListener(view -> {
            Intent intent = new Intent(UserInfoActivity.this, EditContactActivity.class);
            intent.putExtra("userId", contact.getUserId());
            intent.putExtra("isFriend", contact.getIsFriend());
            startActivity(intent);
        });

        mDescRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, EditContactActivity.class);
                intent.putExtra("userId", contact.getUserId());
                intent.putExtra("isFriend", contact.getIsFriend());
                startActivity(intent);
            }
        });

        mPhoneTempTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, EditContactActivity.class);
                intent.putExtra("userId", contact.getUserId());
                intent.putExtra("isFriend", contact.getIsFriend());
                startActivity(intent);
            }
        });

        mOperateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                intent.putExtra("contactId", userId);
                intent.putExtra("contactNickName", contact.getUserNickName());
                intent.putExtra("contactAvatar", contact.getUserAvatar());
                startActivity(intent);
            }
        });

        mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", contact.getUserAvatar());
                startActivity(intent);
            }
        });

        mMomentsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, UserFriendsCircleActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
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
                mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
            } else if (circlePhotoList.size() == 2) {
                mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                mMomentsPhoto2Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
            } else if (circlePhotoList.size() == 3) {
                mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                mMomentsPhoto2Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                mMomentsPhoto3Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
            } else if (circlePhotoList.size() >= 4) {
                mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                mMomentsPhoto2Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                mMomentsPhoto3Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                mMomentsPhoto4Sdv.setVisibility(View.VISIBLE);
                mMomentsPhoto4Sdv.setImageURI(Uri.parse(circlePhotoList.get(3)));
            }
        }

        if (!TextUtils.isEmpty(user.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
        }
        if (Constant.USER_SEX_MALE.equals(user.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(user.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_female);
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
            mEditContactRl.setVisibility(View.VISIBLE);
        } else {
            mEditContactRl.setVisibility(View.GONE);
        }

        // 是否星标好友
        if (Constant.RELA_IS_STAR_FRIEND.equals(user.getIsStarFriend())) {
            mStarFriendsIv.setVisibility(View.VISIBLE);
        } else {
            mStarFriendsIv.setVisibility(View.GONE);
        }
    }

    /**
     * 从服务器获取用户最新信息
     *
     * @param userId 用户ID
     */
    public void getContactFromServer(final String userId, final String contactId) {
        String url = Constant.BASE_URL + "users/" + userId + "/friends/" + contactId;

        mVolleyUtil.httpGetRequest(url, response -> {
            User user = JSON.parseObject(response, User.class);

//            mUserDao.saveUser(user);
            loadData(user);
        }, volleyError -> {

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = mUserDao.getUserById(userId);
        loadData(user);
        getContactFromServer(mUser.getUserId(), userId);
    }
}
