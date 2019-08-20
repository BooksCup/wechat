package com.bc.wechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private VolleyUtil volleyUtil;

    EditText mPhoneEt;
    EditText mPasswordEt;
    Button mLoginBtn;
    TextView mRegisterTv;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PreferencesUtil.getInstance().init(this);
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new ProgressDialog(LoginActivity.this);
        initView();
    }

    private void initView() {
        mPhoneEt = findViewById(R.id.et_user_phone);
        mPasswordEt = findViewById(R.id.et_password);
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterTv = findViewById(R.id.tv_register);

        mPhoneEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());
        mLoginBtn.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                dialog.setMessage(getString(R.string.logging_in));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                final String phone = mPhoneEt.getText().toString().trim();
                final String password = mPasswordEt.getText().toString().trim();
                login(phone, password);
                break;
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
            boolean passwordEtHasText = mPasswordEt.getText().length() > 0;
            if (phoneEtHasText && passwordEtHasText) {
                mLoginBtn.setTextColor(0xFFFFFFFF);
                mLoginBtn.setEnabled(true);
            } else {
                mLoginBtn.setTextColor(0xFFD0EFC6);
                mLoginBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void login(String phone, String password) {
        String url = Constant.BASE_URL + "users/login?phone=" + phone + "&password=" + password;
        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server response: " + response);
                User user = JSON.parseObject(response, User.class);
                Log.d(TAG, "userId:" + user.getUserId());
                // 登录成功后设置userId和isLogin至sharedpreferences中
                PreferencesUtil.getInstance().setUserId(user.getUserId());
                PreferencesUtil.getInstance().setLogin(true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(LoginActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(LoginActivity.this,
                                R.string.username_or_password_error, Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
                dialog.dismiss();
            }
        });
    }

}
