package com.ub.techexcel.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.AddFriend;

public class CustomerDao {

	public CustomerOpenhelper helper;
	private Context mContext;

	public CustomerDao(Context context) {
		this.mContext = context;
		helper = new CustomerOpenhelper(context);
	}

	/**
	 * 获得所有消息
	 *
	 * @return
	 */
	public List<AddFriend> queryAll() {
		List<AddFriend> list = new ArrayList<AddFriend>();
		SQLiteDatabase db = helper.getWritableDatabase();
		// Cursor cursor = db
		// .query("customer", null, null, null, null, null, null);
		Cursor cursor = db.rawQuery("select * from customer where userid = ? ",
				new String[] { AppConfig.UserID });
		while (cursor.moveToNext()) {
			AddFriend addfriends = new AddFriend();
			addfriends.setSourceID(cursor.getString(cursor
					.getColumnIndex("sourceid")));
			addfriends.setTargetID(cursor.getString(cursor
					.getColumnIndex("targetid")));
			addfriends.setTime(cursor.getString(cursor.getColumnIndex("time")));
			addfriends.setType(cursor.getString(cursor.getColumnIndex("type")));
			list.add(addfriends);
		}
		cursor.close();
		db.close();
		return list;
	}

	public boolean isExist(AddFriend addFriend) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "select * from customer where sourceid=? and targetid=? and userid=?  ";
		Cursor cursor = db.rawQuery(sql, new String[] {
				addFriend.getSourceID() + "", addFriend.getTargetID() + "",
				AppConfig.UserID + "" });
		Log.e("ISEXIST",
				addFriend.getSourceID() + "   " + addFriend.getTargetID()
						+ "   " + AppConfig.UserID + "");
		if (cursor.moveToNext() == false) {
			Log.e("是否存在", "不存在");
			cursor.close();
			db.close();
			return false;
		} else {
			Log.e("是否存在", "存在");
			cursor.close();
			db.close();
			return true;
		}
	}

	/**
	 * 登录是插入好友信息
	 * 
	 * @param addFriend
	 * @param isMyFriend
	 *            是否是自己的好友
	 */

	public void insert(AddFriend addFriend, boolean isMyFriend) {
		Log.e("addFriend", "sourceid  " + addFriend.getSourceID()
				+ "  targetid " + addFriend.getTargetID());
		if (addFriend.getType().equals("1") || addFriend.getType().equals("3")) {
			if (isExist(addFriend) && !isMyFriend) { // 数据库存在并且不是自己的好友(删除好友)时
				delete1(addFriend); // 删除好友后 重新添加 改变状态
			} else if (!isExist(addFriend)) {
				insertDB(addFriend);
			}
		} else if (addFriend.getType().equals("2")) { // 已添加
			if (!isExist(addFriend)) {
				delete2(addFriend); // 删除请求加好友时插入数据库的数据数据库有记录
				AppConfig.isUpdateCustomer = true; // 刷新好友列表
				AppConfig.isUpdateDialogue = true;
				insertDB(addFriend);
			}
		}

	}

	private void insertDB(AddFriend addFriend) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"insert into customer(userid,sourceid,targetid,time,type) values(?,?,?,?,?)",
				new String[] { AppConfig.UserID, addFriend.getSourceID(),
						addFriend.getTargetID(), addFriend.getTime(),
						addFriend.getType() });
		Log.e("插入成功", "插入成功");
		db.close();
	}

	private void delete2(AddFriend addFriend) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"delete from customer where sourceid=? and targetid=? and userid=? ",
				new String[] { addFriend.getTargetID(),
						addFriend.getSourceID(), AppConfig.UserID });
		Log.e("删除成功", "删除成功");
		db.close();
	}

	private void delete1(AddFriend addFriend) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"delete from customer where sourceid=? and targetid=? and userid=? ",
				new String[] { addFriend.getSourceID(),
						addFriend.getTargetID(), AppConfig.UserID });
		Log.e("删除成功", "删除成功");
		db.close();
	}

	/**
	 * 删除好友
	 * 
	 * @param addFriend
	 */

	public void deleteFriends(String friendsid) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"delete from customer where sourceid=? and type=? and userid=? ",
				new String[] { friendsid, "2", AppConfig.UserID });
		Log.e("删除haoyo成功", "删除haoyo成功");
		db.close();

	}

	public void update(AddFriend addFriend) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"update customer set type=? where sourceid=? and targetid=? and userid=? ",
				new String[] { addFriend.getType() + "",
						addFriend.getSourceID() + "",
						addFriend.getTargetID() + "", AppConfig.UserID });
		AppConfig.isUpdateCustomer = true; // 刷新好友列表
		AppConfig.isUpdateDialogue = true;
		Log.e("修改成功", "修改成功");
		db.close();

	}

	/**
	 * @param id
	 */
	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from customer where userid=? ",
				new String[] { AppConfig.UserID });
		db.close();
	}

}
