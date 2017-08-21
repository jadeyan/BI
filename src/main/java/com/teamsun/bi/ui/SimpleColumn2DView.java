package com.teamsun.bi.ui;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.demo.R;



public class SimpleColumn2DView extends Column2DView {

		Bitmap bgImg;
		
		private String mess;

		public String getMess() {
			return mess;
		}

		public void setMess(String mess) {
			this.mess = mess;
		}

		public SimpleColumn2DView(Context context, AttributeSet attrs) {
			super(context, attrs);
			
			this.setShowPatten(true);
			this.setShowXLable(true);
			this.setShowYLable(true);
			
			if(bgImg==null)
			{
//				bgImg=BitmapFactory.decodeResource(getResources(), R.drawable.kpi_item_background);
				bgImg = BIProjectActivity.getInstance().uiControl.getResource(R.drawable.kpi_item_background);
			}
			
			setWillNotDraw(false);
			
		}
		
		public void clear(){
			super.clear();
//			if(bgImg != null){
//				bgImg.recycle();
//				bgImg = null;
//			}
		}
		
		protected void setupLayout()
		{
			LayoutInflater factory = LayoutInflater.from(mContext);
			int id = R.layout.simplecolumnmap;
			if(!showPatten)
				id = R.layout.simplecolumnmap_nopatten;
	        LinearLayout layout = (LinearLayout)factory.inflate(id, null);
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT
			);
	        this.addView(layout,params1);
	        
	        final int count = getChildCount();
	        if(count>0)
	        	child=getChildAt(0);
	        
		}


		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int h = measureHeight(heightMeasureSpec);

			int w = measureWidth(widthMeasureSpec);

			this.measureChildren(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h-(h/7-18)/2, MeasureSpec.EXACTLY));
			setMeasuredDimension(w, h);
		}

		private int measureHeight(int measureSpec) {

			int specSize = MeasureSpec.getSize(measureSpec);

			int specMode = MeasureSpec.getMode(measureSpec);

			int ret = bgImg.getHeight();
			
			if (specMode == MeasureSpec.AT_MOST) {

				ret = Math.min(ret, specSize);

			} else if (specMode == MeasureSpec.EXACTLY) {

				ret = specSize;

			} else if (specMode == MeasureSpec.UNSPECIFIED) {

			}

			return ret;

		}

		private int measureWidth(int measureSpec) {

			int specSize = MeasureSpec.getSize(measureSpec);

			int specMode = MeasureSpec.getMode(measureSpec);

			int ret = bgImg.getWidth();

			if (specMode == MeasureSpec.AT_MOST) {

				ret = Math.min(ret, specSize);

			} else if (specMode == MeasureSpec.EXACTLY) {

				ret = specSize;

			} else if (specMode == MeasureSpec.UNSPECIFIED) {

			}

			return ret;

		}
		
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub

//			if(bgImg!=null)
//			Rect srcRect=new Rect(0,0,bgImg.getWidth(),bgImg.getHeight());
			Rect desRect=new Rect();
			getDrawingRect(desRect);
//			canvas.drawBitmap(bgImg, srcRect, desRect,  mPaint);


			BitmapShader bs= new BitmapShader(bgImg,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
			mPaint.setShader(bs);
			mPaint.setStyle(Style.FILL);
			canvas.drawRoundRect(new RectF(desRect),8,8, mPaint);
			
			if(chartData == null){
				Paint paint = new Paint();
				paint.setTextSize(20);
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
				paint.setColor(0xffffffff);
				Paint.FontMetrics fm = paint.getFontMetrics();
				paint.setAntiAlias(true);
				
				String str = mess;
				if(str == null)
					str = "数据获取失败";
				int sw = (int)paint.measureText(str);
				int fy = (int)Math.abs(fm.top) + desRect.height()/2;
				canvas.drawText(str, (desRect.width() - sw)/2, 
						fy, paint);
			}
		}
		
//		public Column2DView getInstance()
//		{
//			return instance;
//		}

	}
