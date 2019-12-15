package com.bc.wechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bc.wechat.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ViewPagerImageActivity extends Activity {
    ViewPager mPhotoVp;
    TextView mPhotoNumTv;

    SimpleDraweeView[] mPhotoSdvs;

    private ArrayList<String> mPhotoList;
    private int mSelectPosition;


    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_image);
        initView();
    }


    public void initView() {
        mPhotoNumTv = findViewById(R.id.tv_photo_num);

        Intent intent = getIntent();
        if (intent != null) {
            mPhotoList = intent.getStringArrayListExtra("photoList");
            mSelectPosition = intent.getIntExtra("position", 0);

            mPhotoSdvs = new SimpleDraweeView[mPhotoList.size()];
            mPhotoVp = findViewById(R.id.vp_photo);

            mPhotoNumTv.setText(mSelectPosition + 1 + "/" + mPhotoList.size());
            mPhotoVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    mSelectPosition = position;
                    mPhotoNumTv.setText(mSelectPosition + 1 + "/" + mPhotoList.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            mPagerAdapter = new PagerAdapter() {
                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    SimpleDraweeView photoSdv = new SimpleDraweeView(ViewPagerImageActivity.this);
                    mPhotoSdvs[position] = photoSdv;

                    GenericDraweeHierarchy hierarchy = photoSdv.getHierarchy();
                    hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                    photoSdv.setImageURI(Uri.parse(mPhotoList.get(position)));
                    container.addView(photoSdv);
                    return photoSdv;
                }

                @Override
                public int getCount() {
                    return mPhotoList.size();
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(mPhotoSdvs[position]);
                }

                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;
                }
            };
            mPhotoVp.setAdapter(mPagerAdapter);
            setDefaultItem(mSelectPosition);
        } else {
            return;
        }
    }


    private void setDefaultItem(int position) {
        try {
            Class c = Class.forName("android.support.v4.view.ViewPager");
            Field field = c.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(mPhotoVp, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPagerAdapter.notifyDataSetChanged();
        mPhotoVp.setCurrentItem(position);
    }
}
