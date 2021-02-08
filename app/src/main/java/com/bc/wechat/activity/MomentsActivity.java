package com.bc.wechat.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.MomentsAdapter;
import com.bc.wechat.entity.Location;
import com.bc.wechat.entity.MyMedia;
import com.bc.wechat.entity.RecyclerViewItem;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.widget.ImagePickerLoader;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MomentsActivity extends BaseActivity {
    private static final String TAG = "FriendsCircleActivity";
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.parallax)
    ImageView parallax;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.qtb_topbar)
    Toolbar topbar;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.sdv_avatar)
    QMUIRadiusImageView sdvAvatar;
    @BindView(R.id.cover)
    LinearLayout cover;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    private int mOffset = 0;
    private int mScrollY = 0;
    int time = 0;
    private User mUser;
    MomentsAdapter mAdapter;
    LinearLayoutManager linearLayoutManager;
    private final int REQUEST_CODE_PICKER = 100;
    ArrayList<MyMedia> photos = new ArrayList<>();
    private ArrayList<RecyclerViewItem> recyclerViewItemList = new ArrayList<>();

    private InputMethodManager mManager;
    //弹窗
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);

        initStatusBar();

        ButterKnife.bind(this);
        PreferencesUtil.getInstance().init(this);
        mUser = PreferencesUtil.getInstance().getUser();
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        initView();
    }

    private void initView() {
        loadMyTestDate();
        Glide.with(sdvAvatar).load(mUser.getUserAvatar()).into(sdvAvatar);
        tvNickname.setText(mUser.getUserNickName());
        topbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        parallax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("Sdfasfadsfasf");
            }
        });
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setOnMultiListener(new SimpleMultiListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                topbar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Log.d(TAG, "onRefresh: " + "刷新");
                refreshLayout.finishRefresh(3000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                time = time + 1;
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = SmartUtil.dp2px(300);
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.design_default_color_on_secondary) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    parallax.setTranslationY(mOffset - mScrollY);
//                    QMUIStatusBarHelper.translucent(MomentsActivity.this);
                    topbar.setBackgroundColor(getColor(R.color.moments_common_bg));
                }
                if (scrollY < 300) {
                    topbar.setBackgroundColor(0);
                }
                lastScrollY = scrollY;
            }
        });

        mAdapter = new MomentsAdapter(recyclerViewItemList, this);
        linearLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(linearLayoutManager);
        rvList.setAdapter(mAdapter);
        topbar.setBackgroundColor(0);
    }

    // 自定义的测试数据（假装这是网络请求并解析后的数据）
    private void loadMyTestDate() {
        // 先构造MyMedia
        String imgUrl1 = "https://i0.hdslb.com/bfs/album/0b6e13b1028b9a7426990034488b4af04b54c719.png";
        String imgUrl2 = "https://i0.hdslb.com/bfs/album/7db905515628e6c18d8a61f4369a505f1ab0dec2.jpg";
        String imgUrl3 = "https://i0.hdslb.com/bfs/album/f26eba49f3a8c8fc394f629aba27c7e1da812698.png";
        // 视频内容：敲架子鼓
        String videoUrl1 = "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4";
        // 视频内容：感受到鸭力
        String videoUrl2 = "http://gslb.miaopai.com/stream/w95S1LIlrb4Hi4zGbAtC4TYx0ta4BVKr-PXjuw__.mp4?vend=miaopai&ssig=8f20ca2d86ec365f0f777b769184f8aa&time_stamp=1574944581588&mpflag=32&unique_id=1574940981591448";
        // 视频内容：狗崽子
        String videoUrl4 = "http://gslb.miaopai.com/stream/7-5Q7kCzeec9tu~9XvZAxNizNAL1TJC7KtJCuw__.mp4?vend=miaopai&ssig=82b42debfc2a51569bafe6ac7a993d89&time_stamp=1574944868488&mpflag=32&unique_id=1574940981591448";
        String videoUrl3 = videoUrl4;

        MyMedia myMedia1 = new MyMedia(imgUrl1, videoUrl1);
        MyMedia myMedia2 = new MyMedia(imgUrl2);
        MyMedia myMedia3 = new MyMedia(imgUrl3, videoUrl2);
        MyMedia myMedia4 = new MyMedia(imgUrl1, videoUrl3);
        MyMedia myMedia5 = new MyMedia(imgUrl3, videoUrl4);
        // 再构造mediaList
        // 1张图片
        ArrayList<MyMedia> mediaList1 = new ArrayList<>();
        mediaList1.add(myMedia2);
        // 2张图片
        ArrayList<MyMedia> mediaList2 = new ArrayList<>();
        mediaList2.add(myMedia1);
        mediaList2.add(myMedia2);
        // 4张图片
        ArrayList<MyMedia> mediaList4 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mediaList4.add(myMedia1);
            mediaList4.add(myMedia2);
        }
        // 10张图片
        ArrayList<MyMedia> mediaList10 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mediaList10.add(myMedia1);
            mediaList10.add(myMedia2);
            mediaList10.add(myMedia3);
            mediaList10.add(myMedia4);
            mediaList10.add(myMedia5);
        }

        Location location = new Location();
        location.setAddress("Test Address");
        // 最后构造EvaluationItem
        final RecyclerViewItem recyclerViewItem1 = new RecyclerViewItem(mediaList1, "河北经贸大学自强社是在校学生处指导、学生资助管理中心主办下，于2008年4月15日注册成立的，一个以在校学生为主体的学生公益社团。历经十年的发展，在学生处、学生资助管理中心的大力支持下，在每一届自强人的团结努力下，自强社已经由成... ", "2019-11-02",
                "10080", "自强社", location, imgUrl1);
        final RecyclerViewItem recyclerViewItem2 = new RecyclerViewItem(mediaList2, "河北经贸大学信息技术学院成立于1996年，由原计算机系/经济信息系合并组建而成，是我校建设的第一批学院。", "2019-11-02",
                "10080", "信息技术学院", location, imgUrl2);
        final RecyclerViewItem recyclerViewItem4 = new RecyclerViewItem(mediaList4, "河北经贸大学信息技术学院成立于1996年，由原计算机系/经济信息系合并组建而成，是我校建设的第一批学院。", "2019-11-02",
                "10080", "信息技术学院", location, imgUrl2);
        final RecyclerViewItem recyclerViewItem10 = new RecyclerViewItem(mediaList10, "河北经贸大学雷雨话剧社是河北经贸大学唯一以话剧为主，兼小品，相声等多种表演艺术形式，由一批热爱表演，热爱话剧，热爱中国传统艺术与当代流行艺术结合的同学共同组成的文艺类大型社团。雷雨话剧社坚持以追求话剧“更新颖”、“更大型”、“更专业”为奋斗目标，坚持在继承传统文化和前辈的演出经验... ", "2019-11-02",
                "10080", "雷雨话剧社", location, imgUrl3);
        recyclerViewItemList.add(recyclerViewItem1);
        recyclerViewItemList.add(recyclerViewItem2);
        recyclerViewItemList.add(recyclerViewItem4);
        recyclerViewItemList.add(recyclerViewItem10);
    }

    @OnClick({R.id.sdv_avatar, R.id.iv_camera, R.id.cover})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sdv_avatar:
                ToastUtils.showShort("点击了头像");
                break;
            case R.id.iv_camera:
                showPop(view);
                break;
            case R.id.cover:
                new ImagePicker()
                        .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .pickType(ImagePickType.MULTI)
                        .displayer(new ImagePickerLoader())
                        .maxNum(1)
                        .start(MomentsActivity.this, REQUEST_CODE_PICKER);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            for (ImageBean imageBean : list) {
                Glide.with(parallax).load(imageBean.getImagePath()).into(parallax);
            }
        }
    }

    private void showPop(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.popup_window_add_friends_circle, null);
        // 给popwindow加上动画效果
        LinearLayout mPopRootLl = view.findViewById(R.id.ll_pop_root);
        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        mPopRootLl.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
        // 设置popwindow的宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mPopupWindow = new PopupWindow(view, dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.5f);  //透明度

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        // 弹出的位置
        mPopupWindow.showAtLocation(flRoot, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        // 取消
        RelativeLayout mCancelRl = view.findViewById(R.id.rl_cancel);
        RelativeLayout mAddByAlbum = view.findViewById(R.id.rl_add_by_album);
        mCancelRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        mAddByAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(FriendsCircleActivity.this, FriendsCircleSendActivity.class);
//                startActivity(intent);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     * 1.0完全不透明，0.0f完全透明
     *
     * @param bgAlpha 透明度值
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }
}
