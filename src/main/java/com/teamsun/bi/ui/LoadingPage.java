package com.teamsun.bi.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.view.View;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.demo.R;


public class LoadingPage extends View implements Runnable {

	public int width;
	public int height;
	int curX;
	int beginX = 0;
	int endX = 0;
	int progressH;
	Handler handler;
	boolean quit = false;
	BIProjectActivity context;
	Bitmap pro1, pro2;
	
	public LoadingPage(BIProjectActivity context, Handler handler) {
		super(context);
		this.context = context;
		this.handler = handler;
		this.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.load_background));

		pro1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.progress_1);
		pro2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.progress_2);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		sizeChanged(w, h);
	}

	public void sizeChanged(int w, int h) {
		width = w;
		height = h;
//		midlet.initWebPage(width, height);
	}

	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		super.onDraw(canvas);
		canvas.clipRect(0, 0, width, height);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL_AND_STROKE);
		progressH = 0;
		
		int imgW = pro1.getWidth();
		int de = 0;
		de = (width - imgW) / 2;
		progressH = height / 2 + height / 4 + 10;
		canvas.drawBitmap(pro1, width-de-imgW, progressH, paint);
		beginX = width - de - imgW + 25;
		endX = beginX + imgW - 50;
		if (curX < beginX)
			curX = beginX;
		canvas.drawBitmap(pro2, curX, progressH-5, paint);
	}

	public void run() {
		curX = beginX;
		while (!quit) {
			if (this.width <= 320)
				curX++;
			else
				curX += 2;
			if (endX > 0 && curX >= endX) {
				handler.post(mShow);
				quit = true;
				curX = beginX;
			}
			this.postInvalidate(beginX, progressH - 5, this.width,
					progressH + 10);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	final Runnable mShow = new Runnable(){

		@Override
		public void run() {
			context.showLoginPage();
		}};

}
