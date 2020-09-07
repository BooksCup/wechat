package com.bc.wechat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bc.wechat.utils.DensityUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    // 设置备注和标签
    @BindView(R.id.rl_edit_contact)
    RelativeLayout mEditContactRl;

    // 电话号码
    @BindView(R.id.ll_mobiles)
    LinearLayout mMobileLl;

    // 描述
    @BindView(R.id.rl_desc)
    RelativeLayout mDescRl;

    @BindView(R.id.tv_desc)
    TextView mDescTv;

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
    private User mContact;
    private VolleyUtil mVolleyUtil;
    private UserDao mUserDao;
    private String mContactId;


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
        mContactId = getIntent().getStringExtra("userId");

        mContact = mUserDao.getUserById(mContactId);
        loadData(mContact);

        getContactFromServer(mUser.getUserId(), mContactId);
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.sdv_avatar, R.id.rl_edit_contact, R.id.ll_mobiles,
            R.id.rl_desc, R.id.rl_moments, R.id.rl_operate})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            // 头像
            case R.id.sdv_avatar:
                intent = new Intent(UserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", mContact.getUserAvatar());
                startActivity(intent);
                break;
            // 进入编辑联系人页(设置备注和标签)
            // 设置备注和标签
            // 电话号码
            // 描述
            case R.id.rl_edit_contact:
            case R.id.ll_mobiles:
            case R.id.rl_desc:
                intent = new Intent(UserInfoActivity.this, EditContactActivity.class);
                intent.putExtra("userId", mContact.getUserId());
                intent.putExtra("isFriend", mContact.getIsFriend());
                startActivity(intent);
                break;
            // 朋友圈
            case R.id.rl_moments:
                intent = new Intent(UserInfoActivity.this, UserFriendsCircleActivity.class);
                intent.putExtra("userId", mContactId);
                startActivity(intent);
                break;
            // 发消息
            case R.id.rl_operate:
                intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                intent.putExtra("targetType", Constant.TARGET_TYPE_SINGLE);
                intent.putExtra("contactId", mContactId);
                intent.putExtra("contactNickName", mContact.getUserNickName());
                intent.putExtra("contactAvatar", mContact.getUserAvatar());
                startActivity(intent);
                break;
        }
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

        List<String> mobileList;
        try {
            mobileList = JSON.parseArray(user.getUserContactMobiles(), String.class);
            if (null == mobileList) {
                mobileList = new ArrayList<>();
            }
        } catch (Exception e) {
            mobileList = new ArrayList<>();
        }

        // 备注
        if (!TextUtils.isEmpty(user.getUserContactAlias())) {
            mNameTv.setText(user.getUserContactAlias());
            mNickNameLl.setVisibility(View.VISIBLE);
            mNickNameTv.setText("昵称：" + user.getUserNickName());
        } else {
            mNickNameLl.setVisibility(View.GONE);
            mNameTv.setText(user.getUserNickName());
        }

        // 手机号
        if (mobileList.size() > 0) {
            mMobileLl.setVisibility(View.VISIBLE);
            mMobileLl.removeAllViews();
            for (int i = 0; i < mobileList.size(); i++) {
                addMobileView(i, mobileList.size(), mobileList.get(i));
            }
        } else {
            mMobileLl.setVisibility(View.GONE);
        }

        // 描述
        if (!TextUtils.isEmpty(user.getUserContactDesc())) {
            mDescRl.setVisibility(View.VISIBLE);
            mDescTv.setText(user.getUserContactDesc());
        } else {
            mDescRl.setVisibility(View.GONE);
        }

        // 电话号码,标签,描述任一信息不为空则隐藏"设置备注和标签"
        if (!TextUtils.isEmpty(user.getUserContactDesc()) || mobileList.size() > 0) {
            mEditContactRl.setVisibility(View.GONE);
        } else {
            mEditContactRl.setVisibility(View.VISIBLE);
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
        String url = Constant.BASE_URL + "users/" + userId + "/contacts/" + contactId;

        mVolleyUtil.httpGetRequest(url, response -> {
            User user = JSON.parseObject(response, User.class);
            mUserDao.saveUser(user);
            loadData(user);
        }, volleyError -> {

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = mUserDao.getUserById(mContactId);
        loadData(user);
        getContactFromServer(mUser.getUserId(), mContactId);
    }

    private void addMobileView(int index, int mobileListSize, String mobile) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(this, 48));
        View view = LayoutInflater.from(this).inflate(R.layout.item_user_info_contact_mobile, null);
        view.setLayoutParams(lp);
        TextView mobileTempTv = view.findViewById(R.id.tv_mobile_temp);
        View shortDividerView = view.findViewById(R.id.view_divider_short);
        View longDividerView = view.findViewById(R.id.view_divider_long);
        if (index == 0) {
            mobileTempTv.setVisibility(View.VISIBLE);
        } else {
            mobileTempTv.setVisibility(View.INVISIBLE);
        }

        if (index == mobileListSize - 1) {
            longDividerView.setVisibility(View.VISIBLE);
            shortDividerView.setVisibility(View.GONE);
        } else {
            longDividerView.setVisibility(View.GONE);
            shortDividerView.setVisibility(View.VISIBLE);
        }

        TextView mobileTv = view.findViewById(R.id.tv_mobile);
        mobileTv.setText(mobile);
        mMobileLl.addView(view);
    }
}
