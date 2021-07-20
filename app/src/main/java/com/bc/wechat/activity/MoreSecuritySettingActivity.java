package com.bc.wechat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.EditDialog;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更多安全设置
 *
 * @author zhou
 */
public class MoreSecuritySettingActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.rl_qq_id)
    RelativeLayout mQqIdRl;

    @BindView(R.id.rl_email)
    RelativeLayout mEmailRl;

    @BindView(R.id.tv_qq_is_linked)
    TextView mQqIdIsLinkedTv;

    @BindView(R.id.tv_email_is_linked)
    TextView mEmailIsLinkedTv;

    VolleyUtil mVolleyUtil;
    User mUser;
    LoadingDialog mDialog;

    @Override
    public int getContentView() {
        return R.layout.activity_more_security_setting;
    }

    @Override
    public void initView() {
        initStatusBar();
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

        getUserFromServer(mUser.getUserId());
        refreshLinkedStatus();
    }

    public void back(View view) {
        finish();
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

        String qqIsLinked = mUser.getUserIsQqLinked();
        String qqId = mUser.getUserQqId();
        if (Constant.QQ_ID_NOT_LINK.equals(qqIsLinked)) {
            mQqIdIsLinkedTv.setText("未绑定");
        } else if (Constant.QQ_ID_LINKED.equals(qqIsLinked)) {
            mQqIdIsLinkedTv.setText(qqId);
        } else {
            mQqIdIsLinkedTv.setText("未绑定");
        }
    }

    @OnClick({R.id.rl_qq_id, R.id.rl_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_qq_id:
                startActivity(new Intent(MoreSecuritySettingActivity.this, QqIdLinkBeginActivity.class));
                break;
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

        mVolleyUtil.httpPostRequest(url, paramMap, s -> {
            mDialog.dismiss();
            Toast.makeText(MoreSecuritySettingActivity.this,
                    "验证邮件已发送，请尽快登录邮箱验证", Toast.LENGTH_SHORT).show();
            mUser.setUserEmail(email);
            // 邮箱已绑定但未验证
            mUser.setUserIsEmailLinked(Constant.EMAIL_NOT_VERIFIED);
            PreferencesUtil.getInstance().setUser(mUser);
            refreshLinkedStatus();
        }, volleyError -> mDialog.dismiss());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserFromServer(mUser.getUserId());
    }

    /**
     * 从服务器获取用户最新信息
     *
     * @param userId 用户ID
     */
    public void getUserFromServer(final String userId) {
        String url = Constant.BASE_URL + "users/" + userId;

        mVolleyUtil.httpGetRequest(url, response -> {
            User user = JSON.parseObject(response, User.class);
            PreferencesUtil.getInstance().setUser(user);
            mUser = user;
            refreshLinkedStatus();
        }, volleyError -> {

        });
    }

}