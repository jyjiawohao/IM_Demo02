package com.sizu.mingteng.im_demo02.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.sizu.mingteng.im_demo02.presenter.ConversationPresenter;
import com.sizu.mingteng.im_demo02.view.ConversationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017/5/19.
 */

public class ConversationPresenterImpl implements ConversationPresenter {
    private ConversationView mConversationView;
    private List<EMConversation> mEMConversationList = new ArrayList<>();

    public ConversationPresenterImpl(ConversationView conversationView) {
        mConversationView=conversationView;
    }

    @Override
    public void initConversation() {
        //获取所的对话消息  包含谁发送的消息  //获取所有会话
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        //EMConversation username = allConversations.get("username");
        mEMConversationList.clear();
        mEMConversationList.addAll(allConversations.values());
        /**
         * 排序，最近的时间在最上面(时间的倒序)
         * 回传到View层
         */
        Collections.sort(mEMConversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {

                return (int) (o2.getLastMessage().getMsgTime()-o1.getLastMessage().getMsgTime());
            }
        });
        mConversationView.onInitConversation(mEMConversationList);
    }
}
