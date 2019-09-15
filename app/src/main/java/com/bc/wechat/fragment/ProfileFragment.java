package com.bc.wechat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.activity.MyUserInfoActivity;
import com.bc.wechat.activity.SettingActivity;
import com.bc.wechat.utils.PreferencesUtil;

/**
 * tab - "我"
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mMyInfoRl;
    private RelativeLayout mSettingRl;
    private TextView mNickNameTv;
    private TextView mWxIdTv;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PreferencesUtil.getInstance().init(getActivity());
        initView();

    }

    private void initView() {
        mMyInfoRl = getView().findViewById(R.id.rl_myinfo);
        mSettingRl = getView().findViewById(R.id.rl_setting);
        mNickNameTv = getView().findViewById(R.id.tv_name);
        mWxIdTv = getView().findViewById(R.id.tv_wxid);

        mMyInfoRl.setOnClickListener(this);
        mSettingRl.setOnClickListener(this);

        mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
        mWxIdTv.setText("微信号:" + PreferencesUtil.getInstance().getUserWxId());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 个人页面
            case R.id.rl_myinfo:
                startActivity(new Intent(getActivity(), MyUserInfoActivity.class));
                break;
            // 设置页面
            case R.id.rl_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mNickNameTv.setText(PreferencesUtil.getInstance().getUserNickName());
        mWxIdTv.setText("微信号:" + PreferencesUtil.getInstance().getUserWxId());
    }
}
