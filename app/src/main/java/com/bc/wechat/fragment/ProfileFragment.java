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

import com.bc.wechat.R;
import com.bc.wechat.activity.MyUserInfoActivity;
import com.bc.wechat.activity.SettingActivity;

/**
 * tab - "我"
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mMyInfoRl;
    private RelativeLayout mSettingRl;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    private void initView() {
        mMyInfoRl = getView().findViewById(R.id.rl_myinfo);
        mSettingRl = getView().findViewById(R.id.rl_setting);

        mMyInfoRl.setOnClickListener(this);
        mSettingRl.setOnClickListener(this);
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
}
