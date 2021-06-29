package com.bc.wechat.observer;

import cn.jpush.im.android.api.model.Message;

/**
 * 观察者管理器接口
 *
 * @author zhou
 */
public interface Manager {

    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    void addObserver(Observer observer);

    /**
     * 移除观察者
     *
     * @param observer 观察者
     */
    void removeObserver(Observer observer);

    /**
     * 发送通知
     *
     * @param viewType 页面类型
     * @param msg      消息
     */
    void notify(int viewType, Message msg);

}