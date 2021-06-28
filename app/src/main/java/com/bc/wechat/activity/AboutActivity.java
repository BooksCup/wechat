package com.bc.wechat.activity;

import android.view.View;

import com.bc.wechat.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import butterknife.OnClick;

/**
 * 关于微信
 *
 * @author zhou
 */
public class AboutActivity extends BaseActivity {

    @Override
    public int getContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

}