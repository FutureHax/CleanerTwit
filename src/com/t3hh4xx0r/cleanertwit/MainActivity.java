package com.t3hh4xx0r.cleanertwit;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.t3hh4xx0r.cleanertwit.twitter.TwitterAuth;

public class MainActivity extends ListActivity {
	ImageView pic;
	TextView name;
	private LayoutInflater mInflater;
	public static Vector<RowData> data;
	RowData rd;
	public static ArrayList<String> names;
	Button mAddEntry;
	public static CustomAdapter adapter;

	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.accounts);
		names = new ArrayList<String>();
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();
		
        DBAdapter db = new DBAdapter(this);
   		db.open();
   		if (!db.isLoggedIn()) {
            startActivity(new Intent(this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
   		}
   		db.close();
   		
		Users.getUsers(this);
		updateData();
		
        mAddEntry = (Button) findViewById(R.id.entry_b);
        mAddEntry.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
		       		Intent si = new Intent(v.getContext(), TwitterAuth.class);
		       		si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		       		startActivityForResult(si, 0);
        	}
        });	
        
		adapter = new CustomAdapter(this, R.layout.row, R.id.name, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
		
		this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final Vibrator vibe = (Vibrator) arg1.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
		    	vibe.vibrate(50);
				
		    	BetterPopupWindow dw = new BetterPopupWindow.DemoPopupWindow(arg1, Users.users[arg2].getName(), arg2);
				dw.showLikeQuickAction(0, 30);
				
				return true;
			}
        });	
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String msg = names.get(arg2);	

		        Intent i = new Intent(arg1.getContext(), CleanerActivity.class);
		        i.putExtra("user", msg);
		        startActivity(i);				
			}
		});
	}

	public void updateData() {
		for(int i=0;i<Users.users.length;i++){
			data.clear();
			rd = new RowData(i,Users.users[i].getName());
			data.add(rd);
			names.add(Users.users[i].getName());
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case 0:
	    	Users.getUsers(this);
            Intent mi = new Intent(this, MainActivity.class);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}
	
	 public class RowData {
	       protected String mTitle;
	       RowData(int id, String title){
	    	   mTitle = title;
	       }
	 }
	 
	  class CustomAdapter extends ArrayAdapter<RowData> {
		  String name = null;

		  public CustomAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {               
			  super(context, resource, textViewResourceId, objects);
		  }
	  
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {   
			  ViewHolder holder = null;
			  TextView title = null;
	   		  ImageView i11=null;
			  RowData rowData= getItem(position);
			  if(null == convertView){
				  convertView = mInflater.inflate(R.layout.row, null);
				  holder = new ViewHolder(convertView);
				  convertView.setTag(holder);
			  }
			  
			  holder = (ViewHolder) convertView.getTag();
			  title = holder.gettitle();
			  title.setText(rowData.mTitle);                                                   
			  name = rowData.mTitle;
			  i11 = holder.getImage();
			  Drawable p;
			  p = setProfilePic(name);	
			  i11.setImageDrawable(p);
		  return convertView;
		  }
  
		  private class ViewHolder {
			  	private View mRow;
			  	private TextView title = null;
			  	private ImageView i11=null; 

			  	public ViewHolder(View row) {
			  		mRow = row;
			  	}
			  	
			  	public TextView gettitle() {
			  		if (null == title){
			  			title = (TextView) mRow.findViewById(R.id.name);
			  		}
			  		return title;
			  	}     

			  	public ImageView getImage() {
			  		if (null == i11){
			  			i11 = (ImageView) mRow.findViewById(R.id.pic);
                    }
			  		return i11;
			  	}
		  }
	  } 
	  
		public Drawable setProfilePic(String name){
			File file = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/cleanertwit/profileimages/"+ name + "_image_big.jpg");
			File dir = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/cleanertwit/profileimages/");
			Resources r = getResources();
			Drawable d;
			if (!file.exists()) {
				if (!dir.exists()) {
					if (dir.mkdirs()) {
						Toast.makeText(this, "Creating "+dir, Toast.LENGTH_LONG).show();
					} else{
						Toast.makeText(this, "Failed to create "+dir, Toast.LENGTH_LONG).show();
					}
				}
				try {
				   Twitter twitter = new TwitterFactory().getInstance();
		           ProfileImage image = twitter.getProfileImage(name, ProfileImage.NORMAL);
		           URL src = new URL(image.getURL());
	
		           Bitmap bm = BitmapFactory.decodeStream(src.openConnection().getInputStream());
		           bm = Bitmap.createScaledBitmap(bm, 300, 300, true); 
		           d = new BitmapDrawable(bm);
		           bm.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
				} catch (Exception e) {
					e.printStackTrace();
					d = r.getDrawable(R.drawable.acct_sel);
				}
			} else {
				Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
				d = new BitmapDrawable(bitmap);
			}
	        return d;
	}
	  
	 public static Handler handy = new Handler() {
			public void handleMessage(Message m) {
				switch (m.what) {
				case 0:			
					adapter.notifyDataSetChanged();
				break;
				}
			}
	};
}
