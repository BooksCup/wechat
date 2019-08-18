package com.bc.wechat.activity;

import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends FragmentActivity implements View.OnClickListener {
    private VolleyUtil volleyUtil;
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
        volleyUtil = VolleyUtil.getInstance(this);
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

            case R.id.btn_register:
                String nickName = mNickNameEt.getText().toString();
                String phone = mPhoneEt.getText().toString();
                String password = mPasswordEt.getText().toString();
                if (!validatePassword(password)) {
                    Toast.makeText(RegisterActivity.this, "密码必须是8-16位的数字、字符组合(不能是纯数字)",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                register(nickName, phone, password);
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

    private void register(String nickName, String phone, String password) {
        String url = Constant.BASE_URL + "users";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("nickName", nickName);
        paramMap.put("phone", phone);
        paramMap.put("password", password);

        volleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(RegisterActivity.this,
                                "账号或密码错误，请重新填写。", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        });
    }

    /**
     * 密码规则校验
     * 规则: 密码必须是8-16位的数字、字符组合(不能是纯数字)
     *
     * @param password 密码
     * @return true: 校验成功  false: 校验失败
     */
    private static boolean validatePassword(String password) {
        String regEx = "^(?![^a-zA-Z]+$)(?!\\D+$).{8,16}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
