package com.bc.wechat;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.SDKInitializer;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.enums.ViewType;
import com.bc.wechat.observer.Observer;
import com.bc.wechat.observer.ObserverManager;
import com.bc.wechat.service.LocationService;
import com.bc.wechat.utils.JimUtil;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.TimeUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarContext;

import java.util.Date;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * application
 *
 * @author zhou
 */
public class WechatApplication extends Application implements Observer {

    public static LocationService locationService;
    ObserverManager manager;
    User mUser;

    @Override
    public void onCreate() {
        super.onCreate();

        mUser = PreferencesUtil.getInstance().getUser();
        Fresco.initialize(this);
        SugarContext.init(this);

        // 极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        // 极光IM
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);

        // 上拉下拉控件
//        ClassicsHeader.REFRESH_HEADER_PULLING = ""; //" 下拉可以刷新";
//        ClassicsHeader.REFRESH_HEADER_RELEASE = ""; // "释放立即刷新";
//        ClassicsHeader.REFRESH_HEADER_REFRESHING = ""; // "正在刷新...";
//        ClassicsHeader.REFRESH_HEADER_RELEASE = ""; // "释放立即刷新";
//        ClassicsHeader.REFRESH_HEADER_FINISH = ""; // "刷新完成";
//        ClassicsHeader.REFRESH_HEADER_FAILED = ""; // "刷新失败";
//        ClassicsHeader.REFRESH_HEADER_UPDATE = ""; // "上次更新 M-d HH:mm";
//
//        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在加载"; // "正在加载..."
//        ClassicsFooter.REFRESH_FOOTER_FINISH = ""; // "加载完成"
//        ClassicsFooter.REFRESH_FOOTER_NOTHING = ""; // "全部加载完成"

        // 百度地图
        SDKInitializer.initialize(this);
        locationService = new LocationService(getApplicationContext());

        JMessageClient.registerEventReceiver(this);
        if (manager == null) {
            manager = ObserverManager.getInstance();
            manager.addObserver(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JMessageClient.unRegisterEventReceiver(this);
        if (manager != null) {
            manager.removeObserver(this);
        }
    }

    public void onEvent(MessageEvent event) {
        handleReceivedMessage(event.getMessage());
    }

    public void handleReceivedMessage(Message msg) {
        saveMessage(msg);
        ViewType[] viewTypes = ViewType.values();
        for (ViewType viewType : viewTypes) {
            manager.notify(viewType.getType(), msg);
        }
    }

    /**
     * 保存消息
     *
     * @param msg 消息
     */
    private void saveMessage(Message msg) {
        // 自己发送出的消息(接收方非自己)自己也能收到
        // 如果是自己发送的消息 则无需处理此条消息
        // 发送者
        UserInfo fromUserInfo = msg.getFromUser();
        if (fromUserInfo.getUserName().equals(mUser.getUserId())) {
            return;
        }

        com.bc.wechat.entity.Message message = new com.bc.wechat.entity.Message();
        message.setCreateTime(TimeUtil.getTimeStringAutoShort2(new Date().getTime(), true));

        // 消息发送者信息
        message.setFromUserId(fromUserInfo.getUserName());
        message.setFromUserName(fromUserInfo.getNickname());
        message.setFromUserAvatar(fromUserInfo.getAvatar());

        // 群发 or 单发
        if (msg.getTargetType().equals(ConversationType.single)) {
            message.setTargetType(Constant.TARGET_TYPE_SINGLE);

        } else {
            message.setTargetType(Constant.TARGET_TYPE_GROUP);
            GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
            message.setGroupId(String.valueOf(groupInfo.getGroupID()));
        }

        // 消息接收者信息
        message.setToUserId(mUser.getUserId());

        // 消息类型
        message.setMessageType(JimUtil.getMessageType(msg));
        message.setTimestamp(new Date().getTime());

        if (Constant.MSG_TYPE_TEXT.equals(message.getMessageType())) {
            // 文字
            TextContent messageContent = (TextContent) msg.getContent();
            message.setContent(messageContent.getText());
        } else if (Constant.MSG_TYPE_IMAGE.equals(message.getMessageType())) {
            // 图片
            Map<String, String> messageMap = JSON.parseObject(msg.getContent().toJson(), Map.class);
            Map<String, Object> messageBodyMap = JSON.parseObject(messageMap.get("text"), Map.class);
            message.setMessageBody(JSON.toJSONString(messageBodyMap));

        } else if (Constant.MSG_TYPE_LOCATION.equals(message.getMessageType())) {
            // 位置
            Map<String, String> messageMap = JSON.parseObject(msg.getContent().toJson(), Map.class);
            Map<String, Object> messageBodyMap = JSON.parseObject(messageMap.get("text"), Map.class);
            message.setMessageBody(JSON.toJSONString(messageBodyMap));
        }

        com.bc.wechat.entity.Message.save(message);
    }

    @Override
    public void receiveMsg(int viewType, Message msg) {

    }

}