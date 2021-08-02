package com.bc.wechat.moments.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bc.wechat.utils.SimpleWeakObjectPool;

/**
 * 图片九宫格
 *
 * @author zhou
 */
public class NineGridView extends ViewGroup implements ViewGroup.OnHierarchyChangeListener {
    private NineGridAdapter mAdapter;
    private OnImageClickListener mListener;
    private int mRows;
    private int mColumns;
    private int mSpace;
    private int mChildWidth;
    private int mChildHeight;

    private SimpleWeakObjectPool<View> IMAGE_POOL;

    public NineGridView(Context context) {
        this(context, null);
    }

    public NineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setOnHierarchyChangeListener(this);
        IMAGE_POOL = new SimpleWeakObjectPool<>(5);
        mSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.getResources().getDisplayMetrics());
    }

    private int mSingleWidth;
    private int mSingleHeight;

    public void setSingleImageSize(int width, int height) {
        this.mSingleWidth = width;
        this.mSingleHeight = height;
    }

    public void setAdapter(NineGridAdapter adapter) {
        if (adapter == null || adapter.getCount() <= 0) {
            removeAllViews();
            return;
        }
        if (mImageViews == null) {
            mImageViews = new SparseArray<>();
        } else {
            mImageViews.clear();
        }
        mAdapter = adapter;
        int oldCount = getChildCount();
        int newCount = adapter.getCount();
        initMatrix(newCount);
        removeScrapViews(oldCount, newCount);
        addChildrenData(adapter);
        requestLayout();
    }

    private void removeScrapViews(int oldCount, int newCount) {
        if (newCount < oldCount) {
            removeViewsInLayout(newCount, oldCount - newCount);
        }
    }

    private void initMatrix(int length) {
        if (length <= 3) {
            mRows = 1;
            mColumns = length;
        } else if (length <= 6) {
            mRows = 2;
            mColumns = 3; // 因为length <=6 所以实际Columns<3也是不会导致计算出问题的
            if (length == 4) {
                mColumns = 2;
            }
        } else {
            mRows = 3;
            mColumns = 3;
        }
    }

    private void addChildrenData(NineGridAdapter adapter) {
        int childCount = getChildCount();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            boolean hasChild = i < childCount;
            // 简单的回收机制,主要是为ListView/RecyclerView做优化
            View recycleView = hasChild ? getChildAt(i) : null;
            if (recycleView == null) {
                recycleView = IMAGE_POOL.get();
                View child = adapter.getView(i, recycleView);
                addViewInLayout(child, i, child.getLayoutParams(), true);
                mImageViews.put(i, (ImageView) child);
            } else {
                adapter.getView(i, recycleView);
                mImageViews.put(i, (ImageView) recycleView);
            }
        }
    }


    private SparseArray<ImageView> mImageViews;

    public SparseArray<ImageView> getImageViews() {
        return mImageViews;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
        if (!(child instanceof ImageView)) {
            throw new ClassCastException("addView(View child) NineGridView只能放ImageView");
        }
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        if (childCount <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if ((mRows == 0 || mColumns == 0) && mAdapter == null) {
            initMatrix(childCount);
        }

        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minW, widthMeasureSpec, 1);
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        if (childCount <= 1) {
            if (mSingleWidth == 0) {
                mChildWidth = availableWidth * 2 / 5;
            } else {
                mChildWidth = availableWidth / 2;
            }
            if (mSingleHeight == 0) {
                mChildHeight = mChildWidth;
            } else {
                mChildHeight = (int) ((mSingleHeight / (float) mSingleWidth) * mChildWidth);
            }
        } else {
            mChildWidth = (availableWidth - mSpace * (mColumns - 1)) / 3;
            mChildHeight = mChildWidth;
        }
        int height = mChildHeight * mRows + mSpace * (mRows - 1);
        setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom());
    }

    public int getChildHeight() {
        return mChildHeight;
    }

    public int getChildWidth() {
        return mChildWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    protected void layoutChildren() {
        if (mRows <= 0 || mColumns <= 0) {
            return;
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView view = (ImageView) getChildAt(i);

            int row = i / mColumns;
            int col = i % mColumns;
            int left = (mChildWidth + mSpace) * col + getPaddingLeft();
            int top = (mChildHeight + mSpace) * row + getPaddingTop();
            int right = left + mChildWidth;
            int bottom = top + mChildHeight;
            view.layout(left, top, right, bottom);
            final int position2 = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onImageClick(position2, view);
                    }
                }
            });
        }
    }


    public void setOnImageClickListener(OnImageClickListener listener) {
        mListener = listener;
    }

    public void setSpace(int space) {
        mSpace = space;
    }

    public int getSpace() {
        return mSpace;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {

    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        IMAGE_POOL.put(child);
    }


    public interface NineGridAdapter<T> {
        int getCount();

        T getItem(int position);

        View getView(int position, View itemView);
    }

    public interface OnImageClickListener {
        void onImageClick(int position, View view);
    }

}