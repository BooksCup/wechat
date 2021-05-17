package com.bc.wechat.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.BigImageActivity;
import com.bc.wechat.activity.MyProfileActivity;
import com.bc.wechat.activity.SettingActivity;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.OssUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * tab - "我"
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.sdv_avatar)
    SimpleDraweeView mAvatarSdv;

    @BindView(R.id.tv_name)
    TextView mNickNameTv;

    @BindView(R.id.tv_wx_id)
    TextView mWxIdTv;

    User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = PreferencesUtil.getInstance().getUser();
        initView();

    }

    private void initView() {
        mNickNameTv.setText(mUser.getUserNickName());
        String userWxId = mUser.getUserWxId() == null ? "" : mUser.getUserWxId();
        mWxIdTv.setText("微信号:" + userWxId);
        String userAvatar = mUser.getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            String resizeAvatarUrl = OssUtil.resize(mUser.getUserAvatar());
            mAvatarSdv.setImageURI(Uri.parse(resizeAvatarUrl));
        }
    }

    @OnClick({R.id.rl_me, R.id.rl_settings, R.id.sdv_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            // 个人页面
            case R.id.rl_me:
                startActivity(new Intent(getActivity(), MyProfileActivity.class));
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
