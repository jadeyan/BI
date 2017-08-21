package com.teamsun.bi.ui;

import java.text.DecimalFormat;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.teamsun.bi.demo.R;
import com.teamsun.bi.model.ChartData;
import com.teamsun.bi.model.DataSet;
import com.teamsun.bi.model.DataSet.DataType;



@SuppressWarnings("deprecation")
public class PanelView extends HorizontalScrollView  {

	final int Kbgcolor=Color.WHITE;
	final int Kedgecolor=0xff000000;
	public static final int KAniInterval=1000; //ms
	
//	final int KbgsegmentCount=5;
	final int KbgGridGray=0xfff7f7f7;
	final int KbgEdgeGray=0xffc4c4c4;
	final int KbgLabelAndZeroGray=0xff8d8d8d;
	
	
	
	final int KColumnColorBase=0xff0000;
	
	int KColumnWidth=30;
	
	protected Paint mPaint;  
	protected Context mContext;  
    Rect mRect = new Rect();
    int defaultH;
    
    ChartData chartData;
    
 
    
    AbsoluteLayout alayout;
    //coordinate and columns' size;
    
    //Y�������
    float maxY,minY,intervalY,avgY,avgValue;
    public static final int KSegment=5;
    int scaleLableY[];
    float zeroY;
//    int zeroIndex;
    int coordinateYHeight = 25;
    final int KTitleHeight=0;//50;
    TextView labelYView[];
    
    //x�����
    int intervalX, maxWidth;
    TextView labelXView[];
    
    Vector column2DChartArray;
    Column2DView column2DView;
    
    Vector areas; 
    Vector lines;
    
    int colorIndex;
    
 
	Vector patternName=null;

   
	public PanelView(Context context, AttributeSet attrs){
		super(context);
		this.mContext=context;
		this.setPadding(2, 2, 2, 2);
		mPaint = new Paint();  
		
		mPaint.setAlpha(255);
		
		setWillNotDraw(false);
		
		
		alayout = new AbsoluteLayout(mContext,null);
		alayout.setLayoutParams(new AbsoluteLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,0,0));
		

		patternName=new Vector();

		
		this.addView(alayout);
//		
		
		aniShowPanelView();
		
	}

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		drawBg(canvas);
	}
	
	/* (non-Javadoc)
	 * @see android.view.ViewGroup#onAnimationEnd()
	 */
	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		Log.e("nan,onAnimationEnd","end");
		this.processColumnMap();

		if(column2DChartArray!=null)
		{
			int countCol=this.countColumnMap();
			int size = column2DChartArray.size();
			int cp=size/countCol;
			for(int i=0;i<size;i++)
			{
				Column2D chart=(Column2D)column2DChartArray.elementAt(i);
				
				
//				int n=;
				if(countCol>1)
				{
					//b.setBackgroundColor((0xff000000|(KColumnColorBase-n*0x123456)));
					colorIndex=i/cp;
					
				}
				else
				{
					colorIndex=i;
				}

				int color=Column2DView.colors[colorIndex%Column2DView.colors.length];
				
				chart.setBgColor(color);
				chart.aniShowView();
				alayout.addView(chart);
			}
		}
		if(areas!=null)
		{
			for(int i=0;i<areas.size();i++)
			{
				Area a =(Area)areas.elementAt(i);
				colorIndex=(colorIndex+1)%Column2DView.colors.length;
				a.setBgColor(Column2DView.colors[colorIndex]);
				a.aniShowView();
				alayout.addView(a);
			}
		}
		if(lines!=null)
		{
			for(int i=0;i<lines.size();i++)
			{
				Linear a =(Linear)lines.elementAt(i);
				
				a.setBgColor(Column2DView.colors[colorIndex++%Column2DView.colors.length]);
				a.aniShowView();
				alayout.addView(a);
			}
		}
		
		
		if(column2DView.isShowXLable() && labelXView!=null)
		{
			for(int i=0;i<labelXView.length;i++)
			{
				labelXView[i].setVisibility(View.VISIBLE);
			}
		}
		if(column2DView.isShowYLable() && labelYView!=null)
		{
			for(int i=0;i<labelYView.length;i++)
			{
				if(labelYView[i]!=null)
				labelYView[i].setVisibility(View.VISIBLE);
			}
		}
		requestLayout ();
		this.invalidate();
	}

	/* (non-Javadoc)
	 * @see android.view.View#onAttachedToWindow()
	 */
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		column2DView = getColumn2DViewInstance();
		
		if(column2DView == null)
		{
			return ;
		}
		chartData=column2DView.getChartData(this);
		
	}
	
	protected Column2DView getColumn2DViewInstance()
	{
		ViewParent vp = getParent();
		while(vp!=null && !(vp instanceof Column2DView ))
		{
			vp = vp.getParent();
		}
		if(vp==null)
		{
			return null;
		}
		else
		{
			return (Column2DView)vp;
		}
	}
	


	/* (non-Javadoc)
	 * @see android.widget.HorizontalScrollView#onSizeChanged(int, int, int, int)
	 */
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		setupTitle();
		processXPadding();
		processYPadding();
		
//		processColumnMap();
	}
	
	void setupTitle()
	{
		if(chartData!=null)
		{
			String title=chartData.getTitle();
			if(title!=null)
			{
				TextView tv=(TextView)column2DView.findViewById(R.id.TitleView);
				tv.setText(title);
			}
			String yAxisName= chartData.getyAxisName();
			if(column2DView.isShowYLable() && yAxisName!=null)
			{
				TextView tv=(TextView)column2DView.findViewById(R.id.YAxisName);
				tv.setText(yAxisName);

			}
			String xAxisName= chartData.getxAxisName();
			if(column2DView.isShowXLable() && xAxisName!=null)
			{
				TextView tv=(TextView)column2DView.findViewById(R.id.XAxisName);
				tv.setText(xAxisName);

			}
		}
	}
	
	void processPattern(Vector name)
	{
		if(!column2DView.isShowPatten())
			return;
		if(name!=null && name.size()>0)
		{
			int size=name.size();
			TextView t[]=new TextView[size/2];
			int pmax=0;
			
			Rect r=new Rect();
			int patterSize=0;
			int KPadding = 8;
			for(int i=0;i<size/2;i++)
			{
				String n=(String)name.elementAt(i*2);
				t[i]=new TextView(mContext);
				t[i].setText(n);
				t[i].setTextSize(11);
				t[i].setTextColor(Color.WHITE);
				t[i].getPaint().getTextBounds(n,0,n.length(), r);
				if(pmax<r.width())
				{
					pmax=r.width();
				}

			}
			patterSize=r.height();
			if(patterSize <=2)
				patterSize=9;
			AbsoluteLayout layout=(AbsoluteLayout)column2DView.findViewById(R.id.Pattern);	
			
//			layout.setBackgroundColor(Color.WHITE);	
			
			int w=layout.getWidth();
			int num=layout.getWidth()/(patterSize+pmax+KPadding+1);
			if(num==0) num=1;
			if(num>size)
			{
				num=size;
			}
			
			int top=2,left=(layout.getWidth()-(num*(patterSize+pmax+KPadding+1)))>>1;
			for(int i=0;i<size/2;i++)
			{
				TextView tv=new TextView(mContext);
				Integer n=(Integer)name.elementAt(i*2+1);
				tv.setBackgroundColor(n.intValue()|0xff000000);
				tv.setText("red");
			
				AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,
						left+(i%num)*(patterSize+pmax+KPadding+1),
						top+(i/num)*(patterSize+1));
//				param.x = left+(i%num)*(patterSize+pmax+KPadding+1);
//				param.y = top+(i/num)*(patterSize+1);
				param.width=param.height=patterSize;
				
				layout.addView(tv,param);
				
				AbsoluteLayout.LayoutParams paramTxt = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0,0);
				paramTxt.x=param.x+patterSize+1;
				paramTxt.y=param.y;
				layout.addView(t[i],paramTxt);
			}
			
		}
	}
	
	void processAreaMap(DataSet ds,Rect rect)
	{
		if(ds!=null)
		{
			
			Float[] v = ds.getValue();
			int size = v.length;
			if(size<=0)
				return;
//			else
			if(areas==null)
				areas=new Vector();
			float max=0,min=0;
			for(int i=0;i<size;i++)
			{
				if(v[i]!=null){
					if(max<v[i])
						max=v[i];
					if(min>v[i])
						min=v[i];
				}
			}
			Log.e("area max-min", ""+max+"  "+min);
			float x = intervalX-((KColumnWidth*countColumnMap())>>1);
			float y,h,h1,h2,w;
			if(max>0)
			{
				h1=max*(zeroY-rect.top)/maxY;
				y=zeroY-h1;
			}
			else
			{
				h1=Math.abs(max*(rect.bottom-zeroY)/minY);
				y=zeroY;
			}
			
			if(min<0)
			{
				h2=Math.abs(min*(rect.bottom-zeroY)/minY);
			}
			else
				h2=0;
			h=h1+h2;
			int countX=chartData.getDispLabelX().size();
			w=intervalX*(countX-1);
//			w=intervalX*(labelXView.length-1);
			
			float z=0;
			z=(zeroY-y);
			
			//for test
			{
			Area area = new Area(mContext);
			if(this instanceof SimplePanelView)
			{
				area.setSimple(true);
			}
//			area.setBgColor(Column2DView.colors[(colorIndex++)%Column2DView.colors.length]);
//			area.setBackgroundColor(Column2DView.colors[colorIndex%Column2DView.colors.length]|0x0f000000);
			area.setValues(v);
			area.setZero(z);
			area.setMaxMin(max,min);
//			area.setDrawable(w,h); //��button
			AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams((int)w+8,(int)h+8,(int)x-4,(int)y-4);

			area.setLayoutParams(p);
			areas.add(area);
			//			l.addView(new Button(mContext));
//			this.alayout.addView(area);
			}
			
		}
	}
	
	void processLineMap(DataSet ds,Rect rect)
	{
		if(ds!=null)
		{
			
			Float[] v = ds.getValue();
			int size = v.length;
			if(size<=0)
				return;
//			else
			if(lines==null)
				lines=new Vector();
			float max=0,min=0;
			for(int i=0;i<size;i++)
			{
				if(v[i]!=null){
					if(max<v[i])
						max=v[i];
					if(min>v[i])
						min=v[i];
				}
			}
			Log.e("area max-min", ""+max+"  "+min);
			float x = intervalX-((KColumnWidth*countColumnMap())>>1);
			float y,h,h1,h2,w;
			if(max>0)
			{
				h1=(int)max*(zeroY-rect.top)/maxY;
				y=zeroY-h1;
			}
			else
			{
				h1=Math.abs((int)max*(rect.bottom-zeroY)/minY);
				y=zeroY;
			}
			
			if(min<0)
			{
				h2=Math.abs((int)min*(rect.bottom-zeroY)/minY);
			}
			else
				h2=0;
			h=h1+h2;
			int countX=chartData.getDispLabelX().size();
			w=intervalX*(countX-1);
			
			float z=0;
			z=(zeroY-y);
			
			//for test
			{
			Linear line = new Linear(mContext);
			if(this instanceof SimplePanelView)
			{
				line.setSimple(true);
			}
//			line.setBgColor(Column2DView.colors[(colorIndex++)%Column2DView.colors.length]);
//			line.setBackgroundColor(Column2DView.colors[colorIndex%Column2DView.colors.length]|0x6f000000);
			line.setValues(v);
			line.setZero(z);
			line.setMaxMin(max,min);
			
//			Line.setDrawable(w,h); //��button
			AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams((int)w+8,(int)h+8,(int)x-4,(int)y-4);

			line.setLayoutParams(p);
			lines.add(line);
			//			l.addView(new Button(mContext));
//			this.alayout.addView(area);
			}
			
		}
	}

	
	void processColumnMap() {
		if (chartData != null) {
			float x, y, h, w = KColumnWidth;

			int countCol = countColumnMap();
			float countColWidth = w * countCol;
			column2DChartArray = null;
			System.gc();
			if (countCol > 0) {
				column2DChartArray = new Vector();
			}

			int max = scaleLableY[0];
			int min = scaleLableY[scaleLableY.length - 1];
			int range = max - min;

			Vector v = chartData.getValueArray();

			Rect rect = new Rect();
			this.getDrawingRect(rect);
			rect.bottom -= coordinateYHeight;

			int size = v.size();

			int n = 0;
			int color = 0;
			for (int i = 0; i < size; i++) {
				DataSet ds = (DataSet) v.elementAt(i);

				if (ds.getType() == DataSet.DataType.DataTypeColumn) {
					Float[] dv = ds.getValue();
					for (int j = 0; j < dv.length; j++) {
						x = intervalX * (j + 1) - ((countColWidth) / 2)
								- ((countColWidth) / 2) + n * w;
						if (dv[j] != null) {
							float num = (float) dv[j];
							if (num > 0) {
								h = (int) num * (zeroY - rect.top) / max;
								y = zeroY - h;
							} else {
								h = Math.abs((int) num * (rect.bottom - zeroY)
										/ min);
								y = zeroY;
							}

							AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT, 0, 0);
							param.x = (int) x;
							// ����1px ����
							param.x += (n > 0 ? -1 * n : 0);
							param.y = (int) y - 2;

							param.height = (int) h;
							param.width = (int) w;

							Column2D b = new Column2D(mContext);
							b.setValue(num);

							b.setLayoutParams(param);
							column2DChartArray.add(b);
						}
					}
					n++;
					if (countCol > 1 && patternName != null) {
						// String l=ds.getSeriesName();
						// if(l==null)
						// patternName.add("");
						// else
						patternName.add(ds.getSeriesName());
						patternName.add(new Integer(Column2DView.colors[color++
								% Column2DView.colors.length]));
					} else {
						color = dv.length;
					}
				} else if (ds.getType() == DataSet.DataType.DataTypeArea) {
					processAreaMap(ds, rect);
					patternName.add(ds.getSeriesName());
					patternName.add(new Integer(Column2DView.colors[color++
							% Column2DView.colors.length]));

				} else if (ds.getType() == DataSet.DataType.DataTypeLine) {
					processLineMap(ds, rect);
					patternName.add(ds.getSeriesName());
					patternName.add(new Integer(Column2DView.colors[color++
							% Column2DView.colors.length]));

				}

			}

			processPattern(patternName);
		}
	}
	
	public void setupChart()
	{


	}
	
	public static String formatValue(float value,int precision)
	{
		String label=null;
		String format="###,###.";
		for(int i=0;i<precision;i++)
		{
			format+="#";
		}
			
		float val= Math.abs(value);
		if(val>1000000)
		{
			 label = new DecimalFormat(format).format(val/1000000)+"M";
		}
		else if(val>100)
		{
			label = new DecimalFormat(format).format(val/1000)+"K";
		}
		else
			label=""+(int)val;
		if(value<0)
		{
			label="-"+label;
		}
		return label;
	}
	
	public void processYPadding()
	{
		if(chartData!=null)
		{
			int count=0;
			Vector v=chartData.getValueArray();
			float min=0,max=0,sum=0;
//			float avg=0;
			
			int m=v.size();
			for(int i=0;i<m;i++)
			{
				
				DataSet ds = (DataSet)v.elementAt(i);
				if(ds!=null)
				{
					Float[] dsv=ds.getValue();
					int n=dsv.length;
					count+=n;
					for(int j=0;j<n;j++)
					{
						if(dsv[j]!=null){
							sum+=dsv[j];
							if(min>dsv[j])
							{
								min=dsv[j];
							}
							if(max<dsv[j])
							{
								max=dsv[j];
							}
						}
					}
				}
			}
			avgValue=(int)sum/count;
			getAxisLimits(max,min);
			
			
			if(scaleLableY!=null)
			{
//				column2DView view=Column2DView.getInstance();
				AbsoluteLayout layout=(AbsoluteLayout)column2DView.findViewById(R.id.AbsoluteLayoutLeft);
				Rect rect= new Rect(); 
					layout.getDrawingRect(rect);
					
					rect.top+=KTitleHeight;
					Rect r=new Rect();
					getDrawingRect(r);
					
					int p0=(r.height()-coordinateYHeight)/KSegment;
					avgY=(maxY-avgValue)*(r.height()-coordinateYHeight)/(maxY-minY);

				labelYView = new TextView[scaleLableY.length+(chartData.isShowAvg()?1:0)];
				for(int i=0;i<scaleLableY.length;i++)
				{

					String label=formatValue(scaleLableY[i],2);
					
					TextView t=new TextView(mContext);
					t.setText(label);
					t.setTextSize(9);
					t.setTextColor(0xffffff);
					t.getPaint().getTextBounds(label,0,label.length(), r);
					AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0,0);
					
					param.x = rect.right-r.width()-1;
					param.y = rect.top+p0*i-(r.height()>>1);
					
					if(scaleLableY[i]==0)
					{
						zeroY=p0*i;
//						zeroIndex=i;
					}

					t.setVisibility(View.INVISIBLE);
					labelYView[i]=t;
					layout.addView(t,param);
				}
				//ƽ��ֵ�� +p0*scaleLableY.length-(r.height()>>1)
				if(chartData.isShowAvg())
				{
					String label="average";
					TextView t=new TextView(mContext);
					t.setText(label);
					t.setTextSize(9);
					t.setTextColor(Color.GREEN);
					t.getPaint().getTextBounds(label,0,label.length(), r);
					AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0,0);
					param.x = rect.right-r.width()-1;
					param.y = rect.top+(int)avgY-(r.height()>>1);
					
					t.setVisibility(View.INVISIBLE);
					labelYView[labelYView.length-1]=t;
					layout.addView(t,param);
				}
			}
		}
	}
	
	protected void getAxisLimits (float maxValue , float minValue )
	{

		double maxPowerOfTen  = Math.floor (Math.log10 (Math.abs (maxValue)) / Math.log(10));
		//Get the minimum power of 10 that is applicable to maxvalue
		double minPowerOfTen  = Math.floor (Math.log10 (Math.abs (minValue)) / Math.log(10));
		//Find which powerOfTen (the max power or the min power) is bigger
		//It is this which will be multiplied to get the y-interval
		double powerOfTen  = Math.max (minPowerOfTen, maxPowerOfTen);
		double y_interval  = Math.pow (10, powerOfTen);
		//For accomodating smaller range values (so that scale doesn't represent too large an interval
		if (Math.abs (maxValue) / y_interval < 2 && Math.abs (minValue) / y_interval < 2)
		{
			powerOfTen --;
			y_interval = Math.pow (10, powerOfTen);
		}
		//If the y_interval of min and max is way more than that of range.
		//We need to reset the y-interval as per range
		double rangePowerOfTen  = Math.floor (Math.log10 (maxValue - minValue) / Math.log(10));
		double rangeInterval  = Math.pow (10, rangePowerOfTen);
		//Now, if rangeInterval is 10 times less than y_interval, we need to re-set
		//the limits, as the range is too less to adjust the axis for max,min.
		//We do this only if range is greater than 0 (in case of 1 data on chart).
		if (((maxValue - minValue) > 0) && ((y_interval / rangeInterval) >= 10))
		{
			y_interval = rangeInterval;
			powerOfTen = rangePowerOfTen;
		}
		//Calculate the y-axis upper limit
		double y_topBound  = (Math.floor (maxValue / y_interval) + 1) * y_interval;
		//Calculate the y-axis lower limit
		double y_lowerBound =0;
		//If the min value is less than 0
		if (minValue < 0)
		{
			//Then calculate by multiplying negative numbers with y-axis interval
			y_lowerBound = - 1 * ((Math.floor (Math.abs (minValue / y_interval)) + 1) * y_interval);
		} else 
		{
			//Else, simply set it to 0.
//			if (setMinAsZero)
//			{
				y_lowerBound = 0;
//			} else 
//			{
//				y_lowerBound = Math.floor (Math.abs (minValue / y_interval) - 1) * y_interval;
//				//Now, if minValue>=0, we keep x_lowerBound to 0 - as for values like minValue 2
//				//lower bound goes negative, which is not required.
//				y_lowerBound = (y_lowerBound < 0) ?0 : y_lowerBound;
//			}
		}
		//MaxValue cannot be less than 0 if stopMaxAtZero is set to true
		if ( maxValue <= 0)
		{
			y_topBound = 0;
		}

		maxY = (int)y_topBound;
		minY = (int)y_lowerBound;

//		intervalY = (int)y_interval;

		float range=Math.abs(maxY-minY) ;
		float p1=range/KSegment;
		while (range%KSegment!=0 || range/KSegment%y_interval!=0)
		{
			range+=y_interval;
			p1=range/KSegment;
		}
		
		scaleLableY=new int[KSegment+1];
		if(maxY>0 && minY>=0)
		{
			for(int i=0;i<=KSegment;i++)
			{
				scaleLableY[KSegment-i]=i*(int)p1;
			}
		}
		else
		{
			if(maxY<0 && minY<=0)
			{
				for(int i=0;i<=KSegment;i++)
				{
					scaleLableY[i]=-i*(int)p1;
				}
			}
			else
			{
				if(maxY>0 && minY<0)
				{
					if(maxY%p1!=0)
					{
						float p2=maxY/p1+1;
						while(p2>=5)
						{
							p2=maxY/(p1+=(int)y_interval)+1;
						}
						
						maxY=p2*p1;
						while(minY<maxY-5*p1)
						{
							p1+=(int)y_interval;
							maxY=p2*p1;
						}
						minY=maxY-5*p1;
						for(int i=0;i<=KSegment;i++)
						{
							scaleLableY[i]=(int)(maxY-i*p1);
						}
					}
					else
					{
						for(int i=0;i<=KSegment;i++)
						{
							scaleLableY[i]=(int)(maxY-i*p1);
						}
					}
				}
			}
		}
		
	}

	protected int countColumnMap()
	{
		// ͳ����״ͼ����
		if(chartData==null)
			return 0;
		int count=0;
		Vector v=chartData.getValueArray();
		for(int i=0;i<v.size();i++)
		{
			DataSet ds = (DataSet)v.elementAt(i);
			if(ds.getType()==DataType.DataTypeColumn)
			{
				count++;
			}
		}
		return count;
	}

	public void processXPadding()
	{
//		int colCount = 0;
		int labelXCount = 0;
		
		if(chartData!=null)
		{
			
			
			Vector labels=chartData.getDispLabelX();
			labelXCount=labels.size();
		
			
			Rect r = new Rect();
			int maxW=0;
			
			labelXView = new TextView[labelXCount];
			int tw[]=new int[labelXCount];
			for(int i=0;i<labelXCount;i++)
			{
				String l=(String)labels.elementAt(i);
				labelXView[i] = new TextView(mContext);
				labelXView[i].setText(l);
				labelXView[i].setTextSize(9);
				labelXView[i].setTextColor(KbgLabelAndZeroGray);
				
				labelXView[i].getPaint().getTextBounds(l,0,l.length(), r);
				tw[i]=r.width();
				if(maxW<tw[i])
				{
					maxW=tw[i];
				}
				labelXView[i].setTextColor(/*Column2DView.KCoordinateTextColor*/0xffffffff);

			}
			
			// ͳ����״ͼ����
			int count=countColumnMap();
			//������    
			int padding = 0;
			if(count>=2)
			{
				if(KColumnWidth/count<5)
				{
					KColumnWidth=5;
				}
				
				padding=KColumnWidth*count;
			}
			else
			{
				KColumnWidth=maxW*7/8;
				padding=KColumnWidth*count;
			}
			//��ԭmaxW�Ƚϣ�ȥ��ֵ
			if(padding>maxW)
			{
				maxW=padding;
			}
			//----------
			
			
			maxW+=maxW/4;
			intervalX=maxW; //��ֵ
			for(int i=0;i<labelXCount;i++)
			{
				AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0,0);
				param.x = maxW*(i+1)-(tw[i]>>1)-((KColumnWidth*count)>>1);//����
				param.y = this.getHeight()-coordinateYHeight+2;

				labelXView[i].setVisibility(View.INVISIBLE);
				alayout.addView(labelXView[i],param);
			}
			
			{
				AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0,0);
				param.x = maxW*(labelXCount);//+(maxW>>1);
				param.y = this.getHeight()-coordinateYHeight+2;
				param.width=5;
				View v = new View(mContext);
				alayout.addView(v,param);
				
//				if(lines != null){
//					for(int i=0; i<lines.size(); i++){
//						Linear l = (Linear)lines.elementAt(i);
//						param = new AbsoluteLayout.LayoutParams(9,9,30,this.getHeight()-coordinateYHeight+15);
//						v = new View(mContext);
//						v.setBackgroundColor(Column2DView.colors[colorIndex++%Column2DView.colors.length]);
//						alayout.addView(v,param);
//						
//						param = new AbsoluteLayout.LayoutParams(9,9,42,this.getHeight()-coordinateYHeight+15);
//						TextView tv1 = new TextView(mContext);
//						
//						tv1.setText(l.);
//					}
//				}
			}
			
			tw=null;
			System.gc();
			
			maxWidth = maxW*(labelXCount+1);	
//			for(int i=0;i<v.size();i++)
//			{
//				DataSet ds = (DataSet)v.elementAt(i);
//				if(ds.getType()==DataType.DataTypeColumn)
//				{
//					View aView = new View(mContext);
//				}
//			}
			
		}
		
		
		
	}

	public void drawBg(Canvas canvas)
	{
		//canvas.getClipBounds(mRect);
		getDrawingRect(mRect);

		mPaint.setStyle(Style.FILL);  
		mPaint.setColor(Column2DView.bgcolor);   
        canvas.drawRect(mRect, mPaint);
        
        Rect r=new Rect(mRect);
        r.top=r.bottom-coordinateYHeight;
 
        mRect.bottom-=+r.height();
        drawBgGrid(mRect,canvas,mPaint);

        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Kedgecolor);
        mRect.left+=1;
        mRect.top+=1;
        mRect.right-=1;
        mRect.bottom-=1;
        canvas.drawRect(mRect,mPaint);
        


	}
	
	private void drawBgGrid(Rect rect,Canvas canvas,Paint paint)
	{
        int segH=rect.height()/5;
        Rect r = new Rect(rect);
        r.bottom = r.top+segH;
        
        for(int i=0;i<KSegment;i++)
        {
        	if(i%2>0)
        	{
        		r.top=i*segH;
        		r.bottom=(i+1)*segH;
        		paint.setStyle(Style.FILL);
        		paint.setStrokeWidth(1);
        		paint.setColor(KbgGridGray);
        		canvas.drawRect(r,paint);

        		paint.setColor(KbgEdgeGray);
        		canvas.drawLine(r.left, r.top, r.right, r.top, paint);
        		canvas.drawLine(r.left, r.bottom, r.right, r.bottom, paint);
        		

        	}
    		//paint zero line
    		if(scaleLableY!=null && scaleLableY[i]==0)
    		{
    			paint.setColor(KbgLabelAndZeroGray);
    			paint.setStrokeWidth(2);
        		canvas.drawLine(r.left, zeroY, r.right, zeroY, paint);
    		}
        }
        
        if(chartData!=null && chartData.isShowAvg())
        {
        float avgH= avgY;//(maxY-avgY)* rect.height()/(maxY-minY);
        paint.setColor(Color.GREEN);
		paint.setStrokeWidth(1);
		canvas.drawLine(r.left, avgH, r.right, avgH, paint);
        }
        
	}
	
	void aniShowPanelView()
	{
//		PanelView pv=(PanelView)findViewById(R.id.panelview);
		ScaleAnimation tAnimation = new ScaleAnimation(0f, 1.0f, 1f, 1f,   
	                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,   
	                0.5f);   
        tAnimation.setInterpolator(new AccelerateInterpolator());   
        AnimationSet aa = new AnimationSet(true);   
        aa.addAnimation(tAnimation);   
        aa.setDuration(KAniInterval);   

        startAnimation(aa);
	}

	public void clear() {
		if(column2DChartArray != null){
			for(int i=0; i<column2DChartArray.size(); i++){
				Column2D b = (Column2D)column2DChartArray.get(i);
				b.clear();
			}
			column2DChartArray.clear();
			column2DChartArray = null;
		}
		if(areas != null){
			for(int i=0; i<areas.size(); i++){
				Area a = (Area)areas.elementAt(i);
				a.clear();
			}
			areas.clear();
			areas = null;
		}
		if(patternName != null){
			patternName.clear();
			patternName = null;
		}
		if(lines != null){
			for(int i=0; i<lines.size(); i++){
				Area a = (Area)lines.elementAt(i);
				a.clear();
			}
			lines.clear();
			lines = null;
		}
		if(chartData != null){
			chartData.clear();
		}
		
		if(scaleLableY != null){
			scaleLableY = null;
		}
		if(labelYView != null){
			labelYView = null;
		}
		if(labelXView != null){
			labelXView = null;
		}
		if(column2DView != null){
			column2DView = null;
		}
		if(alayout != null){
			alayout.removeAllViews();
			alayout = null;
		}
		removeAllViews();
	}
}
