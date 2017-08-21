package com.teamsun.bi.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.teamsun.bi.demo.R;
import com.teamsun.bi.model.ChartData;
import com.teamsun.bi.model.PulseChartData;

public class KPIChartPage {

	Context context;
	
	ScrollView backSv;
	
	LinearLayout wholeLayout;
	
	LinearLayout perChartBackLayout;
	
	public String curMetricId; 
	
	public KPIChartPage(Context context){
		this.context = context;
		backSv = new ScrollView(context);
		backSv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.FILL_PARENT));
		wholeLayout = new LinearLayout(context);
		wholeLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.FILL_PARENT));
		wholeLayout.setOrientation(LinearLayout.VERTICAL);
		wholeLayout.setBackgroundResource(R.drawable.bg);
		backSv.addView(wholeLayout);
	}
	
//	public void addPulseView(){
//		perChartBackLayout = initChartBackground();
//		PulseChartView v = new PulseChartView(context);
//		perChartBackLayout.addView(v);
//		wholeLayout.addView(perChartBackLayout);
//	}
	
	public void addPulseView(PulseChartData chart){
		perChartBackLayout = initChartBackground();
		PulseChartView v = new PulseChartView(context);
		if(chart != null){
			v.setData(chart);
			this.curMetricId = chart.metricId;
		}
		perChartBackLayout.addView(v);
		wholeLayout.addView(perChartBackLayout);
	}
	
	public void addErrorPulseView(String mess){
		perChartBackLayout = initChartBackground();
		PulseChartView v = new PulseChartView(context);
		v.setErrorInfo(mess);
		perChartBackLayout.addView(v);
		wholeLayout.addView(perChartBackLayout);
	}

	public void addErrorChartView(String s) {
		SimpleColumn2DView chart = new SimpleColumn2DView(context, null); 
		chart.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT));
		chart.setMess(s);
		perChartBackLayout = initChartBackground();
		perChartBackLayout.addView(chart);
		wholeLayout.addView(perChartBackLayout);
	}
	
	
	public void addChartView(ChartData data){
		SimpleColumn2DView chart = new SimpleColumn2DView(context, null); 
		chart.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT));
		if(data != null){
			chart.setChartData(data);
			this.curMetricId = data.metricId;
			if(data.getValueArray() != null && data.getValueArray().size() == 1)
				chart.setShowPatten(false);
		}
		perChartBackLayout = initChartBackground();
		perChartBackLayout.addView(chart);
		wholeLayout.addView(perChartBackLayout);
	}
	
	private LinearLayout initChartBackground(){
		LinearLayout linear = new LinearLayout(context);
		linear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		linear.setPadding(20, 40, 20, 40);
		return linear;
	}
	
	public ViewGroup getView(){
		return backSv;
	}

	public void clear() {
		int c = wholeLayout.getChildCount();
		for(int i=0; i<c; i++){
			View v = wholeLayout.getChildAt(i);
			if(v instanceof LinearLayout){
				LinearLayout l = (LinearLayout)v;
				if(l.getChildCount()>0){
					for(int j=0; j<l.getChildCount(); j++){
						v = l.getChildAt(j);
						if(v instanceof SimpleColumn2DView){
							SimpleColumn2DView simple = (SimpleColumn2DView)v;
							simple.clear();
						}else if(v instanceof PulseChartView){
							PulseChartView pulse = (PulseChartView)v;
							pulse.clear();
						}
					}
				}
			}
		}
		wholeLayout.removeAllViews();
		backSv.removeAllViews();
		backSv = null;
		wholeLayout = null;
	}
}
