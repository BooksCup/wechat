package com.bc.wechat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.bc.wechat.activity.PeopleNearbyActivity;
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
    private static final int REQUEST_CODE_SCAN = 100;
    private static final int REQUEST_CODE_CAMERA = 110;

    // 朋友圈
    private RelativeLayout mMomentsRl;

    // 扫一扫
    private RelativeLayout mScanRl;

    // 附近的人
    private RelativeLayout mPeopleNearByRl;


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
                requestPerms(getActivity(), permissions, REQUEST_CODE_CAMERA);
            }
        });

        mPeopleNearByRl = getView().findViewById(R.id.rl_people_nearby);
        mPeopleNearByRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConfirmDialog mConfirmDialog = new ConfirmDialog(getActivity(), getString(R.string.tips),
                        getString(R.string.open_people_nearby_tips),
                        getString(R.string.ok), getString(R.string.cancel), getActivity().getColor(R.color.navy_blue));
                mConfirmDialog.setOnDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        mConfirmDialog.dismiss();
                        startActivity(new Intent(getActivity(), PeopleNearbyActivity.class));
                    }

                    @Override
                    public void onCancelClick() {
                        mConfirmDialog.dismiss();
                    }
                });
                // 点击空白处消失
                mConfirmDialog.setCancelable(true);
                mConfirmDialog.show();
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
            if (requestCode == REQUEST_CODE_SCAN) {
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
    public void requestPerms(Activity activity, String[] permissions, int requestCode) {
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

    /**
     * 进入扫一扫页面
     */
    private void startScanActivity() {
        Intent intent = new Intent(getActivity(), CaptureActivity2.class);
        intent.putExtra(CaptureActivity2.USE_DEFUALT_ISBN_ACTIVITY, true);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

}
