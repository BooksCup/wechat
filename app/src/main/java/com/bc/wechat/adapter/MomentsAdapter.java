package com.bc.wechat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bc.wechat.R;
import com.bc.wechat.moments.adapter.NineImageAdapter;
import com.bc.wechat.moments.bean.ExploreDongtaiBean;
import com.bc.wechat.moments.bean.ExplorePostPinglunBean;
import com.bc.wechat.moments.listener.Explore_dongtai1_listener;
import com.bc.wechat.moments.utils.PopupWindowUtil;
import com.bc.wechat.moments.utils.TimeUtil;
import com.bc.wechat.viewholder.moments.BaseViewHolder;
import com.bc.wechat.viewholder.moments.HeadViewHolder;
import com.bc.wechat.viewholder.moments.ImgViewHolder;
import com.bc.wechat.viewholder.moments.TextViewHolder;
import com.bc.wechat.viewholder.moments.VideotViewHolder;
import com.bc.wechat.moments.widget.CommentsView;
import com.bc.wechat.moments.widget.NineGridView;
import com.bc.wechat.moments.widget.PraiseListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import byc.imagewatcher.ImageWatcherHelper;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 朋友圈
 */
public class MomentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;   //helder
    // 文本
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2; //图片
    public static final int TYPE_VIDEO = 3; //视频
    public static final int TYPE_WEB = 4;  //网页

    private View mHeaderView;

    private Activity mContent;
    private List<ExploreDongtaiBean> mList;
    private Explore_dongtai1_listener expandFoldListener;

    private RequestOptions mRequestOptions;
    private DrawableTransitionOptions mDrawableTransitionOptions;
    private ImageWatcherHelper iwHelper;

    public MomentsAdapter(List<ExploreDongtaiBean> list, Activity context, Explore_dongtai1_listener expandFoldListener) {
        mContent = context;
        this.mList = list;
        this.expandFoldListener = expandFoldListener;
        this.mRequestOptions = new RequestOptions().centerCrop();
        this.mDrawableTransitionOptions = DrawableTransitionOptions.withCrossFade();
    }

    public void setIwHelper(ImageWatcherHelper iwHelper) {
        this.iwHelper = iwHelper;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            return new ImgViewHolder(mContent.getLayoutInflater().inflate(R.layout.item_my_moments_image, parent, false));
        } else if (viewType == TYPE_VIDEO) {
            return new VideotViewHolder(mContent.getLayoutInflater().inflate(R.layout.item_my_moments_video, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new HeadViewHolder(mContent.getLayoutInflater().inflate(R.layout.item_my_moments_header, parent, false));
        } else {
            // 默认text
            return new TextViewHolder(mContent.getLayoutInflater().inflate(R.layout.item_my_moments_text, parent, false));
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder1, final int pos) {
        if (viewHolder1 instanceof HeadViewHolder) {
            return;
        }
        final int position = getRealPosition(viewHolder1);
        ExploreDongtaiBean dongtaiBean = mList.get(position);
        if (viewHolder1 instanceof TextViewHolder) {
            //将数据添加到布局中
            TextViewHolder textViewHolder = (TextViewHolder) viewHolder1;
        } else if (viewHolder1 instanceof ImgViewHolder) {
            //将数据添加到另一个布局中
            final ImgViewHolder imgViewHolder = (ImgViewHolder) viewHolder1;
            imgViewHolder.nineGridView.setSingleImageSize(80, 120);
            if (dongtaiBean.getThumbnail() != null && !dongtaiBean.getThumbnail().equals("")) {
                //,分割开的数据取出来
                List<String> result = new ArrayList<String>();
                if (dongtaiBean.getThumbnail().indexOf(",") >= 0) {
                    // 字符串中有逗号  即：str = "a,b,c"
                    result = Arrays.asList(dongtaiBean.getThumbnail().split(","));
                } else {
                    result.add(dongtaiBean.getThumbnail());
                }

                List<String> result1 = new ArrayList<String>();
                if (dongtaiBean.getImgs().indexOf(",") >= 0) {
                    // 字符串中有逗号  即：str = "a,b,c"
                    result1 = Arrays.asList(dongtaiBean.getThumbnail().split(","));
                } else {
                    result1.add(dongtaiBean.getThumbnail());
                }
                final List<Uri> tempYuantu = getImageUriList(result1);
                imgViewHolder.nineGridView.setAdapter(new NineImageAdapter(mContent,
                        mRequestOptions, mDrawableTransitionOptions, result));
                imgViewHolder.nineGridView.setOnImageClickListener(new NineGridView.OnImageClickListener() {
                    @Override
                    public void onImageClick(int position, View view) {
                        if (iwHelper != null) {
                            iwHelper.show((ImageView) view, imgViewHolder.nineGridView.getImageViews(),
                                    tempYuantu);
                        }
                        if (expandFoldListener != null) {
                            //返回主页去弹出评论
                            expandFoldListener.imageOnclick();//刷新

                        }
                    }
                });
            }
        } else if (viewHolder1 instanceof VideotViewHolder) {
            //将数据添加到另一个布局中
            VideotViewHolder videotViewHolder = (VideotViewHolder) viewHolder1;
            System.out.println("");
            if (dongtaiBean.getThumbnail() == null || dongtaiBean.getThumbnail().equals("")) {

            } else {
                String suoluetuUrl = "";
                if (dongtaiBean.getThumbnail().contains(",")) {
                    String temp[] = dongtaiBean.getThumbnail().split(",");
                    loadImage1(mContent, temp[0], videotViewHolder.videos);
                    suoluetuUrl = temp[0];
                } else {
                    loadImage1(mContent, dongtaiBean.getThumbnail(), videotViewHolder.videos);
                    suoluetuUrl = dongtaiBean.getThumbnail();
                }
                final String suoluetuUrl1 = suoluetuUrl;
                videotViewHolder.videos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (expandFoldListener != null) {
                            expandFoldListener.videoOnclick(suoluetuUrl1, mList.get(position).getVideos());
                        }
                    }
                });
            }
        }
        // 处理公用部分
        final BaseViewHolder viewHolder = (BaseViewHolder) viewHolder1;
        // 头像
        if (!TextUtils.isEmpty(dongtaiBean.getHandimg())) {
            viewHolder.mAvatarSdv.setImageURI(Uri.parse(dongtaiBean.getHandimg()));
        }
        // 昵称
        viewHolder.mNickNameTv.setText(dongtaiBean.getNickname());
        viewHolder.mAvatarSdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickUser(mList.get(position).getUserid() + "");
            }
        });
        viewHolder.mNickNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickUser(mList.get(position).getUserid() + "");
            }
        });
        // 内容
        viewHolder.mContentEtv.setText(dongtaiBean.getWrittenwords());
        if (dongtaiBean.getFabulous() != null && dongtaiBean.getFabulous().size() > 0 ||
                dongtaiBean.getEvea() != null && dongtaiBean.getEvea().size() > 0) {
            viewHolder.linearLayoutAll.setVisibility(View.VISIBLE);

            //点赞列表

            if (dongtaiBean.getFabulous() != null && dongtaiBean.getFabulous().size() > 0) {
                viewHolder.dianzanList.setVisibility(View.VISIBLE);

                viewHolder.dianzanList.setDatas(dongtaiBean.getFabulous());
                viewHolder.dianzanList.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        onclickUser(mList.get(position).getFabulous().get(position).getUserid() + "");
                    }
                });
//                viewHolder.dianzanList.notifyDataSetChanged();错误
            } else {
                viewHolder.dianzanList.setVisibility(View.GONE);

            }
            //评论列表
            if (dongtaiBean.getEvea() != null && dongtaiBean.getEvea().size() > 0) {
                System.out.println("评论是否显示1");
                viewHolder.pinglunList.setVisibility(View.VISIBLE);
                viewHolder.pinglunList.setList(dongtaiBean.getEvea());
                viewHolder.pinglunList.setOnCommentListener(new CommentsView.CommentListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void CommentClick(View view, int position, ExplorePostPinglunBean bean) {
                        //如果点击得 是自己
                        if (bean.getCommentsUser().getUserId().equals("我自己")) {
                            //如果是自己发的，可以删除,请求网络，返回数据刷新页面
                            showDeletePopWindow(viewHolder.pinglunList, mList.get(position).getId(), (bean),
                                    viewHolder.getLayoutPosition() - 1, position);
                        } else {
                            //不相同则开始回复，这里需要判断是回复说说的发布者，还是评论者，，
                            if (expandFoldListener != null) {
                                //返回主页去弹出评论
                                expandFoldListener.onPinlunEdit(view, mList.get(position).getId(),
                                        bean.getCommentsUser().getUserId(), bean.getCommentsUser().getUserName());//谁对谁回复，需要判断
                            }
                        }
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void CommentLongClick(View view, int position1, ExplorePostPinglunBean bean) {
                        System.out.println("当前长按点击弹出复制框");
                        showCopyPopWindow(viewHolder.pinglunList, bean.getContent());
                    }

                    @Override
                    public void toUser(String userid) {
                        onclickUser(userid);
                    }
                });
//
                viewHolder.pinglunList.notifyDataSetChanged();
            } else {
                System.out.println("评论是否显示2");
                viewHolder.pinglunList.setVisibility(View.GONE);
            }
        } else {
            viewHolder.linearLayoutAll.setVisibility(View.GONE);
        }
        if ((dongtaiBean.getUserid() + "").equals("我自己 ")) {
            viewHolder.mDeleteTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mDeleteTv.setVisibility(View.GONE);
        }
        if (dongtaiBean.getCreattime() != 0) {
            String time = TimeUtil.converTime1(mContent, dongtaiBean.getCreattime());
            viewHolder.mTimeTv.setText(time + "");
        }
        viewHolder.dianzanPinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandFoldListener != null) {
                    expandFoldListener.onClickEdit(view, position);
                }
            }
        });
        viewHolder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandFoldListener != null) {
                    expandFoldListener.deletePengyouquan(mList.get(position).getId());
                }
            }
        });
    }

    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            if (mList.get(position).getType() == 1) {
                return TYPE_TEXT;
            } else if (mList.get(position).getType() == 2) {
                return TYPE_IMAGE;
            } else if (mList.get(position).getType() == 3) {
                return TYPE_VIDEO;
            } else {
                return TYPE_TEXT;
            }
        } else {
            if (position == 0) {
                return TYPE_HEADER;
            } else if (mList.get(position - 1).getType() == 1) {
                return TYPE_TEXT;
            } else if (mList.get(position - 1).getType() == 2) {
                return TYPE_IMAGE;
            } else if (mList.get(position - 1).getType() == 3) {
                return TYPE_VIDEO;
            } else {
                return TYPE_TEXT;
            }
        }
    }


    @Override
    public int getItemCount() {
        if (mHeaderView != null) {
            return mList.size() + 1;
        } else {
            return mList.size();
        }
    }


//    //    加载网络图片圆角
//    public void loadImage(Context context, String path, ImageView imageView) {
//        RoundedCorners roundedCorners = new RoundedCorners(20);//数字为圆角度数
//        RequestOptions coverRequestOptions = new RequestOptions()
//                .transforms(new CenterCrop(), roundedCorners)//, roundedCorners
////                .error(R.mipmap.default_img)//加载错误显示
////                .placeholder(R.mipmap.default_img)//加载中显示
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全部
////                .skipMemoryCache(true)
//                ;//不做内存缓存
//
//        //Glide 加载图片简单用法
//        Glide.with(context)
//                .load(path)
//                .apply(coverRequestOptions)
//                .into(imageView);
//    }


    //不带圆角，目前视频使用
    public void loadImage1(Context context, String path, ImageView imageView) {
//        RoundedCorners roundedCorners = new RoundedCorners(20);//数字为圆角度数
        RequestOptions coverRequestOptions = new RequestOptions()
                .transforms(new CenterCrop())//, roundedCorners
//                .error(R.mipmap.default_img)//加载错误显示
//                .placeholder(R.mipmap.default_img)//加载中显示
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全部
//                .skipMemoryCache(true)//多种原因造成闪烁，这里是之一，如果坚持跳过内存缓存，需要tag,增加noty的各种判断，
                //但是不跳过，可能因为数据过大会boom,
                ;//不做内存缓存

        //Glide 加载图片简单用法
        Glide.with(context)
                .load(path)
                .apply(coverRequestOptions)
                .into(imageView);


    }

    private void onclickUser(String userid) {
        if (expandFoldListener != null) {
            expandFoldListener.onClickUser(userid);
        }
    }

    private PopupWindow mPopupWindow;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showCopyPopWindow(CommentsView rvComment, String content) {
        View contentView = getCopyPopupWindowContentView(content);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        int[] windowPos = PopupWindowUtil.calculatePopWindowPos(rvComment, contentView, 0, 0);
        mPopupWindow.showAsDropDown(rvComment, 0, -60, windowPos[1]);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }

    private View getCopyPopupWindowContentView(final String content) {
        // 布局ID
        int layoutId = R.layout.popup_copy;
        View contentView = LayoutInflater.from(mContent).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                ClipboardManager mCM = (ClipboardManager) mContent.getSystemService(CLIPBOARD_SERVICE);
                mCM.setPrimaryClip(ClipData.newPlainText(null, content));
                Toast.makeText(mContent, mContent.getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        };
        contentView.findViewById(R.id.menu_copy).setOnClickListener(menuItemOnClickListener);
        return contentView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showDeletePopWindow(View view, int ids, ExplorePostPinglunBean id, int layoutPosition, int position) {
        View contentView = getPopupWindowContentView(ids, id, layoutPosition, position);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        int[] windowPos = PopupWindowUtil.calculatePopWindowPos(view, contentView, 0, 0);
        mPopupWindow.showAsDropDown(view, 0, -40, windowPos[1]);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }

    private View getPopupWindowContentView(final int ids, final ExplorePostPinglunBean id, final int layoutPosition, final int position) {
        // 布局ID
        int layoutId = R.layout.popup_delete;
        View contentView = LayoutInflater.from(mContent).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                    if (expandFoldListener != null) {
                        expandFoldListener.deleteMypinglun(ids, id);
                    }
                }
            }
        };
        contentView.findViewById(R.id.menu_delete).setOnClickListener(menuItemOnClickListener);
        return contentView;
    }

    public List<Uri> getImageUriList(List<String> files) {
        List<Uri> imageUriList = new ArrayList<>();
        if (files != null && files.size() > 0) {
            for (String str : files) {
                imageUriList.add(Uri.parse(str));
            }
        }
        return imageUriList;
    }
}
