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
import com.bc.wechat.utils.ValidateUtil;
import com.bc.wechat.widget.LoadingDialog;

/**
 * 登录
 *
 * @author zhou
 */
public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mPhoneEt;
    private Button mNextBtn;
    LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(PhoneLoginActivity.this, R.color.bottom_text_color_normal);
        initView();
        mDialog = new LoadingDialog(PhoneLoginActivity.this);
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
                mDialog.setMessage(getString(R.string.please_wait));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                String phone = mPhoneEt.getText().toString();

                // 是否有效手机号
                boolean isValidChinesePhone = ValidateUtil.isValidChinesePhone(phone);
                if (isValidChinesePhone) {
                    mDialog.dismiss();
                    // 有效
                    Intent intent = new Intent(PhoneLoginActivity.this, PhoneLoginFinalActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else {
                    mDialog.dismiss();
                    // 无效
                    showWarningDialog(PhoneLoginActivity.this, "手机号码错误",
                            "你输入的是一个无效的手机号码",
                            "确定");
                }
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
                mNextBtn.setBackgroundResource(R.drawable.btn_login_next_enable);
                mNextBtn.setTextColor(getColor(R.color.register_btn_text_enable));
                mNextBtn.setEnabled(true);
            } else {
                // "下一步"按钮不可用
                mNextBtn.setBackgroundResource(R.drawable.btn_login_next_disable);
                mNextBtn.setTextColor(getColor(R.color.register_btn_text_disable));
                mNextBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
