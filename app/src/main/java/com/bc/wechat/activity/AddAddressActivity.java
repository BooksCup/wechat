package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import com.bc.wechat.R;

public class AddAddressActivity extends FragmentActivity implements View.OnClickListener {

    private EditText mAddressInfoEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mAddressInfoEt = findViewById(R.id.et_address_info);

        mAddressInfoEt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_address_info:
                startActivity(new Intent(AddAddressActivity.this, PickProvinceActivity.class));
                break;
        }
    }
}
