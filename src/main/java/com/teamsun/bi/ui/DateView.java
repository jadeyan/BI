package com.teamsun.bi.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.demo.R;
import com.teamsun.bi.entry.Common;
import com.teamsun.bi.model.KPIDataManager;

public class DateView extends View {

	Bitmap month_def, month_focus;
	
	Bitmap day_def, day_focus;
	
	public static final int MODEL_MONTH = 0;
	
	public static final int MODEL_DAY = 1;
	
	private int model;
	
	int width, height, defWidth, defHeight;
	public DateView(Context context) {
		super(context);
		init();
	}

	public DateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setModel(int model){
		this.model = model;
	}
	
	private void init(){
		month_def = BitmapFactory.decodeResource(getResources(), R.drawable.month_def);
		day_def = BitmapFactory.decodeResource(getResources(), R.drawable.day_def);
		month_focus = BitmapFactory.decodeResource(getResources(), R.drawable.month_focus);
		day_focus = BitmapFactory.decodeResource(getResources(), R.drawable.day_focus);
		model = 0;
		defWidth = month_def.getWidth()+day_def.getWidth();
		defHeight = month_def.getHeight();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		height = measureHeight(heightMeasureSpec);

		width = measureWidth(widthMeasureSpec);
		
		setMeasuredDimension(width, height);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(width != defWidth || height != defHeight){
			if(width/height >= defWidth/defHeight){
				month_def = Common.scale(month_def, defWidth*height/defHeight, height);
				month_focus = Common.scale(month_focus, defWidth*height/defHeight, height);
				day_def = Common.scale(day_def, defWidth*height/defHeight, height);
				day_focus = Common.scale(day_focus, defWidth*height/defHeight, height);
			}else{
				month_def = Common.scale(month_def, width, width*defHeight/defWidth);
				month_focus = Common.scale(month_focus, width, width*defHeight/defWidth);
				day_def = Common.scale(day_def, width, width*defHeight/defWidth);
				day_focus = Common.scale(day_focus, width, width*defHeight/defWidth);
			}
		}
	}
	
	@Override 
	protected void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setStyle(Style.FILL_AND_STROKE);
		if(model == MODEL_MONTH){
			canvas.drawBitmap(month_focus, 0, 0, p);
			canvas.drawBitmap(day_def, month_focus.getWidth(), 0, p);
		}else{
			canvas.drawBitmap(month_def, 0, 0, p);
			canvas.drawBitmap(day_focus, month_def.getWidth(), 0, p);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int act = event.getAction();
		switch(act){
		case MotionEvent.ACTION_DOWN:{
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			break;
		}
		case MotionEvent.ACTION_UP:{
//				int x = (int)event.getX();
//				BIProjectActivity context = BIProjectActivity.getInstance();
//				String dateTime = KPIDataManager.getInstance().getDateTime();
//				if(dateTime.indexOf("-")>-1){
//					dateTime = dateTime.replaceAll("-", "");
//				}
//				if(x < width/2){
//					setModel(MODEL_MONTH);
//					context.uiControl.window
//						.showCalendar(context, dateTime,
//								MyDatePickerDialog.model_month);
//				}else if(x > width/2){
//					setModel(MODEL_DAY);
//					context.uiControl.window
//					.showCalendar(context, dateTime,
//							MyDatePickerDialog.model_day);
//				}
				postInvalidate();
			break;
		}
		case MotionEvent.ACTION_CANCEL:{
			break;
		}
		}
		return true;
	}
	
	private int measureHeight(int measureSpec) {
		
		int specSize = MeasureSpec.getSize(measureSpec);
		
		int specMode = MeasureSpec.getMode(measureSpec);
		
		int ret = Math.max(month_def.getHeight(), day_def.getHeight());
		
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
		
		int ret = month_def.getWidth()+day_def.getWidth();
		
		if(specMode == MeasureSpec.AT_MOST){
			
			ret = Math.min(ret, specSize);
			
		}else if(specMode == MeasureSpec.EXACTLY){
			
			ret = specSize;
			
		}else if(specMode == MeasureSpec.UNSPECIFIED){
			
		}

		return ret;

	}
}
