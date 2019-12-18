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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class FriendsCircleActivity extends FragmentActivity {

    private ListView listView;
    private User user;
    private VolleyUtil volleyUtil;
    private FriendsCircleDao friendsCircleDao;
    private List<FriendsCircle> friendsCircleList = new ArrayList<>();
    FriendsCircleAdapter mAdapter;
    RefreshLayout refreshLayout;
    long timeStamp;
    LinearLayout mBottonLl;
    private InputMethodManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_circle);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(this);
        friendsCircleDao = new FriendsCircleDao();
        timeStamp = 0L;
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        listView = findViewById(R.id.ll_friends_circle);
        mBottonLl = findViewById(R.id.ll_bottom);
        View headerView = LayoutInflater.from(this).inflate(R.layout.item_friends_circle_header, null);

        friendsCircleList = friendsCircleDao.getFriendsCircleList(Constant.DEFAULT_PAGE_SIZE, timeStamp);

        FriendsCircleAdapter.ClickListener clickListener = new FriendsCircleAdapter.ClickListener() {
            @Override
            public void onClick(Object... objects) {
                String circleId = String.valueOf(objects[1]);
                Toast.makeText(FriendsCircleActivity.this, circleId, Toast.LENGTH_SHORT).show();
                mBottonLl.setVisibility(View.VISIBLE);
            }
        };

        mAdapter = new FriendsCircleAdapter(friendsCircleList, this, clickListener);
        listView.setAdapter(mAdapter);
        listView.addHeaderView(headerView, null, false);
        listView.setHeaderDividersEnabled(false);

        // headerView
        ImageView mCoverIv = headerView.findViewById(R.id.iv_cover);
        mCoverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        TextView mNickNameTv = headerView.findViewById(R.id.tv_nick_name);
        mNickNameTv.setText(user.getUserNickName());

        SimpleDraweeView mAvatarSdv = headerView.findViewById(R.id.sdv_avatar);
        mAvatarSdv.setImageURI(Uri.parse(user.getUserAvatar()));

        getFriendsCircleList(user.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);

        // 上拉加载，下拉刷新
        refreshLayout = findViewById(R.id.srl_friends_circle);
        refreshLayout.setPrimaryColorsId(android.R.color.black, android.R.color.white);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                // 下拉刷新
                getFriendsCircleList(user.getUserId(), Constant.DEFAULT_PAGE_SIZE, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                // 上拉加载
                getFriendsCircleList(user.getUserId(), Constant.DEFAULT_PAGE_SIZE, true);
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                mBottonLl.setVisibility(View.GONE);
                return false;
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void back(View view) {
        finish();
    }

    private void getFriendsCircleList(String userId, final int pageSize, final boolean isAdd) {
        timeStamp = isAdd ? timeStamp : 0L;
        String url = Constant.BASE_URL + "friendsCircle?userId=" + userId + "&pageSize=" + pageSize + "&timestamp=" + timeStamp;

        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isAdd) {
                    // 上拉加载
                    refreshLayout.finishLoadMore();
                } else {
                    // 下拉刷新
                    refreshLayout.finishRefresh();
                }

                List<FriendsCircle> list = JSONArray.parseArray(response, FriendsCircle.class);
                if (null != list && list.size() > 0) {
                    for (FriendsCircle friendsCircle : list) {
                        FriendsCircle checkFriendsCircle = friendsCircleDao.getFriendsCircleByCircleId(friendsCircle.getCircleId());
                        if (null != friendsCircle.getLikeUserList()) {
                            friendsCircle.setLikeUserJsonArray(JSON.toJSONString(friendsCircle.getLikeUserList()));
                        }
                        if (null == checkFriendsCircle) {
                            // 不存在,插入
                            friendsCircleDao.addFriendsCircle(friendsCircle);
                        } else {
                            // 存在,修改
                            friendsCircle.setId(checkFriendsCircle.getId());
                            friendsCircleDao.addFriendsCircle(friendsCircle);
                        }
                    }
                    List<FriendsCircle> friendsCircleList = friendsCircleDao.getFriendsCircleList(pageSize, timeStamp);
                    if (isAdd) {
                        // 上拉加载
                        mAdapter.addData(friendsCircleList);
                    } else {
                        // 下拉刷新
                        mAdapter.setData(friendsCircleList);
                    }
                    timeStamp = friendsCircleList.get(friendsCircleList.size() - 1).getTimestamp();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isAdd) {
                    // 上拉加载
                    refreshLayout.finishLoadMore();
                } else {
                    // 下拉刷新
                    refreshLayout.finishRefresh();
                }
                // 网络错误，从本地读取
                List<FriendsCircle> friendsCircleList = friendsCircleDao.getFriendsCircleList(pageSize, timeStamp);
                if (null != friendsCircleList && friendsCircleList.size() > 0) {
                    if (isAdd) {
                        // 上拉加载
                        mAdapter.addData(friendsCircleList);
                    } else {
                        // 下拉刷新
                        mAdapter.setData(friendsCircleList);
                    }
                    timeStamp = friendsCircleList.get(friendsCircleList.size() - 1).getTimestamp();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
