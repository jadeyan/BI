package com.teamsun.bi.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class ImageValueView extends View{

	String mText;
	int color = 0xffffff;
	public ImageValueView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageValueView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ImageValueView(Context context) {
		super(context);
	}
	
	public void setText(String t){
		this.mText = t;
	}
	
	public void setTextColor(int c){
		color = c;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if(mText != null){
			Rect r = new Rect();
			this.getDrawingRect(r);
			Paint mPaint = new Paint();
			mPaint.setColor(color|0xff000000);
			mPaint.setTextSize(20);
			mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
			int sw = (int)mPaint.measureText(mText);
			Paint.FontMetrics fm = mPaint.getFontMetrics();
			int fy = (r.height()-20)/2 - (int)fm.top;
			canvas.drawText(mText, (r.width()-sw)/2, fy, mPaint);
		}
	}
	
}
