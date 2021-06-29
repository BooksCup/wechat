package com.bc.wechat.observer;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.model.Message;

/**
 * 观察者管理器
 *
 * @author zhou
 */
public class ObserverManager implements Manager {

    private static ObserverManager instance = null;
    private List<Observer> Observers = new ArrayList<>();

    private ObserverManager() {
    }

    public static ObserverManager getInstance() {
        if (null == instance) {
            synchronized (ObserverManager.class) {
                if (null == instance) {
                    instance = new ObserverManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    @Override
    public void addObserver(Observer observer) {
        Observers.add(observer);
    }

    /**
     * 移除观察者
     *
     * @param observer 观察者
     */
    @Override
    public void removeObserver(Observer observer) {
        Observers.remove(observer);
    }

    /**
     * 发送通知
     *
     * @param viewType 页面类型
     * @param msg      消息
     */
    @Override
    public void notify(int viewType, Message msg) {
        for (Observer observer : Observers) {
            observer.receiveMsg(viewType, msg);
        }
    }

}