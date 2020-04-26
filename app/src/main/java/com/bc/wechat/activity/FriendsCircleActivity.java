package com.bc.wechat.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.FriendsCircleAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.FriendsCircleDao;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.FriendsCircleComment;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 朋友圈
 *
 * @author zhou
 */
public class FriendsCircleActivity extends BaseActivity {

    private RelativeLayout mRootRl;
    private ImageView mAddFriendsCircleIv;
    private ListView mFriendsCircleLv;
    private User mUser;
    private VolleyUtil mVolleyUtil;
    private FriendsCircleDao mFriendsCircleDao;
    private List<FriendsCircle> mFriendsCircleList = new ArrayList<>();
    FriendsCircleAdapter mAdapter;
    RefreshLayout mRefreshLayout;
    long mTimeStamp;

    private LoadingDialog mDialog;

    private LinearLayout mBottomLl;
    private EditText mCommentEt;
    private Button mSendBtn;

    private InputMethodManager mManager;
    private String mCircleId;

    // 弹窗
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_circle);
        initView();
        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mFriendsCircleDao = new FriendsCircleDao();
        mTimeStamp = 0L;
        mDialog = new LoadingDialog(FriendsCircleActivity.this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);

        mAddFriendsCircleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.popup_window_add_friends_circle, null);
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
                mPopupWindow.showAtLocation(mRootRl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                // 取消
                RelativeLayout mCancelRl = view.findViewById(R.id.rl_cancel);
                mCancelRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });
            }
        });

        mFriendsCircleList = mFriendsCircleDao.getFriendsCircleList(Constant.DEFAULT_PAGE_SIZE, mTimeStamp);

        FriendsCircleAdapter.ClickListener clickListener = new FriendsCircleAdapter.ClickListener() {
            @Override
            public void onClick(Object... objects) {
                String circleId = String.valueOf(objects[1]);
                int position = Integer.parseInt(String.valueOf(objects[2]));
                mCircleId = circleId;
                mBottomLl.setVisibility(View.VISIBLE);

                mCommentEt.setFocusable(true);
                mCommentEt.setFocusableInTouchMode(true);
                mCommentEt.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                mFriendsCircleLv.smoothScrollToPosition(position);
            }
        };

        mCommentEt.addTextChangedListener(new TextChange());
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage("正在发表...");
                mDialog.show();
                String commentContent = mCommentEt.getText().toString();
                addFriendsCircleComment(mCircleId, commentContent);

                // 处理软键盘和编辑栏
                mBottomLl.setVisibility(View.GONE);
                mCommentEt.setText("");
            }
        });

        mAdapter = new FriendsCircleAdapter(mFriendsCircleList, this, clickListener);
        mFriendsCircleLv.setAdapter(mAdapter);
        mFriendsCircleLv.addHeaderView(headerView, null, false);
        mFriendsCircleLv.setHeaderDividersEnabled(false);

        // headerView
        ImageView mCoverIv = headerView.findViewById(R.id.iv_cover);
        mCoverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        TextView mNickNameTv = headerView.findViewById(R.id.tv_nick_name);
        mNickNameTv.setText(mUser.getUserNickName());

        SimpleDraweeView mAvatarSdv = headerView.findViewById(R.id.sdv_avatar);
        if (!TextUtils.isEmpty(mUser.getUserAvatar())) {
            mAvatarSdv.setImageURI(Uri.parse(mUser.getUserAvatar()));
        }
        getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);

        mRefreshLayout.setPrimaryColorsId(android.R.color.black, android.R.color.white);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                // 下拉刷新
                getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                // 上拉加载
                getFriendsCircleList(mUser.getUserId(), Constant.DEFAULT_PAGE_SIZE, true);
            }
        });

        mFriendsCircleLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                mBottomLl.setVisibility(View.GONE);
                return false;
            }
        });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean commentEtHasText = mCommentEt.getText().length() > 0;
            if (commentEtHasText) {
                mSendBtn.setBackgroundColor(Color.parseColor("#45c01a"));
                mSendBtn.setTextColor(Color.parseColor("#ffffff"));
                mSendBtn.setEnabled(true);
            } else {
                mSendBtn.setBackgroundColor(Color.parseColor("#cccccc"));
                mSendBtn.setTextColor(Color.parseColor("#666667"));
                mSendBtn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void initView() {
        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mRootRl = findViewById(R.id.rl_root);
        mAddFriendsCircleIv = findViewById(R.id.iv_add_friends_circle);
        mFriendsCircleLv = findViewById(R.id.ll_friends_circle);

        mBottomLl = findViewById(R.id.ll_bottom);
        mCommentEt = findViewById(R.id.et_comment);
        mSendBtn = findViewById(R.id.btn_send);

        // 上拉加载，下拉刷新
        mRefreshLayout = findViewById(R.id.srl_friends_circle);
    }


    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                mManager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void back(View view) {
        finish();
    }

    private void getFriendsCircleList(String userId, final int pageSize, final boolean isAdd) {
        mTimeStamp = isAdd ? mTimeStamp : 0L;
        String url = Constant.BASE_URL + "friendsCircle?userId=" + userId + "&pageSize=" + pageSize + "&timestamp=" + mTimeStamp;

        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isAdd) {
                    // 上拉加载
                    mRefreshLayout.finishLoadMore();
                } else {
                    // 下拉刷新
                    mRefreshLayout.finishRefresh();
                }

                List<FriendsCircle> list = JSONArray.parseArray(response, FriendsCircle.class);
                if (null != list && list.size() > 0) {
                    for (FriendsCircle friendsCircle : list) {
                        FriendsCircle checkFriendsCircle = mFriendsCircleDao.getFriendsCircleByCircleId(friendsCircle.getCircleId());
                        if (null != friendsCircle.getLikeUserList()) {
                            friendsCircle.setLikeUserJsonArray(JSON.toJSONString(friendsCircle.getLikeUserList()));
                        }
                        if (null != friendsCircle.getFriendsCircleCommentList()) {
                            friendsCircle.setFriendsCircleCommentJsonArray(JSON.toJSONString(friendsCircle.getFriendsCircleCommentList()));
                        }

                        if (null == checkFriendsCircle) {
                            // 不存在,插入
                            mFriendsCircleDao.addFriendsCircle(friendsCircle);
                        } else {
                            // 存在,修改
                            friendsCircle.setId(checkFriendsCircle.getId());
                            mFriendsCircleDao.addFriendsCircle(friendsCircle);
                        }
                    }
                    List<FriendsCircle> friendsCircleList = mFriendsCircleDao.getFriendsCircleList(pageSize, mTimeStamp);
                    if (isAdd) {
                        // 上拉加载
                        mAdapter.addData(friendsCircleList);
                    } else {
                        // 下拉刷新
                        mAdapter.setData(friendsCircleList);
                    }
                    mTimeStamp = friendsCircleList.get(friendsCircleList.size() - 1).getTimestamp();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isAdd) {
                    // 上拉加载
                    mRefreshLayout.finishLoadMore();
                } else {
                    // 下拉刷新
                    mRefreshLayout.finishRefresh();
                }
                // 网络错误，从本地读取
                List<FriendsCircle> friendsCircleList = mFriendsCircleDao.getFriendsCircleList(pageSize, mTimeStamp);
                if (null != friendsCircleList && friendsCircleList.size() > 0) {
                    if (isAdd) {
                        // 上拉加载
                        mAdapter.addData(friendsCircleList);
                    } else {
                        // 下拉刷新
                        mAdapter.setData(friendsCircleList);
                    }
                    mTimeStamp = friendsCircleList.get(friendsCircleList.size() - 1).getTimestamp();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addFriendsCircleComment(final String circleId, final String content) {
        String url = Constant.BASE_URL + "friendsCircle/" + circleId + "/comment";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("content", content);
        paramMap.put("userId", mUser.getUserId());

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FriendsCircleComment friendsCircleComment = JSON.parseObject(response, FriendsCircleComment.class);
                List<FriendsCircle> friendsCircleList = mAdapter.getData();
                for (FriendsCircle friendsCircle : friendsCircleList) {
                    if (circleId.equals(friendsCircle.getCircleId())) {
                        List<FriendsCircleComment> friendsCircleCommentList =
                                CommonUtil.getListFromJson(friendsCircle.getFriendsCircleCommentJsonArray(), FriendsCircleComment.class);
                        friendsCircleComment.setCommentUserNickName(mUser.getUserNickName());
                        friendsCircleCommentList.add(friendsCircleComment);
                        friendsCircle.setFriendsCircleCommentList(friendsCircleCommentList);
                        friendsCircle.setFriendsCircleCommentJsonArray(JSON.toJSONString(friendsCircleCommentList));
                        FriendsCircle.save(friendsCircle);
                    }
                }
                mAdapter.setData(friendsCircleList);
                mAdapter.notifyDataSetChanged();

                mDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
            }
        });
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
