package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 微信号修改
 *
 * @author zhou
 */
public class EditWeChatIdActivity extends BaseActivity2 {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_wechat_id)
    EditText mWeChatIdEt;

    @BindView(R.id.v_wechat_id)
    View mWechatIdView;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wechat_id);
        ButterKnife.bind(this);

        initStatusBar();

        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(EditWeChatIdActivity.this);
        initView();

        mSaveTv.setOnClickListener(view -> {
            mDialog.setMessage(getString(R.string.saving));
            mDialog.show();
            String userId = mUser.getUserId();
            String userWxId = mWeChatIdEt.getText().toString();
            updateUserWxId(userId, userWxId);
        });
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);

        mWeChatIdEt.setText(mUser.getUserWxId());
        // 光标移至最后
        CharSequence charSequence = mWeChatIdEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mWeChatIdEt.addTextChangedListener(new TextChange());

        mWeChatIdEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                mWechatIdView.setBackgroundColor(getColor(R.color.divider_green));
            } else {
                mWechatIdView.setBackgroundColor(getColor(R.color.divider_grey));
            }
        });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newWxId = mWeChatIdEt.getText().toString();
            String oldWxId = mUser.getUserWxId() == null ? "" : mUser.getUserWxId();
            // 是否填写
            boolean isWxIdHasText = mWeChatIdEt.length() > 0;
            // 是否修改
            boolean isWxIdChanged = !oldWxId.equals(newWxId);

            if (isWxIdHasText && isWxIdChanged) {
                // 可保存
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                // 不可保存
                mSaveTv.setTextColor(getColor(R.color.btn_text_default_color));
                mSaveTv.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void back(View view) {
        finish();
    }

    private void updateUserWxId(final String userId, final String userWxId) {
        String url = Constant.BASE_URL + "users/" + userId + "/userWxId";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userWxId", userWxId);

        mVolleyUtil.httpPutRequest(url, paramMap, response -> {
            mDialog.dismiss();
            setResult(RESULT_OK);
            mUser.setUserWxId(userWxId);
            mUser.setUserWxIdModifyFlag(Constant.USER_WX_ID_MODIFY_FLAG_TRUE);
            PreferencesUtil.getInstance().setUser(mUser);
            finish();
        }, volleyError -> {
            mDialog.dismiss();
            if (volleyError instanceof NetworkError) {
                Toast.makeText(EditWeChatIdActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                return;
            } else if (volleyError instanceof TimeoutError) {
                Toast.makeText(EditWeChatIdActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}
