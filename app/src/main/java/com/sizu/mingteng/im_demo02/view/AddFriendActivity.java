package com.sizu.mingteng.im_demo02.view;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.sizu.mingteng.im_demo02.R;
import com.sizu.mingteng.im_demo02.model.User;
import com.sizu.mingteng.im_demo02.presenter.AddFriendPresenter;
import com.sizu.mingteng.im_demo02.presenter.impl.AddFriendPresenterImpl;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lenovo on 2017/5/15.
 */

public class AddFriendActivity extends BaseActivity implements AddFriendView, SearchView.OnQueryTextListener {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.iv_nodata)
    ImageView mIvNodata;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private AddFriendPresenter mAddFriendPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }*/
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);
        mToolbar.setTitle("搜好友");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAddFriendPresenter = new AddFriendPresenterImpl(this);
    }

    /**
     * 搜索
     */
    private SearchView mSearchView;
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_friend_menu,menu);
    /**
     * 初始化菜单中的SearchView
     */
    MenuItem menuItem = menu.findItem(R.id.search);
    mSearchView = (SearchView) menuItem.getActionView();
    /**
     * 在SearchView中添加提示
     */
    mSearchView.setQueryHint("搜好友");
    mSearchView.setOnQueryTextListener(this);
    return true;
}

    /**
     * 返回
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    /**
     *  文本提交的时候
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query)){
            showToast("请输入用户名再搜索！");
            return false;
        }
        mAddFriendPresenter.searchFriend(query);
        //隐藏软键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(),0);
        return true;
    }

    /**
     * 查询文本变化
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText )){
            showToast(newText);
        }
        return true;
    }


    @Override
    public void onSearchResult(List<User> list, List<String> contactsList, boolean success, String msg) {

    }
}
