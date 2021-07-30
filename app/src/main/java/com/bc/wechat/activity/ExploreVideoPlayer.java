package com.bc.wechat.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bc.wechat.R;

import org.song.videoplayer.DemoQSVideoView;
import org.song.videoplayer.IVideoPlayer;
import org.song.videoplayer.PlayListener;
import org.song.videoplayer.QSVideo;
import org.song.videoplayer.Util;
import org.song.videoplayer.media.AndroidMedia;
import org.song.videoplayer.media.BaseMedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ExploreVideoPlayer extends AppCompatActivity {
    private String typeImg = "";
    private String typeHttpUrl = "";

    DemoQSVideoView demoVideoView;

    String url;
    Class<? extends BaseMedia> decodeMedia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19)//透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_explore_dynamic_video_player);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent intent = getIntent();
        typeImg = intent.getStringExtra("typeImg");
        typeHttpUrl = intent.getStringExtra("typeHttpUrl");

        demoVideoView = findViewById(R.id.qs);
//        demoVideoView.getCoverImageView().setImageResource(R.mipmap.cover);
        demoVideoView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));//getResources().getDisplayMetrics().widthPixels * 9 / 16)
        //进入全屏的模式 0横屏 1竖屏 2传感器自动横竖屏 3根据视频比例自动确定横竖屏      -1什么都不做
        demoVideoView.enterFullMode = 3;
        //是否非全屏下也可以手势调节进度
        demoVideoView.isWindowGesture = true;
        demoVideoView.setPlayListener(new PlayListener() {

            int mode;

            @Override
            public void onStatus(int status) {//播放状态
                if (status == IVideoPlayer.STATE_AUTO_COMPLETE)
                    demoVideoView.quitWindowFullscreen();//播放完成退出全屏
            }

            @Override//全屏/普通/浮窗
            public void onMode(int mode) {
                this.mode = mode;
            }

            @Override
            public void onEvent(int what, Integer... extra) {
                if (what == DemoQSVideoView.EVENT_CONTROL_VIEW && mode == IVideoPlayer.MODE_WINDOW_NORMAL) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        if (extra[0] == 0)//状态栏隐藏/显示
                            Util.CLEAR_FULL(ExploreVideoPlayer.this);
                        else
                            Util.SET_FULL(ExploreVideoPlayer.this);
                    }
                }
                //系统浮窗点击退出退出activity
                if (what == DemoQSVideoView.EVENT_CLICK_VIEW
                        && extra[0] == R.id.help_float_close)
                    if (demoVideoView.isSystemFloatMode())
                        finish();
            }

        });
        play(typeHttpUrl, AndroidMedia.class);
    }

    private void play(String url, Class<? extends BaseMedia> decodeMedia) {
        demoVideoView.release();
        demoVideoView.setDecodeMedia(decodeMedia);

        demoVideoView.setUp(
                QSVideo.Build(url).title("这是标清标题").definition("标清").resolution("标清 720P").build(),
                QSVideo.Build(url).title("这是高清标题").definition("高清").resolution("高清 1080P").build(),
                QSVideo.Build(url).title("这是2K标题").definition("2K").resolution("超高清 2K").build(),
                QSVideo.Build(url).title("这是4K标题").definition("4K").resolution("极致 4K").build()
        );
//        demoVideoView.setUp(url, "这是一个标题");
        //demoVideoView.seekTo(12000);
        demoVideoView.openCache();//缓存配置见最后,缓存框架可能会出错,
        demoVideoView.play();
        this.url = url;
        this.decodeMedia = decodeMedia;

    }

    //返回键
    @Override
    public void onBackPressed() {
        //全屏和系统浮窗不finish
        if (demoVideoView.onBackPressed()) {
            if (demoVideoView.isSystemFloatMode())
                //系统浮窗返回上一界面 android:launchMode="singleTask"
                moveTaskToBack(true);
            return;
        }
        super.onBackPressed();
    }

    private boolean playFlag;//记录退出时播放状态 回来的时候继续播放
    private int position;//记录销毁时的进度 回来继续该进度播放
    private Handler handler = new Handler();

    @Override
    public void onResume() {
        super.onResume();
        if (playFlag)
            demoVideoView.play();
        handler.removeCallbacks(runnable);
        if (position > 0) {
            demoVideoView.seekTo(position);
            position = 0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (demoVideoView.isSystemFloatMode())
            return;
        //暂停
        playFlag = demoVideoView.isPlaying();
        demoVideoView.pause();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (demoVideoView.isSystemFloatMode())
            return;
        //进入后台不马上销毁,延时15秒
        handler.postDelayed(runnable, 1000 * 15);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();//销毁
        if (demoVideoView.isSystemFloatMode())
            demoVideoView.quitWindowFloat();
        demoVideoView.release();
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (demoVideoView.getCurrentState() != IVideoPlayer.STATE_AUTO_COMPLETE)
                position = demoVideoView.getPosition();
            demoVideoView.release();
        }
    };
}
