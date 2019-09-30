package com.ub.techexcel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomerOpenhelper extends SQLiteOpenHelper {

	public CustomerOpenhelper(Context context) {
		super(context, "mycustomers2.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * private String sourceID;// 消息属性，可随意定义 private String targetID;
		 * private String time; private String type; private String userID;
		 * 
		 * userid  不同账号的flag
		 */
		String sql = "create table if not exists  customer(_id integer primary key autoincrement,"
				+ "userid varchar(36),sourceid varchar(36),targetid varchar(36),time varchar(36), type varchar(36))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
