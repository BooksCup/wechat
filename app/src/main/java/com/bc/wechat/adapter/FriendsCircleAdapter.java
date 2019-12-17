package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.bc.wechat.R;
import com.bc.wechat.activity.ViewPagerImageActivity;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.utils.TimestampUtil;
import com.bc.wechat.widget.FriendsCirclePhotoGridView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class FriendsCircleAdapter extends BaseAdapter {
    private List<FriendsCircle> friendsCircleList;
    private Context mContext;

    // 首页弹出框
    private PopupWindow mPopupWindow;
    private View mPopupView;

    public FriendsCircleAdapter(List<FriendsCircle> friendsCircleList, Context context) {
        this.friendsCircleList = friendsCircleList;
        this.mContext = context;
    }

    public void setData(List<FriendsCircle> dataList) {
        this.friendsCircleList = dataList;
    }

    public void addData(List<FriendsCircle> dataList) {
        this.friendsCircleList.addAll(dataList);
    }

    @Override
    public int getCount() {
        return friendsCircleList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsCircleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendsCircle friendsCircle = friendsCircleList.get(position);
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_friends_circle, null);
            viewHolder = new ViewHolder();
            viewHolder.mAvatarSdv = convertView.findViewById(R.id.sdv_avatar);
            viewHolder.mNickNameTv = convertView.findViewById(R.id.tv_nick_name);
            viewHolder.mContentTv = convertView.findViewById(R.id.tv_content);
            viewHolder.mMoreTv = convertView.findViewById(R.id.tv_more);
            viewHolder.mCreateTimeTv = convertView.findViewById(R.id.tv_create_time);
            viewHolder.mPhotosGv = convertView.findViewById(R.id.gv_friends_circle_photo);
            viewHolder.mCommentIb = convertView.findViewById(R.id.ib_comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAvatarSdv.setImageURI(Uri.parse(friendsCircle.getUserAvatar()));
        viewHolder.mNickNameTv.setText(friendsCircle.getUserNickName());
        viewHolder.mContentTv.setText(friendsCircle.getCircleContent());
        viewHolder.mCreateTimeTv.setText(TimestampUtil.getTimePoint(friendsCircle.getTimestamp()));

        List<String> photoList;
        try {
            photoList = JSONArray.parseArray(friendsCircle.getCirclePhotos(), String.class);
            // 如果friendsCircle.getCirclePhotos为"", photoList == null
            if (null == photoList) {
                photoList = new ArrayList<>();
            }
        } catch (Exception e) {
            photoList = new ArrayList<>();
        }

        final ArrayList<String> photoArraylist = new ArrayList<>();
        photoArraylist.addAll(photoList);

        viewHolder.mPhotosGv.setAdapter(new FriendsCirclePhotoAdapter(photoList, mContext));

        viewHolder.mPhotosGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ViewPagerImageActivity.class);
                intent.putStringArrayListExtra("photoList", photoArraylist);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

        viewHolder.mMoreTv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewHolder.mContentTv.getLineCount() > 4) {
                    viewHolder.mMoreTv.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mMoreTv.setVisibility(View.GONE);
                }
                return true;
            }
        });

        viewHolder.mMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.mContentTv.getMaxLines() <= 4) {
                    viewHolder.mMoreTv.setText("收起");
                    viewHolder.mContentTv.setMaxLines(viewHolder.mContentTv.getLineCount());
                } else {
                    viewHolder.mMoreTv.setText("全文");
                    viewHolder.mContentTv.setMaxLines(4);
                }

            }
        });

        viewHolder.mCommentIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopupWindow();
                if (!mPopupWindow.isShowing()) {
                    mPopupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int popupWidth = mPopupView.getMeasuredWidth();
                    int popupHeight = mPopupView.getMeasuredHeight();
                    int[] location = new int[2];
                    viewHolder.mCommentIb.getLocationOnScreen(location);
                    int x = location[0] - popupWidth - 10;
                    int y = location[1] - popupHeight / 2 + viewHolder.mCommentIb.getHeight() / 2;

                    mPopupWindow.showAtLocation(viewHolder.mCommentIb, Gravity.NO_GRAVITY, x, y);
                } else {
                    mPopupWindow.dismiss();
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView mAvatarSdv;
        TextView mNickNameTv;
        TextView mContentTv;
        TextView mMoreTv;
        TextView mCreateTimeTv;
        FriendsCirclePhotoGridView mPhotosGv;
        ImageButton mCommentIb;
    }

    /**
     * 初始化首页弹出框
     */
    private void initPopupWindow() {
        mPopupView = View.inflate(mContext, R.layout.popup_window_friend_circle_interact, null);
        mPopupWindow = new PopupWindow();
        // 设置SelectPicPopupWindow的View
        mPopupWindow.setContentView(mPopupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 刷新状态
        mPopupWindow.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
    }
}
