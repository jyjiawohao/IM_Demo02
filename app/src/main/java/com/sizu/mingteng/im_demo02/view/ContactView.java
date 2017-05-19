package com.sizu.mingteng.im_demo02.view;

import java.util.List;

/**
 * Created by lenovo on 2017/5/14.
 */

public interface ContactView {
    void onInitContacts(List<String> contacts);

    void updateContacts(boolean success, String msg);

    void onDelete(String contact, boolean success, String msg);
}
