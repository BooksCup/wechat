package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
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
public class PickDistrictActivity extends FragmentActivity {
    private ListView mDistrictLv;
    private AreaAdapter mDistrictAdapter;

    private AreaDao mAreaDao;
    private String mCityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_picker);
        initView();
        mAreaDao = new AreaDao();

        mCityName = getIntent().getStringExtra("cityName");
        final List<Area> areaList = mAreaDao.getDistrictListByCityName(mCityName);
        mDistrictAdapter = new AreaAdapter(this, areaList);
        mDistrictLv.setAdapter(mDistrictAdapter);
        mDistrictLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mDistrictLv = findViewById(R.id.lv_area);
    }
}
