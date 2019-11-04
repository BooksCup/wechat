package com.google.zxing.client.android;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.common.Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    private CameraManager cameraManager;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private final int frameColor;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    /*************************onDraw绘制相关*************************************/
    //刷新界面的时间
    private static final long ANIMATION_DELAY = 10L;
    //四个绿色边角对应的长度
    private int screenRate;
    //四个绿色边角对应的宽度
    private static final int CORNER_WIDTH = 10;
    private static final int MIDDLE_LINE_PADDING = 5;
    private static final int SPEED = 5;
    //手机的屏幕密度
    private static float density;
    private static final int TEXT_SIZE = 12;
    private static final int TEXT_PADDING_TOP = 40;
    private Paint paint;
    private int slideTop;

    private boolean isFirst;
    private Paint framePaint;
    private Paint textPaint;
    private String textHint;

    //手电筒相关
    private Bitmap flashLightBitmap;
    private Bitmap openFlashLightBitmap;
    private Bitmap scanLineBitmap;
    private String flashLightOpenText;
    private String flashLightCloseText;
    private Paint flashLightTextPaint;
    private boolean isOpenFlashLight;
    private int UNIT = 50;
    private int TEXT_UNIT = 40;
    private float flashLightBottomDistance;
    private float flashTextBottomDistance;
    private Rect flashRect;
    private Rect flashOpenRect;
    private Rect frame;
    private onFlashLightStateChangeListener mOnFlashLightStateChangeListener;

    //自动放大标识 目前思路根据距离
    private boolean isLongDistance;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;
        screenRate = (int) (20 * density);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.black_40_alpha);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.chaoxing_scan_blue);
        resultPointColor = resources.getColor(R.color.chaoxing_scan_blue);
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
        //frame边框画笔
        framePaint.setColor(frameColor);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(1);
        //textPaint
        textHint = resources.getString(R.string.scan_tips);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE * density, context.getResources().getDisplayMetrics()));
        textPaint.setTypeface(Typeface.create("System", Typeface.BOLD));
        //flashLight
        flashLightBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.scan_flashlight)).getBitmap();
        openFlashLightBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.scan_open_flashlight)).getBitmap();
        scanLineBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.zxing_scan_line)).getBitmap();
        flashRect = new Rect();
        flashOpenRect = new Rect();
        flashLightOpenText = resources.getString(R.string.open_flash_light);
        flashLightCloseText = resources.getString(R.string.close_flash_light);
        flashLightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        flashLightTextPaint.setColor(Color.WHITE);
        flashLightTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE * density, context.getResources().getDisplayMetrics()));
        flashLightBottomDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, UNIT * density, context.getResources().getDisplayMetrics());
        flashTextBottomDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, TEXT_UNIT * density, context.getResources().getDisplayMetrics());

    }

    //初始化cameraManager对象
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //中间的扫描框,想要修改扫描框的大小可以去CameraManager里面修改
        frame = cameraManager.getFramingRect();
        if (frame == null) {
            return;
        }
        //初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
        }
        //获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        //绘制frame边框
        canvas.drawRect(frame, framePaint);
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            //画扫描框边上的角，总共8个部分
            paint.setColor(frameColor);
            canvas.drawRect(frame.left, frame.top, frame.left + screenRate,
                    frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
                    + screenRate, paint);
            canvas.drawRect(frame.right - screenRate, frame.top, frame.right,
                    frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
                    + screenRate, paint);
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
                    + screenRate, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - screenRate,
                    frame.left + CORNER_WIDTH, frame.bottom, paint);
            canvas.drawRect(frame.right - screenRate, frame.bottom - CORNER_WIDTH,
                    frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - screenRate,
                    frame.right, frame.bottom, paint);


            //绘制文字
            canvas.drawText(textHint, (width - textPaint.measureText(textHint)) / 2, (frame.bottom + (float) TEXT_PADDING_TOP * density), textPaint);


            if (Constants.isWeakLight) {

                if (!isOpenFlashLight) {
                    flashRect.left = (width - flashLightBitmap.getWidth()) / 2;
                    flashRect.right = (width + flashLightBitmap.getWidth()) / 2;
                    flashRect.bottom = (int) (frame.bottom - flashLightBottomDistance);
                    flashRect.top = (int) (frame.bottom - flashLightBottomDistance - flashLightBitmap.getHeight());
                    canvas.drawBitmap(flashLightBitmap, null, flashRect, paint);

                    Rect flashTextRect = new Rect();
                    flashTextRect.left = (int) ((width - flashLightTextPaint.measureText(flashLightOpenText)) / 2);
                    flashTextRect.right = (int) ((width + flashLightTextPaint.measureText(flashLightOpenText)) / 2);
                    flashTextRect.bottom = (int) (frame.bottom - flashTextBottomDistance);
                    flashTextRect.top = flashTextRect.bottom - 5;
                    canvas.drawText(flashLightOpenText, flashTextRect.left, flashRect.bottom + 50, flashLightTextPaint);
                }
            }
            if (!Constants.isWeakLight || isOpenFlashLight) {
                //绘制中间的线
                slideTop += SPEED;
                if (slideTop >= frame.bottom) {
                    slideTop = frame.top;
                }
                Rect lineRect = new Rect();
                lineRect.left = frame.left + MIDDLE_LINE_PADDING;
                lineRect.right = frame.right - MIDDLE_LINE_PADDING;
                lineRect.top = slideTop - scanLineBitmap.getHeight() / 2;
                lineRect.bottom = slideTop + scanLineBitmap.getHeight() / 2;
                canvas.drawBitmap(scanLineBitmap, null, lineRect, paint);

            }
            if (isOpenFlashLight) {

                flashOpenRect.left = (width - openFlashLightBitmap.getWidth()) / 2;
                flashOpenRect.right = (width + openFlashLightBitmap.getWidth()) / 2;
                flashOpenRect.bottom = (int) (frame.bottom - flashLightBottomDistance);
                flashOpenRect.top = (int) (frame.bottom - flashLightBottomDistance - openFlashLightBitmap.getHeight());
                canvas.drawBitmap(openFlashLightBitmap, null, flashOpenRect, paint);

                Rect flashOpenTextRect = new Rect();
                flashOpenTextRect.left = (int) ((width - flashLightTextPaint.measureText(flashLightCloseText)) / 2);
                flashOpenTextRect.right = (int) ((width + flashLightTextPaint.measureText(flashLightCloseText)) / 2);
                flashOpenTextRect.bottom = (int) (frame.bottom - flashTextBottomDistance);
                flashOpenTextRect.top = flashOpenTextRect.bottom - 5;
                canvas.drawText(flashLightCloseText, flashOpenTextRect.left, flashOpenRect.bottom + 50, flashLightTextPaint);

            }


            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            if (null != currentPossible && currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                if (null != currentPossible && currentPossible.isEmpty()) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top
                                + point.getY(), 6.0f, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }

            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        if (!isOpenFlashLight) {
            isOpenFlashLight = openFlashLight(x, y);
        } else {
            isOpenFlashLight = false;
        }
        if (null != mOnFlashLightStateChangeListener) {
            mOnFlashLightStateChangeListener.openFlashLight(isOpenFlashLight);
        }
        return super.onTouchEvent(event);
    }


    private boolean openFlashLight(int x, int y) {
        Rect rect = flashRect;
        rect.left = flashRect.left - 10;
        rect.right = flashRect.right + 10;
        rect.top = flashRect.top - 10;
        rect.bottom = flashRect.bottom + 10;
        return rect.contains(x, y);
    }

    public void reOnDraw() {
        if (null != frame) {
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    public interface onFlashLightStateChangeListener {
        void openFlashLight(boolean open);
    }

    public void setOnFlashLightStateChangeListener(onFlashLightStateChangeListener onFlashLightStateChangeListener) {
        mOnFlashLightStateChangeListener = onFlashLightStateChangeListener;
    }

    public void setWeakLight(boolean weakLight) {
        //  Constants.isWeakLight = weakLight;
//        if (null != frame) {
//            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
//                    frame.right, frame.bottom);
//        }

    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }


    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
}