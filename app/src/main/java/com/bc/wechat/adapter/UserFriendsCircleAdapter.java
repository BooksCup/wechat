package com.bc.wechat.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bc.wechat.R;
import com.bc.wechat.entity.UserFriendsCircle;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.TimestampUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class UserFriendsCircleAdapter extends BaseAdapter {
    private List<UserFriendsCircle> userFriendsCircleList;
    private Context mContext;

    public UserFriendsCircleAdapter(List<UserFriendsCircle> userFriendsCircleList, Context context) {
        this.userFriendsCircleList = userFriendsCircleList;
        this.mContext = context;
    }

    public void setData(List<UserFriendsCircle> dataList) {
        this.userFriendsCircleList = dataList;
    }

    public List<UserFriendsCircle> getData() {
        return this.userFriendsCircleList;
    }

    public void addData(List<UserFriendsCircle> dataList) {
        this.userFriendsCircleList.addAll(dataList);
    }

    @Override
    public int getCount() {
        return userFriendsCircleList.size();
    }

    @Override
    public Object getItem(int position) {
        return userFriendsCircleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final UserFriendsCircle userFriendsCircle = userFriendsCircleList.get(position);

        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_friends_circle, null);
            viewHolder = new ViewHolder();

            viewHolder.mTimeMonthTv = convertView.findViewById(R.id.tv_time_month);
            viewHolder.mTimeDayTv = convertView.findViewById(R.id.tv_time_day);

            viewHolder.mCircleContentRl = convertView.findViewById(R.id.rl_circle_content);
            viewHolder.mCircleContentTv = convertView.findViewById(R.id.tv_circle_content);

            viewHolder.mCircleContentWithPhotoRl = convertView.findViewById(R.id.rl_circle_content_with_photo);
            viewHolder.mCircleContentWithPhotoTv = convertView.findViewById(R.id.tv_circle_content_with_photo);
            viewHolder.mCirclePhotoNumTv = convertView.findViewById(R.id.tv_circle_photo_num);

            viewHolder.mCirclePhoto1Ll = convertView.findViewById(R.id.ll_circle_photo_1);
            viewHolder.mCirclePhoto2Ll = convertView.findViewById(R.id.ll_circle_photo_2);
            viewHolder.mCirclePhoto3Rl = convertView.findViewById(R.id.rl_circle_photo_3);
            viewHolder.mCirclePhoto4Rl = convertView.findViewById(R.id.rl_circle_photo_4);

            viewHolder.mCirclePhoto1Sdv = convertView.findViewById(R.id.sdv_circle_photo_1);

            viewHolder.mCirclePhoto2_1Sdv = convertView.findViewById(R.id.sdv_circle_photo_2_1);
            viewHolder.mCirclePhoto2_2Sdv = convertView.findViewById(R.id.sdv_circle_photo_2_2);

            viewHolder.mCirclePhoto3_1Sdv = convertView.findViewById(R.id.sdv_circle_photo_3_1);
            viewHolder.mCirclePhoto3_2Sdv = convertView.findViewById(R.id.sdv_circle_photo_3_2);
            viewHolder.mCirclePhoto3_3Sdv = convertView.findViewById(R.id.sdv_circle_photo_3_3);

            viewHolder.mCirclePhoto4_1Sdv = convertView.findViewById(R.id.sdv_circle_photo_4_1);
            viewHolder.mCirclePhoto4_2Sdv = convertView.findViewById(R.id.sdv_circle_photo_4_2);
            viewHolder.mCirclePhoto4_3Sdv = convertView.findViewById(R.id.sdv_circle_photo_4_3);
            viewHolder.mCirclePhoto4_4Sdv = convertView.findViewById(R.id.sdv_circle_photo_4_4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        List<String> photoList = CommonUtil.getListFromJson(userFriendsCircle.getCirclePhotos(), String.class);
        if (0 == photoList.size()) {
            // 纯文字
            viewHolder.mCircleContentRl.setVisibility(View.VISIBLE);
            viewHolder.mCircleContentWithPhotoRl.setVisibility(View.GONE);

            viewHolder.mCircleContentTv.setText(userFriendsCircle.getCircleContent());
        } else {
            // 图文
            viewHolder.mCircleContentRl.setVisibility(View.GONE);
            viewHolder.mCircleContentWithPhotoRl.setVisibility(View.VISIBLE);

            viewHolder.mCircleContentWithPhotoTv.setText(userFriendsCircle.getCircleContent());
        }


        if (0 == photoList.size()) {
            viewHolder.mCirclePhoto1Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto2Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto3Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhoto4Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhotoNumTv.setVisibility(View.GONE);

        } else if (1 == photoList.size()) {
            viewHolder.mCirclePhoto1Ll.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhoto2Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto3Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhoto4Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhotoNumTv.setVisibility(View.GONE);
            viewHolder.mCirclePhoto1Sdv.setImageURI(Uri.parse(photoList.get(0)));

        } else if (2 == photoList.size()) {
            viewHolder.mCirclePhoto1Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto2Ll.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhoto3Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhoto4Rl.setVisibility(View.GONE);

            viewHolder.mCirclePhotoNumTv.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhotoNumTv.setText("共" + photoList.size() + "张");

            viewHolder.mCirclePhoto2_1Sdv.setImageURI(Uri.parse(photoList.get(0)));
            viewHolder.mCirclePhoto2_2Sdv.setImageURI(Uri.parse(photoList.get(1)));

        } else if (3 == photoList.size()) {
            viewHolder.mCirclePhoto1Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto2Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto3Rl.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhoto4Rl.setVisibility(View.GONE);

            viewHolder.mCirclePhotoNumTv.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhotoNumTv.setText("共" + photoList.size() + "张");

            viewHolder.mCirclePhoto3_1Sdv.setImageURI(Uri.parse(photoList.get(0)));
            viewHolder.mCirclePhoto3_2Sdv.setImageURI(Uri.parse(photoList.get(1)));
            viewHolder.mCirclePhoto3_3Sdv.setImageURI(Uri.parse(photoList.get(2)));

        } else if (4 == photoList.size()) {
            viewHolder.mCirclePhoto1Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto2Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto3Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhoto4Rl.setVisibility(View.VISIBLE);

            viewHolder.mCirclePhotoNumTv.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhotoNumTv.setText("共" + photoList.size() + "张");

            viewHolder.mCirclePhoto4_1Sdv.setImageURI(Uri.parse(photoList.get(0)));
            viewHolder.mCirclePhoto4_2Sdv.setImageURI(Uri.parse(photoList.get(1)));
            viewHolder.mCirclePhoto4_3Sdv.setImageURI(Uri.parse(photoList.get(2)));
            viewHolder.mCirclePhoto4_4Sdv.setImageURI(Uri.parse(photoList.get(3)));

        } else {
            viewHolder.mCirclePhoto1Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto2Ll.setVisibility(View.GONE);
            viewHolder.mCirclePhoto3Rl.setVisibility(View.GONE);
            viewHolder.mCirclePhoto4Rl.setVisibility(View.VISIBLE);

            viewHolder.mCirclePhotoNumTv.setVisibility(View.VISIBLE);
            viewHolder.mCirclePhotoNumTv.setText("共" + photoList.size() + "张");

            viewHolder.mCirclePhoto4_1Sdv.setImageURI(Uri.parse(photoList.get(0)));
            viewHolder.mCirclePhoto4_2Sdv.setImageURI(Uri.parse(photoList.get(1)));
            viewHolder.mCirclePhoto4_3Sdv.setImageURI(Uri.parse(photoList.get(2)));
            viewHolder.mCirclePhoto4_4Sdv.setImageURI(Uri.parse(photoList.get(3)));
        }

        String[] time = TimestampUtil.getYearMonthAndDay(userFriendsCircle.getTimestamp());
        viewHolder.mTimeMonthTv.setVisibility(View.VISIBLE);
        viewHolder.mTimeMonthTv.setText(time[1] + "月");
        viewHolder.mTimeDayTv.setVisibility(View.VISIBLE);
        viewHolder.mTimeDayTv.setText(time[2]);

        if (position != 0) {
            UserFriendsCircle lastUserFriendsCircle = userFriendsCircleList.get(position - 1);
            String[] lastTime = TimestampUtil.getYearMonthAndDay(lastUserFriendsCircle.getTimestamp());
            if (time[1].equals(lastTime[1]) && time[2].equals(lastTime[2])) {
                viewHolder.mTimeMonthTv.setVisibility(View.INVISIBLE);
                viewHolder.mTimeDayTv.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.mTimeMonthTv.setVisibility(View.VISIBLE);
                viewHolder.mTimeDayTv.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTimeMonthTv;
        TextView mTimeDayTv;

        RelativeLayout mCircleContentWithPhotoRl;
        TextView mCircleContentWithPhotoTv;

        RelativeLayout mCircleContentRl;
        TextView mCircleContentTv;
        TextView mCirclePhotoNumTv;

        LinearLayout mCirclePhoto1Ll;
        LinearLayout mCirclePhoto2Ll;
        RelativeLayout mCirclePhoto3Rl;
        RelativeLayout mCirclePhoto4Rl;

        SimpleDraweeView mCirclePhoto1Sdv;

        SimpleDraweeView mCirclePhoto2_1Sdv;
        SimpleDraweeView mCirclePhoto2_2Sdv;

        SimpleDraweeView mCirclePhoto3_1Sdv;
        SimpleDraweeView mCirclePhoto3_2Sdv;
        SimpleDraweeView mCirclePhoto3_3Sdv;

        SimpleDraweeView mCirclePhoto4_1Sdv;
        SimpleDraweeView mCirclePhoto4_2Sdv;
        SimpleDraweeView mCirclePhoto4_3Sdv;
        SimpleDraweeView mCirclePhoto4_4Sdv;

    }
}

