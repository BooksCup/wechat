package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.EditDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 更多安全设置
 *
 * @author zhou
 */
public class MoreSecuritySettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mEmailRl;
    private TextView mEmailIsLinkedTv;

    private VolleyUtil mVolleyUtil;
    private User mUser;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_security_setting);
        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mEmailRl = findViewById(R.id.rl_email);
        mEmailIsLinkedTv = findViewById(R.id.tv_email_is_linked);
        refreshLinkedStatus();

        mEmailRl.setOnClickListener(this);
    }

    /**
     * 刷新绑定状态
     */
    private void refreshLinkedStatus() {
        String emailIsLinked = mUser.getUserIsEmailLinked();
        if (Constant.EMAIL_NOT_LINK.equals(emailIsLinked)) {
            mEmailIsLinkedTv.setText(getString(R.string.not_linked));
        } else if (Constant.EMAIL_NOT_VERIFIED.equals(emailIsLinked)) {
            mEmailIsLinkedTv.setText(getString(R.string.not_verified));
        } else if (Constant.EMAIL_VERIFIED.equals(emailIsLinked)) {
            mEmailIsLinkedTv.setText(getString(R.string.verified));
        } else {
            mEmailIsLinkedTv.setText(getString(R.string.not_linked));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_email:
                // 未绑定出弹窗
                // 绑定未验证或者已绑定都是进邮件认证页面
                if (Constant.EMAIL_NOT_LINK.equals(mUser.getUserIsEmailLinked())) {
                    openEditEmailDialog();
                } else {
                    startActivity(new Intent(MoreSecuritySettingActivity.this, EmailLinkActivity.class));
                }
                break;
        }
    }

    private void openEditEmailDialog() {
        final EditDialog mEditDialog = new EditDialog(MoreSecuritySettingActivity.this, getString(R.string.edit_email),
                "", getString(R.string.edit_email_tips),
                getString(R.string.ok), getString(R.string.cancel));
        mEditDialog.setOnDialogClickListener(new EditDialog.OnDialogClickListener() {
            @Override
            public void onOkClick() {
                mEditDialog.dismiss();
                String email = mEditDialog.getContent();

                mDialog.setMessage(getString(R.string.linking));
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                linkEmail(mUser.getUserId(), email, mUser.getUserWxId());
            }

            @Override
            public void onCancelClick() {
                mEditDialog.dismiss();
            }
        });

        // 点击空白处消失
        mEditDialog.setCancelable(false);
        mEditDialog.show();
    }

    private void linkEmail(String userId, final String email, final String wechatId) {
        String url = Constant.BASE_URL + "email/link";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("to", email);
        paramMap.put("wechatId", wechatId);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mDialog.dismiss();
                Toast.makeText(MoreSecuritySettingActivity.this,
                        "验证邮件已发送，请尽快登录邮箱验证", Toast.LENGTH_SHORT).show();
                mUser.setUserEmail(email);
                // 邮箱已绑定但未验证
                mUser.setUserIsEmailLinked(Constant.EMAIL_NOT_VERIFIED);
                PreferencesUtil.getInstance().setUser(mUser);
                refreshLinkedStatus();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
            }
        });
    }
}
