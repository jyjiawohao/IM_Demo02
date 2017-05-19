package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.sizu.mingteng.im_demo02.db.DBUtils;
import com.sizu.mingteng.im_demo02.model.User;
import com.sizu.mingteng.im_demo02.presenter.AddFriendPresenter;
import com.sizu.mingteng.im_demo02.view.AddFriendView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/5/15.
 */

public class AddFriendPresenterImpl implements AddFriendPresenter {
    private AddFriendView mAddFriendView;

    public AddFriendPresenterImpl(AddFriendView addFriendView) {
        mAddFriendView = addFriendView;
    }

    @Override
    public void searchFriend(String keyword) {
        String currentUser = EMClient.getInstance().getCurrentUser(); //获取用户名
        /**
         * 根据view 传递 的关键字去后台查找数据
         * SDK 不提供好友查找的服务，如需要查找好友，需要调用开发者自己服务器的用户查询接口。
         为了保证查找到的好友可以添加，需要将开发者自己服务器的用户数据（用户的环信 ID）
         ，通过 SDK 的后台接口导入到环信服务器中。
         */
        List<User> list=new ArrayList<>();
        List<String> contacts = DBUtils.getContacts(currentUser); //根据关键字查找数据库
        //获取到数据
        mAddFriendView.onSearchResult(list, contacts, true, null);
        //没有找到数据
       /* if (e == null) {
            mAddFriendView.onSearchResult(null, null, false, "没有找到对应的用户。");
        } else {
            mAddFriendView.onSearchResult(null, null, false, e.getMessage());
        }*/
    }

    @Override
    public void addFriend(String username) {

    }
}
