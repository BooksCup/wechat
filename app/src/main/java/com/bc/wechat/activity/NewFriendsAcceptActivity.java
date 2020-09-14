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
import com.bc.wechat.dao.FriendApplyDao;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 好友申请
 *
 * @author zhou
 */
public class NewFriendsAcceptActivity extends BaseActivity {
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

    @BindView(R.id.tv_from)
    TextView mFromTv;

    @BindView(R.id.rl_accept)
    RelativeLayout mAcceptRl;

    // 朋友圈图片
    private SimpleDraweeView mMomentsPhoto1Sdv;
    private SimpleDraweeView mMomentsPhoto2Sdv;
    private SimpleDraweeView mMomentsPhoto3Sdv;
    private SimpleDraweeView mMomentsPhoto4Sdv;

    VolleyUtil mVolleyUtil;
    FriendApplyDao mFriendApplyDao;
    UserDao mUserDao;
    FriendApply mFriendApply;
    LoadingDialog mDialog;
    User mContact;
    User mUser;
    String mContactId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_accept);
        ButterKnife.bind(this);
        initStatusBar();
        StatusBarUtil.setStatusBarColor(NewFriendsAcceptActivity.this, R.color.status_bar_color_white);

        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);
        mVolleyUtil = VolleyUtil.getInstance(this);
        mFriendApplyDao = new FriendApplyDao();
        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        final String applyId = getIntent().getStringExtra("applyId");
        mFriendApply = mFriendApplyDao.getFriendApplyByApplyId(applyId);
        mContactId = mFriendApply.getFromUserId();
        mContact = mUserDao.getUserById(mContactId);

        loadData(mContact);
        getContactFromServer(mUser.getUserId(), mContactId);
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.iv_setting})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_setting:
                intent = new Intent(NewFriendsAcceptActivity.this, UserSettingActivity.class);
                intent.putExtra("contactId", mContactId);
                intent.putExtra("isFriend", Constant.IS_NOT_FRIEND);
                startActivity(intent);
                break;
        }
    }


    private void loadData(User user) {
        if (!TextUtils.isEmpty(user.getUserContactAlias())) {
            mNameTv.setText(user.getUserContactAlias());
            mNickNameLl.setVisibility(View.VISIBLE);
            mNickNameTv.setText("昵称：" + user.getUserNickName());
        } else {
            mNickNameLl.setVisibility(View.GONE);
            mNameTv.setText(user.getUserNickName());
        }
        if (!TextUtils.isEmpty(user.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));
        }

        // 性别
        if (Constant.USER_SEX_MALE.equals(user.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(user.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        // 来源
        if (Constant.CONTACTS_FROM_PHONE.equals(user.getUserContactFrom())) {
            mFromTv.setText("对方通过搜索手机号添加");
        } else if (Constant.CONTACTS_FROM_WX_ID.equals(user.getUserContactFrom())) {

        } else if (Constant.CONTACTS_FROM_PEOPLE_NEARBY.equals(user.getUserContactFrom())) {

        } else if (Constant.CONTACTS_FROM_CONTACT.equals(user.getUserContactFrom())) {

        }


        if (!TextUtils.isEmpty(user.getUserLastestCirclePhotos())) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(user.getUserLastestCirclePhotos(), String.class);
                if (null == circlePhotoList) {
                    circlePhotoList = new ArrayList<>();
                }
            } catch (Exception e) {
                circlePhotoList = new ArrayList<>();
            }
            switch (circlePhotoList.size()) {
                case 1:
                    mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    break;
                case 2:
                    mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mMomentsPhoto2Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    break;
                case 3:
                    mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mMomentsPhoto2Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    mMomentsPhoto3Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                    break;
                case 4:
                    mMomentsPhoto1Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto1Sdv.setImageURI(Uri.parse(circlePhotoList.get(0)));
                    mMomentsPhoto2Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto2Sdv.setImageURI(Uri.parse(circlePhotoList.get(1)));
                    mMomentsPhoto3Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto3Sdv.setImageURI(Uri.parse(circlePhotoList.get(2)));
                    mMomentsPhoto4Sdv.setVisibility(View.VISIBLE);
                    mMomentsPhoto4Sdv.setImageURI(Uri.parse(circlePhotoList.get(3)));
                    break;
                default:
                    break;
            }
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
}
