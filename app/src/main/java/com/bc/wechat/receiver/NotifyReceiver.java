package com.bc.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bc.wechat.activity.MainActivity;
import com.bc.wechat.activity.NewFriendsMsgActivity;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.FriendApply;
import com.bc.wechat.utils.ExampleUtil;
import com.bc.wechat.utils.PreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class NotifyReceiver extends BroadcastReceiver {
    private static final String TAG = "NotifyReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                messageProcessEntrance(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

//                //打开自定义的Activity
//                Intent i = new Intent(context, TestActivity.class);
//                i.putExtras(bundle);
//                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                context.startActivity(i);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
    }

    /**
     * 消息处理总入口
     *
     * @param context
     * @param bundle
     */
    private void messageProcessEntrance(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Map<String, String> extrasMap = new HashMap<>();
        extrasMap = JSON.parseObject(extras, extrasMap.getClass());
        String serviceType = extrasMap.get("serviceType");
        if (Constant.PUSH_SERVICE_TYPE_ADD_FRIENDS_APPLY.equals(serviceType)) {
            processAddFriendsMessage(context, bundle, extrasMap);
        }
    }

    /**
     * 处理加好友消息
     *
     * @param context
     * @param bundle
     */
    private void processAddFriendsMessage(Context context, Bundle bundle, Map<String, String> extrasMap) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        FriendApply friendApply = com.alibaba.fastjson.JSONObject.parseObject(extrasMap.get("friendApply"), FriendApply.class);
        List<FriendApply> friendApplyList = FriendApply.find(FriendApply.class, "from_user_id = ?", friendApply.getFromUserId());
        if (null != friendApplyList && friendApplyList.size() > 0) {
            // 已存在,更新时间戳
            FriendApply.update(friendApply);
        } else {
            // 不存在,插入sqlite
            FriendApply.save(friendApply);
        }

        if (MainActivity.isForeground) {
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_MAIN);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            PreferencesUtil.getInstance().setNewFriendsUnreadNumber(PreferencesUtil.getInstance().getNewFriendsUnreadNumber() + 1);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        } else if (NewFriendsMsgActivity.isForeground) {
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION_ADD_FRIENDS_NEW_FRIENDS_MSG);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            PreferencesUtil.getInstance().setNewFriendsUnreadNumber(0);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        } else {
            PreferencesUtil.getInstance().setNewFriendsUnreadNumber(PreferencesUtil.getInstance().getNewFriendsUnreadNumber() + 1);
        }
    }
}
