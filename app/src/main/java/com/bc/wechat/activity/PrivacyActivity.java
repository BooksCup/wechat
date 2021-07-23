package com.bc.wechat.activity;

import android.widget.TextView;

import com.bc.wechat.R;

import butterknife.BindView;

/**
 * "设置" - "隐私"
 *
 * @author zhou
 */
public class PrivacyActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @Override
    public int getContentView() {
        return R.layout.activity_privacy;
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

    }

}