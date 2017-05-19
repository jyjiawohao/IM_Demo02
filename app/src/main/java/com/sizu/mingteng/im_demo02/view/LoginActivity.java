package com.sizu.mingteng.im_demo02.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sizu.mingteng.im_demo02.MainActivity;
import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.presenter.LoginView;
import com.sizu.mingteng.im_demo02.presenter.impl.LoginPresenterImpl;
import com.sizu.mingteng.im_demo02.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lenovo on 2017/5/11.
 */

public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener, LoginView {
    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.til_username)
    TextInputLayout mTilUsername;
    @InjectView(R.id.et_pwd)
    EditText mEtPwd;
    @InjectView(R.id.til_pwd)
    TextInputLayout mTilPwd;
    @InjectView(R.id.btn_login)
    Button mBtnLogin;
    private LoginPresenterImpl mLoginPresenter;
    private final int REQUEST_SDCARD = 1; //不能小于0
    public int LOCATION = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        /**
         * 数据的回显
         */
        mEtUsername.setText(getUserName());
        mEtPwd.setText(getPwd());
        mEtPwd.setOnEditorActionListener(this);
        mLoginPresenter = new LoginPresenterImpl(this);
    }

    @OnClick({R.id.btn_login, R.id.tv_newuser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_newuser:
                startActivity(RegistActivity.class, false);
                break;
        }
    }

    private void login() {
        String username = mEtUsername.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();

        if (!StringUtils.checkUsername(username)) {
            mTilUsername.setErrorEnabled(true); //TextInputLayout  的方法
            mTilUsername.setError("用户名不合法");

            mEtUsername.requestFocus(View.FOCUS_RIGHT);

            return;
        } else {
            mTilUsername.setErrorEnabled(false);
        }
        if (!StringUtils.checkPwd(pwd)) {
            mTilPwd.setErrorEnabled(true);
            mTilPwd.setError("密码不合法");

            mEtPwd.requestFocus(View.FOCUS_RIGHT);
            return;
        } else {
            mTilPwd.setErrorEnabled(false);
        }
        /**
         * 1. 动态申请权限
         */
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_SDCARD);
            return;
        }*/

        boolean b = checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
        if (b == false) {
            requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, LOCATION);
        }
        if (b == false)
            Toast.makeText(this, "请授予权限 ", Toast.LENGTH_SHORT).show();

        if (b){
            showDialog("正在玩命登录中...");
            mLoginPresenter.login(username, pwd);
        }
    }

    /**
     * 请求权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean b = checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
        if (b)
            //被授权了
            login();
        else
            showToast("没有给予该应用权限，不让你用了");
    }

    /**
     * 判断在编辑的时候  编辑到et_pwd 最后一个的时候  点击软键盘下一步的时候就去登录
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.et_pwd) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
            }
        }
        return false;
    }


    /**
     * 当再次startActivity的时候，接收新的Intent对象
     * <p>
     * 调用的前提是该启动模式是singleTask，或者singleTop但是他得在最上面才有效
     * singleTop启动模式onCreate 不走 走onNewIntent方法
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mEtUsername.setText(getUserName());
        mEtPwd.setText(getPwd());
    }

    /**
     * LoginPresenterImpl 登录逻辑
     *
     * @param username
     * @param pwd
     * @param success
     * @param msg
     */
    @Override
    public void onLogin(String username, String pwd, boolean success, String msg) {
        hideDialog(); //隐藏对话框
        if (success) {
            /**
             * 1.保存用户
             * 2. 跳转到主界面
             */
            saveUser(username, pwd); //存储用户账户 密码
            startActivity(MainActivity.class, true);
        } else {
            /**
             * 1.Toast
             */
            showToast("登录失败了：" + msg);
        }
    }
}
