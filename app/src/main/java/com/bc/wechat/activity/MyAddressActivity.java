package com.bc.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.adapter.MyAddressAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.dao.AddressDao;
import com.bc.wechat.entity.Address;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.List;

public class MyAddressActivity extends FragmentActivity {

    private LinearLayout mRootLl;
    private ImageView mAddIv;
    private ListView mAddressLv;
    private MyAddressAdapter mMyAddressAdapter;
    private User mUser;
    private VolleyUtil mVolleyUtil;
    private AddressDao mAddressDao;
    // 弹窗
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        initView();

        mUser = PreferencesUtil.getInstance().getUser();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mAddressDao = new AddressDao();

        final List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter = new MyAddressAdapter(this, addressList);
        mAddressLv.setAdapter(mMyAddressAdapter);

        getAddressListByUserId(mUser.getUserId());
    }

    private void initView() {
        mRootLl = findViewById(R.id.ll_root);

        mAddIv = findViewById(R.id.iv_add);
        mAddressLv = findViewById(R.id.lv_address);

        mAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAddressActivity.this, AddAddressActivity.class));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 本地读取
        List<Address> addressList = mAddressDao.getAddressList();
        mMyAddressAdapter.setData(addressList);
        mMyAddressAdapter.notifyDataSetChanged();

        // 服务器读取
        getAddressListByUserId(mUser.getUserId());
    }

    private void getAddressListByUserId(String userId) {
        String url = Constant.BASE_URL + "users/" + userId + "/address";
        mVolleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Address> addressList = JSONArray.parseArray(response, Address.class);
                if (null != addressList && addressList.size() > 0) {
                    // 持久化
                    mAddressDao.clearAddress();
                    for (Address address : addressList) {
                        if (null != address) {
                            mAddressDao.saveAddress(address);
                        }
                    }
                }
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
                mAddressLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        showOperation(view);
                        return false;
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                List<Address> addressList = mAddressDao.getAddressList();
                mMyAddressAdapter.setData(addressList);
                mMyAddressAdapter.notifyDataSetChanged();
                mAddressLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        showOperation(view);
                        return false;
                    }
                });
            }
        });
    }

    private void showOperation(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.popup_window_address_setting, null);
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

        // 取消
        RelativeLayout mCancelRl = view.findViewById(R.id.rl_cancel);
        mCancelRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
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
