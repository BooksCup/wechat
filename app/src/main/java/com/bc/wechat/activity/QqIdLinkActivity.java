package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * 开始绑定QQ号
 *
 * @author zhou
 */
public class QqIdLinkActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText mQqIdEt;
    private EditText mQqPasswordEt;
    private TextView mCompleteTv;

    private View mQqIdVi;
    private View mQqPasswordVi;

    private VolleyUtil mVolleyUtil;
    private User mUser;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_qq_id);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(QqIdLinkActivity.this, R.color.bottom_text_color_normal);

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

        initView();
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mQqIdEt = findViewById(R.id.et_qq_id);
        mQqPasswordEt = findViewById(R.id.et_qq_password);
        mCompleteTv = findViewById(R.id.tv_complete);

        mQqIdVi = findViewById(R.id.vi_qq_id);
        mQqPasswordVi = findViewById(R.id.vi_qq_password);
        mCompleteTv.setOnClickListener(this);

        mQqIdEt.setOnFocusChangeListener(this);
        mQqPasswordEt.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_complete:
                final String qqId = mQqIdEt.getText().toString();
                final String qqPassword = mQqPasswordEt.getText().toString();
                linkQqId(mUser.getUserId(), qqId, qqPassword);
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.et_qq_id:
                if (hasFocus) {
                    mQqIdVi.setBackgroundColor(getColor(R.color.wechat_btn_green));
                } else {
                    mQqIdVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
            case R.id.et_qq_password:
                if (hasFocus) {
                    mQqPasswordVi.setBackgroundColor(getColor(R.color.wechat_btn_green));
                } else {
                    mQqPasswordVi.setBackgroundColor(getColor(R.color.picker_list_divider));
                }
                break;
        }
    }

    /**
     * 绑定QQ号
     *
     * @param userId         用户ID
     * @param userQqId       用户QQ号
     * @param userQqPassword 用户QQ密码
     */
    private void linkQqId(String userId, final String userQqId, final String userQqPassword) {
        mDialog = new LoadingDialog(QqIdLinkActivity.this);
        mDialog.setMessage(getString(R.string.linking));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String url = Constant.BASE_URL + "users/" + userId + "/qqIdLink";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("userQqId", userQqId);
        paramMap.put("userQqPassword", userQqPassword);

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mDialog.dismiss();
                showAlertDialog(QqIdLinkActivity.this, "提示", "已绑定。", "确定", true);
                mUser.setUserQqId(userQqId);
                mUser.setUserQqPassword(userQqPassword);
                mUser.setUserIsQqLinked(Constant.QQ_ID_LINKED);
                PreferencesUtil.getInstance().setUser(mUser);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
            }
        });
    }
}
