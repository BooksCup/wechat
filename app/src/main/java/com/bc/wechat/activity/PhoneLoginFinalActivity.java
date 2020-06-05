package com.bc.wechat.activity;

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
public class PhoneLoginFinalActivity extends BaseActivity {

    private EditText mPhoneEt;
    private EditText mPasswordEt;
    private Button mLoginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login_final);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(PhoneLoginFinalActivity.this, R.color.bottom_text_color_normal);
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mPhoneEt = findViewById(R.id.et_phone);
        mPasswordEt = findViewById(R.id.et_password);

        mLoginBtn = findViewById(R.id.btn_login);

        mPhoneEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean phoneEtHasText = mPhoneEt.getText().length() > 0;
            boolean passwordEtHasText = mPasswordEt.getText().length() > 0;
            if (phoneEtHasText && passwordEtHasText) {
                // "登录"按钮可用
                mLoginBtn.setBackgroundColor(getColor(R.color.register_btn_bg_enable));
                mLoginBtn.setTextColor(getColor(R.color.register_btn_text_enable));
                mLoginBtn.setEnabled(true);
            } else {
                // "登录"按钮不可用
                mLoginBtn.setBackgroundColor(getColor(R.color.register_btn_bg_disable));
                mLoginBtn.setTextColor(getColor(R.color.register_btn_text_disable));
                mLoginBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
