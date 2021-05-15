package com.bc.wechat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.NewFriendsMsgAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.utils.ExampleUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新的朋友
 *
 * @author zhou
 */
public class NewFriendsActivity extends BaseActivity {
    @BindView(R.id.ll_root)
    LinearLayout mRootLl;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.lv_new_friends_msg)
    ListView mNewFriendsMsgLv;

    NewFriendsMsgAdapter mNewFriendsMsgAdapter;
    MessageReceiver mMessageReceiver;

    public static boolean isForeground = false;

    List<FriendApply> mFriendApplyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);

        ButterKnife.bind(this);

        initStatusBar();

        registerMessageReceiver();
        initView();

        mFriendApplyList = FriendApply.listAll(FriendApply.class, "time_stamp desc");

        mNewFriendsMsgAdapter = new NewFriendsMsgAdapter(this, mFriendApplyList);
        mNewFriendsMsgLv.setAdapter(mNewFriendsMsgAdapter);

        mNewFriendsMsgLv.setOnItemClickListener((parent, view, position, id) -> {
            FriendApply friendApply = mFriendApplyList.get(position);
            if (Constant.FRIEND_APPLY_STATUS_ACCEPT.equals(friendApply.getStatus())) {
                // 如果已通过申请
                // 进入用户详情页
                startActivity(new Intent(NewFriendsActivity.this, UserInfoActivity.class).
                        putExtra("userId", friendApply.getFromUserId()));
            } else {
                // 未通过申请
                // 进入好友申请处理页面
                startActivity(new Intent(NewFriendsActivity.this, NewFriendsAcceptActivity.class).
                        putExtra("applyId", friendApply.getApplyId())
                );
            }
        });

        mNewFriendsMsgLv.setOnItemLongClickListener((adapterView, view, position, id) -> {
            FriendApply friendApply = mFriendApplyList.get(position);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.popup_window_new_friends_msg, null);
            // 给popwindow加上动画效果
            LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
            view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
            mPopRootLl.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
            // 设置popwindow的宽高
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            PopupWindow mPopupWindow = new PopupWindow(view, dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

            // 使其聚集
            mPopupWindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupWindow.setOutsideTouchable(true);

            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            backgroundAlpha(0.5f);  //透明度

            mPopupWindow.setOnDismissListener(() -> backgroundAlpha(1f));
            // 弹出的位置
            mPopupWindow.showAtLocation(mRootLl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            // 删除
            RelativeLayout mDeleteRl = view.findViewById(R.id.rl_delete);
            mDeleteRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendApply.delete(friendApply);
                    mFriendApplyList.remove(position);
                    mNewFriendsMsgAdapter.notifyDataSetChanged();
                    mPopupWindow.dismiss();
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

            return true;
        });
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);
    }

    @OnClick({R.id.tv_add, R.id.tv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                startActivity(new Intent(NewFriendsActivity.this, AddContactsActivity.class));
                break;
            case R.id.tv_search:
                startActivity(new Intent(NewFriendsActivity.this, AddFriendsBySearchActivity.class));
                break;
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;

        // 刷新好友申请列表
        mFriendApplyList = FriendApply.listAll(FriendApply.class, "time_stamp desc");
        mNewFriendsMsgAdapter.setData(mFriendApplyList);
        mNewFriendsMsgAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MainActivity.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_NEW_FRIENDS_MSG);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MainActivity.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_APPLY_NEW_FRIENDS_MSG.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(MainActivity.KEY_MESSAGE);
                    String extras = intent.getStringExtra(MainActivity.KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(MainActivity.KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(MainActivity.KEY_EXTRAS + " : " + extras + "\n");
                    }
                    mFriendApplyList = FriendApply.listAll(FriendApply.class, "time_stamp desc");
                    mNewFriendsMsgAdapter.setData(mFriendApplyList);
                    mNewFriendsMsgAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
            }
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
