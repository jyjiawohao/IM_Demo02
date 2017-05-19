package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.sizu.mingteng.im_demo02.listener.CallBackListener;
import com.sizu.mingteng.im_demo02.presenter.LoginPresenter;
import com.sizu.mingteng.im_demo02.presenter.LoginView;

/**
 * Created by lenovo on 2017/5/11.
 */

public class LoginPresenterImpl implements LoginPresenter {
    private LoginView mLoginView;
    public LoginPresenterImpl(LoginView loginView) {
        mLoginView=loginView;
    }

    @Override
    public void login(final String username, final String pwd) {
        //环信目前（3.5.x）的所有回调方法都是在子线程中回调的  环信的所回调都是在子线程中 但是回调回来的操作需要在主线程中所以重写
        EMClient.getInstance().login(username, pwd, new CallBackListener() {
            @Override
            public void onMainSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                //以上两个方法是为了保证进入主页面后本地会话和群组都 load 完毕

                mLoginView.onLogin(username,pwd,true,null);
            }

            @Override
            public void onMainError(int i, String s) {
                mLoginView.onLogin(username,pwd,false,s);
            }
        });

       /* EMClient.getInstance().login(username,pwd,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });*/

    }
}
