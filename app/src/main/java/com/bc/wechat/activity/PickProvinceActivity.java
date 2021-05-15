package com.bc.wechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.AreaAdapter;
import com.bc.wechat.dao.AreaDao;
import com.bc.wechat.entity.Area;

import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择省
 *
 * @author zhou
 */
public class PickProvinceActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    @BindView(R.id.lv_area)
    ListView mProvinceLv;

    AreaAdapter mProvinceAdapter;
    AreaDao mAreaDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_picker);
        ButterKnife.bind(this);
        initStatusBar();
        initView();
        mAreaDao = new AreaDao();

        final List<Area> areaList = mAreaDao.getProvinceList();
        mProvinceAdapter = new AreaAdapter(this, areaList);
        mProvinceLv.setAdapter(mProvinceAdapter);
        mProvinceLv.setOnItemClickListener((parent, view, position, id) -> {
            Area area = areaList.get(position);
            Intent intent = new Intent(PickProvinceActivity.this, PickCityActivity.class);
            intent.putExtra("provinceName", area.getName());
            startActivity(intent);
        });

        // 压入销毁栈
        FinishActivityManager.getManager().addActivity(this);
    }

    public void back(View view) {
        finish();
        FinishActivityManager.getManager().finishActivity(this);
    }

    private void initView() {
        setTitleStrokeWidth(mTitleTv);
    }

}