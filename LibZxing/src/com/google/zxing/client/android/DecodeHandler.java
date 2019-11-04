/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.Constants;
import com.google.zxing.common.HybridBinarizer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Map;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final CaptureActivity activity;
    private final MultiFormatReader multiFormatReader;
    private boolean running = true;
    private int frameCount;
    private long intervalTime;
    private static final long INTERVAL = 30 * 1000L;
    private boolean isResetTime = true;
    private Rect frameRect;


    DecodeHandler(CaptureActivity activity, Map<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.activity = activity;
        frameRect = activity.getCameraManager().getFramingRect();
    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        } else if (message.what == R.id.quit) {
            running = false;
            Looper.myLooper().quit();
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {

        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        frameCount++;
        //丢弃前2帧并每隔2帧分析下预览帧color值
        if (frameCount > 2 && frameCount % 2 == 0) {
            analysisBitmapColor(data, width, height);
        }
        long start = System.currentTimeMillis();
        if (isResetTime) {
            intervalTime = System.currentTimeMillis();
            isResetTime = false;
        }
        Result rawResult = null;
        final PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(rotatedData, height, width);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }
        final Handler handler = activity.getHandler();
        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (handler != null) {
                float point1X = rawResult.getResultPoints()[0].getX();
                float point1Y = rawResult.getResultPoints()[0].getY();
                float point2X = rawResult.getResultPoints()[1].getX();
                float point2Y = rawResult.getResultPoints()[1].getY();
                int len = (int) Math.sqrt(Math.abs(point1X - point2X) * Math.abs(point1X - point2X) + Math.abs(point1Y - point2Y) * Math.abs(point1Y - point2Y));
                if (frameRect != null) {
                    int frameWidth = frameRect.right - frameRect.left;
                    Camera camera = activity.getCameraManager().getCameraNotStatic();
                    Camera.Parameters parameters = camera.getParameters();
                    final int maxZoom = parameters.getMaxZoom();
                    int zoom = parameters.getZoom();
                    if (parameters.isZoomSupported()) {
                        if (len <= frameWidth / 4) {
                            if (zoom == 0) {
                                zoom = maxZoom / 3;
                            } else {
                                zoom = zoom + 10;
                            }
                            if (zoom > maxZoom) {
                                zoom = maxZoom;
                            }
                            parameters.setZoom(zoom);
                            camera.setParameters(parameters);
                            final Result finalRawResult = rawResult;
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = Message.obtain(handler, R.id.decode_succeeded, finalRawResult);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                                    message.setData(bundle);
                                    message.sendToTarget();
                                }
                            }, 1000);

                        } else {
                            Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                            message.setData(bundle);
                            message.sendToTarget();
                        }
                    }
                } else {
                    Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
//                if (!Constants.isWeakLight) {
//                    long failedTimeStamp = System.currentTimeMillis();
//                    if (failedTimeStamp - intervalTime > INTERVAL) {
//                        isResetTime = true;
//                        intervalTime = System.currentTimeMillis();
//                        Camera camera = activity.getCameraManager().getCameraNotStatic();
//                        Camera.Parameters parameters = camera.getParameters();
//                        final int maxZoom = parameters.getMaxZoom();
//                        int zoom = parameters.getZoom();
//                        if (parameters.isZoomSupported()) {
//                            if (zoom == 0) {
//                                zoom = maxZoom / 2;
//                            } else {
//                                zoom = zoom + 10;
//                            }
//                            if (zoom > maxZoom) {
//                                zoom = maxZoom;
//                            }
//                            parameters.setZoom(zoom);
//                            camera.setParameters(parameters);
//                        }
//                    }
//                }
            }
        }
    }

    private void analysisBitmapColor(byte[] data, int width, int height) {
        int[] rgb = decodeYUV420SP(data, width, height);
        Bitmap bmp = null;
        if (null != frameRect) {
            //取矩形扫描框frameRect的2分之一创建为bitmap来分析
            bmp = Bitmap.createBitmap(rgb, frameRect.left + (frameRect.right - frameRect.left) / 4, frameRect.width() / 2, frameRect.width() / 2, frameRect.height() / 2, Bitmap.Config.ARGB_4444);
        }
        if (bmp != null) {
            float color = getAverageColor(bmp);
            DecimalFormat decimalFormat1 = new DecimalFormat("0.00");
            String percent = decimalFormat1.format(color / -16777216);
            float floatPercent = Float.parseFloat(percent);
            Log.e(TAG, " color= " + color + " floatPercent= " + floatPercent + " bmp width= " + bmp.getWidth() + " bmp height= " + bmp.getHeight());
            Constants.isWeakLight = (color == -16777216 || (floatPercent >= 0.95 && floatPercent <= 1.00));
            bmp.recycle();
        }
    }

    private int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        int rgb[] = new int[width * height];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) &
                        0xff00) | ((b >> 10) & 0xff);


            }
        }
        return rgb;
    }

    private int getAverageColor(Bitmap bitmap) {
        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
            }
        }
        int averageColor = Color.rgb(redBucket / pixelCount, greenBucket
                / pixelCount, blueBucket / pixelCount);
        return averageColor;
    }
}
