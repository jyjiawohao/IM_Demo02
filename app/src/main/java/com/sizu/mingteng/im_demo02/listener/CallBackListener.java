package com.sizu.mingteng.im_demo02.listener;

import com.hyphenate.EMCallBack;
import com.sizu.mingteng.im_demo02.utils.ThreadUtils;

/**
 * Created by lenovo on 2017/5/11.
 * 包装设计模式 也是装饰设计模式 包他的方法保留 包装一下
 * 请求环信 登录 退出 回调的接口 (包装)
 */

public abstract class CallBackListener implements EMCallBack {
    public  abstract void onMainSuccess(); //主线程

    public abstract void onMainError(int i, String s);  //主线程
    @Override
    public void onSuccess() {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainSuccess();
            }
        });
    }

    @Override
    public void onError(final int i, final String s) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainError(i,s);
            }
        });
    }

    @Override
    public void onProgress(int i, String s) {

    }
}
