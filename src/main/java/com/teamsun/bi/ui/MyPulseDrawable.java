package com.teamsun.bi.ui;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.demo.R;
import com.teamsun.bi.entry.Common;
import com.teamsun.bi.model.PulseChartData;
import com.teamsun.bi.model.PulseChartData.SingleSection;

public class MyPulseDrawable extends Drawable{

	float startAngle, angleRange;
	
	private int defExtrAngle = 180;
	PulseChartData chartData;
	Bitmap backImg, panImg, pointImg;
	int centerX ,centerY ,radius;;
	
	public String errorInfo;
	
	public MyPulseDrawable(Context context){
		init();
	}
	
	public void setChartData(PulseChartData chartData) {
		this.chartData = chartData;
	}
	
	private void init(){
		startAngle = 90 + defExtrAngle/2;
		angleRange = 360-defExtrAngle;

//		backImg = BitmapFactory.decodeResource(BIProjectActivity.getInstance().getResources(),
//				R.drawable.kpi_item_background);
//		
//		pointImg = BitmapFactory.decodeResource(BIProjectActivity.getInstance().getResources(),
//				R.drawable.pic01);
//
//		panImg = BitmapFactory.decodeResource(BIProjectActivity.getInstance().getResources(),
//				R.drawable.pic00);
		backImg = BIProjectActivity.getInstance().uiControl.getResource(R.drawable.kpi_item_background);
		pointImg = BIProjectActivity.getInstance().uiControl.getResource(R.drawable.pic01);
		panImg = BIProjectActivity.getInstance().uiControl.getResource(R.drawable.pic00);
	}
	
	public int[] getBackImgSize(){
		int[] size = new int[2];
		size[0] = backImg.getWidth();
		size[1] = backImg.getHeight();
		return size;
	}
	
	public void clear(){
//		Log.e("pulse", "recycle");
//		if(backImg != null){
//			backImg.recycle();
//			backImg = null;
//		}
//		if(pointImg != null){
//			pointImg.recycle();
//			pointImg = null;
//		}
//		if(panImg != null){
//			panImg.recycle();
//			panImg = null;
//		}
		if(chartData != null){
			chartData.clear();
			chartData = null;
		}
	}
	
	protected void onBoundsChange (Rect bounds){
		int w1 = bounds.right-bounds.left;
		int h1 = bounds.bottom - bounds.top;
		if(w1 != backImg.getWidth() || h1 != backImg.getHeight()){
			backImg = Common.scale(backImg, w1, h1);
		}

		centerX = (bounds.right-bounds.left)/2;
		centerY = (bounds.bottom - bounds.top)*17/24;
		int w = bounds.right-bounds.left-10;
		int h;
		if(defExtrAngle >= 180)
			h = bounds.bottom - bounds.top - 50;
		else
			h = bounds.bottom-bounds.top-25;
		
		radius = (w>h)?h/2:w/2;

		int width = panImg.getWidth();
		int height = panImg.getHeight();
		
		int scanR = radius+10*width/115;
		panImg = Common.scale(panImg, scanR*2, height*scanR*2/width);
	}
	
	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		paint.setStyle(Style.FILL_AND_STROKE);

		Rect rect = this.getBounds();
		RectF r = new RectF(rect);
		r.set(centerX-radius,centerY-radius, centerX+radius,centerY+radius);
		
		canvas.drawBitmap(backImg, 0, 0, paint);
		
		if(chartData != null){
			
			paint.setTextSize(20);
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
			paint.setColor(0xffffffff);
			Paint.FontMetrics fm = paint.getFontMetrics();
			paint.setAntiAlias(true);
			
			String str = chartData.capTitle;
			if(chartData.unit!=null)
				str = str.concat("(").concat(chartData.unit).concat(")");
			int sw = (int)paint.measureText(str);
			int fy = (int)Math.abs(fm.top) + ((rect.bottom-rect.top)/7-18)/2;
			canvas.drawText(str, (rect.right-rect.left - sw)/2, 
					fy, paint);
			float n=0;
			float sum= chartData.upperLimit - chartData.lowerLimit;
			Vector<SingleSection> v = chartData.getData();
			int len=v.size();
			for(int i=0;i<len;i++)
			{
				SingleSection da = (SingleSection)v.elementAt(i);
				int c = da.color;
				if(c == -1)
					c = 0xfffefefe;
				paint.setColor(c|0xff000000);
				float ang=(da.highVar-da.lowVar)*angleRange/sum;
				canvas.drawArc(r, (startAngle+n)%360, ang , true, paint);
				n+=ang;
			}
			
			int panExt = panImg.getHeight()*6/115;//12为转盘图的底边宽，115为320屏的转盘图高
			int panX = centerX-panImg.getWidth()/2;
			int panY = centerY - panImg.getHeight() + panExt;
			
			canvas.drawBitmap(panImg, panX, panY, paint);

			canvas.save();
			int fw = pointImg.getWidth();
			int fh = pointImg.getHeight();
			canvas.translate(centerX-fw/2, centerY-fh-6*radius/90);
			Matrix matrix = new Matrix();

//			Log.e("pulseDrawable", "curVar:"+chartData.curVar+"   lower:"+chartData.lowerLimit);
			float f = chartData.curVar-chartData.lowerLimit;
//			Log.e("pulseDrawable", "range:"+f + "sum:" + sum);
			float g = f*angleRange/sum;
//			Log.e("pulseDrawable", "single angle:"+g);
			float an = g + startAngle - 270;
//			Log.e("pulseDrawable", "angle:"+an);
			matrix.setRotate(an, (float)fw/2, (float)(fh+6*radius/90));//设置旋转角度与旋转中心
			canvas.drawBitmap(pointImg, matrix, null);
			canvas.restore();
			
			paint.setTextSize(20);
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
			paint.setColor(0xffffffff);
			str = "当前值："+chartData.curVar + "("+chartData.unit+")";
			sw = (int)paint.measureText(str);
			fy = rect.height() - (rect.height()/7);
			canvas.drawText(str, (rect.right-rect.left - sw)/2, 
					fy, paint);
		}else{
			paint.setTextSize(20);
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
			paint.setColor(0xffffffff);
			Paint.FontMetrics fm = paint.getFontMetrics();
			paint.setAntiAlias(true);
			
			String str = errorInfo;
			if(str == null)
				str = "数据获取失败";
			int sw = (int)paint.measureText(str);
			int fy = (int)Math.abs(fm.top) + rect.height()/2;
			canvas.drawText(str, (rect.width() - sw)/2, 
					fy, paint);
		}
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		
	}

}
