package com.bc.wechat.activity;

import android.widget.TextView;

import com.bc.wechat.R;

import butterknife.BindView;

/**
 * 小程序
 *
 * @author zhou
 */
public class MpActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @Override
    public int getContentView() {
        return R.layout.activity_mp;
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
