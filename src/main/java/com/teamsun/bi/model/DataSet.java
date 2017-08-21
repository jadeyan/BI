package com.teamsun.bi.model;

import java.util.Vector;

public class DataSet {
	public enum DataType {
		DataTypeColumn, DataTypeLine, DataTypeArea
	};

	private DataType type;
	private Float[] value;
	private Vector<Float> vars;

	private String seriesName;
	
	public void clear(){
		if(value != null && value.length>0){
			for(int i=0; i<value.length; i++){
				value[i] = null;
			}
			value = null;
		}
		if(vars != null && vars.size()>0){
			vars.clear();
			vars = null;
		}
	}

	/**
	 * @return the seriesName
	 */
	public String getSeriesName() {
		return seriesName;
	}

	/**
	 * @param seriesName
	 *            the seriesName to set
	 */
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	/**
	 * @return the value
	 */
	public Float[] getValue() {
		if(value == null || value.length == 0
				&& vars.size()>0){
			value = new Float[vars.size()];
			for(int i=0; i<vars.size(); i++)
				value[i] = (Float)vars.elementAt(i);
		}
		return value;
	}
	
	public void appendValue(Float v){
		vars.add(v);
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Float[] v) {
		value = v;
	}

	public DataSet() {
		this(DataType.DataTypeColumn);
		seriesName = "";
	}

	public DataSet(DataType datatypecolumn) {
		this.type = datatypecolumn;
		 vars=new Vector();
	}

	/**
	 * @return the type
	 */
	public DataType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(DataType type) {
		this.type = type;
	}
}
