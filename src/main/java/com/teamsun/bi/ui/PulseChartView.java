package com.teamsun.bi.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.teamsun.bi.model.PulseChartData;

public class PulseChartView extends View{

	MyPulseDrawable drawable;
	int w, h;
	
	private String errorInfo;
	
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
		drawable.errorInfo = errorInfo;
	}
	public PulseChartView(Context context){
		super(context);
		init(context);
	}
	public PulseChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void clear(){
		if(drawable != null){
			drawable.clear();
		}
	}
	
	private void init(Context context){
		drawable = new MyPulseDrawable(context);
	}
	
	public void setData(PulseChartData data){
		drawable.setChartData(data);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		h = measureHeight(heightMeasureSpec);

		w = measureWidth(widthMeasureSpec);
		
		setMeasuredDimension(w, h);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.w = w;
		this.h = h;
	}
	
	@Override 
	protected void onDraw(Canvas canvas) {
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
	}

	
	private int measureHeight(int measureSpec) {
		
		int specSize = MeasureSpec.getSize(measureSpec);
		
		int specMode = MeasureSpec.getMode(measureSpec);
		
		int ret = drawable.getBackImgSize()[1];
		
		if(specMode == MeasureSpec.AT_MOST){
			
			ret = Math.min(ret, specSize);
			
		}else if(specMode == MeasureSpec.EXACTLY){
			
			ret = specSize;
			
		}else if(specMode == MeasureSpec.UNSPECIFIED){
			
		}

		return ret;

	}

	private int measureWidth(int measureSpec) {

		int specSize = MeasureSpec.getSize(measureSpec);
		
		int specMode = MeasureSpec.getMode(measureSpec);
		
		int ret = drawable.getBackImgSize()[0];
		
		if(specMode == MeasureSpec.AT_MOST){
			
			ret = Math.min(ret, specSize);
			
		}else if(specMode == MeasureSpec.EXACTLY){
			
			ret = specSize;
			
		}else if(specMode == MeasureSpec.UNSPECIFIED){
			
		}

		return ret;

	}
}
