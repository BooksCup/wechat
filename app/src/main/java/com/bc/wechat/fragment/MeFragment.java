package com.bc.wechat.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.BigImageActivity;
import com.bc.wechat.activity.MyUserInfoActivity;
import com.bc.wechat.activity.SettingActivity;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.OssUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * tab - "我"
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout mMyInfoRl;
    private RelativeLayout mSettingRl;
    private SimpleDraweeView mAvatarSdv;
    private TextView mNickNameTv;
    private TextView mWxIdTv;
    User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PreferencesUtil.getInstance().init(getActivity());
        mUser = PreferencesUtil.getInstance().getUser();
        initView();

    }

    private void initView() {
        mMyInfoRl = getView().findViewById(R.id.rl_me);
        mSettingRl = getView().findViewById(R.id.rl_settings);
        mAvatarSdv = getView().findViewById(R.id.sdv_avatar);
        mNickNameTv = getView().findViewById(R.id.tv_name);
        mWxIdTv = getView().findViewById(R.id.tv_wx_id);

        mMyInfoRl.setOnClickListener(this);
        mSettingRl.setOnClickListener(this);
        mAvatarSdv.setOnClickListener(this);

        mNickNameTv.setText(mUser.getUserNickName());
        String userWxId = mUser.getUserWxId() == null ? "" : mUser.getUserWxId();
        mWxIdTv.setText("微信号:" + userWxId);
        String userAvatar = mUser.getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            String resizeAvatarUrl = OssUtil.resize(mUser.getUserAvatar());
            mAvatarSdv.setImageURI(Uri.parse(resizeAvatarUrl));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 个人页面
            case R.id.rl_me:
                startActivity(new Intent(getActivity(), MyUserInfoActivity.class));
                break;
            // 设置页面
            case R.id.rl_settings:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            // 头像点击放大
            case R.id.sdv_avatar:
                Intent intent = new Intent(getActivity(), BigImageActivity.class);
                intent.putExtra("imgUrl", mUser.getUserAvatar());
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = PreferencesUtil.getInstance().getUser();
        mNickNameTv.setText(mUser.getUserNickName());
        String userWxId = mUser.getUserWxId() == null ? "" : mUser.getUserWxId();
        mWxIdTv.setText("微信号:" + userWxId);
        if (!TextUtils.isEmpty(mUser.getUserAvatar())) {
            String resizeAvatarUrl = OssUtil.resize(mUser.getUserAvatar());
            mAvatarSdv.setImageURI(Uri.parse(resizeAvatarUrl));
        }
    }
}
