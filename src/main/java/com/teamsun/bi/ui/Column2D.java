package com.teamsun.bi.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.demo.R;

public class Column2D extends Button {

	private float value;
	private int bgColor;
	
	Context mContext;
	private boolean isSimple;
//	Bitmap normal;
//	Bitmap pressed;
	public Column2D(Context context) {
		super(context);
		mContext=context;
		
		setTextSize(9);
	}
	
	public void clear(){
//		if(normal != null && !normal.isRecycled()){
//			normal.recycle();
//		}
//		System.gc();
	}
	
	private void setDrawable()
	{
		int	rid = R.drawable.column;
//		normal = BitmapFactory.decodeResource(mContext.getResources(), rid);
		Bitmap normal = BIProjectActivity.getInstance().uiControl.getResource(rid);
		BitmapDrawable normal1= new BitmapDrawable(normal);
		
	
		StateListDrawable bg = new StateListDrawable();
        
        bg.addState(View.PRESSED_ENABLED_STATE_SET, normal1);
        bg.addState(View.ENABLED_FOCUSED_STATE_SET, normal1);
        bg.addState(View.ENABLED_STATE_SET, normal1);
        bg.addState(View.FOCUSED_STATE_SET, normal1);
        bg.addState(View.EMPTY_STATE_SET, normal1);
		
		setBackgroundDrawable(bg);
	}
	/* (non-Javadoc)
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
	}
	
	/**
	 * @return the bgColor
	 */
	public int getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor the bgColor to set
	 */
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
		setDrawable();
	}	

	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(float value) {
		this.value = value;
		this.setText(PanelView.formatValue(value,1));
	}

	
	public void aniShowView()
	{
//		PanelView pv=(PanelView)findViewById(R.id.panelview);
		ScaleAnimation tAnimation = new ScaleAnimation(1f, 1.0f, 0f, 1f,   
	                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,   
	                value>0?1f:0);   
        tAnimation.setInterpolator(new AccelerateInterpolator());   
        AnimationSet aa = new AnimationSet(true);   
        aa.addAnimation(tAnimation);   
        aa.setDuration(PanelView.KAniInterval);   
        startAnimation(aa);
	}

	/**
	 * @return the isSimple
	 */
	public boolean isSimple() {
		return isSimple;
	}

	/**
	 * @param isSimple the isSimple to set
	 */
	public void setSimple(boolean isSimple) {
		this.isSimple = isSimple;
		if(isSimple)
		{
			this.setText("");
		}
	}
}
