package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.sizu.mingteng.im_demo02.db.DBUtils;
import com.sizu.mingteng.im_demo02.presenter.ContactPresenter;
import com.sizu.mingteng.im_demo02.utils.ThreadUtils;
import com.sizu.mingteng.im_demo02.view.ContactView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lenovo on 2017/5/14.
 */

public class ContactPresenterImpl implements ContactPresenter {
    private ContactView mContactView;
    private List<String> contactList = new ArrayList<>();

    public ContactPresenterImpl(ContactView contactView) {
        mContactView = contactView;
    }

    @Override
    public void initContacts() {
        /**
         * 1. 首先访问本地的缓存联系人 (数据库)
         * 2. 然后开辟子线程去环信后台获取当前用户的联系人
         * 3. 更新本地的缓存，刷新UI
         */
        final String currentUser = EMClient.getInstance().getCurrentUser();
        List<String> contacts = DBUtils.getContacts(currentUser); //获取联系人 1. 首先访问本地的缓存联系人 (数据库)

        contactList.clear();

        contactList.addAll(contacts);

        mContactView.onInitContacts(contactList);
        updateContactsFromServer(currentUser);


    }

    private void updateContactsFromServer(final String currentUser) {
        // 然后开辟子线程去环信后台获取当前用户的联系人
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //2 去环信的后台拿当前用户的所有用户
                    List<String> contactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //集合排序
                    Collections.sort(contactsFromServer, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    //3 更新本地的缓存，刷新UI  参数1:用户名  根据用户名更新数据库
                    DBUtils.updateContacts(currentUser,contactsFromServer);
                    contactList.clear();
                    contactList.addAll(contactsFromServer);
                    //通知view 刷新ui
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.updateContacts(true,null);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.updateContacts(false,e.getMessage());
                        }
                    });
                }
            }
        });
    }

    /**
     * 刷新的时候重新请求网络
     */
    @Override
    public void updateContacts() {
        updateContactsFromServer(EMClient.getInstance().getCurrentUser());
    }

    /**
     * 开启子线程  调用环信的删除 用户
     * @param contact
     */
    @Override
    public void deleteContact(final String contact) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                    afterDelete(contact, true,null);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    afterDelete(contact,false,e.toString());
                }
            }
        });
    }

    /**
     * 根据删除 成功/失败的状态 返回状态到view 主线程操作
     * @param contact
     * @param success
     * @param msg
     */
    private void afterDelete(final String contact, final boolean success, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mContactView.onDelete(contact, success, msg);
            }
        });
    }
}
