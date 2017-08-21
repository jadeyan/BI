package com.teamsun.bi.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import com.teamsun.bi.model.DataSet.DataType;

public class ChartData {
	private Vector labelX;
	private Vector valueArray;
	private Vector labelY;
	private boolean isShowAvg;
	public String title, metricId, dataTime, unit, orgName, orgCd;
	private String xAxisName,yAxisName;
	
	Random rand;

	private int curDataId = -1;
	public ChartData()
	{
//		test();
		rand=new Random();
	}
	
	public ChartData(String title, String metricId, String dataTime, String unit, 
			String orgName, String orgCd){
		this.title = title;
		this.metricId = metricId;
		this.dataTime = dataTime;
		this.unit = unit;
		this.orgCd = orgCd;
		this.orgName = orgName;
		labelX = new Vector();
		labelY = new Vector();
		valueArray = new Vector();
	}
	
	public void appendLableX(String l){
		if(labelX == null)
			labelX = new Vector();
		labelX.add(l);
	}

	public int getCurDataId() {
		return curDataId;
	}
	
	public void setCurDataId(int curDataId) {
		this.curDataId = curDataId;
	}
	
	public void setCurDataId(String curLabel){
		if(labelX != null && labelX.contains(curLabel))
			curDataId = labelX.indexOf(curLabel);
	}
	
	public void addLineData(String series){
		DataSet dat=new DataSet(DataType.DataTypeLine);
		dat.setSeriesName(series);
		valueArray.add(dat);
	}
	
	public void addSingleData(Float var){
		DataSet ds = (DataSet)valueArray.get(valueArray.size()-1);
		ds.appendValue(var);
	}

	public Vector getDispLabelX(){
		if(labelX!= null && labelX.size()>0){
			Vector v = new Vector();
			for(int i=0; i<labelX.size(); i++){
				String tmp = (String)labelX.elementAt(i);
				SimpleDateFormat sdf = null;
				if(tmp.length() == 6)
					sdf = new SimpleDateFormat("yyyyMM");
				else if(tmp.length() == 8)
					sdf= new SimpleDateFormat("yyyyMMdd");
				if(sdf != null){
					try {
						Date dt2 = sdf.parse(tmp);
						tmp = tmp.substring(tmp.length()-2);
						v.addElement(tmp);
					} catch (ParseException e) {
					}
				}
			}
			if(v.size()>0)
				return v;
		}
		return labelX;
	}

	/**
	 * @param labelX the labelX to set
	 */
	public void setLabelX(Vector labelX) {
		this.labelX = labelX;
	}

	/**
	 * @return the valueArray
	 */
	public Vector getValueArray() {
		return valueArray;
	}

	/**
	 * @param valueArray the valueArray to set
	 */
	public void setValueArray(Vector valueArray) {
		this.valueArray = valueArray;
	}

	/**
	 * @return the labelY
	 */
	public Vector getLabelY() {
		return labelY;
	}

	/**
	 * @param labelY the labelY to set
	 */
	public void setLabelY(Vector labelY) {
		this.labelY = labelY;
	}

	/**
	 * @return the isShowAvg
	 */
	public boolean isShowAvg() {
		return isShowAvg;
	}

	/**
	 * @param isShowAvg the isShowAvg to set
	 */
	public void setShowAvg(boolean isShowAvg) {
		this.isShowAvg = isShowAvg;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		if(unit != null)
			title = title.concat("(").concat(unit).concat(")");
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the xAxisName
	 */
	public String getxAxisName() {
		return xAxisName;
	}

	/**
	 * @param xAxisName the xAxisName to set
	 */
	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	/**
	 * @return the yAxisName
	 */
	public String getyAxisName() {
		return yAxisName;
	}

	/**
	 * @param yAxisName the yAxisName to set
	 */
	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public void addColumnDataSet(String series) {
		DataSet dat=new DataSet(DataType.DataTypeColumn);
		dat.setSeriesName(series);
		valueArray.add(dat);
	}

	public void addData(Float float1) {
		DataSet dat = (DataSet)valueArray.get(valueArray.size()-1);
		dat.appendValue(float1);
	}

	public void clear() {
		if(valueArray != null){
			if(valueArray.size()>0){
				Enumeration em = valueArray.elements();
				while(em.hasMoreElements()){
					DataSet ds = (DataSet)em.nextElement();
					ds.clear();
				}
			}
			valueArray.clear();
			valueArray = null;
		}
		if(labelX != null){
			labelX.clear();
			labelX = null;
		}
		if(labelY != null){
			labelY.clear();
			labelY = null;
		}
	}


}
