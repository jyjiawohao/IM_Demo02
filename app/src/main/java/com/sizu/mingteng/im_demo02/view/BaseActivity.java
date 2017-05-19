package com.sizu.mingteng.im_demo02.view;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sizu.mingteng.im_demo02.App;
import com.sizu.mingteng.im_demo02.PermissionActivity;
import com.sizu.mingteng.im_demo02.utils.Constant;
import com.sizu.mingteng.im_demo02.utils.SPUtils;
import com.sizu.mingteng.im_demo02.utils.ToastUtils;

/**
 * Created by lenovo on 2017/5/11.
 */

public class BaseActivity extends PermissionActivity {
    private App mApp;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *  所有的Activity都依附于一个Application，在Activity中只要通过 getApplication（）方法，
         *  就能拿到当前应用中的Application对象
         */
        mApp= (App) getApplication();
        mApp.addActivity(this);
        mProgressDialog = new ProgressDialog(this); //设置进度对话框
        mProgressDialog.setCancelable(false);
    }
    public void saveUser(String username,String pwd){
        SPUtils.put(mApp, Constant.SP_KEY_USERNAME,username);
        SPUtils.put(mApp, Constant.SP_KEY_PWD,pwd);
    }

    /**
     * 获取用户名
     * @return
     */
    public String getUserName(){
        return (String) SPUtils.get(mApp,Constant.SP_KEY_USERNAME,"");
    }

    /**
     * 获取密码
     * @return
     */
    public String getPwd(){
        return (String)SPUtils.get(mApp,Constant.SP_KEY_PWD,"");
    }

    /**
     * 显示diaog
     * @param msg
     */
    public void showDialog(String msg){
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    /**
     * 隐藏 diaog
     */
    public void hideDialog(){
        mProgressDialog.hide();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApp.removeActivity(this);
        mProgressDialog.dismiss();
    }

    /**
     * 吐司
     * @param msg
     */
    public void showToast(String msg){
        ToastUtils.showToast(this,msg);
    }

    /**
     * 带参数  是否需要 finish 当前activity
     * @param clazz
     * @param isFinish
     * @param contact
     */
    public void startActivity(Class clazz, boolean isFinish,String username, String contact) {
        Intent intent = new Intent(this,clazz);
        if (contact!=null){
            intent.putExtra(username,contact);
        }
        startActivity(intent);
        if (isFinish){
            finish();
        }
    }

    public void startActivity(Class clazz, boolean isFinish, String contact) {
        Intent intent = new Intent(this,clazz);
        if (contact!=null){
            intent.putExtra("username",contact);
        }
        startActivity(intent);
        if (isFinish){
            finish();
        }
    }

    /**
     * 不带参数的
     * @param clazz
     * @param isFinish
     */
    public void startActivity(Class clazz,boolean isFinish){
        startActivity(clazz,isFinish,null);
    }


    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mApp, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mApp, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }



}
