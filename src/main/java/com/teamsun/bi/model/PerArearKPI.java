package com.teamsun.bi.model;

import java.util.ArrayList;
import java.util.Hashtable;

public class PerArearKPI {

	ArrayList<KPIData> datas;
	
	private Hashtable<String, KPIData> dataIdMaps;
	
	String orgId;
	
	String orgName;
	
	private String curDate;
	
	public PerArearKPI(String orgName, String orgId){
		datas = new ArrayList<KPIData>();
		this.orgName = orgName;
		this.orgId = orgId;
		dataIdMaps = new Hashtable<String, KPIData>();
	}
	
	public String getOrgName(){
		return orgName;
	}
	
	public String getOrgId(){
		return orgId;
	}

	public String getCurDate() {
		if(curDate == null)
			curDate = KPIDataManager.getInstance().getDateTime();
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}
	
	public void addData(KPIData data){
		datas.add(data);
		dataIdMaps.put(data.metricId, data);
	}
	
	public ArrayList<KPIData> getDatas(){
		return datas;
	}

	public KPIData getData(String metricId) {
		if(metricId != null){
			if(dataIdMaps.containsKey(metricId))
				return dataIdMaps.get(metricId);
		}
		return null;
	}
}
