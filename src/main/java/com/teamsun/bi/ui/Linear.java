package com.teamsun.bi.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.teamsun.bi.model.Chart;

public class Linear extends Area implements Chart{

//	private int values[];
	
	public Linear(Context context)
	{
		super(context);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Rect r=new Rect();
		this.getDrawingRect(r);
		r.left+=4;
		r.top+=4;
		r.right-=4;
		r.bottom-=4;
//		Path path = new Path();
		
		float range=(int)(max-min);
		float x,y;
		int size = values.length;
		float w=r.width()/(size-1);
		
		if(point==null)
		{
			point=new Float[size][2];
		}
		paint.setColor(getBgColor());

		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		for(int i=0;i<size;i++)
		{
			if(values[i]!= null){
				int v=(int)Math.abs(values[i]-max);
				y=r.top+(int)(v*r.height()/range);
				if(y>r.bottom)
					y=r.bottom;
				if(y<0)
					y=0;
				if(i==size-1)
				{
					x=r.right;
				}
				else
					x=r.left+w*i;
				point[i][0]=x;
				point[i][1]=y;
			}else{
				point[i][0]=null;
				point[i][1]=null;
			}
			
			
		}
		
		for(int i=1;i<size;i++){
			if (point[i - 1][0] != null && point[i - 1][1] != null
					&& point[i][0] != null && point[i][1] != null) {
//				Log.e("line", "i-1  px:" + point[i - 1][0] + "  i-1 py:"
//						+ point[i - 1][1] + "  i  px:" + point[i][0]
//						+ "  i  py:" + point[i][1]);
				canvas.drawLine(point[i - 1][0], point[i - 1][1], point[i][0],
						point[i][1], paint);
			}
		}
		

		paint.setStyle(Paint.Style.FILL);
		
		for(int i=0;i<size;i++)
		{
			paint.setColor(getBgColor());
			if(point[i][0]!=null && point[i][1]!=null){
				canvas.drawCircle(point[i][0],point[i][1],radius, paint);
				paint.setColor(Color.WHITE);
				canvas.drawCircle(point[i][0],point[i][1],radius-2, paint);
			}
			
		}

	}


}
