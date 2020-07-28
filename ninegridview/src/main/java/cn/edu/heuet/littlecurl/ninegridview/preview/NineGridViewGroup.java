package cn.edu.heuet.littlecurl.ninegridview.preview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import cn.edu.heuet.littlecurl.ninegridview.R;
import cn.edu.heuet.littlecurl.ninegridview.base.NineGridViewAdapter;
import cn.edu.heuet.littlecurl.ninegridview.bean.NineGridItem;

import java.util.ArrayList;
import java.util.List;

public class NineGridViewGroup extends ViewGroup {

    public static final int MODE_FILL = 0;          //填充模式，类似于微信
    public static final int MODE_GRID = 1;          //网格模式，类似于QQ，4张图会 2X2布局

    private static ImageLoader mImageLoader; //全局的图片加载器(必须设置,否者不显示图片)

    private int singleMediaSize = 300;              // 单张图片时的最大大小 300 x 300,单位dp
    private float singleImageRatio = 1.0f;          // 单张图片的宽高比(宽/高)
    private int maxGridSize = 9;                   // 最大显示的图片数
    private int gridSpacing = 3;                    // 宫格间距，单位dp
    private int mode = MODE_GRID;                   // 默认使用grid模式

    private int columnCount;    // 列数
    private int rowCount;       // 行数
    private int gridWidth;      // 宫格宽度
    private int gridHeight;     // 宫格高度

    private List<ImageView> imageViewList;
    private List<NineGridItem> nineGridItemList;
    private NineGridViewAdapter mAdapter;

    public NineGridViewGroup(Context context) {
        this(context, null);
    }

    public NineGridViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 单位转换：int 转为 dp
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        gridSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gridSpacing, dm);
        singleMediaSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, singleMediaSize, dm);
        // 从属性文件中获取数据
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NineGridViewGroup);
        gridSpacing = (int) a.getDimension(R.styleable.NineGridViewGroup_ngv_gridSpacing, gridSpacing);
        singleMediaSize = a.getDimensionPixelSize(R.styleable.NineGridViewGroup_ngv_singleMediaSize, singleMediaSize);
        singleImageRatio = a.getFloat(R.styleable.NineGridViewGroup_ngv_singleMediaRatio, singleImageRatio);
        maxGridSize = a.getInt(R.styleable.NineGridViewGroup_ngv_maxSize, maxGridSize);
        mode = a.getInt(R.styleable.NineGridViewGroup_ngv_mode, mode);
        a.recycle();

        imageViewList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取总宽度,包含padding值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int totalWidth = width - getPaddingLeft() - getPaddingRight();
        int gridNum = nineGridItemList.size();
        if (nineGridItemList != null && gridNum > 0) {
            // 只有一张图片的情况
            if (gridNum == 1) {
                gridWidth = singleMediaSize > totalWidth ? totalWidth : singleMediaSize;
                gridHeight = (int) (gridWidth / singleImageRatio);
                //矫正图片显示区域大小，不允许超过最大显示范围
                if (gridHeight > singleMediaSize) {
                    float ratio = singleMediaSize * 1.0f / gridHeight;
                    gridWidth = (int) (gridWidth * ratio);
                    gridHeight = singleMediaSize;
                }
            }
            // 有 2 张 或 4 张图片的情况
            else if (gridNum == 2 || gridNum == 4) {
                gridWidth = singleMediaSize > totalWidth / 2 ? totalWidth / 2 : singleMediaSize;
                gridHeight = (int) (gridWidth / singleImageRatio);
                //矫正图片显示区域大小，不允许超过最大显示范围
                if (gridHeight > singleMediaSize) {
                    float ratio = singleMediaSize * 1.0f / gridHeight;
                    gridWidth = (int) (gridWidth * ratio);
                    gridHeight = singleMediaSize;
                }
            }
            // 有其他数量多张图片的情况
            else {
                // gridWidth = gridHeight = (totalWidth - gridSpacing * (columnCount - 1)) / columnCount;
                //这里无论是几张图片，宽高都按总宽度的 1/3
                gridHeight = (totalWidth - gridSpacing * 2) / 3;
                gridWidth = gridHeight;
            }
            width = gridWidth * columnCount + gridSpacing * (columnCount - 1) + getPaddingLeft() + getPaddingRight();
            height = gridHeight * rowCount + gridSpacing * (rowCount - 1) + getPaddingTop() + getPaddingBottom();
        }
        // 存储计算得到的ViewGroup的宽高
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (nineGridItemList == null) return;
        int childrenCount = nineGridItemList.size();
        for (int i = 0; i < childrenCount; i++) {
            ImageView childrenView = (ImageView) getChildAt(i);
            int rowNum = i / columnCount;
            int columnNum = i % columnCount;
            int left = (gridWidth + gridSpacing) * columnNum + getPaddingLeft();
            int top = (gridHeight + gridSpacing) * rowNum + getPaddingTop();
            int right = left + gridWidth;
            int bottom = top + gridHeight;
            childrenView.layout(left, top, right, bottom);
            if (mImageLoader != null) {
                mImageLoader.onDisplayImage(getContext(),
                        childrenView,
                        nineGridItemList.get(i).thumbnailUrl);
            }
        }
    }

    /**
     * 设置适配器
     */
    public void setAdapter(@NonNull NineGridViewAdapter adapter) {
        // 初始化mAdapter，此类的其他方法会用到
        mAdapter = adapter;
        List<NineGridItem> nineGridItemList = adapter.getNineGridItemList();

        if (nineGridItemList == null || nineGridItemList.isEmpty()) {
            this.setVisibility(GONE);
            return;
        } else {
            this.setVisibility(VISIBLE);
        }

        int gridCount = nineGridItemList.size();
        if (maxGridSize > 0 && gridCount > maxGridSize) {
            nineGridItemList = nineGridItemList.subList(0, maxGridSize);
            gridCount = nineGridItemList.size();   //再次获取图片数量
        }

        //默认是3列显示，行数根据图片的数量决定
        rowCount = gridCount / 3 + (gridCount % 3 == 0 ? 0 : 1);
        columnCount = 3;
        //grid模式下，显示4张使用2X2模式
        if (mode == MODE_GRID) {
            if (gridCount == 4) {
                rowCount = 2;
                columnCount = 2;
            }
        }

        // 保证View的复用，避免重复创建
        if (this.nineGridItemList == null) {
            for (int i = 0; i < gridCount; i++) {
                ImageView iv = getImageView(i);
                if (iv == null)
                    return;
                addView(iv, generateDefaultLayoutParams());
            }
        } else {
            int oldViewCount = this.nineGridItemList.size();
            int newViewCount = gridCount;
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = oldViewCount; i < newViewCount; i++) {
                    ImageView iv = getImageView(i);
                    if (iv == null)
                        return;
                    addView(iv, generateDefaultLayoutParams());
                }
            }
        }
        //修改最后一个条目，决定是否显示更多
        if (adapter.getNineGridItemList().size() > maxGridSize) {
            View child = getChildAt(maxGridSize - 1);
            if (child instanceof NineGridItemWrapperView) {
                NineGridItemWrapperView imageView = (NineGridItemWrapperView) child;
                imageView.setMoreNum(adapter.getNineGridItemList().size() - maxGridSize);
            }
        }
        this.nineGridItemList = nineGridItemList;
        // 请求重新布局
        requestLayout();
    }

    /**
     * 获得 ImageView 保证了 ImageView 的重用
     */
    private ImageView getImageView(final int position) {
        ImageView imageView;
        if (position < imageViewList.size()) {
            imageView = imageViewList.get(position);
        } else {
            imageView = mAdapter.generateImageView(getContext());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.onNineGridItemClick(getContext(), NineGridViewGroup.this, position, mAdapter.getNineGridItemList());
                }
            });
            imageViewList.add(imageView);
        }
        return imageView;
    }

    /**
     * 设置宫格间距
     */
    public void setGridSpacing(int spacing) {
        gridSpacing = spacing;
    }

    /**
     * 设置只有一张图片时的宽高比
     */
    public void setSingleImageRatio(float ratio) {
        singleImageRatio = ratio;
    }

    /**
     * 设置最大图片数
     */
    public void setMaxSize(int maxSize) {
        maxGridSize = maxSize;
    }

    public int getMaxSize() {
        return maxGridSize;
    }

    public static void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * MODE_GRID 仿QQ空间 4 = 2*2 （默认）
     * MODE_FILL 仿微信   4 = 3+1
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    public interface ImageLoader {
        /**
         * 需要子类实现该方法，以确定如何加载和显示图片
         */
        void onDisplayImage(Context context, ImageView imageView, String url);

        Bitmap getCacheImage(String url);
    }


}
