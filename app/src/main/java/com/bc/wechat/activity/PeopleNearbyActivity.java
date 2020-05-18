package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bc.wechat.R;
import com.bc.wechat.adapter.PeopleNearbyAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.PeopleNearby;
import com.bc.wechat.entity.PositionInfo;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附近的人
 *
 * @author zhou
 */
public class PeopleNearbyActivity extends FragmentActivity {

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private ListView mPeopleNearbyLv;
    private PeopleNearbyAdapter mPeopleNearbyAdapter;
    private List<PeopleNearby> mPeopleNearbyList = new ArrayList<>();
    private VolleyUtil mVolleyUtil;
    private User mUser;
    LoadingDialog mDialog;
    // 即使设置只定位1次也会频繁定位，待定位问题
    private boolean mLocateFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);
        initView();
        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();
        mDialog = new LoadingDialog(PeopleNearbyActivity.this);

        mDialog.setMessage(getString(R.string.searching_people_nearby));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        initLocationClientOptions();
        mLocationClient.start();
    }

    /**
     * 初始化配置项
     */
    private void initLocationClientOptions() {
        LocationClientOption option = new LocationClientOption();
        // 可选，设置定位模式，默认高精度
        // LocationMode.Hight_Accuracy：高精度；
        // LocationMode. Battery_Saving：低功耗；
        // LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        // GCJ02：国测局坐标；
        // BD09ll：百度经纬度坐标；
        // BD09：百度墨卡托坐标；
        // 海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setCoorType("bd09ll");
        // 可选，设置发起定位请求的间隔，int类型，单位ms
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(30000);
        // 可选，设置是否使用gps，默认false
        // 使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);
        // 可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);
        // 可选，定位SDK内部是一个service，并放到了独立进程。
        // 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);
        // 可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);
        // 可选，V7.2版本新增能力
        // 如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        // 可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);
        // 可选，设置是否需要最新版本的地址信息。默认需要，即参数为true
        option.setNeedNewVersionRgc(true);
        // 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
    }

    private void initView() {
        mPeopleNearbyLv = findViewById(R.id.lv_people_nearby);
        mPeopleNearbyAdapter = new PeopleNearbyAdapter(PeopleNearbyActivity.this,
                R.layout.item_people_nearby, mPeopleNearbyList);
        mPeopleNearbyLv.setAdapter(mPeopleNearbyAdapter);
    }

    public void back(View view) {
        finish();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            // 以下只列举部分获取经纬度相关（常用）的结果信息
            // 更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            // 获取经度信息
            double longitude = location.getLongitude();
            // 获取纬度信息
            double latitude = location.getLatitude();
//            // 获取定位精度，默认值为0.0f
//            float radius = location.getRadius();
//            // 获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//            String coorType = location.getCoorType();
//            // 获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//            int errorCode = location.getLocType();
            // 区县信息
            String district = location.getDistrict();

            if (!mLocateFlag) {
                PositionInfo positionInfo = new PositionInfo(longitude, latitude);
                PreferencesUtil.getInstance().setPositionInfo(positionInfo);

                getPeopleNearbyList(mUser.getUserId(), longitude, latitude, district);
            }
            mLocateFlag = true;
        }
    }

    /**
     * 上传位置信息并获取附近的人列表
     *
     * @param userId    用户ID
     * @param longitude 经度
     * @param latitude  纬度
     * @param district  区县信息
     */
    private void getPeopleNearbyList(String userId, double longitude, double latitude, String district) {
        String url = Constant.BASE_URL + "peopleNearby";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("longitude", String.valueOf(longitude));
        paramMap.put("latitude", String.valueOf(latitude));
        if (!TextUtils.isEmpty(district)) {
            paramMap.put("district", district);
        }

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                mPeopleNearbyList = JSON.parseArray(response, PeopleNearby.class);
                mPeopleNearbyAdapter.setData(mPeopleNearbyList);
                mPeopleNearbyAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mDialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(PeopleNearbyActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(PeopleNearbyActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
