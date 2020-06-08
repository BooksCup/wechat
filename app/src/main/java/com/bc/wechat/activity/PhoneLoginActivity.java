package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.ValidateUtil;
import com.bc.wechat.widget.LoadingDialog;

/**
 * 登录
 *
 * @author zhou
 */
public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTitleTv;

    private EditText mPhoneEt;

    private EditText mAccountEt;
    private EditText mPasswordEt;

    private Button mNextBtn;
    LoadingDialog mDialog;

    private TextView mLoginTypeTv;
    private LinearLayout mLoginViaMobileNumberLl;
    private LinearLayout mLoginViaWechatIdOrEmailOrQqId;

    private String mLoginType = Constant.LOGIN_TYPE_PHONE_AND_PASSWORD;

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
        mTitleTv = findViewById(R.id.tv_title);

        mPhoneEt = findViewById(R.id.et_phone);

        mAccountEt = findViewById(R.id.et_account);
        mPasswordEt = findViewById(R.id.et_password);

        mNextBtn = findViewById(R.id.btn_next);
        mLoginTypeTv = findViewById(R.id.tv_login_type);
        mLoginViaMobileNumberLl = findViewById(R.id.ll_login_via_mobile_number);
        mLoginViaWechatIdOrEmailOrQqId = findViewById(R.id.ll_login_via_wechat_id_email_qq_id);

        mPhoneEt.addTextChangedListener(new TextChange());
        mAccountEt.addTextChangedListener(new TextChange());
        mPasswordEt.addTextChangedListener(new TextChange());

        mNextBtn.setOnClickListener(this);
        mLoginTypeTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_type:
                // 登录方式切换
                // 手机号登录 or 其他账号(微信号/QQ号/邮箱)登录
                if (Constant.LOGIN_TYPE_PHONE_AND_PASSWORD.equals(mLoginType)) {
                    // 当前登录方式手机号登录
                    // 切换为其他账号登录
                    mLoginViaWechatIdOrEmailOrQqId.setVisibility(View.VISIBLE);
                    mLoginViaMobileNumberLl.setVisibility(View.GONE);

                    mLoginType = Constant.LOGIN_TYPE_OTHER_ACCOUNTS_AND_PASSWORD;
                    mPhoneEt.setText("");

                    mTitleTv.setText(getString(R.string.login_via_wechat_id_email_qq_id));
                    mLoginTypeTv.setText(getString(R.string.use_mobile_number_to_login));

                    mNextBtn.setText(getString(R.string.login));
                } else {
                    // 当前登录方式其他账号登录
                    // 切换为手机号登录
                    mLoginViaMobileNumberLl.setVisibility(View.VISIBLE);
                    mLoginViaWechatIdOrEmailOrQqId.setVisibility(View.GONE);

                    mLoginType = Constant.LOGIN_TYPE_PHONE_AND_PASSWORD;
                    mAccountEt.setText("");
                    mPasswordEt.setText("");

                    mTitleTv.setText(getString(R.string.login_via_mobile_number));
                    mLoginTypeTv.setText(getString(R.string.use_wechat_id_email_qq_id_to_login));

                    mNextBtn.setText(getString(R.string.next));
                }
                break;

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
            if (Constant.LOGIN_TYPE_PHONE_AND_PASSWORD.equals(mLoginType)) {
                // 手机号登录
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
            } else {
                // 其他账号登录
                boolean accountEtHasText = mAccountEt.getText().length() > 0;
                boolean passwordEtHasText = mPasswordEt.getText().length() > 0;
                if (accountEtHasText && passwordEtHasText) {
                    // "登录"按钮可用
                    mNextBtn.setBackgroundResource(R.drawable.btn_login_next_enable);
                    mNextBtn.setTextColor(getColor(R.color.register_btn_text_enable));
                    mNextBtn.setEnabled(true);
                } else {
                    // "登录"按钮不可用
                    mNextBtn.setBackgroundResource(R.drawable.btn_login_next_disable);
                    mNextBtn.setTextColor(getColor(R.color.register_btn_text_disable));
                    mNextBtn.setEnabled(false);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
