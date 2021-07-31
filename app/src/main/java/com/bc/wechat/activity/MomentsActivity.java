package com.bc.wechat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.MomentsAdapter;
import com.bc.wechat.moments.adapter.Utils;
import com.bc.wechat.moments.bean.ExploreDongtaiBean;
import com.bc.wechat.moments.bean.ExplorePostDianzanBean;
import com.bc.wechat.moments.bean.ExplorePostPinglunBean;
import com.bc.wechat.moments.listener.Explore_dongtai1_listener;
import com.bc.wechat.moments.utils.CustomDotIndexProvider;
import com.bc.wechat.moments.utils.CustomLoadingUIProvider;
import com.bc.wechat.moments.utils.GlideSimpleLoader;
import com.bc.wechat.moments.utils.KeyboardUtil;
import com.bc.wechat.moments.widget.LikePopupWindow;
import com.bc.wechat.moments.widget.OnPraiseOrCommentClickListener;
import com.bc.wechat.utils.StatusBarUtil;
import com.bc.wechat.views.CustomProgressDrawable;
import com.bc.wechat.views.CustomSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import byc.imagewatcher.ImageWatcher;
import byc.imagewatcher.ImageWatcherHelper;

/**
 * 朋友圈
 *
 * @author zhou
 */
public class MomentsActivity extends BaseActivity2 implements Explore_dongtai1_listener, ImageWatcher.OnPictureLongPressListener {

    @BindView(R.id.srl_refresh)
    CustomSwipeRefreshLayout mRefreshSrl;

    private RecyclerView mRecyclerView;

    private MomentsAdapter mAdapter;
    private List<ExploreDongtaiBean> mList = new ArrayList<>();

    private LinearLayout llComment;
    private EditText etComment;
    private TextView tvSend;

    public ImageWatcherHelper iwHelper;//方式二

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);
        ButterKnife.bind(this);
        StatusBarUtil.setStatusBarColor(MomentsActivity.this, R.color.color_moments_default_cover);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        llComment = findViewById(R.id.ll_comment);
        etComment = findViewById(R.id.et_comment);
        tvSend = findViewById(R.id.tv_send_comment);
        boolean isTranslucentStatus = false;
        //        新的初始化方式二，不再需要在布局文件中加入<ImageWatcher>标签 减少布局嵌套
        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(this) : 0) // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
                .setErrorImageRes(R.mipmap.error_picture) // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
                .setOnPictureLongPressListener(this)
                .setOnStateChangedListener(new ImageWatcher.OnStateChangedListener() {
                    @Override
                    public void onStateChangeUpdate(ImageWatcher imageWatcher, ImageView clicked, int position, Uri uri, float animatedValue, int actionTag) {
                        Log.e("IW", "onStateChangeUpdate [" + position + "][" + uri + "][" + animatedValue + "][" + actionTag + "]");
                    }

                    @Override
                    public void onStateChanged(ImageWatcher imageWatcher, int position, Uri uri, int actionTag) {
                        if (actionTag == ImageWatcher.STATE_ENTER_DISPLAYING) {
                            //  Toast.makeText(getApplicationContext(), "点击了图片 [" + position + "]" + uri + "", Toast.LENGTH_SHORT).show();
                        } else if (actionTag == ImageWatcher.STATE_EXIT_HIDING) {
                            //  Toast.makeText(getApplicationContext(), "退出了查看大图", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setIndexProvider(new CustomDotIndexProvider())//自定义页码指示器（默认数字）
                .setLoadingUIProvider(new CustomLoadingUIProvider()); // 自定义LoadingUI


        setData();

        mAdapter = new MomentsAdapter(mList, this, this);
        mAdapter.setIwHelper(iwHelper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);
        setHeader(mRecyclerView);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideInput();
                return false;
            }
        });

        CustomProgressDrawable drawable = new CustomProgressDrawable(this, mRefreshSrl);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moments_refresh_icon);
        drawable.setBitmap(bitmap);

        mRefreshSrl.setProgressView(drawable);
        mRefreshSrl.setBackgroundColor(Color.BLACK);
        mRefreshSrl.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT);//背景设置透明
        mRefreshSrl.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        mRefreshSrl.setRefreshing(false);
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });
    }

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(this).inflate(R.layout.item_my_moments_header, view, false);
        mAdapter.setHeaderView(header);
    }

    public void setData() {
        //文字示例
        ExploreDongtaiBean bean = new ExploreDongtaiBean();
        bean.setType(1);
        bean.setCreattime(System.currentTimeMillis());
        bean.setId(469);
        bean.setUserid(28);
        bean.setNickname("悠麦尔");
        bean.setHandimg("https://image.renlaibang.com/Fpi0bpgGfdUP1zC5FUdMxZaSowos");
        bean.setWrittenwords("测试内容\nf\nw\nss");
        bean.setLikeuself(1);
//        "altnickname":"",
//                "altuserid":"",
//                "content":"八菱科技",
//                "creattime":1608632610000,
//                "friendid":468,
//                "id":60,
//                "userid":28,
//                "usernickname":"污嫚尔"
//    }
        List<ExplorePostPinglunBean> evea = new ArrayList<>();
        ExplorePostPinglunBean pinglunBean = new ExplorePostPinglunBean();
        pinglunBean.setId(60);
        pinglunBean.setUserid(28);
        pinglunBean.setUsernickname("悠麦尔");
        pinglunBean.setContent("hello word");
        pinglunBean.setCreattime(1608632610000l);
        pinglunBean.setFriendid(468);
        evea.add(pinglunBean);

        ExplorePostPinglunBean pinglunBean1 = new ExplorePostPinglunBean();
        pinglunBean1.setId(60);
        pinglunBean1.setUserid(29);
        pinglunBean1.setUsernickname("老鹰");
        pinglunBean1.setContent("你好");
        pinglunBean1.setCreattime(1608632610000l);
        pinglunBean1.setFriendid(468);
        pinglunBean1.setAltuserid(28 + "");
        pinglunBean1.setAltnickname("悠麦尔");
        evea.add(pinglunBean1);

        bean.setEvea(evea);

        List<ExplorePostDianzanBean> fabulous = new ArrayList<>();//点赞列表
        ExplorePostDianzanBean dianzanBean = new ExplorePostDianzanBean();
        dianzanBean.setCreattime(1608958770000l);
        dianzanBean.setFriendid(468);
        dianzanBean.setId(294);
        dianzanBean.setUserid(28);
        dianzanBean.setUsernickname("umr");
        fabulous.add(dianzanBean);

        ExplorePostDianzanBean dianzanBean1 = new ExplorePostDianzanBean();
        dianzanBean1.setCreattime(1608958770001l);
        dianzanBean1.setFriendid(468);
        dianzanBean1.setId(295);
        dianzanBean1.setUserid(29);
        dianzanBean1.setUsernickname("悠麦尔");
        fabulous.add(dianzanBean1);

        bean.setFabulous(fabulous);
        mList.add(bean);
        //图片示例
        ExploreDongtaiBean bean1 = new ExploreDongtaiBean();
        bean1.setType(2);
        bean1.setCreattime(System.currentTimeMillis());
        bean1.setId(469);
        bean1.setUserid(28);
        bean1.setNickname("悠麦尔");
        bean1.setHandimg("https://image.renlaibang.com/Fpi0bpgGfdUP1zC5FUdMxZaSowos");
        bean1.setWrittenwords("测试内容\nf\nw\nss");
        bean1.setLikeuself(1);
        bean1.setThumbnail("https://image.renlaibang.com/Fhn9WrrxQBPO0IAm395oD3f-k2ZD,https://image.renlaibang.com/Fpbcw0AeZIB3gs7unQIvCAL7Gd0e,https://image.renlaibang.com/Fp5mQrZYnhSRoKayTsPEaBO18Wrt,https://image.renlaibang.com/Ft4S9melEv7AG4QZQKPRZXJltJOk,https://image.renlaibang.com/FgiOlNKk71GfgzunMvRXwi1zzIPp,https://image.renlaibang.com/FrbthQNvH4Y9yM4T_QfydcJhZEiY,https://image.renlaibang.com/Fkvw8YZtmg8kICNRgagrcA53NVvk");
        bean1.setImgs("https://image.renlaibang.com/FiP9T9ncrNL-AH45IMhQaZgqwwSk,https://image.renlaibang.com/Fpbcw0AeZIB3gs7unQIvCAL7Gd0e,https://image.renlaibang.com/Fp5mQrZYnhSRoKayTsPEaBO18Wrt,https://image.renlaibang.com/Fv4GWEHOMvdMkBHz12XWxtEJMSd1,https://image.renlaibang.com/FgiOlNKk71GfgzunMvRXwi1zzIPp,https://image.renlaibang.com/FhAkT41j6Ma5wGRmYcm4MQF2NjSt,https://image.renlaibang.com/Fj8opkB-dS_Jmg-K41oWRhJQs0i0");
        mList.add(bean1);
        //视频示例
        ExploreDongtaiBean bean2 = new ExploreDongtaiBean();
        bean2.setType(3);
        bean2.setCreattime(System.currentTimeMillis());
        bean2.setId(469);
        bean2.setUserid(28);
        bean2.setNickname("悠麦尔");
        bean2.setHandimg("https://image.renlaibang.com/Fpi0bpgGfdUP1zC5FUdMxZaSowos");
        bean2.setWrittenwords("测试内容\nf\nw\nss");
        bean2.setLikeuself(1);
        bean2.setThumbnail("https://image.renlaibang.com/FmQdWZrmemYUMGrnNFfewz6t4HAR");
        bean2.setVideos("https://image.renlaibang.com/FkHt1Sft6H0zrhAzwWWfdwrw0h4L");
        mList.add(bean2);
    }

    //评论
    @Override
    public void onPinlunEdit(View view, int friendid, String userid, String userName) {
        showPinglunPopupWindow1(view, friendid, userid, userName);
    }

    //点击昵称，头像
    @Override
    public void onClickUser(String userid) {

    }

    //点击评论点赞按钮
    @Override
    public void onClickEdit(View view, int position) {
        //评论弹框
        showLikePopupWindow(view, position);
    }

    //点击删除朋友圈按钮
    @Override
    public void deletePengyouquan(int id) {

    }

    //删除评论按钮
    @Override
    public void deleteMypinglun(int ids, ExplorePostPinglunBean id) {

    }

    //点击图片
    @Override
    public void imageOnclick() {

    }

    //点击视频
    @Override
    public void videoOnclick(String img, String httpUrl) {
        hideInput();
        Intent dynamicHaoyou = new Intent(this, ExploreVideoPlayer.class);
        dynamicHaoyou.putExtra("typeImg", img);
        dynamicHaoyou.putExtra("typeHttpUrl", httpUrl);
        startActivity(dynamicHaoyou);
    }

    public void hideInput() {
        llComment.setVisibility(View.GONE);
        KeyboardUtil.hideSoftInput(this, etComment);
    }

    private LikePopupWindow likePopupWindow;

    private void showLikePopupWindow(final View view, int position) {
        //item 底部y坐标
        final int mBottomY = getCoordinateY(view) + view.getHeight();
        if (likePopupWindow == null) {
            likePopupWindow = new LikePopupWindow(this, 0);//0.1,分别代表是否点赞
        }
        likePopupWindow.setOnPraiseOrCommentClickListener(new OnPraiseOrCommentClickListener() {
            @Override
            public void onPraiseClick(int position) {
                //调用点赞接口
                likePopupWindow.dismiss();
            }

            @Override
            public void onCommentClick(int position) {
                llComment.setVisibility(View.VISIBLE);
                etComment.requestFocus();
                etComment.setHint("说点什么");

                KeyboardUtil.showSoftInput(MomentsActivity.this);
                likePopupWindow.dismiss();
                etComment.setText("");
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int y = getCoordinateY(llComment);// - 20;
                        //评论时滑动到对应item底部和输入框顶部对齐
                        mRecyclerView.smoothScrollBy(0, mBottomY - y);
                    }
                }, 300);
            }

            @Override
            public void onClickFrendCircleTopBg() {

            }

            @Override
            public void onDeleteItem(String id, int position) {

            }

        }).setTextView(0).setCurrentPosition(position);
        if (likePopupWindow.isShowing()) {
            likePopupWindow.dismiss();
        } else {
            likePopupWindow.showPopupWindow(view);
        }
    }

    /**
     * 获取控件左上顶点Y坐标
     *
     * @param view
     * @return
     */
    private int getCoordinateY(View view) {
        int[] coordinate = new int[2];
        view.getLocationOnScreen(coordinate);
        return coordinate[1];
    }

    @Override
    public void onPictureLongPress(ImageView v, Uri uri, int pos) {

    }

    @Override
    public void onBackPressed() {
        //方式二
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
    }

    //当前我给谁发评论，
    private void showPinglunPopupWindow1(View view, int friendid, String userid, String username) {
        //item 底部y坐标
        final int mBottomY = getCoordinateY(view) + view.getHeight();

        llComment.setVisibility(View.VISIBLE);
        etComment.requestFocus();
        if (userid == null || userid.equals("")) {//回复这条评论的发送人，楼主
            etComment.setHint("说点什么");
        } else {
            etComment.setHint("回复:" + username);//回复这个人
        }

        KeyboardUtil.showSoftInput(this, etComment);
        etComment.setText("");
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                int y = getCoordinateY(llComment);// - 20;
                //评论时滑动到对应item底部和输入框顶部对齐
                mRecyclerView.smoothScrollBy(0, mBottomY - y);
            }
        }, 300);

    }
}