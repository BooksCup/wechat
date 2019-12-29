package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;

import com.bc.wechat.R;

/**
 * 设置备注和标签
 *
 * @author zhou
 */
public class SetRemarkAndTagActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);
    }

    public void back(View view) {
        finish();
    }
}
