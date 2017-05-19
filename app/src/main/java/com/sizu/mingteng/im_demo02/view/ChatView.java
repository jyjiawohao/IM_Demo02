package com.sizu.mingteng.im_demo02.view;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by lenovo on 2017/5/16.
 */

public interface ChatView {
    void onInit(List<EMMessage> emMessageList);

    void onUpdate(int size);
}
