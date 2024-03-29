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
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

/**
 * 二维码名片
 *
 * @author zhou
 */
public class MyQrCodeActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.tv_nick_name)
    TextView mNickNameTv;

    @BindView(R.id.iv_sex)
    ImageView mSexIv;

    @BindView(R.id.tv_region)
    TextView mRegionTv;

    @BindView(R.id.sdv_qr_code)
    SimpleDraweeView mQrCodeSdv;

    User mUser;

    @Override
    public int getContentView() {
        return R.layout.activity_my_qr_code;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
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
        mNickNameTv.setText(mUser.getUserNickName());

        if (Constant.USER_SEX_MALE.equals(mUser.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_male);
        } else if (Constant.USER_SEX_FEMALE.equals(mUser.getUserSex())) {
            mSexIv.setImageResource(R.mipmap.icon_sex_female);
        } else {
            mSexIv.setVisibility(View.GONE);
        }

        mRegionTv.setText(mUser.getUserRegion());

        if (!TextUtils.isEmpty(mUser.getUserQrCode())) {
            mQrCodeSdv.setImageURI(Uri.parse(mUser.getUserQrCode()));
        }
    }

    public void back(View view) {
        finish();
    }

}