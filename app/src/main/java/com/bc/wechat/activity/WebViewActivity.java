package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.bc.wechat.R;

public class WebViewActivity extends Activity {
    private WebView mContentWv;
    public static final String RESULT = "result_data";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initViews();
    }

    private void initViews() {
        mContentWv = findViewById(R.id.wv_content);
        Intent intent = getIntent();
        String result = intent.getStringExtra(RESULT);
        mContentWv.loadUrl(result);
    }
}
