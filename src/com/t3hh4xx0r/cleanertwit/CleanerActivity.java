package com.t3hh4xx0r.cleanertwit;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import twitter4j.Paging;
import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CleanerActivity extends Activity {
	TextView userTV;
	ImageView userIV;
	String username;
	EditText keywordET;
	Button searchB;
	String keyword;
    RelativeLayout progress;
    TextView tweetText;
    Handler mHandler;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.cleaner);
		
		username = getIntent().getStringExtra("user");
		
        progress = (RelativeLayout) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        tweetText = (TextView) findViewById(R.id.progress_name);
		
		userTV = (TextView) findViewById(R.id.userTV);
		userTV.setText(username);
		
		userIV = (ImageView) findViewById(R.id.userIV);
		userIV.setImageDrawable(setProfilePic(username));
		
		keywordET = (EditText) findViewById(R.id.keywordET);
		searchB = (Button) findViewById(R.id.searchB);
		searchB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyword = keywordET.getText().toString();
		        new CreateArrayListTask().execute();
			}
		});		
		
		mHandler = new Handler();
	}
	
	public Drawable setProfilePic(String name){
		File file = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/cleanertwit/profileimages/"+ name + "_image_small.jpg");
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
	           ProfileImage image = twitter.getProfileImage(name, ProfileImage.MINI);
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
	
    private class CreateArrayListTask extends AsyncTask<String, String, ArrayList<String>> {       
    	ArrayList<String> selectedTweets = new ArrayList<String>();    	
		protected ArrayList<String> doInBackground(String... params) { 
			Twitter t = new TwitterFactory().getInstance();
			int page = 1;
			Paging paging = new Paging (page, 200);
			List<twitter4j.Status> statuses = null;
			try {
				statuses = t.getUserTimeline(username, paging);
				for (int i=0;i<statuses.size();i++) {
					publishProgress(statuses.get(i).getText());
					if (statuses.get(i).getText().contains(keyword)) {
						selectedTweets.add(statuses.get(i).getText());
					}
				}
				do {
					page = page+1;
					paging = new Paging (page, 200);
					statuses = t.getUserTimeline(username, paging);

					for (int i=0;i<statuses.size();i++) {
						publishProgress(statuses.get(i).getText());
						if (statuses.get(i).getText().contains(keyword)) {
							selectedTweets.add(statuses.get(i).getText());
						}
					}
				} while (statuses.size() > 0);
			} catch (TwitterException e) {
				e.printStackTrace();
				if (e.exceededRateLimitation()) {
					final double waitInMins = e.getRateLimitStatus().getSecondsUntilReset()/60.0;
   				 	mHandler.post(new Runnable() {
   				 		@Override
   				 		public void run() {
   							Toast.makeText(CleanerActivity.this, "Rate limit exceeded.\nTry again in "+ Double.toString(waitInMins)+ "minutes.", Toast.LENGTH_LONG).show();
   				 		}
   				 	});
				}
			}

 			return selectedTweets;
		}
		
		protected void onProgressUpdate(String... tweet) {
			if (progress.getVisibility() == View.GONE) {
				progress.setVisibility(View.VISIBLE);
			}
			tweetText.setText("Checking..."+tweet[0]);			
		}
		
        protected void onPostExecute(ArrayList<String> selectedTweets) {
        	progress.setVisibility(View.GONE);
			for (int i=0;i<selectedTweets.size();i++) {
				Log.d("TWEETS", selectedTweets.get(i));
			}
	        Intent i = new Intent(CleanerActivity.this, SearchResults.class);
	        i.putExtra("results", selectedTweets);
	        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
	        startActivity(i);			

        }
    }
}
