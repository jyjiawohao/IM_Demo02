package com.sizu.mingteng.im_demo02;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.sizu.mingteng.im_demo02.view.AddFriendActivity;
import com.sizu.mingteng.im_demo02.view.BaseActivity;
import com.sizu.mingteng.im_demo02.view.fragment.BaseFragment;
import com.sizu.mingteng.im_demo02.view.fragment.FragmentFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

   /* @InjectView(R.id.fl_content)
    FrameLayout mFlContent;*/

    @InjectView(R.id.bottom_navigation_bar)
    BottomNavigationBar mBottomNavigationBar;
    @InjectView(R.id.activity_main)
    LinearLayout mActivityMain;
    private int[] titleIds = {R.string.conversation, R.string.contact, R.string.plugin};
    private BadgeItem mBadgeItem;//设置角标 也就是 消息徽章

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initToolbar();
        initBottomNavigation();
        initFirstFragment();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message){
        updateUnreadCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadCount();
    }

    public void updateUnreadCount() {
        //获取所有的未读消息
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount>99){
            mBadgeItem.setText("99+");
            mBadgeItem.show(true);
        }else if (unreadMsgsCount>0){
            mBadgeItem.setText(unreadMsgsCount+"");
            mBadgeItem.show(true);
        }else{
            mBadgeItem.hide(true);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTvTitle.setText(titleIds[0]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置导航栏打开
    }

    private void initBottomNavigation() {
        /**
         * MODE_DEFAULT
         如果Item的个数<=3就会使用MODE_FIXED模式，否则使用MODE_SHIFTING模式
         MODE_FIXED
         填充模式，未选中的Item会显示文字，没有换挡动画。
         MODE_SHIFTING
         换挡模式，未选中的Item不会显示文字，选中的会显示文字。在切换的时候会有一个像换挡的动画
         */
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);//设置背景风格
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);//设置样式

        BottomNavigationItem conversationItem = new BottomNavigationItem(R.mipmap.conversation_selected_2, "消息");
        mBadgeItem = new BadgeItem();
        mBadgeItem.setGravity(Gravity.RIGHT);
        mBadgeItem.setTextColor(getResources().getColor(R.color.white)); //设置徽章的颜色 背景
        mBadgeItem.setBackgroundColor(getResources().getColor(R.color.red));
        mBadgeItem.setText("");
        mBadgeItem.show();
        conversationItem.setBadgeItem(mBadgeItem);//绑定徽章
        // conversationItem.setActiveColor(getResources().getColor(R.color.btn_normal));//选中的颜色
        // conversationItem.setInActiveColor(getResources().getColor(R.color.inActive));//没选中的颜色

        mBottomNavigationBar.addItem(conversationItem);

        BottomNavigationItem contactItem = new BottomNavigationItem(R.mipmap.contact_selected_2, "联系人");
//        contactItem.setActiveColor(getResources().getColor(R.color.btn_normal));//选中的颜色
//        contactItem.setInActiveColor(getResources().getColor(R.color.inActive));//没选中的颜色
        mBottomNavigationBar.addItem(contactItem);
        BottomNavigationItem pluginItem = new BottomNavigationItem(R.mipmap.plugin_selected_2, "动态");
//        pluginItem.setActiveColor(getResources().getColor(R.color.btn_normal));//选中的颜色
//        pluginItem.setInActiveColor(getResources().getColor(R.color.inActive));//没选中的颜色
        mBottomNavigationBar.addItem(pluginItem);

        //  mBottomNavigationBar.setActiveColor(R.color.btn_normal);//选中的颜色  (只需要在这里设置一次,就不用每次都设置了)
        // mBottomNavigationBar.setInActiveColor(R.color.inActive);//没选中的颜色

        mBottomNavigationBar.initialise(); //初始化
        mBottomNavigationBar.setTabSelectedListener(this); //设置监听
        // mBottomNavigationBar.setFirstSelectedPosition(1); //设置默认被选中的位置 默认是0
    }

    /**
     * 创建 fragment  解决重影问题
     */
    private void initFirstFragment() {
        /**重影  的Fragment
         *
         * 如果这个Activity中已经有老（就是Activity保存的历史的状态，又恢复了）的Fragment，先全部移除
         * 重新加载过了 fragment  在onCreate(Bundle savedInstanceState) 的savedInstanceState里面保存了数据 activity又没有销毁
         * 这个我们重新启动这个activity 就会重新走onCreate, 就会重新重影
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        for (int i = 0; i < titleIds.length; i++) {
            Fragment fragment = supportFragmentManager.findFragmentByTag(i + "");//用tag 找到记录每个fragment
            if (fragment != null)
                fragmentTransaction.remove(fragment); //判断当前 的fragment是否是创建了,如果创建了就移除
        }
        fragmentTransaction.commit(); //提交事务
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, FragmentFactory.getFragment(0), "0").commit();
        mTvTitle.setText(R.string.conversation); //设置标题
    }

    /**
     * 右边 菜单  默认图标不显示
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 右边 菜单 默认图标不显示
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuBuilder builder = (MenuBuilder) menu; //显示图片
        builder.setOptionalIconsVisible(true);
        return true;
    }

    /**
     * 菜单的点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_friend: //添加好友
                //TODO 跳转到添加好友 Activity
                startActivity(AddFriendActivity.class, false);
                break;
            case R.id.menu_scan:
                showToast("分享好友");
                break;
            case R.id.menu_about:
                showToast("关于我们");
                break;
            case android.R.id.home://导航栏 返回键
                finish();
                break;
        }

        return true;
    }

    /**
     * BottomNavigationBar 状态 选中的
     *
     * @param position
     */
    @Override
    public void onTabSelected(int position) {
        /**
         * 先判断当前Fragment是否被添加到了MainActivity中
         * 如果添加了则直接显示即可
         * 如果没有添加则添加，然后显示
         */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment fragment = FragmentFactory.getFragment(position); //根据bottom点击的位置获取对应的fragment布局
        if (!fragment.isAdded())
            transaction.add(R.id.fl_content, fragment, "" + position); //判断当前视图是否已经添加如果没有添加就 添加
        transaction.show(fragment).commit();
        mTvTitle.setText(titleIds[position]); //设置标题
    }

    /**
     * 未选中的隐藏掉视图
     *
     * @param position
     */
    @Override
    public void onTabUnselected(int position) {
        getSupportFragmentManager().beginTransaction().hide(FragmentFactory.getFragment(position)).commit();
    }

    /**
     * 又选择了
     * @param position
     */
    @Override
    public void onTabReselected(int position) {

    }



}
