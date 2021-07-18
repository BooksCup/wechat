package com.bc.wechat.activity;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

/**
 * 自己用户详情页
 *
 * @author zhou
 */
public class UserInfoMyActivity extends BaseActivity {

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.tv_name)
    TextView mNameTv;

    @BindView(R.id.iv_sex)
    ImageView mSexIv;

    @BindView(R.id.tv_wx_id)
    TextView mWxIdTv;

    User mUser;

    @Override
    public int getContentView() {
        return R.layout.activity_user_info_my;
    }

    @Override
    public void initView() {
        initStatusBar();
        StatusBarUtil.setStatusBarColor(UserInfoMyActivity.this, R.color.status_bar_color_white);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        mUser = PreferencesUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(mUser.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(mUser.getUserAvatar()));
        }
        mNameTv.setText(mUser.getUserNickName());
        if (Constant.USER_SEX_MALE.equals(mUser.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(mUser.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mUser.getUserWxId())) {
            mWxIdTv.setText("微信号：" + mUser.getUserWxId());
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}