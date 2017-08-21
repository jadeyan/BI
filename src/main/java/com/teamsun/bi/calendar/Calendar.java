package com.teamsun.bi.calendar;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.View;

public class Calendar extends CalendarParent 
{
	private ArrayList<CalendarElement> elements = new ArrayList<CalendarElement>();
    public Grid grid;
	public Calendar(Activity activity, View view)
	{	
		super(activity,view);
		elements.add(new Border(activity, view));//设置日历边框的绘制数据
		elements.add(new Week(activity, view));//设置日历内容中周名（日，一，二。。。六）
		grid = new Grid(activity, view);//设置日历内容中日期文字的绘制数据
		elements.add(grid);
	}

	@Override
	public void draw(Canvas canvas)
	{
		for (CalendarElement ce : elements)//绘制日历中所有的直线和文字
			ce.draw(canvas);
	}

}
