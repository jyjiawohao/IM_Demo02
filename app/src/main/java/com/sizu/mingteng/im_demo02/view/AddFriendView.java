package com.sizu.mingteng.im_demo02.view;


import com.sizu.mingteng.im_demo02.model.User;

import java.util.List;

/**
 * Created by lenovo on 2017/5/15.
 */

public interface AddFriendView {
    /**
     * 收索结果
     * @param list
     * @param contactsList
     * @param success
     * @param msg
     */
    void onSearchResult(List<User> list, List<String> contactsList, boolean success, String msg);
}
