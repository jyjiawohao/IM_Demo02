package com.sizu.mingteng.im_demo02.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sizu.mingteng.im_demo02.MainActivity;
import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.adapter.ContactAdapter;
import com.sizu.mingteng.im_demo02.event.OnContactUpdateEvent;
import com.sizu.mingteng.im_demo02.presenter.ContactPresenter;
import com.sizu.mingteng.im_demo02.presenter.impl.ContactPresenterImpl;
import com.sizu.mingteng.im_demo02.utils.ToastUtils;
import com.sizu.mingteng.im_demo02.view.ChatActivity;
import com.sizu.mingteng.im_demo02.view.ContactView;
import com.sizu.mingteng.im_demo02.widget.ContactLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lenovo on 2017/5/12.
 * 联系
 */

public class ContactFragment extends BaseFragment implements ContactView, ContactAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.contactLayout)
    ContactLayout mContactLayout;
    private ContactPresenter mContactPresenter;
    private ContactAdapter mContactAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this); //注册 EventBus
        //第一次进来的时候 找本地缓存好友, 然后在开启一个子线程去那当前的好友列表
        mContactPresenter = new ContactPresenterImpl(this);
        mContactPresenter.initContacts();
        mContactLayout.setOnRefreshListener(this);
        /**
         * 初始化联系人 列表
         */
    }

    /**
     * ThreadMode.MAIN   主线程调用
     * POSTING      这个方法是在哪个线程发送调用 就在哪个线程接收
     * BACKGROUND  后台线程 如果是在发送的线程不是在主线程 那么接收就在哪个线程  如果是在主线程,那么订阅这就在这个线程(后台)
     * ASYNC  不管是在哪个线程发送就在后台线程
     *
     * @param onContactUpdateEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnContactUpdateEvent onContactUpdateEvent) {
        mContactPresenter.updateContacts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this); //销毁EventBus
    }

    /**
     * 初始化 联系人 从数据库获取的
     *
     * @param contacts
     */
    @Override
    public void onInitContacts(List<String> contacts) {
        mContactAdapter = new ContactAdapter(contacts);
        mContactLayout.setAdapter(mContactAdapter);
        mContactAdapter.setOnItemClickListener(this);
    }

    /**
     * 请求环信 获取的联系人数据
     *
     * @param success
     * @param msg
     */
    @Override
    public void updateContacts(boolean success, String msg) {
        mContactAdapter.notifyDataSetChanged();
        //隐藏下拉刷新
        if (mContactLayout != null)
            mContactLayout.setRefreshing(false);
    }

    /**
     * 删除
     *
     * @param contact
     * @param success
     * @param msg
     */
    @Override
    public void onDelete(String contact, boolean success, String msg) {
        if (success) {
            ToastUtils.showToast(getActivity(), "友尽");
            /*
            List<String> data = mContactAdapter.getData();
            data.remove(mPosition);
            mContactAdapter.notifyItemRemoved(mPosition);
            mContactAdapter.notifyItemRangeChanged(0,data.size());*/
        } else {
            ToastUtils.showToast(getActivity(), "删除失败，要不再续前缘？");
        }
    }

    /**
     * 适配器 item 长按点击事件
     *
     * @param contact
     * @param position
     */
    private int mPosition; //记录删除的位置 用来移除

    @Override
    public void onItemLongClick(final String contact, final int position) {
        Snackbar.make(mContactLayout, "您和" + contact + "确定友尽了吗？", Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPosition = position;
                        mContactPresenter.deleteContact(contact);
                    }
                }).show();
    }

    /**
     * 适配器 item 点击事件
     *
     * @param contact
     * @param position
     */
    @Override
    public void onItemClick(String contact, int position) {
        MainActivity activity = (MainActivity) getActivity();
        activity.startActivity(ChatActivity.class, false, "username", contact);
    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        /**
         * 1. 访问网络，获取联系人
         * 2. 如果拿到数据了，更新数据库
         * 3. 更新UI
         * 4. 隐藏下拉刷新
         */
        mContactPresenter.updateContacts();
    }
}
