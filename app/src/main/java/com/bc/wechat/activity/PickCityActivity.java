package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.AreaAdapter;
import com.bc.wechat.dao.AreaDao;
import com.bc.wechat.entity.Area;

import java.util.List;

/**
 * 省份选择
 *
 * @author zhou
 */
public class PickCityActivity extends FragmentActivity {
    private ListView mProvinceLv;
    private AreaAdapter mCityAdapter;

    private AreaDao mAreaDao;
    private String mProvinceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_picker);
        initView();
        mAreaDao = new AreaDao();

        mProvinceName = getIntent().getStringExtra("provinceName");
        List<Area> areaList = mAreaDao.getCityListByProvinceName(mProvinceName);
        mCityAdapter = new AreaAdapter(this, areaList);
        mProvinceLv.setAdapter(mCityAdapter);
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mProvinceLv = findViewById(R.id.lv_area);
    }
}
