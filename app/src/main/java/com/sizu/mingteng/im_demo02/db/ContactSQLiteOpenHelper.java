package com.sizu.mingteng.im_demo02.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2017/5/14.
 */

public class ContactSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "contacts.db";
    private static final int VERSION=1;
    public static final String T_CONTACT = "t_contact";
    public static final String USERNAME = "username";
    public static final String CONTACT = "contact"; //排序

    /**
     * 构造函数 可以私有掉
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    private ContactSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ContactSQLiteOpenHelper(Context context){
        super(context,DB_NAME,null,VERSION);

    }

    //初始化表结构
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + T_CONTACT + "(_id integer primary key," + USERNAME + " varchar(20)," + CONTACT + " varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
