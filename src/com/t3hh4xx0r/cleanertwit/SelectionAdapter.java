package com.t3hh4xx0r.cleanertwit;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectionAdapter extends BaseAdapter {
	ArrayList<String> selectionList;
    static ArrayList<String> selections = new ArrayList<String>();
	
	 private LayoutInflater mInflater;
	 Context ctx;	 

	 public SelectionAdapter(Context context, ArrayList<String> list) {
	  selectionList = list;
	  mInflater = LayoutInflater.from(context);
	  ctx = context;	  
	 }
	 
	 public int getCount() {
	  return selectionList.size();
	 }

	 public Object getItem(int position) {
	  return selectionList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(final int position, View convertView, ViewGroup parent) {
	  final ViewHolder holder;
	  if (convertView == null) {
		  convertView = mInflater.inflate(R.layout.selection_results, null);
		  holder = new ViewHolder();
		  holder.title = (TextView) convertView.findViewById(R.id.text);
		  holder.statusBox = (CheckBox) convertView.findViewById(R.id.status);
		  convertView.setTag(holder);   	   	  
	  } else {
		  holder = (ViewHolder) convertView.getTag();
	  }
	  holder.title.setText(selectionList.get(position));
	  return convertView;
	 }

	 static class ViewHolder {
	  TextView title;
	  CheckBox statusBox;
	 }
	}