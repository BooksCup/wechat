package com.bc.wechat.activity;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendDao;
import com.bc.wechat.entity.Friend;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * 注册
 *
 * @author zhou
 */
public class RegisterActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    public static int sequence = 1;

    private VolleyUtil volleyUtil;
    TextView mAgreementTv;
    EditText mNickNameEt;
    EditText mPhoneEt;
    EditText mPasswordEt;

    ImageView mHidePasswordIv;
    ImageView mShowPasswordIv;

    Button mRegisterBtn;

    ProgressDialog dialog;
    FriendDao friendDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new ProgressDialog(RegisterActivity.this);
        friendDao = new FriendDao();
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
                dialog.setMessage(getString(R.string.registering));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();

                String nickName = mNickNameEt.getText().toString();
                String phone = mPhoneEt.getText().toString();
                String password = mPasswordEt.getText().toString();
                if (!validatePassword(password)) {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, R.string.password_rules,
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
            public void onResponse(String response) {
                dialog.dismiss();
                Log.d(TAG, "server response: " + response);
                final User user = JSON.parseObject(response, User.class);
                Log.d(TAG, "userId:" + user.getUserId());
                // 登录成功后设置user和isLogin至sharedpreferences中
                PreferencesUtil.getInstance().setUser(user);
                PreferencesUtil.getInstance().setLogin(true);
                // 注册jpush
                JPushInterface.setAlias(RegisterActivity.this, sequence, user.getUserId());
                List<User> friendList = user.getFriendList();
                if (null != friendList && friendList.size() > 0) {
                    for (User userFriend : friendList) {
                        if (null != userFriend) {
                            friendDao.saveFriendByUserInfo(userFriend);
                        }
                    }
                }

                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
                // 登录极光im
                JMessageClient.login(user.getUserId(), user.getUserImPassword(), new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        Log.d(TAG, "responseCode: " + responseCode + ", responseMessage: " + responseMessage);
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(RegisterActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(RegisterActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(RegisterActivity.this,
                                R.string.username_or_password_error, Toast.LENGTH_SHORT)
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
