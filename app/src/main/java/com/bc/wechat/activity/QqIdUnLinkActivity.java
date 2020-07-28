package com.bc.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.bc.wechat.widget.NoTitleConfirmDialog;

import androidx.annotation.Nullable;

/**
 * 解绑QQ号
 *
 * @author zhou
 */
public class QqIdUnLinkActivity extends BaseActivity {

    private Button mUnLinkQqIdBtn;

    private VolleyUtil mVolleyUtil;
    private User mUser;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlink_qq_id);

        initStatusBar();
        StatusBarUtil.setStatusBarColor(QqIdUnLinkActivity.this, R.color.status_bar_color_white);

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(this);

        mUnLinkQqIdBtn = findViewById(R.id.btn_unlink_qq);
        mUnLinkQqIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NoTitleConfirmDialog mNoTitleConfirmDialog = new NoTitleConfirmDialog(QqIdUnLinkActivity.this,
                        "确定解绑QQ",
                        getString(R.string.ok), getString(R.string.cancel), getColor(R.color.navy_blue));
                mNoTitleConfirmDialog.setOnDialogClickListener(new NoTitleConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        mNoTitleConfirmDialog.dismiss();
                        mDialog.setMessage("解绑中");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();
                        unLinkQqId(mUser.getUserId());
                    }

                    @Override
                    public void onCancelClick() {
                        mNoTitleConfirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                mNoTitleConfirmDialog.setCancelable(true);
                mNoTitleConfirmDialog.show();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    /**
     * 解绑QQ号
     *
     * @param userId 用户ID
     */
    private void unLinkQqId(String userId) {
        String url = Constant.BASE_URL + "users/" + userId + "/qqIdLink";

        mVolleyUtil.httpDeleteRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mDialog.dismiss();
                // 解绑成功
                mUser.setUserQqId("");
                mUser.setUserQqPassword("");
                mUser.setUserIsQqLinked(Constant.QQ_ID_NOT_LINK);
                PreferencesUtil.getInstance().setUser(mUser);
                QqIdUnLinkActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
            }
        });
    }
}
