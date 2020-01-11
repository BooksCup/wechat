package com.bc.wechat.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.bc.wechat.entity.DeviceInfo;

public class DeviceInfoUtil {

    private static final String TAG = "DeviceInfoUtil";
    private static DeviceInfoUtil instance;

    public DeviceInfoUtil() {

    }

    public static DeviceInfoUtil getInstance() {
        if (instance == null) {
            instance = new DeviceInfoUtil();
        }
        return instance;
    }

    /**
     * 获取手机品牌
     *
     * @return 手机品牌
     */
    public String getPhoneBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取操作系统版本
     *
     * @return 操作系统版本
     */
    public String getOS() {
        return "Android" + Build.VERSION.RELEASE;
    }


    /**
     * 获取手机分辨率
     *
     * @param context context
     * @return 手机分辨率
     */
    public String getResolution(Context context) {
        // 方法1 Android获得屏幕的宽和高
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        Log.w(TAG, "分辨率：" + screenWidth + "*" + screenHeight);
        return screenWidth + "*" + screenHeight;

    }


//    /**
//     * 获取唯一设备号
//     */
//    public String getDeviceNo(Context context) {
//        if (!checkReadPhoneStatePermission(context)) {
//            Log.w(TAG, "获取唯一设备号: 无权限");
//            return "";
//        }
//        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
////        @SuppressLint("HardwareIds") String szImei = TelephonyMgr.getDeviceId();
//
//        Method method = null;
//        String imei2 = "";
//        try {
//            method = TelephonyMgr.getClass().getMethod("getDeviceId", int.class);
//            String imei1 = TelephonyMgr.getDeviceId();
//            String imei0 = (String) method.invoke(TelephonyMgr, 0);
//            imei2 = (String) method.invoke(TelephonyMgr, 1);
//            String meid = (String) method.invoke(TelephonyMgr, 2);
//
//            Log.e(TAG, "唯一设备号szImei-0 is  ：" + imei0 + "  ---  imei1: " + imei1 + "  ---  imei2: " + imei2 + "   -meid is ：" + meid);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//            Log.w(TAG, "唯一设备号imei-NoSuchMethodException: " + e.getMessage());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//            Log.w(TAG, "唯一设备号imei-IllegalAccessException: " + e.getMessage());
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//            Log.w(TAG, "唯一设备号imei-InvocationTargetException: " + e.getMessage());
//        }
//        return imei2;
//    }
//
//    /**
//     * 获取运营商
//     *
//     * @param context
//     * @return
//     */
//    public String getNetOperator(Context context) {
//        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String iNumeric = manager.getSimOperator();
//        String netOperator = "";
//        if (iNumeric.length() > 0) {
//            if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
//                // 中国移动
//                netOperator = "中国移动";
//            } else if (iNumeric.equals("46003")) {
//                // 中国电信
//                netOperator = "中国电信";
//            } else if (iNumeric.equals("46001")) {
//                // 中国联通
//                netOperator = "中国联通";
//            } else {
//                //未知
//                netOperator = "未知";
//            }
//        }
//        Log.w(TAG, "运营商：" + netOperator);
//        return netOperator;
//    }
//
//
//    /**
//     * 获取联网方式
//     */
//    public String getNetMode(Context context) {
//        String strNetworkType = "未知";
//        ConnectivityManager manager = (ConnectivityManager) context.
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            int netMode = networkInfo.getType();
//            if (netMode == ConnectivityManager.TYPE_WIFI) {
//                strNetworkType = "WIFI";
//                //wifi
//            } else if (netMode == ConnectivityManager.TYPE_MOBILE) {
//                int networkType = networkInfo.getSubtype();
//                switch (networkType) {
//
//                    //2g
//                    case TelephonyManager.NETWORK_TYPE_GPRS:
//                    case TelephonyManager.NETWORK_TYPE_EDGE:
//                    case TelephonyManager.NETWORK_TYPE_CDMA:
//                    case TelephonyManager.NETWORK_TYPE_1xRTT:
//                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
//                        strNetworkType = "2G";
//                        break;
//
//                    //3g
//                    case TelephonyManager.NETWORK_TYPE_UMTS:
//                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                    case TelephonyManager.NETWORK_TYPE_HSDPA:
//                    case TelephonyManager.NETWORK_TYPE_HSUPA:
//                    case TelephonyManager.NETWORK_TYPE_HSPA:
//                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
//                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
//                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
//                        strNetworkType = "3G";
//                        break;
//
//                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
//                        strNetworkType = "4G";
//                        break;
//
//                    default:
//                        String _strSubTypeName = networkInfo.getSubtypeName();
//                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
//                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
//                            strNetworkType = "3G";
//                        } else {
//                            strNetworkType = _strSubTypeName;
//                        }
//                        break;
//                }
//            }
//        }
//        Log.w(TAG, "联网方式:" + strNetworkType);
//        return strNetworkType;
//    }
//
//    private boolean checkReadPhoneStatePermission(Context context) {
//        try {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE},
//                        10);
//                return false;
//            }
//        } catch (IllegalArgumentException e) {
//            return false;
//        }
//        return true;
//    }
//
//
//    public String getMEID(Context context) {
//        if (!checkReadPhoneStatePermission(context)) {
//            Log.w(TAG, "获取唯一设备号-getMEID: 无权限");
//            return "";
//        }
//        String meid = "";
//        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (null != mTelephonyMgr) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                meid = mTelephonyMgr.getMeid();
//                Log.i(TAG, "Android版本大于o-26-优化后的获取---meid:" + meid);
//            } else {
//                meid = mTelephonyMgr.getDeviceId();
//            }
//        }
//
//        Log.i(TAG, "优化后的获取---meid:" + meid);
//
//        return meid;
//    }
//
//    public String getIMEI(Context context) {
//        if (!checkReadPhoneStatePermission(context)) {
//            Log.w(TAG, "获取唯一设备号-getIMEI: 无权限");
//            return "";
//        }
//        String imei1 = "";
//        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (null != mTelephonyMgr) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                imei1 = mTelephonyMgr.getImei(0);
//                Log.i(TAG, "Android版本大于o-26-优化后的获取---imei-1:" + imei1);
//            } else {
//                try {
//                    imei1 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 0);
//                } catch (Exception e) {
//                    try {
//                        imei1 = getDoubleImei(mTelephonyMgr, "getDeviceId", 0);
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                    Log.e(TAG, "get device id fail: " + e.toString());
//                }
//            }
//        }
//
//        Log.i(TAG, "优化后的获取---imei1：" + imei1);
//        return imei1;
//    }
//
//    public String getIMEI2(Context context) {
//        if (!checkReadPhoneStatePermission(context)) {
//            Log.w(TAG, "获取唯一设备号-getIMEI2: 无权限");
//            return "";
//        }
//        String imei2 = "";
//        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (null != mTelephonyMgr) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                imei2 = mTelephonyMgr.getImei(1);
//                mTelephonyMgr.getMeid();
//                Log.i(TAG, "Android版本大于o-26-优化后的获取---imei-2:" + imei2);
//            } else {
//                try {
//                    imei2 = getDoubleImei(mTelephonyMgr, "getDeviceIdGemini", 1);
//                } catch (Exception e) {
//                    try {
//                        imei2 = getDoubleImei(mTelephonyMgr, "getDeviceId", 1);
//                    } catch (Exception ex) {
//                        Log.e(TAG, "get device id fail: " + e.toString());
//                    }
//                }
//            }
//        }
//
//        Log.i(TAG, "优化后的获取--- imei2:" + imei2);
//        return imei2;
//    }
//
//    /**
//     * 获取双卡手机的imei
//     */
//    private String getDoubleImei(TelephonyManager telephony, String predictedMethodName, int slotID) throws Exception {
//        String inumeric = null;
//
//        Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
//        Class<?>[] parameter = new Class[1];
//        parameter[0] = int.class;
//        Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
//        Object[] obParameter = new Object[1];
//        obParameter[0] = slotID;
//        Object ob_phone = getSimID.invoke(telephony, obParameter);
//        if (ob_phone != null) {
//            inumeric = ob_phone.toString();
//        }
//        return inumeric;
//    }

    /**
     * 获取设备信息
     *
     * @param context context
     * @return 设备信息
     */
    public DeviceInfo getDeviceInfo(Context context) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setPhoneBrand(getPhoneBrand());
        deviceInfo.setPhoneModel(getPhoneModel());
        deviceInfo.setOs(getOS());
        deviceInfo.setResolution(getResolution(context));
        return deviceInfo;
    }
}
