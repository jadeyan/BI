package com.teamsun.bi.model;

public interface Chart {
	/**
	 * @return the bgColor
	 */
	public int getBgColor() ;

	/**
	 * @param bgColor the bgColor to set
	 */
	public void setBgColor(int bgColor) ;

	/**
	 * @return the value
	 */
	public int getValue();

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) ;

	
	public void aniShowView();
}
