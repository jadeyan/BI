package com.teamsun.bi.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.teamsun.bi.demo.R;

public class ArearGrid {

	GridView g ;
	MainWindow window;
	public ArearGrid(Context context, String[] data, MainWindow window) {
		this.window = window;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		g = (GridView)inflater.inflate(R.layout.grid_2, null);
		g.setAdapter(new MyAdapter(context, data));
	}
	
	public GridView getView(){
		return g;
	}

	public class MyAdapter extends BaseAdapter {
		public MyAdapter(Context c, String[] mThumbIds) {
			mContext = c;
			this.mThumbIds = mThumbIds;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			Button b;
			if (convertView == null) {

				b = new Button(mContext);
				b.setLayoutParams(new GridView.LayoutParams(80, LayoutParams.WRAP_CONTENT));
			} else {
				b = (Button) convertView;
			}

			b.setText(mThumbIds[position]);
			b.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					window.closePopupWindow(mThumbIds[position]);
				}});
			return b;
		}

		private Context mContext;

		final private String[] mThumbIds;
	}

}
