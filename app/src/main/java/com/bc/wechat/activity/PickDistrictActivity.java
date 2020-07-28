package com.bc.wechat.activity;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.AreaAdapter;
import com.bc.wechat.dao.AreaDao;
import com.bc.wechat.entity.Area;
import com.bc.wechat.utils.PreferencesUtil;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * 区县选择
 *
 * @author zhou
 */
public class PickDistrictActivity extends BaseActivity {
    private TextView mTitleTv;

    private ListView mDistrictLv;
    private AreaAdapter mDistrictAdapter;

    private AreaDao mAreaDao;

    private String mProvinceName;
    private String mCityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_picker);
        initStatusBar();
        initView();
        PreferencesUtil.getInstance().init(this);
        mAreaDao = new AreaDao();

        mProvinceName = getIntent().getStringExtra("provinceName");
        mCityName = getIntent().getStringExtra("cityName");
        final List<Area> areaList = mAreaDao.getDistrictListByCityName(mCityName);
        mDistrictAdapter = new AreaAdapter(this, areaList);
        mDistrictLv.setAdapter(mDistrictAdapter);
        mDistrictLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Area area = areaList.get(position);

                PreferencesUtil.getInstance().setPickedProvince(mProvinceName);
                PreferencesUtil.getInstance().setPickedCity(mCityName);
                PreferencesUtil.getInstance().setPickedDistrict(area.getName());
                PreferencesUtil.getInstance().setPickedPostCode(area.getPostCode());

                // 销毁省、市、区的选择页面
                FinishActivityManager.getManager().finishActivity(PickProvinceActivity.class);
                FinishActivityManager.getManager().finishActivity(PickCityActivity.class);
                FinishActivityManager.getManager().finishActivity(PickDistrictActivity.this);
            }
        });
        // 压入销毁栈
        FinishActivityManager.getManager().addActivity(this);
    }

    public void back(View view) {
        finish();
        FinishActivityManager.getManager().finishActivity(this);
    }

    private void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        TextPaint paint = mTitleTv.getPaint();
        paint.setFakeBoldText(true);

        mDistrictLv = findViewById(R.id.lv_area);
    }
}
