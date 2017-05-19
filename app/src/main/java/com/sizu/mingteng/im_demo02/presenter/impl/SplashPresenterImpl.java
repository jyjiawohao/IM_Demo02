package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.sizu.mingteng.im_demo02.presenter.SplashPresenter;
import com.sizu.mingteng.im_demo02.presenter.SplashView;

/**
 * Created by lenovo on 2017/5/11.
 *
 * 判断是否已经登录过环信
 */

public class SplashPresenterImpl implements SplashPresenter {
    private SplashView mSplashView;

    public SplashPresenterImpl(SplashView splashView) {
        mSplashView = splashView;
    }

    @Override
    public void checkLogined() {
        if (EMClient.getInstance().isLoggedInBefore() && EMClient.getInstance().isConnected()) {
            //已经登录过了
            mSplashView.onCheckedLogin(true);
        } else {
            //还未登录
            mSplashView.onCheckedLogin(false);
        }
    }
}
