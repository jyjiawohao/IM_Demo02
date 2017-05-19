package com.sizu.mingteng.im_demo02.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/5/14.
 */

public class DBUtils {
    private static Context sContext;
    private static boolean isInit;
    public  static  void  initDB(Context context){
        sContext=context;
        isInit=true;
    }

    /**
     * 获取数据
     * @param userName
     * @return
     */
    public static List<String> getContacts(String userName){
        if (!isInit){
            throw new RuntimeException("使用DBUtils之前请 现在Application中初始化！");
        }
        ContactSQLiteOpenHelper openHeper=new ContactSQLiteOpenHelper(sContext);
        SQLiteDatabase db = openHeper.getReadableDatabase();
        Cursor cursor = db.query(ContactSQLiteOpenHelper.T_CONTACT
                , new String[]{ContactSQLiteOpenHelper.CONTACT}
                , ContactSQLiteOpenHelper.USERNAME + "=?"
                , new String[]{userName}
                , null, null, ContactSQLiteOpenHelper.CONTACT);
       List<String> contactsList= new ArrayList<>();
        while (cursor.moveToNext()){ //移动游标
            String contact = cursor.getString(0);
            contactsList.add(contact);
        }
        /**
         * 一定要记得关闭读取流 不然会内存泄漏
         */
        cursor.close();
        db.close();
        return contactsList;
    }

    /**
     *  更新数据库
     * @param userName
     * @param contactsList
     *
     *  1. 先删除username的所有的联系人
     * 2. 再添加contactsList添加进去
    *
     */
    public static void updateContacts(String userName,List<String> contactsList){
        ContactSQLiteOpenHelper openHelper = new ContactSQLiteOpenHelper(sContext);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction(); //开启事务
        db.delete(ContactSQLiteOpenHelper.T_CONTACT
                ,ContactSQLiteOpenHelper.USERNAME+"=?"
                ,new String[]{userName});

        ContentValues values = new ContentValues();//比map更 高效的 集合
        values.put(ContactSQLiteOpenHelper.USERNAME,userName);
        for (int i = 0; i < contactsList.size(); i++) {
            String contact = contactsList.get(i);
            values.put(ContactSQLiteOpenHelper.CONTACT,contact);
            db.insert(ContactSQLiteOpenHelper.T_CONTACT,null,values); //插入
        }
        db.setTransactionSuccessful(); //提交事务
        db.endTransaction(); //关闭事务
        db.close(); //关闭流
    }
}
