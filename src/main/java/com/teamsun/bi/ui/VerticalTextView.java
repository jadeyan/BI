package com.teamsun.bi.ui; /**
 * 
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * @author Nan
 *
 */
public class VerticalTextView extends TextView {

	/**
	 * @param context
	 */
	public VerticalTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public VerticalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public VerticalTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.rotate(-90,this.getWidth()>>1,this.getHeight()>>1);
//		canvas.rotate(-20);
		super.onDraw(canvas);
		Paint paint = this.getPaint();
		int textSize=0;
		CharSequence text = getText();
		if(text != null)
			textSize = text.toString().length();
		Log.e("verticalTextView", "text:"+text);
		Log.e("verticalTextView", "textSize:"+textSize);
		canvas.drawText(text,0,textSize,getWidth()>>1,this.getHeight(), paint);

		
	}

}
