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
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.blankj.utilcode.util.CollectionUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 陌生人用户详情页
 *
 * @author zhou
 */
public class UserInfoStrangerActivity extends BaseActivity {
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.tv_name)
    TextView mNameTv;

    @BindView(R.id.tv_nick_name)
    TextView mNickNameTv;

    @BindView(R.id.iv_sex)
    ImageView mSexIv;

    @BindView(R.id.tv_whats_up)
    TextView mWhatsUpTv;

    @BindView(R.id.tv_from)
    TextView mFromTv;

    // 标签
    @BindView(R.id.rl_tags)
    RelativeLayout mTagsRl;

    @BindView(R.id.tv_tags)
    TextView mTagsTv;

    // 描述
    @BindView(R.id.rl_desc)
    RelativeLayout mDescRl;

    @BindView(R.id.tv_desc)
    TextView mDescTv;

    @BindView(R.id.rl_whats_up)
    RelativeLayout mWhatsUpRl;

    @BindView(R.id.rl_edit_contact)
    RelativeLayout mEditContactRl;

    @BindView(R.id.ll_nick_name)
    LinearLayout mNickNameLl;

    @BindView(R.id.rl_add)
    RelativeLayout mAddRl;

    private UserDao mUserDao;
    private VolleyUtil mVolleyUtil;
    private User mUser;
    private String mContactId;
    private User mContact;
    private String mFrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_stranger);
        ButterKnife.bind(this);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(UserInfoStrangerActivity.this, R.color.status_bar_color_white);

        mUserDao = new UserDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mContactId = getIntent().getStringExtra("contactId");
        mFrom = getIntent().getStringExtra("from");
        // 加载本地数据
        mContact = mUserDao.getUserById(mContactId);
        loadData(mContact);
        // 加载服务器最新数据并保存至本地
        getContactFromServer(mUser.getUserId(), mContactId);
    }

    @OnClick({R.id.sdv_avatar, R.id.rl_edit_contact, R.id.rl_tags, R.id.rl_desc, R.id.rl_add})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.sdv_avatar:
                intent = new Intent(UserInfoStrangerActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", mContact.getUserAvatar());
                startActivity(intent);
                break;
            case R.id.rl_edit_contact:
            case R.id.rl_tags:
            case R.id.rl_desc:
                intent = new Intent(UserInfoStrangerActivity.this, EditContactActivity.class);
                intent.putExtra("userId", mContactId);
                intent.putExtra("isFriend", Constant.IS_NOT_FRIEND);
                startActivity(intent);
                break;
            case R.id.rl_add:
                intent = new Intent(UserInfoStrangerActivity.this, NewFriendsApplyConfirmActivity.class);
                intent.putExtra("friendId", mContactId);
                startActivity(intent);
                break;

        }
    }

    public void back(View view) {
        finish();
    }

    // 渲染数据
    private void loadData(User user) {
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

        if (TextUtils.isEmpty(user.getUserSign())) {
            mWhatsUpRl.setVisibility(View.GONE);
        } else {
            mWhatsUpTv.setText(user.getUserSign());
        }

        if (Constant.CONTACTS_FROM_PHONE.equals(mFrom)) {
            mFromTv.setText(getString(R.string.search_friend_by_phone));
        } else if (Constant.CONTACTS_FROM_WX_ID.equals(mFrom)) {
            mFromTv.setText(getString(R.string.search_friend_by_wx_id));
        } else if (Constant.CONTACTS_FROM_PEOPLE_NEARBY.equals(mFrom)) {
            mFromTv.setText(getString(R.string.search_friend_by_people_nearby));
        } else if (Constant.CONTACTS_FROM_CONTACT.equals(mFrom)) {
            mFromTv.setText(getString(R.string.search_friend_by_contact));
        }

        // 标签
        List<String> userContactTagList = user.getUserContactTagList();
        if (!CollectionUtils.isEmpty(userContactTagList)) {
            mTagsRl.setVisibility(View.VISIBLE);
            mTagsTv.setText(String.join(",", userContactTagList));
        } else {
            mTagsRl.setVisibility(View.GONE);
        }

        // 描述
        if (!TextUtils.isEmpty(user.getUserContactDesc())) {
            mDescRl.setVisibility(View.VISIBLE);
            mDescTv.setText(user.getUserContactDesc());
        } else {
            mDescRl.setVisibility(View.GONE);
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
    }

    /**
     * 从服务器获取用户最新信息
     *
     * @param userId    用户ID
     * @param contactId 联系人用户ID
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
        User contact = mUserDao.getUserById(mContactId);
        loadData(contact);
        getContactFromServer(mUser.getUserId(), mContactId);
    }
}
