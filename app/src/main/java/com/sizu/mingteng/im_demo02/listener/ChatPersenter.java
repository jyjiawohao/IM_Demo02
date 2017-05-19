package com.sizu.mingteng.im_demo02.listener;

/**
 * Created by lenovo on 2017/5/16.
 */

public interface ChatPersenter {
    void initChat(String contact);

    void sendMessage(String username, String msg);

    void updateData(String username);
}
