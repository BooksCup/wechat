package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bc.wechat.R;

public class UpdateNickNameActivity extends FragmentActivity {
    private EditText mNickNameEt;
    private TextView mSaveTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick_name);
        initView();

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void initView() {
        mNickNameEt = findViewById(R.id.et_nick);
        mSaveTv = findViewById(R.id.tv_save);
    }

    public void back(View view) {
        finish();
    }
}