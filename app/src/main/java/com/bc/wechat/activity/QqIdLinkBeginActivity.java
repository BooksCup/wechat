package com.bc.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 开始绑定QQ号
 *
 * @author zhou
 */
public class QqIdLinkBeginActivity extends BaseActivity {

    @BindView(R.id.ll_root)
    LinearLayout mRootLl;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_qq_id)
    TextView mQqIdTv;

    @BindView(R.id.btn_link_qq)
    Button mLinkQqBtn;

    @BindView(R.id.iv_setting)
    ImageView mSettingIv;

    // 弹窗
    PopupWindow mPopupWindow;

    @Override
    public int getContentView() {
        return R.layout.activity_link_qq_id_begin;
    }

    @Override
    public void initView() {
        initStatusBar();
        StatusBarUtil.setStatusBarColor(QqIdLinkBeginActivity.this, R.color.status_bar_color_white);
        setTitleStrokeWidth(mTitleTv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        renderView();
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderView();
    }

    private void renderView() {
        User user = PreferencesUtil.getInstance().getUser();
        if (Constant.QQ_ID_LINKED.equals(user.getUserIsQqLinked())) {
            // 绑定QQ
            mQqIdTv.setVisibility(View.VISIBLE);
            mLinkQqBtn.setVisibility(View.GONE);
            mSettingIv.setVisibility(View.VISIBLE);

            mQqIdTv.setText("绑定QQ:" + user.getUserQqId());
        } else {
            // 未绑定QQ
            mQqIdTv.setVisibility(View.GONE);
            mLinkQqBtn.setVisibility(View.VISIBLE);
            mSettingIv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_link_qq, R.id.iv_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_link_qq:
                startActivity(new Intent(QqIdLinkBeginActivity.this, QqIdLinkActivity.class));
                break;
            case R.id.iv_setting:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.popup_window_qq_link_setting, null);
                // 给popwindow加上动画效果
                LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                mPopRootLl.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
                // 设置popwindow的宽高
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                mPopupWindow = new PopupWindow(view, dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

                // 使其聚集
                mPopupWindow.setFocusable(true);
                // 设置允许在外点击消失
                mPopupWindow.setOutsideTouchable(true);

                // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                backgroundAlpha(0.5f);  //透明度

                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                    }
                });
                // 弹出的位置
                mPopupWindow.showAtLocation(mRootLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                // 解绑QQ号
                RelativeLayout mUnLinkQqIdRl = view.findViewById(R.id.rl_unlink_qq_id);
                mUnLinkQqIdRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        startActivity(new Intent(QqIdLinkBeginActivity.this, QqIdUnLinkActivity.class));
                    }
                });

                // 取消
                RelativeLayout mCancelRl = view.findViewById(R.id.rl_cancel);
                mCancelRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });

                break;
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     * 1.0完全不透明，0.0f完全透明
     *
     * @param bgAlpha 透明度值
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
