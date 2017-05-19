package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.sizu.mingteng.im_demo02.presenter.RegistPresenter;
import com.sizu.mingteng.im_demo02.utils.ThreadUtils;
import com.sizu.mingteng.im_demo02.view.RegistView;

/**
 * Created by lenovo on 2017/5/11.
 */

public class RegistPresenterImpl implements RegistPresenter {
    private RegistView mRegistView;
    public RegistPresenterImpl(RegistView registView) {
        mRegistView=registView;
    }

    @Override
    public void regist(final String username, final String pwd) {
        /**
         * 1. 先注册 后台  数据库
         * 2. 如果后台成功了再去注册环信平台
         * 3. 如果后台成功了，环信失败了，则再去把后台 上的数据给删除掉
         */
        // TODO  1:先注册 后台  数据库

        //TODO 2 :成功了再去注册环信平台
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //因为这行代码是同步方法,代码走到这里会堵塞到这里,所以在子线程里面开启注册的方法
                    // ,因为每次都调用(开启子线程是一个耗时的操作,所以写一个线程池)
                    EMClient.getInstance().createAccount(username, pwd); //子线程 注册环信
                    //环信注册成功
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO 3 如果后台成功了 环信成功了
                            mRegistView.onRegist(username,pwd,true,null);//主线程 回调
                        }
                    });
                } catch (final HyphenateException e1) {
                    e1.printStackTrace();
                    //TODO 3 环信失败了 将后台数据库 上注册的user给删除掉
                    //TODO user.delete();

                    //环信注册失败了
                    ThreadUtils.runOnMainThread(new Runnable() { //主线程
                        @Override
                        public void run() {
                            mRegistView.onRegist(username,pwd,false,e1.toString());
                        }
                    });
                }
            }
        });

    }
}
