package com.sizu.mingteng.im_demo02.view;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sizu.mingteng.im_demo02.MainActivity;
import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.adapter.AnimatorListenerAdapter;
import com.sizu.mingteng.im_demo02.presenter.SplashPresenter;
import com.sizu.mingteng.im_demo02.presenter.SplashView;
import com.sizu.mingteng.im_demo02.presenter.impl.SplashPresenterImpl;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends BaseActivity implements SplashView {
    private static final long DURATION = 2000;
    @InjectView(R.id.iv_splash)
    ImageView mIvSplash;
    @InjectView(R.id.activity_splash)
    LinearLayout mActivitySplash;
    private SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        mSplashPresenter = new SplashPresenterImpl(this);
        /**
         * 1. 判断是否已经登录了
         *
         * 2. 如果登录了，直接进入MainActivity
         *
         * 3. 否则闪屏2秒后（渐变动画），进入LoginActivity
         */
        mSplashPresenter.checkLogined();
    }

    @Override
    public void onCheckedLogin(boolean isLogined) {
        if (isLogined){
            startActivity(MainActivity.class,true);
        }else {
//            否则闪屏2秒后（渐变动画），进入LoginActivity
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvSplash, "alpha", 0, 1).setDuration(DURATION);
            alpha.start();
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startActivity(LoginActivity.class,true);
                }
            });
        }
    }
}
