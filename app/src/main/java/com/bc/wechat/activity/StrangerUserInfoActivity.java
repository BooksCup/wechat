package com.bc.wechat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.Nullable;


/**
 * 陌生人用户详情页
 *
 * @author zhou
 */
public class StrangerUserInfoActivity extends BaseActivity {
    private TextView mTitleTv;
    private SimpleDraweeView mAvatarSdv;
    private TextView mNameTv;
    private TextView mNickNameTv;
    private ImageView mSexIv;
    private TextView mSignTv;
    private TextView mSourceTv;
    private TextView mDescTv;

    private RelativeLayout mSignRl;
    private RelativeLayout mSetRemarkAndTagRl;
    private RelativeLayout mDescRl;
    private LinearLayout mNickNameLl;
    private RelativeLayout mOperateRl;

    private UserDao mUserDao;
    private VolleyUtil mVolleyUtil;
    private User mUser;
    private String mUserId;
    private String mSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stranger_user_info);
        initStatusBar();

        mUserDao = new UserDao();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mNameTv = findViewById(R.id.tv_name);
        mNickNameTv = findViewById(R.id.tv_nick_name);
        mSexIv = findViewById(R.id.iv_sex);
        mSignTv = findViewById(R.id.tv_sign);
        mSourceTv = findViewById(R.id.tv_source);
        mDescTv = findViewById(R.id.tv_desc);

        mSignRl = findViewById(R.id.rl_sign);
        mSetRemarkAndTagRl = findViewById(R.id.rl_set_remark_and_tag);
        mDescRl = findViewById(R.id.rl_desc);
        mNickNameLl = findViewById(R.id.ll_nick_name);
        mOperateRl = findViewById(R.id.rl_operate);

        mUserId = getIntent().getStringExtra("userId");
        mSource = getIntent().getStringExtra("source");
        final User user = mUserDao.getUserById(mUserId);
        loadData(user);

        getFriendFromServer(mUser.getUserId(), mUserId);

        mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrangerUserInfoActivity.this, BigImageActivity.class);
                intent.putExtra("imgUrl", user.getUserAvatar());
                startActivity(intent);
            }
        });

        mSetRemarkAndTagRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrangerUserInfoActivity.this, EditContactActivity.class);
                intent.putExtra("userId", mUserId);
                intent.putExtra("isFriend", Constant.IS_NOT_FRIEND);
                startActivity(intent);
            }
        });

        mDescRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrangerUserInfoActivity.this, EditContactActivity.class);
                intent.putExtra("userId", mUserId);
                intent.putExtra("isFriend", Constant.IS_NOT_FRIEND);
                startActivity(intent);
            }
        });

        mOperateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StrangerUserInfoActivity.this, NewFriendsApplyConfirmActivity.class);
                intent.putExtra("friendId", mUserId);
                startActivity(intent);
            }
        });
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
            mSignRl.setVisibility(View.GONE);
        } else {
            mSignTv.setText(user.getUserSign());
        }

        if (Constant.FRIENDS_SOURCE_BY_PHONE.equals(mSource)) {
            mSourceTv.setText(getString(R.string.search_friend_by_phone));
        } else if (Constant.FRIENDS_SOURCE_BY_WX_ID.equals(mSource)) {
            mSourceTv.setText(getString(R.string.search_friend_by_wx_id));
        } else if (Constant.FRIENDS_SOURCE_BY_PEOPLE_NEARBY.equals(mSource)) {
            mSourceTv.setText(getString(R.string.search_friend_by_people_nearby));
        } else if (Constant.FRIENDS_SOURCE_BY_CONTACT.equals(mSource)) {
            mSourceTv.setText(getString(R.string.search_friend_by_contact));
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

    @Override
    protected void onResume() {
        super.onResume();
        User user = mUserDao.getUserById(mUserId);
        loadData(user);
        getFriendFromServer(mUser.getUserId(), mUserId);
    }
}
