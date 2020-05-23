package com.bc.wechat.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendApplyDao;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友申请
 *
 * @author zhou
 */
public class NewFriendsAcceptActivity extends Activity {

    private TextView mNickNameTv;
    private SimpleDraweeView mAvatarSdv;
    private TextView mSignTv;
    private ImageView mSexIv;
    private RelativeLayout mAcceptRl;

    // 朋友圈图片
    private SimpleDraweeView mCirclePhoto1Sdv;
    private SimpleDraweeView mCirclePhoto2Sdv;
    private SimpleDraweeView mCirclePhoto3Sdv;
    private SimpleDraweeView mCirclePhoto4Sdv;

    private VolleyUtil mVolleyUtil;
    private FriendApplyDao mFriendApplyDao;
    private UserDao mUserDao;
    private FriendApply mFriendApply;

    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_accept);
        mDialog = new LoadingDialog(this);
        mVolleyUtil = VolleyUtil.getInstance(this);
        mFriendApplyDao = new FriendApplyDao();
        mUserDao = new UserDao();
        initView();
    }

    private void initView() {
        mNickNameTv = findViewById(R.id.tv_name);
        mAvatarSdv = findViewById(R.id.sdv_avatar);
        mSexIv = findViewById(R.id.iv_sex);
        mSignTv = findViewById(R.id.tv_sign);

        mCirclePhoto1Sdv = findViewById(R.id.sdv_circle_photo_1);
        mCirclePhoto2Sdv = findViewById(R.id.sdv_circle_photo_2);
        mCirclePhoto3Sdv = findViewById(R.id.sdv_circle_photo_3);
        mCirclePhoto4Sdv = findViewById(R.id.sdv_circle_photo_4);

        mAcceptRl = findViewById(R.id.rl_accept);

        final String applyId = getIntent().getStringExtra("applyId");
        mFriendApply = mFriendApplyDao.getFriendApplyByApplyId(applyId);

        mNickNameTv.setText(mFriendApply.getFromUserNickName());
        if (!TextUtils.isEmpty(mFriendApply.getFromUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(mFriendApply.getFromUserAvatar()));
        }
        if (Constant.USER_SEX_MALE.equals(mFriendApply.getFromUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(mFriendApply.getFromUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        mSignTv.setText(mFriendApply.getFromUserSign());

        if (!TextUtils.isEmpty(mFriendApply.getFromUserLastestCirclePhotos())) {
            // 渲染朋友圈图片
            List<String> circlePhotoList;
            try {
                circlePhotoList = JSON.parseArray(mFriendApply.getFromUserLastestCirclePhotos(), String.class);
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

        mAcceptRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage("正在处理...");
                mDialog.show();
                acceptFriendApply(applyId);
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void acceptFriendApply(String applyId) {
        String url = Constant.BASE_URL + "friendApplies";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("applyId", applyId);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                mFriendApply.setStatus(Constant.FRIEND_APPLY_STATUS_ACCEPT);
                FriendApply.save(mFriendApply);

                User user = new User();
                user.setUserId(mFriendApply.getFromUserId());
                user.setUserNickName(mFriendApply.getFromUserNickName());
                user.setUserAvatar(mFriendApply.getFromUserAvatar());
                user.setUserHeader(CommonUtil.setUserHeader(mFriendApply.getFromUserNickName()));
                user.setUserSex(mFriendApply.getFromUserSex());
                user.setIsFriend(Constant.IS_FRIEND);
                mUserDao.saveUser(user);

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(NewFriendsAcceptActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
