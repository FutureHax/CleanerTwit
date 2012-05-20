package com.t3hh4xx0r.cleanertwit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

public class Users {

    static User[] users;
    public static int user;
	public static SharedPreferences prefs;
    
	public static void getUsers(Context ctx) {
        DBAdapter db = new DBAdapter(ctx);
   		db.open();
   		Cursor c = db.getAllUsers();
   		user = c.getCount();
   		users = new User[c.getCount()];
   		int i=0;
   		try {
   			while (c.moveToNext()) {
   				i++;
   	       		users[i-1] = new User();
	        	users[i-1].setId(c.getString(c.getColumnIndex("user_id")));	
	        	users[i-1].setName(c.getString(c.getColumnIndex("username")));	
	        	users[i-1].setToken(c.getString(c.getColumnIndex("oauth_token")));	
	        	users[i-1].setSecret(c.getString(c.getColumnIndex("oauth_token_secret")));	
   			}
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		c.close();
   		db.close();
}	
}
