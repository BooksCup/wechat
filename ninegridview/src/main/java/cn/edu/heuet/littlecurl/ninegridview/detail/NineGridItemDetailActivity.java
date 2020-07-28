package cn.edu.heuet.littlecurl.ninegridview.detail;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.viewpager2.widget.ViewPager2;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.List;

import cn.edu.heuet.littlecurl.ninegridview.R;
import cn.edu.heuet.littlecurl.ninegridview.bean.NineGridItem;
import uk.co.senab.photoview.PhotoView;

/**
 * 图片详情呈现
 * 用到了ViewPager2
 * ViewPager2是谷歌2019年新推出的控件
 * 使用方法仅仅需要一个RecyclerView的适配器即可，别无其他
 */
public class NineGridItemDetailActivity extends Activity implements
        ViewTreeObserver.OnPreDrawListener {

    public static final String MEDIA_INFO = "MEDIA_INFO";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";
    private static final int ANIMATE_DURATION = 200;

    private static RelativeLayout rootView;         // 当前页面根布局
    private View mediaItemView;                     // 视频或图片布局
    private List<NineGridItem> nineGridItemList;    // 视频地址集合
    private int currentItem;                        // 当前页面索引
    private int lastItem;                           // 上一个页面索引

    private ViewPager2Adapter viewPager2Adapter;
    private int mediaItemViewRealHeight;
    private int mediaItemViewRealWidth;
    private int screenWidth;
    private int screenHeight;
    private ViewPager2 viewPager2;
    private TextView tv_pager;
    private Context context;

    public static RelativeLayout getRootView() {
        return rootView;
    }

    @SuppressLint("StringFormatMatches")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninegrid_itemdetail);
        context = NineGridItemDetailActivity.this;
        viewPager2 = findViewById(R.id.viewPager2);
        tv_pager = findViewById(R.id.tv_pager);
        rootView = findViewById(R.id.rootView);
        rootView.setBackgroundColor(Color.BLACK);

        // 测量整个屏幕的宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        // 获取传递过来的数据
        Intent intent = getIntent();
        nineGridItemList = (List<NineGridItem>) intent.getSerializableExtra(MEDIA_INFO);
        currentItem = intent.getIntExtra(CURRENT_ITEM, 0);
        // onCreate中更新底部文字
        tv_pager.setText(String.format(getString(R.string.select), currentItem + 1, nineGridItemList.size()));
        // ViewPager2适配器，本质是RecyclerView的适配器
        viewPager2Adapter = new ViewPager2Adapter(this, nineGridItemList);
        viewPager2.setAdapter(viewPager2Adapter);
        /**************  下面这句必须写在设置完适配器之后，因为必须先有数据，才能跳转到具体的页面 **************/
        // onCreate中设置当前显示页面
        viewPager2.setCurrentItem(currentItem, false);
        /******************************************************************************************************/
        // 增加装饰（每一页左右padding）
        viewPager2.addItemDecoration(new SpaceItemDecoration(this, 10));
        // 监听ViewPager2的页面切换事件
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback);
    }

    // 实现页面切换的监听事件，为防止onCreate()方法过于臃肿，把它写到外面了
    ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);

        }

        /**
         * 页面切换结束位置: position
         */
        @SuppressLint("StringFormatMatches")
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // 记录位置变化
            lastItem = currentItem;
            currentItem = position;
            // 更新底部文字
            tv_pager.setText(String.format(getString(R.string.select), currentItem + 1, nineGridItemList.size()));
            // 大于0说明有播放
            if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                //当前播放的位置
                int currentPlayingPosition = GSYVideoManager.instance().getPlayPosition();
                if (currentPlayingPosition != currentItem) {
                    GSYVideoManager.onPause();
                    if (!GSYVideoManager.isFullState((Activity) context)) {
                        GSYVideoManager.releaseAllVideos();
                        viewPager2Adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    /**
     * 返回箭头
     */
    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    //-------------------------------- Activity生命周期相关 --------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }
    //-------------------------------- 动画相关方法 ↓↓↓ --------------------------------

    /**
     * 点击九宫格之后，绘制之前 的动画
     */
    @Override
    public boolean onPreDraw() {
        rootView.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = viewPager2Adapter.getPrimaryItem();
        final PhotoView photoView = viewPager2Adapter.getPrimaryPhotoView();
        final StandardGSYVideoPlayer gsyVideoPlayer = viewPager2Adapter.getPrimaryVideoView();

        if (existVideoUrl(nineGridItemList, currentItem)) {
            mediaItemView = gsyVideoPlayer;
            computeMediaViewWidthAndHeight(mediaItemView);
        } else {
            mediaItemView = photoView;
            computeMediaViewWidthAndHeight(mediaItemView);
        }

        final NineGridItem nineGridItem = nineGridItemList.get(currentItem);
        final float vx = nineGridItem.nineGridViewItemWidth * 1.0f / mediaItemViewRealWidth;
        final float vy = nineGridItem.nineGridViewItemHeight * 1.0f / mediaItemViewRealHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1)
                    fraction = 1;
                view.setTranslationX(evaluateInt(fraction, nineGridItem.nineGridViewItemX + nineGridItem.nineGridViewItemWidth / 2 - mediaItemView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, nineGridItem.nineGridViewItemY + nineGridItem.nineGridViewItemHeight / 2 - mediaItemView.getHeight() / 2, 0));
                // 缩放
                view.setScaleX(evaluateFloat(fraction, vx, 1));
                view.setScaleY(evaluateFloat(fraction, vy, 1));
                view.setAlpha(fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.TRANSPARENT, Color.BLACK));
            }
        });
        addIntoListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
        return true;
    }

    /**
     * 单击图片之后，退出详情页的动画
     */
    public void finishActivityAnim() {
        final View view = viewPager2Adapter.getPrimaryItem();
        final ImageView imageView = viewPager2Adapter.getPrimaryPhotoView();
        final StandardGSYVideoPlayer gsyVideoPlayer = viewPager2Adapter.getPrimaryVideoView();
        if (existVideoUrl(nineGridItemList, currentItem)) {
            mediaItemView = gsyVideoPlayer;
            computeMediaViewWidthAndHeight(mediaItemView);
        } else {
            mediaItemView = imageView;
            computeMediaViewWidthAndHeight(mediaItemView);
        }
        final NineGridItem nineGridItem = nineGridItemList.get(currentItem);
        final float vx = nineGridItem.nineGridViewItemWidth * 1.0f / mediaItemViewRealWidth;
        final float vy = nineGridItem.nineGridViewItemHeight * 1.0f / mediaItemViewRealHeight;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1)
                    fraction = 1;
                view.setTranslationX(evaluateInt(fraction, nineGridItem.nineGridViewItemX + nineGridItem.nineGridViewItemWidth / 2 - mediaItemView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, nineGridItem.nineGridViewItemY + nineGridItem.nineGridViewItemHeight / 2 - mediaItemView.getHeight() / 2, 0));
                view.setScaleX(evaluateFloat(fraction, 1, vx));
                view.setScaleY(evaluateFloat(fraction, 1, vy));
                view.setAlpha(1 - fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.BLACK, Color.TRANSPARENT));
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
    }

    /**
     * 计算图片/视频的宽高
     */
    private void computeMediaViewWidthAndHeight(View view) {
        if (view instanceof PhotoView) {
            PhotoView photoView = (PhotoView) view;
            // 获取真实大小
            Drawable drawable = photoView.getDrawable();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int intrinsicWidth = drawable.getIntrinsicWidth();
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            float h = screenHeight * 1.0f / intrinsicHeight;
            float w = screenWidth * 1.0f / intrinsicWidth;
            if (h > w) h = w;
            else w = h;
            // 得出当宽高至少有一个充满的时候图片对应的宽高
            mediaItemViewRealHeight = (int) (intrinsicHeight * h);
            mediaItemViewRealWidth = (int) (intrinsicWidth * w);
        } else if (view instanceof VideoView) {
            VideoView videoView = (VideoView) view;
            int intrinsicHeight = videoView.getHeight();
            int intrinsicWidth = videoView.getHeight();
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            float h = screenHeight * 1.0f / intrinsicHeight;
            float w = screenWidth * 1.0f / intrinsicWidth;
            if (h > w) h = w;
            else w = h;
            // 得出当宽高至少有一个充满的时候图片对应的宽高
            mediaItemViewRealHeight = (int) (intrinsicHeight * h);
            mediaItemViewRealWidth = (int) (intrinsicWidth * w);
        }
    }

    /**
     * 进场动画过程监听
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(Color.BLACK);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 退场动画过程监听
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(Color.BLACK);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * Integer 估值器
     */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    /**
     * @param nineGridItemList
     * @param position
     * @return
     */
    private boolean existVideoUrl(List<NineGridItem> nineGridItemList, int position) {
        return !TextUtils.isEmpty(nineGridItemList.get(position).getVideoUrl());
    }

}
