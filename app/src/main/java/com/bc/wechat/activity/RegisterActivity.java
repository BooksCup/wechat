package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bc.wechat.R;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener {
    TextView mAgreementTv;
    EditText mNickNameEt;
    EditText mPhoneEt;
    EditText mPasswordEt;

    ImageView mHidePasswordIv;
    ImageView mShowPasswordIv;

    Button mRegisterBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mNickNameEt = findViewById(R.id.et_nick_name);
        mPhoneEt = findViewById(R.id.et_phone);
        mPasswordEt = findViewById(R.id.et_password);
        mAgreementTv = findViewById(R.id.tv_agreement);

        mHidePasswordIv = findViewById(R.id.iv_password_hide);
        mShowPasswordIv = findViewById(R.id.iv_password_show);

        mRegisterBtn = findViewById(R.id.btn_register);

        String agreement = "<font color=" + "\"" + "#AAAAAA" + "\">" + "点击上面的"
                + "\"" + "注册" + "\"" + "按钮,即表示你同意" + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + "《腾讯微信软件许可及服务协议》"
                + "</font>" + "</u>";
        mAgreementTv.setText(Html.fromHtml(agreement));

        mNickNameEt.addTextChangedListener(new TextChange());
        mPhoneEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());

        mHidePasswordIv.setOnClickListener(this);
        mShowPasswordIv.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_password_hide:
                // 点击显示密码
                mHidePasswordIv.setVisibility(View.GONE);
                mShowPasswordIv.setVisibility(View.VISIBLE);

                mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // 光标移至最后
                CharSequence charSequence = mPasswordEt.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
            case R.id.iv_password_show:
                // 点击隐藏密码
                mHidePasswordIv.setVisibility(View.VISIBLE);
                mShowPasswordIv.setVisibility(View.GONE);

                mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                // 光标移至最后
                charSequence = mPasswordEt.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
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
            boolean nickNameHasText = mNickNameEt.getText().toString().length() > 0;
            boolean phoneHasText = mPhoneEt.getText().toString().length() > 0;
            boolean passwordHasText = mPasswordEt.getText().toString().length() > 0;
            if (nickNameHasText && phoneHasText && passwordHasText) {
                mRegisterBtn.setTextColor(0xFFFFFFFF);
                mRegisterBtn.setEnabled(true);
            } else {
                mRegisterBtn.setTextColor(0xFFD0EFC6);
                mRegisterBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
