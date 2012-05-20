package com.t3hh4xx0r.cleanertwit;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_TOKEN = "oauth_token";
    public static final String KEY_SECRET = "oauth_token_secret";
    
    private static final String DATABASE_NAME = "cleanertwit.db";
    private static final String USER_TABLE = "users";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_USERS =
            "create table users (_id integer primary key autoincrement, "
            + "username text not null, user_id text not null, oauth_token text not null, oauth_token_secret text not null);";
                   
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(CREATE_USERS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }
    }    
    
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
    	DBHelper.close();
    }

    public long insertUser(String name, String id, String token, String secret) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, name);
        initialValues.put(KEY_USERID, id);
        initialValues.put(KEY_TOKEN, token);
        initialValues.put(KEY_SECRET, secret);
        
        return db.insert(USER_TABLE, null, initialValues);
    }    
    
    
    public Cursor getAllUsers() 
    {
    	Cursor mCursor = db.query(USER_TABLE, new String[] {
        		KEY_ROWID, 
                KEY_USERNAME,
                KEY_USERID,
                KEY_TOKEN,
                KEY_SECRET}, 
                null, 
                null, 
                null, 
                null, 
                null);
	
		return mCursor;
    }
    
   public boolean deleteUser(String[] user) {
    	
        Cursor mCursor = db.query(true, USER_TABLE, new String[] {
        		KEY_ROWID
        		}, 
        		"username=?", 
        		user,
        		null, 
        		null, 
        		null, 
        		null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
                
        return db.delete(USER_TABLE, KEY_ROWID + 
        		"=" + mCursor.getString(0), null) > 0;  
    }	
   
	
	public boolean isLoggedIn() {
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM USERS", null);
		if (cur != null) {
		    cur.moveToFirst();
		    if (cur.getInt (0) == 0) {
		    	cur.close();
		    	return false;
		    } else{
		    	cur.close();
		    	return true;
		    }
		} else {
			return false;
		}			
	}
}