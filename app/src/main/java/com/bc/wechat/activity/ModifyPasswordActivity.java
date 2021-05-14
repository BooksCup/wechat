package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.MD5Util;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.bc.wechat.widget.NoTitleAlertDialog;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bc.wechat.utils.ValidateUtil.validatePassword;

/**
 * 设置密码
 *
 * @author zhou
 */
public class ModifyPasswordActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_complete)
    TextView mCompleteTv;

    @BindView(R.id.et_wechat_id)
    EditText mWechatIdEt;

    @BindView(R.id.et_old_password)
    EditText mOldPasswordEt;

    @BindView(R.id.et_new_password)
    EditText mNewPasswordEt;

    @BindView(R.id.et_confirm_password)
    EditText mConfirmPasswordEt;

    VolleyUtil mVolleyUtil;
    User mUser;
    LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(ModifyPasswordActivity.this);

        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);

        mWechatIdEt.setText(mUser.getUserWxId());
    }

    @OnClick({R.id.tv_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_complete:
                submitForm();
                break;
        }
    }

    /**
     * 提交表单
     */
    private void submitForm() {

        String oldPassword = mOldPasswordEt.getText().toString();
        String newPassword = mNewPasswordEt.getText().toString();
        String confirmPassword = mConfirmPasswordEt.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            showAlertDialog(ModifyPasswordActivity.this, getString(R.string.tips),
                    getString(R.string.old_password_empty), getString(R.string.ok), true);
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            showAlertDialog(ModifyPasswordActivity.this, getString(R.string.tips),
                    getString(R.string.new_password_empty), getString(R.string.ok), true);
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            showAlertDialog(ModifyPasswordActivity.this, getString(R.string.tips),
                    getString(R.string.confirm_password_empty), getString(R.string.ok), true);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlertDialog(ModifyPasswordActivity.this, getString(R.string.tips),
                    getString(R.string.confirm_password_incorrect), getString(R.string.ok), true);
            return;
        }

        if (!validatePassword(newPassword)) {
            showAlertDialog(ModifyPasswordActivity.this, getString(R.string.tips),
                    getString(R.string.password_rules), getString(R.string.ok), true);
            return;
        }

        mDialog.setMessage(getString(R.string.sending));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        modifyPassword(mUser.getUserId(), oldPassword, newPassword);
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    private void modifyPassword(final String userId, final String oldPassword, final String newPassword) {
        String url = Constant.BASE_URL + "users/" + userId + "/userPassword";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("oldPassword", MD5Util.encode(oldPassword, "utf8"));
        paramMap.put("newPassword", MD5Util.encode(newPassword, "utf8"));

        mVolleyUtil.httpPutRequest(url, paramMap, response -> {
            mDialog.dismiss();
            final NoTitleAlertDialog mNoTitleAlertDialog = new NoTitleAlertDialog(ModifyPasswordActivity.this,
                    getString(R.string.modify_password_success_tips), getString(R.string.ok));
            mNoTitleAlertDialog.setOnDialogClickListener(() -> {
                mNoTitleAlertDialog.dismiss();
                ModifyPasswordActivity.this.finish();
            });
            // 点击空白处消失
            mNoTitleAlertDialog.setCancelable(false);
            mNoTitleAlertDialog.show();
        }, volleyError -> {
            mDialog.dismiss();
            if (volleyError instanceof NetworkError) {
                Toast.makeText(ModifyPasswordActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                return;
            } else if (volleyError instanceof TimeoutError) {
                Toast.makeText(ModifyPasswordActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                return;
            }

            int errorCode = volleyError.networkResponse.statusCode;
            switch (errorCode) {
                case 400:
                    showAlertDialog(ModifyPasswordActivity.this, getString(R.string.tips),
                            getString(R.string.old_password_incorrect), getString(R.string.ok), true);
                    break;
            }
        });
    }
}
