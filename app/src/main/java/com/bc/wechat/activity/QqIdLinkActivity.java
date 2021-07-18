package com.bc.wechat.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 开始绑定QQ号
 *
 * @author zhou
 */
public class QqIdLinkActivity extends BaseActivity implements View.OnFocusChangeListener {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_qq_id)
    EditText mQqIdEt;

    @BindView(R.id.et_qq_password)
    EditText mQqPasswordEt;

    @BindView(R.id.vi_qq_id)
    View mQqIdVi;

    @BindView(R.id.vi_qq_password)
    View mQqPasswordVi;

    VolleyUtil mVolleyUtil;
    User mUser;
    LoadingDialog mDialog;

    @Override
    public int getContentView() {
        return R.layout.activity_link_qq_id;
    }

    @Override
    public void initView() {
        initStatusBar();
        StatusBarUtil.setStatusBarColor(QqIdLinkActivity.this, R.color.bottom_text_color_normal);
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {
        mQqIdEt.setOnFocusChangeListener(this);
        mQqPasswordEt.setOnFocusChangeListener(this);
    }

    @Override
    public void initData() {
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.tv_complete})
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

        mVolleyUtil.httpPostRequest(url, paramMap, s -> {
            mDialog.dismiss();
            showAlertDialog(QqIdLinkActivity.this, "提示", "已绑定。", "确定", true);
            mUser.setUserQqId(userQqId);
            mUser.setUserQqPassword(userQqPassword);
            mUser.setUserIsQqLinked(Constant.QQ_ID_LINKED);
            PreferencesUtil.getInstance().setUser(mUser);
        }, volleyError -> mDialog.dismiss());
    }

}