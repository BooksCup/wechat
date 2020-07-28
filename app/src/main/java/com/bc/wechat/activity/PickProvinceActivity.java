package com.bc.wechat.activity;

import android.content.Intent;
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

import java.util.List;

import androidx.annotation.Nullable;

/**
 * 省选择
 *
 * @author zhou
 */
public class PickProvinceActivity extends BaseActivity {
    private TextView mTitleTv;

    private ListView mProvinceLv;
    private AreaAdapter mProvinceAdapter;

    private AreaDao mAreaDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_picker);
        initStatusBar();
        initView();
        mAreaDao = new AreaDao();

        final List<Area> areaList = mAreaDao.getProvinceList();
        mProvinceAdapter = new AreaAdapter(this, areaList);
        mProvinceLv.setAdapter(mProvinceAdapter);
        mProvinceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Area area = areaList.get(position);
                Intent intent = new Intent(PickProvinceActivity.this, PickCityActivity.class);
                intent.putExtra("provinceName", area.getName());
                startActivity(intent);
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

        mProvinceLv = findViewById(R.id.lv_area);
    }
}
