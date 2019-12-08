package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;

import com.bc.wechat.R;

public class QrCodeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
    }

    public void back(View view) {
        finish();
    }
}
