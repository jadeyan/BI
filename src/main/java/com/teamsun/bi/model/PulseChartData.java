package com.teamsun.bi.model;

import java.util.ArrayList;
import java.util.Vector;

public class PulseChartData {

	public float lowerLimit, upperLimit, varDistance, curVar;
	
	public String lowerDisp, upperDisp;
	
	public Vector<SingleSection> data;
	
	public String capTitle, metricId, unit, dateTime, orgName, orgCd;
	
	public ArrayList<String> valueList;
	
	private float lastVar;
	
	public PulseChartData(String title, String metricId, String unit, 
			String dateTime, String orgName,String orgCd){
		data = new Vector<SingleSection>();
		valueList = new ArrayList<String>(); 
		this.capTitle = title;
		this.metricId = metricId;
		this.unit = unit;
		this.dateTime = dateTime;
		this.orgName = orgName;
		this.orgCd = orgCd;
	}

	public void setMaxValue() {
		upperLimit = lastVar;
	}

	public void addValue(String var, String color){
		valueList.add(var);
		float v = Float.parseFloat(var);
		if(valueList.size() == 1){
			lastVar = v;
			lowerLimit = v;
		}
		else {
			if(color.startsWith("#"))
				color = color.substring(1);
			int c = Integer.parseInt(color, 16);
			addData(new SingleSection(lastVar, v, c));
			lastVar = v;
		}
	}
	
	private void addData(SingleSection se){
		data.addElement(se);
	}
	
	public Vector<SingleSection> getData(){
		return data;
	}
	
	public class SingleSection{
		public float lowVar, highVar;
		public int color = -1;
		public SingleSection(float lowVar, float highVar, int color){
			this.lowVar = lowVar;
			this.highVar = highVar;
			this.color = color;
		}
	}
	
	public void clear(){
		if(data != null){
			data.clear();
			data = null;
		}
		if(valueList != null){
			valueList.clear();
			valueList = null;
		}
	}

//	public void deal() {
//		capTitle = "新增用户数";
//		lowerLimit = 0;
//		upperLimit = 100;
//		varDistance = 10;
//		curVar = 70;
//		
////		SingleSection se = new SingleSection(0, 30);
////		addData(se);
////		se = new SingleSection(30, 85);
////		addData(se);
////		se = new SingleSection(85, 100);
////		addData(se);
//	}
	
}
