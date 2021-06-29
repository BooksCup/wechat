package com.bc.wechat.observer;

import cn.jpush.im.android.api.model.Message;

/**
 * 观察者
 *
 * @author zhou
 */
public interface Observer {

    /**
     * 接收消息
     *
     * @param viewType 页面类型
     * @param msg      消息
     */
    void receiveMsg(int viewType, Message msg);

}