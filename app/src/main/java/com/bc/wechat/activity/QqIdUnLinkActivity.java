package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bc.wechat.R;
import com.bc.wechat.utils.StatusBarUtil;

/**
 * 解绑QQ号
 *
 * @author zhou
 */
public class QqIdUnLinkActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlink_qq_id);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(QqIdUnLinkActivity.this, R.color.bottom_text_color_normal);
    }

    public void back(View view) {
        finish();
    }
}
