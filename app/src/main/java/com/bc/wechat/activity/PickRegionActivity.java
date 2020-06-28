package com.bc.wechat.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bc.wechat.R;
import com.bc.wechat.adapter.RegionAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Region;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 地区
 *
 * @author zhou
 */
public class PickRegionActivity extends BaseActivity {
    private static final int REQUEST_CODE_LOCATION = 1;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.tv_region)
    TextView mRegionTv;

    @BindView(R.id.lv_region)
    ListView mRegionLv;

    @BindView(R.id.iv_region)
    ImageView mRegionIv;

    private RegionAdapter mRegionAdapter;

    private String mRegion;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    // 即使设置只定位1次也会频繁定位，待定位问题
    private boolean mLocateFlag = false;

    private VolleyUtil mVolleyUtil;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_picker);
        ButterKnife.bind(this);
        initStatusBar();

        mVolleyUtil = VolleyUtil.getInstance(this);
        mUser = PreferencesUtil.getInstance().getUser();

        initView();

        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        initLocationClientOptions();


        String[] permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
        requestPermissions(PickRegionActivity.this, permissions, REQUEST_CODE_LOCATION);
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        final List<Region> regionList = new ArrayList<>();
        regionList.add(new Region("阿鲁巴"));
        regionList.add(new Region("阿尔巴尼亚"));
        regionList.add(new Region("阿尔及利亚"));
        regionList.add(new Region("阿富汗"));
        regionList.add(new Region("阿根廷"));
        regionList.add(new Region("阿拉伯联合酋长国"));
        regionList.add(new Region("阿曼"));
        regionList.add(new Region("阿塞拜疆"));
        regionList.add(new Region("爱尔兰"));
        mRegionAdapter = new RegionAdapter(this, regionList);
        mRegionLv.setAdapter(mRegionAdapter);

        mRegionLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Region region = regionList.get(position);
                mRegion = region.getName();
                mUser.setUserRegion(mRegion);
                PreferencesUtil.getInstance().setUser(mUser);
                modifyRegion();
                finish();
            }
        });
    }

    /**
     * 初始化配置项
     */
    private void initLocationClientOptions() {
        LocationClientOption option = new LocationClientOption();
        // 可选，设置定位模式，默认高精度
        // LocationMode.Hight_Accuracy：高精度；
        // LocationMode.Battery_Saving：低功耗；
        // LocationMode.Device_Sensors：仅使用设备；
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

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeCriteriaException) {
                // Location failed beacuse we can not get any loc information!
                // 获取不到定位
                handleLocationError();
            } else {
                // 此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
                // 以下只列举部分获取经纬度相关（常用）的结果信息
                // 更多结果信息获取说明，请参照类参考中BDLocation类中的说明
                String country = location.getCountry();
                String province = location.getProvince();
                String city = location.getCity();
                mRegionTv.setTextColor(getColor(R.color.common_item_black));
                mRegionTv.setText(country + " " + province + " " + city);
                if (!mLocateFlag) {
                    mRegion = province + " " + city;
                }
                mLocateFlag = true;
            }
        }
    }

    @OnClick({R.id.rl_region})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_region:
                if (!TextUtils.isEmpty(mRegion)) {
                    mUser.setUserRegion(mRegion);
                    PreferencesUtil.getInstance().setUser(mUser);
                    modifyRegion();
                    finish();
                }
                break;
        }
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0开始的动态权限，这里进行版本判断
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                switch (requestCode) {
                    case REQUEST_CODE_LOCATION:
                        mLocationClient.start();
                        break;
                }

            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
            }
        }
    }

    /**
     * requestPermissions的回调
     * 一个或多个权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // 判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            switch (requestCode) {
                case REQUEST_CODE_LOCATION:
                    // 同意定位权限
                    mLocationClient.start();
                    break;
            }
        } else {
            // 获取不到定位
            handleLocationError();
        }
    }

    /**
     * 修改地区
     */
    private void modifyRegion() {
        String url = Constant.BASE_URL + "users/" + mUser.getUserId() + "/userRegion";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userRegion", mRegion);

        mVolleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

    /**
     * 处理定位异常
     */
    private void handleLocationError() {
        mRegionIv.setImageResource(R.mipmap.icon_location_error);
        mRegionTv.setTextColor(getColor(R.color.tips_grey));
        mRegionTv.setText("无法获取你的位置信息");
    }
}
