package com.bc.wechat.activity;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bc.wechat.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;


import java.io.File;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

public class MessageBigImageActivity extends Activity {
    private PhotoDraweeView mPhotoDraweeView;
    private String mImgUrl;
    private String mLocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mPhotoDraweeView = findViewById(R.id.pdv_image);
    }

    private void initData() {
        mImgUrl = getIntent().getStringExtra("imgUrl");
        mLocalPath = getIntent().getStringExtra("localPath");

        Uri uri;
        if (!TextUtils.isEmpty(mLocalPath)) {
            // 本地读
            uri = Uri.fromFile(new File(mLocalPath));
        } else {
            // 网络获取
            uri = Uri.parse(mImgUrl);
        }

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(uri);//设置图片url
        controller.setOldController(mPhotoDraweeView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || mPhotoDraweeView == null) {
                    return;
                }
                mPhotoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        mPhotoDraweeView.setController(controller.build());
    }

    private void initEvent() {
        //添加点击事件
        mPhotoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
    }
}
