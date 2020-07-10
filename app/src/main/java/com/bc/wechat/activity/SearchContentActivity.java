package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bc.wechat.R;

/**
 * 搜一搜
 *
 * @author zhou
 */
public class SearchContentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);

        initStatusBar();
    }

    public void back(View view) {
        finish();
    }
}
