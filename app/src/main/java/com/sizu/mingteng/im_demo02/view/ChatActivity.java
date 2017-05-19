package com.sizu.mingteng.im_demo02.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.sizu.mingteng.im_demo02.R;

/**
 * Created by lenovo on 2017/5/17.
 */

public class ChatActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EaseChatFragment chatFragment = new EaseChatFragment();
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, getIntent().getStringExtra("username"));
        chatFragment.setArguments(args);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().add(R.id.Container, chatFragment).commit();
        //container是你创建的activity的根布局的id
    }
}
