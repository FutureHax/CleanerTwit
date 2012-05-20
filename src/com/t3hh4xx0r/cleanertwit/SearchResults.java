package com.t3hh4xx0r.cleanertwit;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

public class SearchResults extends ListActivity {

	ListView lv1;
	ArrayList<String> results;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	    setContentView(R.layout.search_results);
	        
	    lv1 = (ListView) findViewById(android.R.id.list);
	    
	    results = getIntent().getStringArrayListExtra("results");
        lv1.setAdapter(new SelectionAdapter(SearchResults.this, results));
	}
}
