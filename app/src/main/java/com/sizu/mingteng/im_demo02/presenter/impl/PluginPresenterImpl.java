package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.sizu.mingteng.im_demo02.listener.CallBackListener;
import com.sizu.mingteng.im_demo02.presenter.PluginPresenter;
import com.sizu.mingteng.im_demo02.view.PluginView;

/**
 * Created by lenovo on 2017/5/14.
 */

public class PluginPresenterImpl implements PluginPresenter {
    private PluginView mPluginView;

    public PluginPresenterImpl(PluginView pluginView) {
        mPluginView = pluginView;
    }

    @Override
    public void logout() {
        /**异步方法
         * 参数1：true代表解除绑定，不再推送消息
         */
        EMClient.getInstance().logout(true, new CallBackListener() {
            @Override
            public void onMainSuccess() {
//                EMClient.getInstance().contactManager()
                mPluginView.onLogout(EMClient.getInstance().getCurrentUser(),true,null);
            }

            @Override
            public void onMainError(int i, String s) {
                mPluginView.onLogout(EMClient.getInstance().getCurrentUser(),false,s);
            }
        });
    }
}
