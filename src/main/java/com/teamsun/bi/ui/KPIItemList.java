package com.teamsun.bi.ui;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.teamsun.bi.demo.R;
import com.teamsun.bi.model.KPIData;
import com.teamsun.bi.model.KPIDataProperty;

public class KPIItemList {

	ArrayList<KPIData> data;
	MyAdapter adapter;

	private LinearLayout mainLayout;

	private ListView itemList;
	
	private UiControl ui;
	
	Hashtable<String, Integer> kpiMaps;
	public KPIItemList(Context context, final ArrayList<KPIData> data, final UiControl ui){
		this.ui = ui;
		mainLayout = new LinearLayout(context);
		mainLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		mainLayout.setBackgroundColor(0xEEF0F0);
		itemList = new ListView(context);
		adapter = new MyAdapter(context, data);
		if(data != null)
			itemList.setAdapter(adapter);
		mainLayout.addView(itemList);
		
		kpiMaps = new Hashtable<String, Integer>();
	}
	
	public ViewGroup getView(){
		return mainLayout;
	}
	
	public void setData(ArrayList<KPIData> data){
		this.data = data;
		if(adapter != null){
			adapter.clear();
			adapter.setData(data);
			adapter.notifyDataSetChanged();
		}
		ListAdapter adp = itemList.getAdapter();
		if(adp == null){
			itemList.setAdapter(adapter);
		}
	}

	public class MyAdapter extends BaseAdapter {

		ArrayList<KPIData> data;
		private LayoutInflater mInflater;
		Context context;
		Bitmap huan_dec;
		Bitmap tong_dec;
		Bitmap huan_inc;
		Bitmap tong_inc;
		Bitmap decline;
		Bitmap grow;
		Bitmap valueBg;

		public MyAdapter(Context context, ArrayList<KPIData> data) {
			super();
			this.context = context;
			this.data = data;
			mInflater = LayoutInflater.from(context);
			huan_dec = BitmapFactory.decodeResource(context.getResources(), R.drawable.huan_dec);
			huan_inc = BitmapFactory.decodeResource(context.getResources(), R.drawable.huan_inc);
			tong_dec = BitmapFactory.decodeResource(context.getResources(), R.drawable.tong_dec);
			tong_inc = BitmapFactory.decodeResource(context.getResources(), R.drawable.tong_inc);
			decline = BitmapFactory.decodeResource(context.getResources(), R.drawable.item_decline);
			grow = BitmapFactory.decodeResource(context.getResources(), R.drawable.item_grow);
			valueBg = BitmapFactory.decodeResource(context.getResources(), R.drawable.value_bg);
		}
		
		public void clear() {
			
		}

		public void setData(ArrayList<KPIData> data){
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				LinearLayout l = (LinearLayout)convertView;
				holder.name = (TextView)l.findViewById(R.id.item_name);
				holder.value = (TextView)l.findViewById(R.id.item_value);
				holder.icon = (ImageValueView)l.findViewById(R.id.item_button );
				holder.img = (ImageButton)l.findViewById(R.id.img);
				holder.icon.setTag(new Integer(0));
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView.setBackgroundResource(R.drawable.item_background);
			convertView.setClickable(true);
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					KPIData kpi = data.get(position);
					ui.requestChartPage(kpi);
				}});
			final KPIData kpi = data.get(position);
			KPIDataProperty pro = kpi.getProperty();
			if(pro !=null)
				holder.name.setText(pro.metricName+"("+pro.unit+")");
			holder.icon.setTextColor(Color.WHITE);
			updateIcon(holder, kpi);
			holder.icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					int i = 0;
					if(kpiMaps != null && kpiMaps.containsKey(kpi.metricId)){
						i = kpiMaps.get(kpi.metricId);
					}
					i = (i+1)%2;
					kpiMaps.put(kpi.metricId, new Integer(i));
//					Log.e("itemList--put", "id:"+kpi.metricId + "   tag:"+i);
					updateIcon(holder, kpi);
				}});
			return convertView;
		}

		private void updateIcon(ViewHolder holder, final KPIData kpi) {
			String var;
//			Log.e("itemList--holder", "holder:"+holder.toString());
			int tag = 0;
			if(kpiMaps != null && kpiMaps.containsKey(kpi.metricId)){
				tag = kpiMaps.get(kpi.metricId);
//				Log.e("itemList--get", "id:"+kpi.metricId + "   tag:"+tag);
			}
//			Log.e("itemList", "id:"+kpi.metricId + "   tag:"+tag);
			if (tag == 0) {
				var = kpi.ringValue;
				if (var != null && var.startsWith("-")) {
					holder.img.setBackgroundDrawable(new BitmapDrawable(
								huan_dec));
				} else
					holder.img.setBackgroundDrawable(new BitmapDrawable(
							huan_inc));
				holder.value.setText("当期值："+kpi.curValue);
			}else{
				var = kpi.likeValue;
				if (var != null && var.startsWith("-")) {
					holder.img.setBackgroundDrawable(new BitmapDrawable(tong_dec));
				}else{
					holder.img.setBackgroundDrawable(new BitmapDrawable(tong_inc));
				}
				holder.value.setText("累计值："+kpi.sumVar);
			}
			if(var == null)
				var = "";
			holder.icon.setText(var);
			if(var.startsWith("-"))
				holder.icon.setBackgroundDrawable(new BitmapDrawable(decline));
			else 
				holder.icon.setBackgroundDrawable(new BitmapDrawable(grow));
			
		}

		class ViewHolder {
			TextView name;
			ImageValueView icon;
			TextView value;
			ImageButton img;
		}
	}
}
