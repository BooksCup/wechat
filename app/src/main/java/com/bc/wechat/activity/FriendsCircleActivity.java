package com.bc.wechat.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 朋友圈
 *
 * @author zhou
 */
public class FriendsCircleActivity extends FragmentActivity {

    private ListView mFriendsCircleLv;
    private User mUser;
    private VolleyUtil mVolleyUtil;
    private FriendsCircleDao mFriendsCircleDao;
    private List<FriendsCircle> mFriendsCircleList = new ArrayList<>();
    FriendsCircleAdapter mAdapter;
    RefreshLayout mRefreshLayout;
    long mTimeStamp;

    private LinearLayout mBottomLl;
    private EditText mCommentEt;

    private InputMethodManager mManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_circle);
        initView();
        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mFriendsCircleDao = new FriendsCircleDao();
        mTimeStamp = 0L;

        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);

        mFriendsCircleList = mFriendsCircleDao.getFriendsCircleList(Constant.DEFAULT_PAGE_SIZE, mTimeStamp);

        FriendsCircleAdapter.ClickListener clickListener = new FriendsCircleAdapter.ClickListener() {
            @Override
            public void onClick(Object... objects) {
                String circleId = String.valueOf(objects[1]);
                mBottomLl.setVisibility(View.VISIBLE);

                mCommentEt.setFocusable(true);
                mCommentEt.setFocusableInTouchMode(true);
                mCommentEt.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        };

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
        mAvatarSdv.setImageURI(Uri.parse(mUser.getUserAvatar()));

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

    private void initView() {
        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mFriendsCircleLv = findViewById(R.id.ll_friends_circle);

        mBottomLl = findViewById(R.id.ll_bottom);
        mCommentEt = findViewById(R.id.et_comment);

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

}
