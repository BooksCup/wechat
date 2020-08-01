package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 更改名字
 *
 * @author zhou
 */
public class EditNameActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.et_nick)
    EditText mNickNameEt;

    @BindView(R.id.tv_save)
    TextView mSaveTv;

    private VolleyUtil mVolleyUtil;
    LoadingDialog mDialog;
    User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        ButterKnife.bind(this);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(EditNameActivity.this, R.color.common_bg_light_grey);

        PreferencesUtil.getInstance().init(this);

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mDialog = new LoadingDialog(EditNameActivity.this);
        initView();

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage(getString(R.string.saving));
                mDialog.show();
                String userId = mUser.getUserId();
                String userNickName = mNickNameEt.getText().toString();
                updateUserNickName(userId, userNickName);
            }
        });
    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mNickNameEt.setText(mUser.getUserNickName());
        // 光标移至最后
        CharSequence charSequence = mNickNameEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mNickNameEt.addTextChangedListener(new TextChange());
    }

    public void back(View view) {
        finish();
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newNickName = mNickNameEt.getText().toString();
            String oldNickName = mUser.getUserNickName();
            // 是否填写
            boolean isNickNameHasText = newNickName.length() > 0;
            // 是否修改
            boolean isNickNameChanged = !oldNickName.equals(newNickName);

            if (isNickNameHasText && isNickNameChanged) {
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

    private void updateUserNickName(String userId, final String userNickName) {
        String url = Constant.BASE_URL + "users/" + userId + "/userNickName";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userNickName", userNickName);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mDialog.dismiss();
                setResult(RESULT_OK);
                mUser.setUserNickName(userNickName);
                PreferencesUtil.getInstance().setUser(mUser);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(EditNameActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(EditNameActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
