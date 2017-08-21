package com.teamsun.bi.model;

public class KPIDataProperty {

	public String metricId;
	public String unit;
	public String metricName;
	
	public KPIDataProperty(String metricId, String unit, String metricName){
		this.metricId = metricId;
		this.unit = unit;
		this.metricName = metricName;
	}
}
