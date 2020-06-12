package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.bc.wechat.widget.ConfirmDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RelativeLayout mLoginByVerificationCodeRl;

    private EditText mVerificationCodeEt;

    private ImageView mClearPasswordIv;
    private ImageView mClearVerificationCodeIv;

    private Button mLoginBtn;
    private String mPhone;

    // 获取验证码
    private TextView mGetVerificationCodeTv;
    // 倒计时
    private TextView mCountDownTv;

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
        mLoginByVerificationCodeRl = findViewById(R.id.rl_login_by_verification_code);

        mVerificationCodeEt = findViewById(R.id.et_verification_code);

        mClearPasswordIv = findViewById(R.id.iv_clear_password);
        mClearVerificationCodeIv = findViewById(R.id.iv_clear_verification_code);

        mPhoneEt.setHint(mPhone);

        mGetVerificationCodeTv = findViewById(R.id.tv_get_verification_code);
        mCountDownTv = findViewById(R.id.tv_count_down);

        mPasswordEt.addTextChangedListener(new TextChange());
        mVerificationCodeEt.addTextChangedListener(new TextChange());

        mLoginBtn.setOnClickListener(this);
        mLoginTypeTv.setOnClickListener(this);
        mClearPasswordIv.setOnClickListener(this);
        mClearVerificationCodeIv.setOnClickListener(this);
        mGetVerificationCodeTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                mDialog.setMessage(getString(R.string.logging_in));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                if (LOGIN_TYPE_PASSWORD.equals(mLoginType)) {
                    String password = mPasswordEt.getText().toString();
                    login(mLoginType, mPhone, password, "");
                } else {
                    String verificationCode = mVerificationCodeEt.getText().toString();
                    login(mLoginType, mPhone, "", verificationCode);
                }
                break;
            case R.id.tv_login_type:
                // 切换登录方式
                if (LOGIN_TYPE_PASSWORD.equals(mLoginType)) {
                    // 点击切换为验证码登录
                    mLoginByVerificationCodeRl.setVisibility(View.VISIBLE);
                    mLoginByPasswordRl.setVisibility(View.GONE);

                    mLoginType = LOGIN_TYPE_VERIFY_CODE;
                    mLoginTypeTv.setText("用密码登录");
                } else {
                    // 点击切换为密码登录
                    mLoginByPasswordRl.setVisibility(View.VISIBLE);
                    mLoginByVerificationCodeRl.setVisibility(View.GONE);

                    mLoginType = LOGIN_TYPE_PASSWORD;
                    mLoginTypeTv.setText("用短信验证码登录");
                }
                break;

            case R.id.iv_clear_password:
                mPasswordEt.setText("");
                break;
            case R.id.iv_clear_verification_code:
                mVerificationCodeEt.setText("");
                break;

            case R.id.tv_get_verification_code:
                final ConfirmDialog mConfirmDialog = new ConfirmDialog(PhoneLoginFinalActivity.this, "确认手机号码",
                        "我们将发送验证码短信到这个号码：" + mPhone,
                        "确定", "取消", getColor(R.color.navy_blue));
                final VerificationCodeCountDownTimer verificationCodeCountDownTimer =
                        new VerificationCodeCountDownTimer(60000, 1000);
                mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        mConfirmDialog.dismiss();
                        verificationCodeCountDownTimer.start();
                        // 获取验证码
                        getVerificationCode(mPhone);
                    }

                    @Override
                    public void onCancelClick() {
                        mConfirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                mConfirmDialog.setCancelable(true);
                mConfirmDialog.show();
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
                boolean verifyCodeEtHasText = mVerificationCodeEt.getText().length() > 0;
                if (verifyCodeEtHasText) {
                    mClearVerificationCodeIv.setVisibility(View.VISIBLE);
                } else {
                    mClearVerificationCodeIv.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    //倒计时函数
    private class VerificationCodeCountDownTimer extends CountDownTimer {

        public VerificationCodeCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // 计时过程
        @Override
        public void onTick(long l) {
            // 防止计时过程中重复点击
            mGetVerificationCodeTv.setClickable(false);
            mGetVerificationCodeTv.setVisibility(View.GONE);
            mCountDownTv.setVisibility(View.VISIBLE);
            mCountDownTv.setText(l / 1000 + "秒后...");
        }

        // 计时完毕的方法
        @Override
        public void onFinish() {
            // 设置可点击
            mGetVerificationCodeTv.setClickable(true);
            mGetVerificationCodeTv.setVisibility(View.VISIBLE);
            mCountDownTv.setVisibility(View.GONE);
        }
    }

    /**
     * 登录
     *
     * @param phone    手机号
     * @param password 密码
     */
    private void login(String loginType, String phone, String password, String verificationCode) {
        DeviceInfo deviceInfo = DeviceInfoUtil.getInstance().getDeviceInfo(PhoneLoginFinalActivity.this);
        String url;
        if (Constant.LOGIN_TYPE_PHONE_AND_PASSWORD.equals(loginType)) {
            url = Constant.BASE_URL + "users/login?phone=" + phone + "&loginType=" + loginType
                    + "&password=" + MD5Util.encode(password, "utf8") + "&deviceInfo=" + JSON.toJSONString(deviceInfo);
        } else if (Constant.LOGIN_TYPE_PHONE_AND_VERIFICATION_CODE.equals(loginType)) {
            url = Constant.BASE_URL + "users/login?phone=" + phone + "&loginType=" + loginType
                    + "&verificationCode=" + verificationCode + "&deviceInfo=" + JSON.toJSONString(deviceInfo);
        } else {
            url = Constant.BASE_URL + "users/login?phone=" + phone + "&loginType=" + loginType
                    + "&password=" + MD5Util.encode(password, "utf8") + "&deviceInfo=" + JSON.toJSONString(deviceInfo);
        }
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
                    mDialog.dismiss();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(PhoneLoginFinalActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
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

    /**
     * 获取验证码
     *
     * @param phone 手机号
     */
    private void getVerificationCode(final String phone) {
        String url = Constant.BASE_URL + "verificationCode";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("serviceType", Constant.VERIFICATION_CODE_SERVICE_TYPE_LOGIN);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
            }
        });
    }
}
