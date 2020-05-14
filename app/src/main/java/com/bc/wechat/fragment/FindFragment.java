package com.bc.wechat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.R;
import com.bc.wechat.activity.FriendsCircleActivity;
import com.bc.wechat.activity.UserInfoActivity;
import com.bc.wechat.activity.WebViewActivity;
import com.bc.wechat.entity.QrCodeContent;
import com.bc.wechat.widget.ConfirmDialog;
import com.google.zxing.client.android.CaptureActivity2;

import java.util.ArrayList;

/**
 * 发现
 *
 * @author zhou
 */
public class FindFragment extends Fragment {
    private static final int REQUEST_CODE_CAMERA = 1;

    // 朋友圈
    private RelativeLayout mMomentsRl;

    // 扫一扫
    private RelativeLayout mScanRl;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMomentsRl = getView().findViewById(R.id.rl_moments);
        mMomentsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FriendsCircleActivity.class));
            }
        });

        mScanRl = getView().findViewById(R.id.rl_scan);
        mScanRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = new String[]{"android.permission.CAMERA"};
                requestPers(getActivity(), permissions, REQUEST_CODE_CAMERA);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                String isbn = data.getStringExtra("CaptureIsbn");
                if (!TextUtils.isEmpty(isbn)) {
                    if (isbn.contains("http")) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra(WebViewActivity.RESULT, isbn);
                        startActivity(intent);
                    } else {
                        try {
                            QrCodeContent qrCodeContent = JSON.parseObject(isbn, QrCodeContent.class);
                            if (QrCodeContent.QR_CODE_TYPE_USER.equals(qrCodeContent.getType())) {
                                startActivity(new Intent(getActivity(), UserInfoActivity.class).
                                        putExtra("userId", String.valueOf(qrCodeContent.getContentMap().get("userId"))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 动态权限
     */
    public void requestPers(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //Android 6.0开始的动态权限，这里进行版本判断
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                // 非初次进入App且已授权
                startScanActivity();
            } else {
                // 请求权限方法
                String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                // 这个触发下面onRequestPermissionsResult这个回调
                requestPermissions(requestPermissions, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        // 判断是否拒绝  拒绝后要怎么处理 以及取消再次提示的处理
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            // 同意权限做的处理,开启服务提交通讯录
            startScanActivity();
        } else {
            // 拒绝授权做的处理，弹出弹框提示用户授权
            handleRejectPermission(getActivity(), permissions[0]);
        }
    }

    public void handleRejectPermission(final Activity context, String permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            final ConfirmDialog mConfirmDialog = new ConfirmDialog(getActivity(), getString(R.string.request_permission),
                    getString(R.string.request_permission_camera),
                    getString(R.string.go_setting), getString(R.string.cancel), context.getColor(R.color.navy_blue));
            mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    mConfirmDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {
                    mConfirmDialog.dismiss();
                }
            });
            // 点击空白处消失
            mConfirmDialog.setCancelable(false);
            mConfirmDialog.show();
        }
    }

    /**
     * 进入扫一扫页面
     */
    private void startScanActivity() {
        Intent intent = new Intent(getActivity(), CaptureActivity2.class);
        intent.putExtra(CaptureActivity2.USE_DEFUALT_ISBN_ACTIVITY, true);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

}
