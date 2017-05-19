package com.sizu.mingteng.im_demo02;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;
import com.sizu.mingteng.im_demo02.adapter.MessageListenerAdapter;
import com.sizu.mingteng.im_demo02.db.DBUtils;
import com.sizu.mingteng.im_demo02.event.OnContactUpdateEvent;
import com.sizu.mingteng.im_demo02.utils.ThreadUtils;
import com.sizu.mingteng.im_demo02.utils.ToastUtils;
import com.sizu.mingteng.im_demo02.view.BaseActivity;
import com.sizu.mingteng.im_demo02.view.ChatActivity;
import com.sizu.mingteng.im_demo02.view.LoginActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by lenovo on 2017/5/10.
 */

public class App extends Application {
    public static Context context;
    private List<BaseActivity> mBaseActivityList = new ArrayList<>();
    private SoundPool mSoundPool;
    private int mYuluSound;
    private int mDuanSound;
    /**
     * 默认情况下只有当应用第一次确定的时候调用第一次
     * 如果这个应用里面有个service 可以放到独立的进程里面 就会调用多次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initHuanXin();
        initDB();
        initSoundPool();
        context = this;
    }

    /**
     * 正式使用 EaseUI 需要先调用初始化方法，在 Application 的 oncreate 里调用初始化。
     */
    private void initEaseUI() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, options);
    }


    private void initHuanXin() {
        EMOptions options = new EMOptions();
        /**
         *注：如果你的 APP 中有第三方的服务启动，
         * 请在初始化 SDK（EMClient.getInstance().init(applicationContext, options)）
         * 方法的前面添加以下相关代码（相应代码也可参考 Demo 的 application），使用 EaseUI 库的就不用理会这个。
         */
        initCode();

// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);  //false 不需要严重就可以加好友 true就是需要认证
//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);  //关闭的时候改为false


        initEaseUI();
        //添加通讯录监听
        initContactListener();
        //添加消息的监听
        initMessageListener();
        //监听连接状态的改变
        initConnectionListener();
    }



    /**
     * 为了避免环信初始化2次
     * 注：如果你的 APP 中有第三方的服务启动，
     * 请在初始化 SDK（EMClient.getInstance().init(applicationContext, options)）
     * 方法的前面添加以下相关代码（相应代码也可参考 Demo 的 application），使用 EaseUI 库的就不用理会这个。
     */
    private void initCode() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
    /**
     * 添加消息的监听
     */
    private void initMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new MessageListenerAdapter() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                super.onMessageReceived(list);
                if (list != null && list.size() > 0) {
                    /**
                     * 1. 判断当前应用是否在后台运行
                     * 2. 如果是在后台运行，则发出通知栏
                     * 3. 如果是在后台发出长声音
                     * 4. 如果在前台发出短声音
                     */
                    if (isRuninBackground()) {
                        sendNotification(list.get(0));
                        //如果在后台发出长声音  用音乐池 播发短声音
                        // 参数1:音乐ID
                        //参数2/3：左右喇叭声音的大小   4:优先级(0最低) 5:0表示不循环 -1死循环  6: 1 带表正常速度播发
                        mSoundPool.play(mYuluSound,1,1,0,0,1);
                    } else {
                        //如果是前台 发出短声音
                        mSoundPool.play(mDuanSound,1,1,0,0,1);
                    }
                    EventBus.getDefault().post(list.get(0));
                }
            }
        });
    }
    /**
     *  初始化音乐池  播发喇叭的  initMessageListener() 方法里面用
     */
    private void initSoundPool() {
        /**
         * 参数1:加载音乐流第多少个 (只用了俩个音乐) 2:设置音乐的质量 音乐流 3:资源的质量  0
         */
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        mDuanSound = mSoundPool.load(this, R.raw.duan, 1); //预先加载资源  参数3:优先级
        mYuluSound = mSoundPool.load(this, R.raw.yulu, 1);
    }
    /**
     * 判断当前应用是否是在前台进程
     * @return
     */
    private boolean isRuninBackground() {

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);//获取第一个任务栈
        if (runningTaskInfo.topActivity.getPackageName().equals(getPackageName())) { //获取APP的包名和 到期 应用的包名是否一致
            return false;
        } else {
            return true;
        }
    }


    /**
     * 弹出 通知栏
     * @param message 最近一条的消息
     */
    private void sendNotification(EMMessage message) {
        EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody(); //获取消息体

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //获取消息管理器

        Intent mainIntent = new Intent(this,MainActivity.class);  //先启动 mainactivity
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       // 在application 无法开启activity 如果开启需要加 addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chatIntent = new Intent(this, ChatActivity.class);  // 在启动 消息界面
        chatIntent.putExtra("username",message.getFrom());       //在任务栈里面开启不需要 addFlags

        Intent[] intents = {mainIntent,chatIntent}; //意图里面放多个意图
        //延时意图
        /**
         * 参数2：请求码 大于1  3:intent[] 意图数组  4:必须加个标记 (PendingIntent.FLAG_UPDATE_CURRENT)更新通知消息
         */
        PendingIntent pendingIntent = PendingIntent.getActivities(this,1,intents,PendingIntent.FLAG_UPDATE_CURRENT) ;
        /**
         * 消息通知 应用在后台的时候 消息显示在 状态栏
         */
        Notification notification = new Notification.Builder(this)  //创建消息通知
                .setAutoCancel(true)                //是否当点击后自动删除
                .setSmallIcon(R.mipmap.message)     //必须设置  设置小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar)) //设置大图标
                .setContentTitle("您有一条新消息")     //设置标题
                .setContentText(messageBody.getMessage()) //消息的内容
                .setContentInfo(message.getFrom())  // 设置来自谁发送的消息
                .setContentIntent(pendingIntent)   // 设置延时意图  当点击的时候 在跳转 activity
                .setPriority(Notification.PRIORITY_MAX)  //优先级  设置最高
                .build();
        notificationManager.notify(1,notification); //发送通知
    }


    /**
     * 通讯录监听
     */
    private void initContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String s) {
                //好友请求被同意
                //发出通知让ContactFragment更新UI
                EventBus.getDefault().post(new OnContactUpdateEvent(s, true));
            }

            @Override
            public void onContactDeleted(String s) {
                //被删除时回调此方法
                EventBus.getDefault().post(new OnContactUpdateEvent(s, false));
                Log.d(TAG, "onContactDeleted: " + s);
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.d(TAG, "onContactInvited: " + username + "/" + reason);
                //同意或者拒绝
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                //接收好友请求
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                //好友请求被拒绝
            }

            /*@Override
            public void onContactAgreed(String s) {
                //增加了联系人时回调此方法
            }

            @Override
            public void onContactRefused(String s) {
                //好友请求被拒绝
            }*/
        });
    }

    /**
     * 初始化数据库 上下文
     */
    private void initDB() {
        DBUtils.initDB(this);
    }

    /**
     * 存储 activity
     *
     * @param activity
     */
    public void addActivity(BaseActivity activity) {
        if (!mBaseActivityList.contains(activity)) { //如果集合中不存在就添加进集合
            mBaseActivityList.add(activity);
        }
    }

    /**
     * 移除activity
     *
     * @param activity
     */
    public void removeActivity(BaseActivity activity) {
        mBaseActivityList.remove(activity);
    }


    /**
     * 在聊天过程中难免会遇到网络问题，在此 SDK 为您提供了网络监听接口，实时监听
     * 可以根据 disconnect 返回的 error 判断原因。
     * 若服务器返回的参数值为EMError.USER_LOGIN_ANOTHER_DEVICE，
     * 则认为是有同一个账号异地登录；
     * 若服务器返回的参数值为EMError.USER_REMOVED，则是账号在后台被删除
     */
    private void initConnectionListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    /**
                     *  将当前任务栈中所有的Activity给清空掉
                     *  重新打开登录界面
                     */
                    for (BaseActivity baseActivity : mBaseActivityList) {
                        baseActivity.finish();
                    }

                    Intent intent = new Intent(App.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(App.this, "您已在其他设备上登录了，请重新登录。");
                        }
                    });

                } else if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                } else {
                    /*if (NetUtils.hasNetwork(MainActivity.this))
                            //连接不到聊天服务器
                        ToastUtils.showToast(App.this,"连接不到聊天服务器");
                    else
                            //当前网络不可用，请检查网络设置
                        ToastUtils.showToast(App.this,"当前网络不可用，请检查网络设置");
                    */
                }
            }
        });
    }
}
