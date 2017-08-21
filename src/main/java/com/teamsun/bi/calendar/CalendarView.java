package com.teamsun.bi.calendar;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class CalendarView extends View
{
	public Calendar ce;
	Activity context;

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(ce != null)
			ce.draw(canvas);
	}

	public CalendarView(Activity activity)
	{
		super(activity);
		context = activity;
		ce = new Calendar(activity, this);
	}

	@Override//日历的触摸事件
	public boolean onTouchEvent(MotionEvent motion)
	{
		ce.grid.setCellX(motion.getX());//获得当前触摸位置的横坐标
		ce.grid.setCellY(motion.getY());//获得当前触摸位置的纵坐标
		if (ce.grid.inBoundary())
		{
			this.invalidate();//重绘日历内容
		}
		return super.onTouchEvent(motion);
	}

	@Override//日历的键盘事件
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(ce != null){
			switch (keyCode)
			{
				case KeyEvent.KEYCODE_DPAD_UP://按向上键，日期上移一格
				{
					ce.grid.setCurrentRow(ce.grid.getCurrentRow() - 1);
					break;
				}
				case KeyEvent.KEYCODE_DPAD_DOWN://按向下键，日期下移一格
				{
					ce.grid.setCurrentRow(ce.grid.getCurrentRow() + 1);
					break;
				}
				case KeyEvent.KEYCODE_DPAD_LEFT://按向左键，日期左移一格
				{
					ce.grid.setCurrentCol(ce.grid.getCurrentCol() - 1);
					break;
				}
				case KeyEvent.KEYCODE_DPAD_RIGHT://按向右键，日期右移一格
				{
					ce.grid.setCurrentCol(ce.grid.getCurrentCol() + 1);
					break;
				}
			
			}
		}
		return true;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = context.getWindowManager().getDefaultDisplay().getWidth(); 
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		if(w <= 240)
			setMeasuredDimension(w, (int)(h * 0.52));	 
		else if(w <= 320)
			setMeasuredDimension(w, (int)(h * 0.55));
		else
			setMeasuredDimension(w, (int)(h * 0.58));	 
	}

}
