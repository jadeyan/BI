package com.teamsun.bi.model;

public class KPIData {

	public String metricId;
	
	public String curValue, ringValue, likeValue, sumVar;
	
	private KPIDataProperty property;
	
//	private PulseChartData pulseChart;
	
//	private ChartData lineChart, columnChart;
	
	public KPIData(String metricId, String curValue, String ringValue, String likeValue, String sumVar){
		this.metricId = metricId;
		this.curValue = curValue;
		this.ringValue = ringValue;
		this.likeValue = likeValue;
		this.sumVar = sumVar;
	}
	
	public void setProperty(KPIDataProperty property){
		this.property = property;
	}
	
	public KPIDataProperty getProperty(){
		return property;
	}
	
	public void clear() {
	}
}
