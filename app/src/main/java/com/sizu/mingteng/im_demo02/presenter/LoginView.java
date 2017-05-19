package com.sizu.mingteng.im_demo02.presenter;

/**
 * Created by lenovo on 2017/5/11.
 * 登录查看
 */

public interface LoginView {
    void onLogin(String username, String pwd, boolean success, String msg);
}
