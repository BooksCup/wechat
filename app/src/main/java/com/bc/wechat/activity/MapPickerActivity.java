package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bc.wechat.R;
import com.bc.wechat.WechatApplication;
import com.bc.wechat.adapter.MapPickerAdapter;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.service.LocationService;
import com.bc.wechat.utils.BitmapLoaderUtil;
import com.bc.wechat.utils.FileUtil;
import com.bc.wechat.widget.ConfirmDialog;
import com.smarx.notchlib.NotchScreenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;

/**
 * 地图选择器
 *
 * @author zhou
 */
public class MapPickerActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = MapPickerActivity.class.getCanonicalName();
    private static final int REQUEST_PERMISSION_STORAGE = 0x01;

    List<PoiInfo> mPoiInfoList;
    Point mCenterPoint;
    // 地理编码
    GeoCoder mGeoCoder;

    PoiInfo mCurentInfo;

    // 当前经纬度和地理位置
    LatLng mLocationLatLng;

    @BindView(R.id.mv_map)
    MapView mMapView;

    LocationService locationService;
    BaiduMap mBaiduMap;

    @BindView(R.id.rl_map_holder)
    RelativeLayout mMapHolderRl;

    @BindView(R.id.btn_send_location)
    Button mSendLocationBtn;

    @BindView(R.id.tv_status)
    TextView mStatusTv;

    @BindView(R.id.lv_near_by)
    ListView mPoiLv;

    MapPickerAdapter mMapPickerAdapter;
    boolean mSendLocation;
    String mLocationType;

    int mWidth;
    int mHeight;
    float mDensity;
    int mDensityDpi;

    double mLatitude;
    double mLongitude;
    // 地址
    // 类似于"江苏省南京市江宁区庄排路158号"
    String mAddress;

    // 详细地址
    String mAddressDetail;

    // 省
    String mProvince;
    // 市
    String mCity;
    // 区，如:"江宁区"
    String mDistrict;
    // 街道，如:"庄排路"
    String mStreet;
    // 门牌号，如"158号"
    String mStreetNumber;
    String mName;
    NotchScreenManager notchScreenManager = NotchScreenManager.getInstance();

    @Override
    public int getContentView() {
        return R.layout.activity_map_picker;
    }

    @Override
    public void initView() {
        // 支持显示到刘海区域
        notchScreenManager.setDisplayInNotch(this);
        getNotch();
        initStatusBar();

        locationService = WechatApplication.locationService;
        locationService.registerListener(mListener);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDensity = displayMetrics.density;
        mDensityDpi = displayMetrics.densityDpi;
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;
        initMap();

        mSendLocation = getIntent().getBooleanExtra("sendLocation", false);
        mLocationType = getIntent().getStringExtra("locationType");
        if (mSendLocation) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelOffset(R.dimen.map_holder_height));
            mMapHolderRl.setLayoutParams(params);
        } else {
            // 取消监听
            // 否则会一直定位当前位置
            locationService.unregisterListener(mListener);
            mSendLocationBtn.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mMapHolderRl.setLayoutParams(params);

            double latitude = getIntent().getDoubleExtra("latitude", 0);
            double longitude = getIntent().getDoubleExtra("longitude", 0);

            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100).direction(90.f).latitude(latitude).longitude(longitude).build();
            mBaiduMap.setMyLocationData(locationData);
            mBaiduMap.setMyLocationEnabled(true);

            LatLng ll = new LatLng(latitude, longitude);
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_current_location);
            OverlayOptions options = new MarkerOptions().position(ll).icon(descriptor).zIndex(10);
            mBaiduMap.addOverlay(options);

            // 实现动画跳转
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            mBaiduMap.animateMapStatus(u);
            mBaiduMap.clear();
            // 发起反地理编码检索
            mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                    .location(new LatLng(latitude, longitude)));
        }

        mSendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 需要动态申请存储权限
                String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
                requestPermissions(MapPickerActivity.this, permissions, REQUEST_PERMISSION_STORAGE);
            }
        });
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(19.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (null == mCenterPoint) {
                        return;
                    }
                    mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
                    LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
                    mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(currentLatLng));
                }
            }
        });

//        mBaiduMap.setOnMapTouchListener(null);
        // 初始化POI信息列表
        mPoiInfoList = new ArrayList<>();
        // 初始化当前mapView中心屏幕坐标，初始化当前地理位置
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLocationLatLng = mBaiduMap.getMapStatus().target;
        // 定位
        mBaiduMap.setMyLocationEnabled(true);
        // 隐藏百度logo zoomControl
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView || child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoListener);

        mPoiLv.setOnItemClickListener(this);

        mMapPickerAdapter = new MapPickerAdapter(MapPickerActivity.this, mPoiInfoList);
        mPoiLv.setAdapter(mMapPickerAdapter);
    }

    /**
     * 获取刘海区域信息
     */
    private void getNotch() {
        // 获取刘海屏信息
        notchScreenManager.getNotchInfo(this, notchScreenInfo -> {
            Log.i(TAG, "Is this screen notch? " + notchScreenInfo.hasNotch);
            if (notchScreenInfo.hasNotch) {
                for (Rect rect : notchScreenInfo.notchRects) {
                    Log.i(TAG, "notch screen Rect =  " + rect.toShortString());
                    // 将被遮挡的TextView左移
                }
            }
        });
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                MyLocationData data = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build();
                mBaiduMap.setMyLocationData(data);

                // 设置自定义坐标
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
                mBaiduMap.setMyLocationConfiguration(config);
                mAddress = location.getAddrStr();
                mStreet = location.getStreet();
                mCity = location.getCity();

                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLocationLatLng = currentLatLng;

                // 实现动画跳转
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
            }
        }
    };

    OnGetGeoCoderResultListener mGeoListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (null == result || result.error != SearchResult.ERRORNO.NO_ERROR) {
                mStatusTv.setText("发生错误");
                mStatusTv.setVisibility(View.VISIBLE);
            } else {
                mStatusTv.setVisibility(View.GONE);
                // 当前位置信息
                mLocationLatLng = result.getLocation();
                mAddress = result.getAddress();

                mStreet = result.getAddressDetail().street;
                mStreetNumber = result.getAddressDetail().streetNumber;

                mProvince = result.getAddressDetail().province;
                mCity = result.getAddressDetail().city;
                mDistrict = result.getAddressDetail().district;

                mAddressDetail = mDistrict + mStreet + mStreetNumber;

                mLatitude = result.getLocation().latitude;
                mLongitude = result.getLocation().longitude;

                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = "[当前位置]";
                mPoiInfoList.clear();
                mPoiInfoList.add(mCurentInfo);

                // 将周边信息加入列表
                if (null != result.getPoiList()) {
                    mPoiInfoList.addAll(result.getPoiList());
                }
                mMapPickerAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener);
        locationService.stop();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationService.start();
        mMapView.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mMapPickerAdapter.setNotifyTip(position);
        mMapPickerAdapter.notifyDataSetChanged();

        BitmapDescriptor mSelectIcon = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_pick_map_geo);

        mBaiduMap.clear();

        PoiInfo poiInfo = mMapPickerAdapter.getItem(position);
        LatLng latLng = poiInfo.getLocation();
        // 动画跳转
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(mapStatusUpdate);

        // 添加覆盖物
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(mSelectIcon).anchor(0.5f, 0.5f);
        mBaiduMap.addOverlay(overlayOptions);

        mAddressDetail = poiInfo.address;
        mAddress = poiInfo.name;

        mLatitude = poiInfo.location.latitude;
        mLongitude = poiInfo.location.longitude;
    }

    public void back(View view) {
        finish();
    }

    /**
     * 动态权限
     */
    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        // Android 6.0开始的动态权限，这里进行版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                sendLocation();
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
            sendLocation();
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(MapPickerActivity.this, permissions[0], requestCode);
        }
    }

    public void handleRejectPermission(final Activity context, String permission, int requestCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(MapPickerActivity.this, getString(R.string.request_permission),
                    getString(R.string.request_permission_storage),
                    getString(R.string.go_setting), getString(R.string.cancel), getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }

    private void sendLocation() {
        if (Constant.LOCATION_TYPE_AREA.equals(mLocationType)) {
            // 发送省市区街道信息
            sendLocationArea();
        } else {
            // 发送定位信息
            sendLocationMsg();
        }
    }

    /**
     * 发送位置消息
     */
    private void sendLocationMsg() {
        if (null != mLocationLatLng) {
            int left = mWidth / 8;
            int top = (int) (mHeight - 1.5 * mWidth);
            // 计算长宽
            int width = mWidth - 2 * left;
            int height = (int) (0.6 * width);

            int right = mWidth - left;
            int bottom = top + height;


            Rect rect = new Rect(left, top, right, bottom);
            mBaiduMap.snapshotScope(rect, new BaiduMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    String fileName = UUID.randomUUID().toString();
                    final String localPath = BitmapLoaderUtil.saveBitmapToLocal(bitmap, fileName);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> imageList = FileUtil.uploadFile(Constant.BASE_URL + "oss/file", localPath);
                            if (null != imageList && imageList.size() > 0) {
                                Intent intent = new Intent();
                                intent.putExtra("latitude", mLatitude);
                                intent.putExtra("longitude", mLongitude);
                                intent.putExtra("address", mAddress);
                                intent.putExtra("addressDetail", mAddressDetail);
                                intent.putExtra("path", imageList.get(0));
                                setResult(RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

                            }
                        }
                    }).start();

                }
            });
        }
    }

    /**
     * 发送位置信息
     */
    private void sendLocationArea() {
        Intent intent = new Intent();
        // 省
        intent.putExtra("province", mProvince);
        // 市
        intent.putExtra("city", mCity);
        // 区
        intent.putExtra("district", mDistrict);
        // 详细地址
        intent.putExtra("addressDetail", mAddressDetail);
        setResult(RESULT_OK, intent);
        finish();
    }

}