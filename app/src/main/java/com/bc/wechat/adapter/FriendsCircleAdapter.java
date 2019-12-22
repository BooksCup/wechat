package com.bc.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.activity.ViewPagerImageActivity;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.FriendsCircle;
import com.bc.wechat.entity.FriendsCircleComment;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.CommonUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TextParser;
import com.bc.wechat.utils.TimestampUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.FriendsCircleCommentListView;
import com.bc.wechat.widget.FriendsCirclePhotoGridView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FriendsCircleAdapter extends BaseAdapter {
    private List<FriendsCircle> friendsCircleList;
    private Context mContext;

    // 首页弹出框
    private PopupWindow mPopupWindow;
    private View mPopupView;

    private User mUser;
    private VolleyUtil mVolleyUtil;

    private ClickListener mClickListener;

    public FriendsCircleAdapter(List<FriendsCircle> friendsCircleList, Context context, ClickListener clickListener) {
        this.friendsCircleList = friendsCircleList;
        this.mContext = context;
        this.mClickListener = clickListener;
        this.mUser = PreferencesUtil.getInstance().getUser();
        this.mVolleyUtil = VolleyUtil.getInstance(mContext);
    }

    public void setData(List<FriendsCircle> dataList) {
        this.friendsCircleList = dataList;
    }

    public List<FriendsCircle> getData() {
        return this.friendsCircleList;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final FriendsCircle friendsCircle = friendsCircleList.get(position);
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
            viewHolder.mLikeRl = convertView.findViewById(R.id.rl_like);
            viewHolder.mLikeUserTv = convertView.findViewById(R.id.tv_like_user);

            viewHolder.mTempView = convertView.findViewById(R.id.view_temp);

            viewHolder.mCommentTv = convertView.findViewById(R.id.tv_comment);
            viewHolder.mCommentLv = convertView.findViewById(R.id.lv_comment_list);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAvatarSdv.setImageURI(Uri.parse(friendsCircle.getUserAvatar()));
        viewHolder.mNickNameTv.setText(friendsCircle.getUserNickName());
        viewHolder.mContentTv.setText(friendsCircle.getCircleContent());
        viewHolder.mCreateTimeTv.setText(TimestampUtil.getTimePoint(friendsCircle.getTimestamp()));

        List<String> photoList = CommonUtil.getListFromJson(friendsCircle.getCirclePhotos(), String.class);

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

        // 点击头像进入用户详情页
        viewHolder.mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("userId", friendsCircle.getUserId());
                mContext.startActivity(intent);
            }
        });

        // 点击昵称进入用户详情页
        viewHolder.mNickNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("userId", friendsCircle.getUserId());
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
                initPopupWindow(friendsCircle, position);
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

        List<User> likeUserList = CommonUtil.getListFromJson(friendsCircle.getLikeUserJsonArray(), User.class);

        if (likeUserList.size() > 0) {
            viewHolder.mLikeRl.setVisibility(View.VISIBLE);
            TextParser textParser = new TextParser();
            int color = Color.rgb(87, 107, 149);
            int index = 0;
            for (final User likeUser : likeUserList) {
                index++;
                textParser.append(likeUser.getUserNickName(), 20, color, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, UserInfoActivity.class).
                                putExtra("userId", likeUser.getUserId()));
                    }
                });
                if (index < likeUserList.size()) {
                    textParser.append(",", 20, color, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mContext.startActivity(new Intent(mContext, UserInfoActivity.class).
                                    putExtra("userId", likeUser.getUserId()));
                        }
                    });
                }
            }
            textParser.parse(viewHolder.mLikeUserTv);
        } else {
            viewHolder.mLikeRl.setVisibility(View.GONE);
        }


        List<FriendsCircleComment> commentList =
                CommonUtil.getListFromJson(friendsCircle.getFriendsCircleCommentJsonArray(), FriendsCircleComment.class);

        FriendsCircleCommentAdapter adapter = new FriendsCircleCommentAdapter(commentList, mContext);
        viewHolder.mCommentLv.setAdapter(adapter);

        if (commentList.size() > 0) {
            viewHolder.mCommentLv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCommentLv.setVisibility(View.GONE);
        }

        if (likeUserList.size() > 0 && commentList.size() > 0) {
            viewHolder.mTempView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mTempView.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 初始化首页弹出框
     */
    private void initPopupWindow(final FriendsCircle friendsCircle, final int position) {
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

        // "点赞" or "取消"
        TextView mLikeTv = mPopupView.findViewById(R.id.tv_like);
        List<User> likeUserList;
        final List<String> likeUserIdList = new ArrayList<>();
        try {
            likeUserList = JSONArray.parseArray(friendsCircle.getLikeUserJsonArray(), User.class);
            for (User likeUser : likeUserList) {
                likeUserIdList.add(likeUser.getUserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (likeUserIdList.contains(mUser.getUserId())) {
            mLikeTv.setText("取消");
        } else {
            mLikeTv.setText("赞");
        }

        // 点赞
        RelativeLayout mLikeRl = mPopupView.findViewById(R.id.rl_like);
        mLikeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                if (likeUserIdList.contains(mUser.getUserId())) {
                    // 已点赞，取消
                    unLikeFriendsCircle(friendsCircle);
                } else {
                    // 未点赞，点赞
                    likeFriendsCircle(friendsCircle);
                }
            }
        });

        RelativeLayout mCommentRl = mPopupView.findViewById(R.id.rl_comment);
        mCommentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mClickListener.onClick(view, friendsCircle.getCircleId(), position);
            }
        });
    }

    /**
     * 朋友圈点赞
     *
     * @param friendsCircle 朋友圈实体
     */
    private void likeFriendsCircle(final FriendsCircle friendsCircle) {
        String url = Constant.BASE_URL + "friendsCircle/" + friendsCircle.getCircleId() + "/like";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mUser.getUserId());

        mVolleyUtil.httpPostRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<User> likeUserList;
                try {
                    likeUserList = JSONArray.parseArray(friendsCircle.getLikeUserJsonArray(), User.class);
                    likeUserList.add(mUser);
                    friendsCircle.setLikeUserJsonArray(JSON.toJSONString(likeUserList));
                    FriendsCircle.save(friendsCircle);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

    /**
     * 朋友圈取消点赞
     *
     * @param friendsCircle 朋友圈实体
     */
    private void unLikeFriendsCircle(final FriendsCircle friendsCircle) {
        String url = Constant.BASE_URL + "friendsCircle/" + friendsCircle.getCircleId() + "/like?userId=" + mUser.getUserId();

        mVolleyUtil.httpDeleteRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<User> likeUserList;
                try {
                    likeUserList = JSONArray.parseArray(friendsCircle.getLikeUserJsonArray(), User.class);
                    Iterator<User> iterator = likeUserList.iterator();
                    while (iterator.hasNext()) {
                        User likeUser = iterator.next();
                        if (likeUser.getUserId().equals(mUser.getUserId())) {
                            iterator.remove();
                        }
                    }
                    friendsCircle.setLikeUserJsonArray(JSON.toJSONString(likeUserList));
                    FriendsCircle.save(friendsCircle);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

    public interface ClickListener {
        void onClick(Object... objects);
    }

    private static class ViewHolder {
        SimpleDraweeView mAvatarSdv;
        TextView mNickNameTv;
        TextView mContentTv;
        TextView mMoreTv;
        TextView mCreateTimeTv;
        FriendsCirclePhotoGridView mPhotosGv;
        ImageButton mCommentIb;

        // 点赞相关
        RelativeLayout mLikeRl;
        TextView mLikeUserTv;

        // 点赞和评论之间的分割线
        View mTempView;

        // 评论相关
        TextView mCommentTv;
        FriendsCircleCommentListView mCommentLv;
    }
}

