package com.sizu.mingteng.im_demo02.presenter;

/**
 * Created by lenovo on 2017/5/15.
 */

public interface AddFriendPresenter {
    /**
     * 搜索好友
     * @param keyword
     */
    void searchFriend(String keyword);

    /**
     * 添加好友
     * @param username
     */
    void addFriend(String username);
}
