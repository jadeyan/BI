package com.teamsun.bi.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class KPIDataManager {

	private String capTitle;//系统标题
	private String dateTime;//默认日期
	private String defOrgCd;//默认地域
	private Hashtable<String, KPIDataProperty> dataProperties;
	private Hashtable<String , PerArearKPI> arearKPIs;

	private Vector<String> arearNames;
	
	private static KPIDataManager instance;
	
	public static KPIDataManager getInstance(){
		if(instance == null)
			instance = new KPIDataManager();
		return instance;
	}
	
	private KPIDataManager(){
		arearKPIs = new Hashtable<String, PerArearKPI>();
		dataProperties = new Hashtable<String, KPIDataProperty>();
		arearNames = new Vector<String>();
	}
	
	public void addArearStr(String arear){
		arearNames.add(arear);
	}
	
	public String[] getArearStrs(){
		String[] str = new String[arearNames.size()];
		for(int i=0; i<arearNames.size(); i++){
			str[i] = arearNames.elementAt(i);
		}
		return str;
	}
	
	public String getCapTitle() {
		return capTitle;
	}
	
	public KPIDataProperty getProperty(String id){
		if(id!=null && dataProperties.containsKey(id)){
			return dataProperties.get(id);
		}
		return null;
	}
	
	public void addProperty(KPIDataProperty property){
		if(property != null && property.metricId!=null)
			dataProperties.put(property.metricId, property);
	}
	
	public PerArearKPI getPerArear(String id){
		if(id != null && arearKPIs != null && arearKPIs.containsKey(id)){
			return arearKPIs.get(id);
		}
		return null;
	}
	
	public PerArearKPI getPerArearByStr(String arear){
		if(arear != null){
			Enumeration em = arearKPIs.elements();
			while(em.hasMoreElements()){
				PerArearKPI p = (PerArearKPI)em.nextElement();
				if(p.orgName.equals(arear)){
					return p;
				}
			}
		}
		return null;
	}
	
	public void addArear(PerArearKPI arear){
		if(arear != null && arear.orgId != null)
			arearKPIs.put(arear.orgId, arear);
	}
	
	public void setCapTitle(String capTitle) {
		this.capTitle = capTitle;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getDefOrgCd() {
		return defOrgCd;
	}

	public void setDefOrgCd(String defOrgCd) {
		this.defOrgCd = defOrgCd;
	}

	public void clearProperty() {
		dataProperties.clear();
	}

	public PerArearKPI getArearByIndex(int index) {
		Enumeration em = arearKPIs.keys();
		int n=0;
		String key = null;
		while(em.hasMoreElements()){
			String k = (String)em.nextElement();
			if(n == index){
				key = k;
				break;
			}
			n++;
		}
		if(key != null)
			return arearKPIs.get(key);
		else return null;
	}

	public void clear() {
		this.arearKPIs.clear();
		this.arearKPIs = null;
		this.arearNames.clear();
		this.arearNames = null;
		this.dataProperties.clear();
		this.dataProperties = null;
		instance = null;
	}
}
