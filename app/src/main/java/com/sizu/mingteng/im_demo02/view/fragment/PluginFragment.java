package com.sizu.mingteng.im_demo02.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.sizu.mingteng.im_demo02.MainActivity;
import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.presenter.impl.PluginPresenterImpl;
import com.sizu.mingteng.im_demo02.utils.ToastUtils;
import com.sizu.mingteng.im_demo02.view.LoginActivity;
import com.sizu.mingteng.im_demo02.view.PluginView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lenovo on 2017/5/12.
 */

public class PluginFragment extends BaseFragment implements PluginView {
    @InjectView(R.id.btn_logout)
    Button mBtnLogout;
    private PluginPresenterImpl mPluginPresenter;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plugin, container, false);
        ButterKnife.inject(this, view);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        return view;
    }

    /**
     *  这个方法初始化view对象
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mBtnLogout = (Button) view.findViewById(R.id.btn_logout);
        String currentUser = EMClient.getInstance().getCurrentUser();//获取环信的当前用户
        mBtnLogout.setText("退("+currentUser+")退出"); //当前用户

        mPluginPresenter=new PluginPresenterImpl(this);//对象的手动注入
    }

    /**
     * 销毁
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        mProgressDialog.dismiss();
    }

    @OnClick(R.id.btn_logout)
    public void onClick() {

        //退出登录
        mProgressDialog.setMessage("正在退出...");
        mProgressDialog.show();
        mPluginPresenter.logout();
    }

    @Override
    public void onLogout(String currentUser, boolean success, String msg) {
        mProgressDialog.hide();
        if (success){

            MainActivity activity = (MainActivity)getActivity(); //退出成功就跳转到登录界面
            activity.startActivity(LoginActivity.class,true);
           // getActivity().finish(); //mainActivity

        }else{
            ToastUtils.showToast(getContext(),msg);
        }
    }

}
