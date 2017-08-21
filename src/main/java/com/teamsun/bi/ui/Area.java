package com.teamsun.bi.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;

import com.teamsun.bi.demo.R;
import com.teamsun.bi.model.Chart;

public class Area extends AbsoluteLayout implements Chart{
//	private int value;
	private AbsoluteLayout.LayoutParams param ;
	private int bgColor;
	
	private boolean isSimple;
	
	Context mContext;
	
	Float values[];
	float zero;
	float max,min;
	public Float[][] point;
	
	public static final int radius=4;
	public Area(Context context) {
		super(context);
		mContext=context;
		setWillNotDraw(false);
	}
	
	public void clear(){
		if(values != null && values.length>0){
			for(int i=0; i<values.length; i++){
				values[i] = null;
			}
			values = null;
		}
		if(point != null && point.length>0){
			for(int i=0; i<point.length; i++){
				if(point[i] != null && point[i].length>0){
					for(int j=0; j<point[i].length; j++){
						point[i][j] = null;
					}
					point[i] = null;
				}
			}
			point = null;
		}
		param = null;
		this.removeAllViews();
		System.gc();
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Rect r=new Rect();
		this.getDrawingRect(r);
		r.left+=4;
		r.top+=4;
		r.right-=4;
		r.bottom-=4;
		Path path = new Path();
		
		float range=(int)(max-min);
		float x,y;
		int size = values.length;
		float w=r.width()/(size-1);
		
		if(point==null)
		{
			point=new Float[size][2];
		}
//		int zero=r.height()*zeroIndex/PanelView.KSegment;
		
		path.moveTo(r.left,zero);
		for(int i=0;i<size;i++)
		{
			float v=Math.abs(values[i]-max);
			y=(int)(v*r.height()/range);
//			Log.e("draw", "y "+y);
			if(i==size-1)
			{
				x=r.right;
			}
			else
				x=r.left+w*i;
			if(y>r.bottom)
				y=r.bottom;
			if(y<0)
				y=0;
			point[i][0]=x;
			point[i][1]=y;
			path.lineTo(x, y);
		}
		path.lineTo(r.right, zero);
		path.close();
		
		if(!isSimple)
		{
		paint.setColor(bgColor);
		paint.setAlpha(0x50);
		}
		
		else
		{
			LinearGradient lg=new LinearGradient(r.left,r.top,r.left,r.bottom,
					new int []{bgColor,Color.BLACK&0x00ffffff},
					new float[]{0,1},
					Shader.TileMode.CLAMP);
			paint.setShader(lg);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
		}
		canvas.drawPath(path, paint);

		
		paint.setStyle(Paint.Style.STROKE);
		paint.setAlpha(255);
		paint.setStrokeWidth(1);
		paint.setColor(bgColor);
		canvas.drawPath(path, paint);
		
	}

	@Override
	public int getBgColor() {
		return bgColor;
	}

	@Override
	public void setBgColor(int bgColor) {
		this.bgColor=bgColor|0xff000000;
	}

	@Override
	public int getValue() {
		return 0;
	}

	@Override
	public void setValue(int value) {
		
	}

	@Override
	public void aniShowView() {
		ScaleAnimation tAnimation = new ScaleAnimation(1f, 1.0f, 0f, 1f,   
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,   
                0.5f);   
	    tAnimation.setInterpolator(new AccelerateInterpolator());   
	    AnimationSet aa = new AnimationSet(true);   
	    aa.addAnimation(tAnimation);   
	    aa.setDuration(PanelView.KAniInterval);   
	
	    startAnimation(aa);
	}

	/**
	 * @return the values
	 */
	public Float[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Float[] values) {
		this.values = values;
	}

	/**
	 * @return the zero
	 */
	public float getZero() {
		return zero;
	}

	/**
	 * @param zero the zero to set
	 */
	public void setZero(float zero) {
		this.zero = zero;
	}

	public void setMaxMin(float max,float min)
	{
		this.max=max;
		this.min=min;
	}
	
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		

	}
	
	public void setDrawable(int w,int h)
	{
		
		int x, y, size=4;
		
		int p=w/(values.length-1);
		for(int i=0;i<values.length;i++)
		{
			Button b = new Button(mContext);
			b.setBackgroundResource(R.drawable.point_bg);
			
			y=(int)(h*(values[i]-min)/(max-min));
			x=p*i-(size>>1);
			Log.e("setDrawable", "y "+y);
			AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(size,size,x,y);
			b.setLayoutParams(param);
			this.addView(b);
		}
		

		
	}

	/**
	 * @return the isSimples
	 */
	public boolean isSimple() {
		return isSimple;
	}

	/**
	 * @param isSimple the isSimple to set
	 */
	public void setSimple(boolean isSimple) {
		this.isSimple = isSimple;
	}
}
