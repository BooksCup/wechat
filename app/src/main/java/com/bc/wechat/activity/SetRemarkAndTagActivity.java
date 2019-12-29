package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bc.wechat.R;

/**
 * 设置备注和标签
 *
 * @author zhou
 */
public class SetRemarkAndTagActivity extends BaseActivity {

    private EditText mRemarkEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_remark_and_tag);
        initView();
    }

    private void initView() {
        String userNickName = getIntent().getStringExtra("userNickName");

        mRemarkEt = findViewById(R.id.et_remark);
        mRemarkEt.setText(userNickName);
    }

    public void back(View view) {
        finish();
    }
}
