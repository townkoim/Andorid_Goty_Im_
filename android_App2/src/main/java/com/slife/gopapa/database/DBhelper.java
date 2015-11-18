package com.slife.gopapa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @ClassName: DBhelper
 * @Description: 数据库助手类
 * @author 菲尔普斯
 * @date 2014-11-19 下午2:51:52
 * 
 */
public class DBhelper extends SQLiteOpenHelper {
	private SQLiteDatabase db;

	public DBhelper(Context context) {
		super(context, DBConstants.DBNAME, null, DBConstants.DB_VERSION);
		db = super.getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = DBConstants.CREATE_TABLE_RECENT_CONTACT;
		db.execSQL(sql);
		db.execSQL(DBConstants.CREATE_TABLE_ABOUT_MATCH);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			onCreate(db);
		}
	}

	public void excuteSQL(String sql, Object... bindArgs) {
		db.execSQL(sql, bindArgs);
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		long rows = 0;
		rows = db.insert(table, nullColumnHack, values);
		return rows;
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	public Cursor query(String sql, String... selectionArgs) {
		return db.rawQuery(sql, selectionArgs);

	}

	public void destory() {
		if (db != null) {
			db.close();
		}
	}

}
