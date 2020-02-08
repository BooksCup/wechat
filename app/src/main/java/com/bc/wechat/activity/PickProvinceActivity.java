package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.ProvinceAdapter;
import com.bc.wechat.entity.Area;

import java.util.ArrayList;
import java.util.List;

public class PickProvinceActivity extends FragmentActivity {
    private ListView mProvinceLv;
    private ProvinceAdapter mProvinceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_picker);
        initView();

        List<Area> areaList = new ArrayList<>();
        areaList.add(new Area());
        areaList.add(new Area());
        areaList.add(new Area());
        mProvinceAdapter = new ProvinceAdapter(this, areaList);
        mProvinceLv.setAdapter(mProvinceAdapter);
    }

    public void back(View view) {
        finish();
    }

    private void initView() {
        mProvinceLv = findViewById(R.id.lv_province);
    }
}
