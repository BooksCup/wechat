package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bc.wechat.R;
import com.bc.wechat.utils.StatusBarUtil;

/**
 * 登录
 *
 * @author zhou
 */
public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mPhoneEt;
    private Button mNextBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(PhoneLoginActivity.this, R.color.bottom_text_color_normal);
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mPhoneEt = findViewById(R.id.et_phone);
        mNextBtn = findViewById(R.id.btn_next);

        mPhoneEt.addTextChangedListener(new TextChange());
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                String phone = mPhoneEt.getText().toString();
                Intent intent = new Intent(PhoneLoginActivity.this, PhoneLoginFinalActivity.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
                break;
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean phoneEtHasText = mPhoneEt.getText().length() > 0;
            if (phoneEtHasText) {
                // "下一步"按钮可用
                mNextBtn.setBackgroundColor(getColor(R.color.register_btn_bg_enable));
                mNextBtn.setTextColor(getColor(R.color.register_btn_text_enable));
                mNextBtn.setEnabled(true);
            } else {
                // "下一步"按钮不可用
                mNextBtn.setBackgroundColor(getColor(R.color.register_btn_bg_disable));
                mNextBtn.setTextColor(getColor(R.color.register_btn_text_disable));
                mNextBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
