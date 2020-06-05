package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.UserDao;
import com.bc.wechat.entity.DeviceInfo;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.DeviceInfoUtil;
import com.bc.wechat.utils.MD5Util;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * 登录
 *
 * @author zhou
 */
public class PhoneLoginFinalActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PhoneLoginFinalActivity";
    private static final String LOGIN_TYPE_PASSWORD = "0";
    private static final String LOGIN_TYPE_VERIFY_CODE = "1";

    public static int sequence = 1;

    private EditText mPhoneEt;
    private EditText mPasswordEt;
    // 登录方式
    // 1.用短信验证码登录
    // 2.用密码登录
    private TextView mLoginTypeTv;

    private RelativeLayout mLoginByPasswordRl;
    private RelativeLayout mLoginByVerifyCodeRl;

    private EditText mVerifyCodeEt;

    private ImageView mClearPasswordIv;
    private ImageView mClearVerifyCodeIv;

    private Button mLoginBtn;
    private String mPhone;

    private VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    private UserDao mUserDao;

    private String mLoginType = LOGIN_TYPE_PASSWORD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login_final);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(PhoneLoginFinalActivity.this, R.color.bottom_text_color_normal);
        mPhone = getIntent().getStringExtra("phone");

        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(PhoneLoginFinalActivity.this);
        mUserDao = new UserDao();
        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mPhoneEt = findViewById(R.id.et_phone);
        mPasswordEt = findViewById(R.id.et_password);
        mLoginBtn = findViewById(R.id.btn_login);
        mLoginTypeTv = findViewById(R.id.tv_login_type);
        mLoginByPasswordRl = findViewById(R.id.rl_login_by_password);
        mLoginByVerifyCodeRl = findViewById(R.id.rl_login_by_verify_code);

        mVerifyCodeEt = findViewById(R.id.et_verify_code);

        mClearPasswordIv = findViewById(R.id.iv_clear_password);
        mClearVerifyCodeIv = findViewById(R.id.iv_clear_verify_code);

        mPhoneEt.setHint(mPhone);

        mPasswordEt.addTextChangedListener(new TextChange());
        mVerifyCodeEt.addTextChangedListener(new TextChange());

        mLoginBtn.setOnClickListener(this);
        mLoginTypeTv.setOnClickListener(this);
        mClearPasswordIv.setOnClickListener(this);
        mClearVerifyCodeIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                mDialog.setMessage(getString(R.string.logging_in));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                String password = mPasswordEt.getText().toString();
                login(mPhone, password);
                break;
            case R.id.tv_login_type:
                // 切换登录方式
                if (LOGIN_TYPE_PASSWORD.equals(mLoginType)) {
                    // 点击切换为验证码登录
                    mLoginByVerifyCodeRl.setVisibility(View.VISIBLE);
                    mLoginByPasswordRl.setVisibility(View.GONE);

                    mLoginType = LOGIN_TYPE_VERIFY_CODE;
                    mLoginTypeTv.setText("用密码登录");
                } else {
                    // 点击切换为密码登录
                    mLoginByPasswordRl.setVisibility(View.VISIBLE);
                    mLoginByVerifyCodeRl.setVisibility(View.GONE);

                    mLoginType = LOGIN_TYPE_PASSWORD;
                    mLoginTypeTv.setText("用短信验证码登录");
                }
                break;

            case R.id.iv_clear_password:
                mPasswordEt.setText("");
                break;
            case R.id.iv_clear_verify_code:
                mVerifyCodeEt.setText("");
                break;
        }
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // 登录方式
            if (LOGIN_TYPE_PASSWORD.equals(mLoginType)) {
                // 密码登录
                boolean passwordEtHasText = mPasswordEt.getText().length() > 0;
                if (passwordEtHasText) {
                    mClearPasswordIv.setVisibility(View.VISIBLE);
                } else {
                    mClearPasswordIv.setVisibility(View.GONE);
                }
            } else {
                // 验证码登录
                boolean verifyCodeEtHasText = mVerifyCodeEt.getText().length() > 0;
                if (verifyCodeEtHasText) {
                    mClearVerifyCodeIv.setVisibility(View.VISIBLE);
                } else {
                    mClearVerifyCodeIv.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * 登录
     *
     * @param phone    手机号
     * @param password 密码
     */
    private void login(String phone, String password) {
        DeviceInfo deviceInfo = DeviceInfoUtil.getInstance().getDeviceInfo(PhoneLoginFinalActivity.this);
        String url = Constant.BASE_URL + "users/login?phone=" + phone
                + "&password=" + MD5Util.encode(password, "utf8") + "&deviceInfo=" + JSON.toJSONString(deviceInfo);
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "server response: " + response);
                final User user = JSON.parseObject(response, User.class);
                Log.d(TAG, "userId:" + user.getUserId());

                // 登录极光im
                JMessageClient.login(user.getUserId(), user.getUserImPassword(), new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (responseCode == 0) {
                            // 极光im登录成功
                            // 登录成功后设置user和isLogin至sharedpreferences中
                            PreferencesUtil.getInstance().setUser(user);
                            PreferencesUtil.getInstance().setLogin(true);
                            // 注册jpush
                            JPushInterface.setAlias(PhoneLoginFinalActivity.this, sequence, user.getUserId());
                            List<User> friendList = user.getFriendList();
                            for (User userFriend : friendList) {
                                if (null != userFriend) {
                                    userFriend.setIsFriend(Constant.IS_FRIEND);
                                    mUserDao.saveUser(userFriend);
                                }
                            }
                            startActivity(new Intent(PhoneLoginFinalActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // 极光im登录失败
                            Toast.makeText(PhoneLoginFinalActivity.this,
                                    R.string.username_or_password_error, Toast.LENGTH_SHORT)
                                    .show();
                        }
                        // 上面都是耗时操作
                        mDialog.dismiss();
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(PhoneLoginFinalActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(PhoneLoginFinalActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(PhoneLoginFinalActivity.this,
                                R.string.username_or_password_error, Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
                mDialog.dismiss();
            }
        });
    }
}
