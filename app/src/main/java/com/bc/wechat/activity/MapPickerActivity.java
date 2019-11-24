package com.bc.wechat.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.bc.wechat.service.LocationService;

import java.util.ArrayList;
import java.util.List;

public class MapPickerActivity extends Activity implements AdapterView.OnItemClickListener {

    List<PoiInfo> mPoiInfoList;
    private Point mCenterPoint;
    // 地理编码
    private GeoCoder mGeoCoder;

    PoiInfo mCurentInfo;


    // 当前经纬度和地理位置
    private LatLng mLocationLatLng;
    private MapView mMapView;

    private LocationService locationService;
    private BaiduMap mBaiduMap;

    private RelativeLayout mMapHolderRl;

    private String mAddress;
    private String mStreet;
    private String mName;
    private String mCity;

    private TextView mStatusTv;

    private MapPickerAdapter mMapPickerAdapter;
    private ListView mPoiLv;

    private boolean mSendLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        locationService = WechatApplication.locationService;
        locationService.registerListener(mListener);

        initView();
        initMap();
    }

    private void initMap() {
        mMapView = findViewById(R.id.mv_map);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(19.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);
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

        mPoiLv = findViewById(R.id.lv_near_by);
        mPoiLv.setOnItemClickListener(this);

        mMapPickerAdapter = new MapPickerAdapter(MapPickerActivity.this, mPoiInfoList);
        mPoiLv.setAdapter(mMapPickerAdapter);
    }

    private void initView() {
        mStatusTv = findViewById(R.id.tv_status);
        mMapHolderRl = findViewById(R.id.rl_map_holder);

        mSendLocation = getIntent().getBooleanExtra("sendLocation", false);
        if (mSendLocation) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelOffset(R.dimen.map_holder_height));
            mMapHolderRl.setLayoutParams(params);
        }
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
                mCity = result.getAddressDetail().city;

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
    }
}
