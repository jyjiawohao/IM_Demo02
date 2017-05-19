package com.sizu.mingteng.im_demo02.view.fragment;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.sizu.mingteng.im_demo02.MainActivity;
import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.adapter.ConversationAdapter;
import com.sizu.mingteng.im_demo02.presenter.ConversationPresenter;
import com.sizu.mingteng.im_demo02.presenter.impl.ConversationPresenterImpl;
import com.sizu.mingteng.im_demo02.view.ChatActivity;
import com.sizu.mingteng.im_demo02.view.ConversationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by lenovo on 2017/5/12.
 */

public class ConversationFragment extends BaseFragment implements View.OnClickListener, ConversationAdapter.OnItemClickListener, ConversationView {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private ConversationPresenter mConversationPresenter;
    private ConversationAdapter mConversationAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation,container,false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        /**
         *  初始化会话列表
         */
        mConversationPresenter = new ConversationPresenterImpl(this);
        mConversationPresenter.initConversation();
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message){
        MainActivity activity = (MainActivity) getActivity();
        activity.showToast("收到信消息："+message.getBody().toString());
        mConversationPresenter.initConversation(); //有最新消息 重新请求网络
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mConversationAdapter!=null){
            mConversationAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mConversationAdapter = null;  //销毁的时候清空
    }

    /**
     *  fab
     * @param v
     */
    @Override
    public void onClick(View v) {
        //将所有的会话全部比较为已读
        ObjectAnimator.ofFloat(mFab,"rotation",0,360).setDuration(1000).start();//设置动画 360度 转一圈
        EMClient.getInstance().chatManager().markAllConversationsAsRead(); //将所有的会话全部比较为已读
        MainActivity activity = (MainActivity) getActivity();
        activity.updateUnreadCount(); //刷新 消息数量
        if (mConversationAdapter!=null){
            mConversationAdapter.notifyDataSetChanged();  //TODO 使用 刷新的时候 未读消息还是没有清空数据
        }
    }

    @Override
    public void onInitConversation(List<EMConversation> emConversationList) {
        if (mConversationAdapter==null){  // 因为是当前类是 单列的是时候
            mConversationAdapter = new ConversationAdapter(emConversationList);
            mRecyclerView.setAdapter(mConversationAdapter);
            mConversationAdapter.setOnItemClickListener(this);
        }else{
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(EMConversation conversation) {
        String name = conversation.conversationId();
        MainActivity activity = (MainActivity) getActivity();
        activity.startActivity(ChatActivity.class,false,name);
    }
}
