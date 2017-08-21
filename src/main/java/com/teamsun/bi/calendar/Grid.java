package com.teamsun.bi.calendar;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import com.teamsun.bi.demo.R;

public class Grid extends CalendarParent implements Serializable
{

	private String[] days = new String[42];
	public String[] monthNames = new String[12];
	private TextView tvMsg1;
	public TextView tvMsg2;
	private int dayColor;
	private int innerGridColor;
	private int prevNextMonthDayColor;
	private int currentDayColor;
	private int todayColor;
	private int todayBackgroundColor;
	private int sundaySaturdayPrevNextMonthDayColor;
	private float daySize;
	private float dayTopOffset;
	private float currentDaySize;
	private float cellX = -1, cellY = -1;

	// 从0开始
	private int currentRow, currentCol;
	private boolean redrawForKeyDown = false;

	// 当前年和月
	public int currentYear, currentMonth;
	// 上月或下月选中的天
	public int currentDay = -1, currentDay1 = -1, currentDayIndex = -1;
	private java.util.Calendar calendar = java.util.Calendar.getInstance();

	public void setCurrentRow(int currentRow)
	{
		if (currentRow < 0)//如果当前行小于0，则跳到上月或上一年的最后一个月
		{
			currentMonth--;
			if (currentMonth == -1)
			{
				currentMonth = 11;
				currentYear--;
			}
			currentDay = getMonthDays(currentYear, currentMonth) + currentDay
					- 7;
			currentDay1 = currentDay;
			cellX = -1;
			cellY = -1;
			view.invalidate();
			return;

		}
		else if (currentRow > 5)//如果当前行大于5，跳到下一月或下一年的第一月
		{
			int n = 0;
			for (int i = 35; i < days.length; i++)
			{
				if (!days[i].startsWith("*"))
					n++;
				else
					break;
			}
			currentDay = 7 - n + currentCol + 1;
			currentDay1 = currentDay;
			currentMonth++;
			if (currentMonth == 12)
			{
				currentMonth = 0;
				currentYear++;
			}
			cellX = -1;
			cellY = -1;
			view.invalidate();
			return;
		}
		this.currentRow = currentRow;
		redrawForKeyDown = true;
		view.invalidate();
	}

	public void setCurrentCol(int currentCol)
	{
		if (currentCol < 0)//当前列小于0
		{
			if (currentRow == 0)//日期跳到上月或上一年的最后一个月
			{

				currentMonth--;

				if (currentMonth == -1)
				{
					currentMonth = 11;
					currentYear--;
				}
				currentDay = getMonthDays(currentYear, currentMonth);
				currentDay1 = currentDay;
				cellX = -1;
				cellY = -1;
				view.invalidate();
				return;
			}

			else
			{
				currentCol = 6;
				setCurrentRow(--currentRow);

			}
		}
		else if (currentCol > 6)//当前列大于6，行数增1，列变成0
		{
			currentCol = 0;
			setCurrentRow(++currentRow);

		}
		this.currentCol = currentCol;
		redrawForKeyDown = true;
		view.invalidate();
	}

	public int getCurrentRow()
	{
		return currentRow;
	}

	public int getCurrentCol()
	{
		return currentCol;
	}

	public void setCellX(float cellX)
	{

		this.cellX = cellX;
	}

	public void setCellY(float cellY)
	{

		this.cellY = cellY;
	}

	private int getMonthDays(int year, int month)
	{
		month++;
		switch (month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
			{
				return 31;
			}
			case 4:
			case 6:
			case 9:
			case 11:
			{
				return 30;
			}
			case 2:
			{
				if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
					return 29;
				else
					return 28;
			}
		}
		return 0;
	}

	private void calculateDays()
	{
		calendar.set(currentYear, currentMonth, 1);

		int week = calendar.get(calendar.DAY_OF_WEEK);
		int monthDays = 0;
		int prevMonthDays = 0;

		monthDays = getMonthDays(currentYear, currentMonth);
		if (currentMonth == 0)
			prevMonthDays = getMonthDays(currentYear - 1, 11);
		else
			prevMonthDays = getMonthDays(currentYear, currentMonth - 1);

		for (int i = week, day = prevMonthDays; i > 1; i--, day--)
		{
			days[i - 2] = "*" + String.valueOf(day);//生成上月分配到当前月的日期文字。（前面加星号，在显示时去掉星号）
		}
		for (int day = 1, i = week - 1; day <= monthDays; day++, i++)
		{
			days[i] = String.valueOf(day);
			if (day == currentDay)
			{
				currentDayIndex = i;

			}
		}
		for (int i = week + monthDays - 1, day = 1; i < days.length; i++, day++)
		{
			days[i] = "*" + String.valueOf(day);//生成下月分配到当前月的日期文字。（前面加星号，在显示时去掉星号）
		}

	}

	public Grid(Activity activity, View view)
	{
		super(activity, view);
		tvMsg1 = (TextView) activity.findViewById(R.id.tvMsg1);
		tvMsg2 = (TextView) activity.findViewById(R.id.tvMsg2);
		dayColor = activity.getResources().getColor(R.color.day_color);//日期文本的颜色(白色)
		todayColor = activity.getResources().getColor(R.color.today_color);//今天的日期文本颜色(白色)
		todayBackgroundColor = activity.getResources().getColor(
				R.color.today_background_color);//今天的日期文本边框颜色(红色)
		innerGridColor = activity.getResources().getColor(
				R.color.inner_grid_color);//日历网格线颜色(白色)
		prevNextMonthDayColor = activity.getResources().getColor(
				R.color.prev_next_month_day_color);//上月和下月日期的文字颜色(灰色)
		currentDayColor = activity.getResources().getColor(
				R.color.current_day_color);//当前日期的文字颜色（白色）
		sundaySaturdayPrevNextMonthDayColor = activity.getResources().getColor(
				R.color.sunday_saturday_prev_next_month_day_color);//周六，日文字颜色（暗红色)
		daySize = activity.getResources().getDimension(R.dimen.day_size);//日期字体尺寸
		dayTopOffset = activity.getResources().getDimension(
				R.dimen.day_top_offset);//日期文字距当前网格顶端的偏移量，用于微调日期文字的位置
		currentDaySize = activity.getResources().getDimension(
				R.dimen.current_day_size);//当前日期文字尺寸
		monthNames = activity.getResources().getStringArray(R.array.month_name);
		paint.setColor(activity.getResources().getColor(R.color.border_color));

		currentYear = calendar.get(calendar.YEAR);
		currentMonth = calendar.get(calendar.MONTH);
	}

	private boolean isCurrentDay(int dayIndex, int currentDayIndex,
			Rect cellRect)
	{
		boolean result = false;
		if (redrawForKeyDown == true)
		{
			result = dayIndex == (7 * ((currentRow > 0) ? currentRow : 0) + currentCol);
			if (result)
				redrawForKeyDown = false;

		}
		else if (cellX != -1 && cellY != -1)
		{
			if (cellX >= cellRect.left && cellX <= cellRect.right
					&& cellY >= cellRect.top && cellY <= cellRect.bottom)
			{
				result = true;
			}
			else
			{
				result = false;
			}
		}
		else
		{
			result = (dayIndex == currentDayIndex);

		}
		if (result)
		{
			if (currentRow > 0 && currentRow < 6)
			{
				currentDay1 = currentDay;

			}
			currentDayIndex = -1;
			cellX = -1;
			cellY = -1;

		}
		return result;
	}

	// 更新当前日期的信息
	private void updateMsg(boolean today)
	{
		String monthName = monthNames[currentMonth];
		String dateString = "";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(currentYear, currentMonth, currentDay);
		dateString = sdf.format(calendar.getTime());
		monthName += "   本月第" + calendar.get(java.util.Calendar.WEEK_OF_MONTH)
				+ "周";
		tvMsg1.setText(monthName);
//		if (today)
//			dateString += "(今天)";
//		dateString += "   本年第" + calendar.get(java.util.Calendar.WEEK_OF_YEAR)
//				+ "周";
		tvMsg2.setText(dateString);
	}

	public boolean inBoundary()
	{
		if (cellX < borderMargin
				|| cellX > (view.getMeasuredWidth() - borderMargin)
				|| cellY < top
				|| cellY > (view.getMeasuredHeight() - borderMargin))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	float top, left;

	@Override
	public void draw(Canvas canvas)
	{
		left = borderMargin;
		top = borderMargin + weekNameSize + weekNameMargin * 2 + 4;
		float calendarWidth = view.getMeasuredWidth() - left * 2;
		float calendarHeight = view.getMeasuredHeight() - top - borderMargin;
		float cellWidth = calendarWidth / 7;
		float cellHeight = calendarHeight / 6;
		paint.setColor(innerGridColor);
		canvas.drawLine(left, top, left + view.getMeasuredWidth()
				- borderMargin * 2, top, paint);//绘制日历网格最顶端的直线
		// 画横线
		for (int i = 1; i < 6; i++)
		{
			 canvas.drawLine(left, top + (cellHeight) * i, left +
			 calendarWidth,
			 top + (cellHeight) * i, paint);
		}
		// 画竖线
		for (int i = 1; i < 7; i++)
		{
			 canvas.drawLine(left + cellWidth * i, top, left + cellWidth * i,
			 view.getMeasuredHeight() - borderMargin, paint);
		}

		// 画日期

		calculateDays();//获得当前日期的天

		java.util.Calendar calendar = java.util.Calendar.getInstance();
		int day = calendar.get(calendar.DATE);
		int myYear = calendar.get(calendar.YEAR), myMonth = calendar
				.get(calendar.MONTH);

		calendar.set(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH),
				1);
		int week = calendar.get(calendar.DAY_OF_WEEK);
		int todayIndex = week + day - 2;
		boolean today = false;
		if (currentDayIndex == -1)
		{
			currentDayIndex = todayIndex;

		}
		boolean flag = false;
		for (int i = 0; i < days.length; i++)
		{
			today = false;
			int row = i / 7;
			int col = i % 7;
			String text = String.valueOf(days[i]);
			if ((i % 7 == 0 || (i - 6) % 7 == 0) && text.startsWith("*"))
			{
				paint.setColor(sundaySaturdayPrevNextMonthDayColor);
			}
			else if (i % 7 == 0 || (i - 6) % 7 == 0)
			{
				paint.setColor(sundaySaturdayColor);
			}
			else if (text.startsWith("*"))
			{
				paint.setColor(prevNextMonthDayColor);
			}
			else
			{
				paint.setColor(dayColor);
			}
			text = text.startsWith("*") ? text.substring(1) : text;

			Rect dst = new Rect();
			dst.left = (int) (left + cellWidth * col);
			dst.top = (int) (top + cellHeight * row);
			dst.bottom = (int) (dst.top + cellHeight + 1);
			dst.right = (int) (dst.left + cellWidth + 1);
			String myText = text;
			paint.setTextSize(daySize);
			float textLeft = left + cellWidth * col
					+ (cellWidth - paint.measureText(myText)) / 2;
			float textTop = top + cellHeight * row
					+ (cellHeight - paint.getTextSize()) / 2 + dayTopOffset;
			if (myYear == currentYear && myMonth == currentMonth
					&& i == todayIndex)//当前日期是今天，在日期文字周围绘制边框
			{
				paint.setTextSize(currentDaySize);
				paint.setColor(todayBackgroundColor);
				dst.left += 1;
				dst.top += 1;
				canvas.drawLine(dst.left, dst.top, dst.right, dst.top, paint);
				canvas.drawLine(dst.right, dst.top, dst.right, dst.bottom,
						paint);
				canvas.drawLine(dst.right, dst.bottom, dst.left, dst.bottom,
						paint);
				canvas.drawLine(dst.left, dst.bottom, dst.left, dst.top, paint);

				paint.setColor(todayColor);
				today = true;
			}

			if (isCurrentDay(i, currentDayIndex, dst) && flag == false)//当单击当前月中显示的上月或下月日期时，自动显示上月或下月的日历
			{
				if (days[i].startsWith("*"))
				{
					// 下月
					if (i > 20)
					{
						currentMonth++;
						if (currentMonth == 12)
						{
							currentMonth = 0;
							currentYear++;
						}

						view.invalidate();//刷新当前日历，重新显示下月的日历。

					}
					// 上月
					else
					{
						currentMonth--;
						if (currentMonth == -1)
						{
							currentMonth = 11;
							currentYear--;
						}
						view.invalidate();//刷新当前日历，重新显示上月的日历。

					}
					currentDay = Integer.parseInt(text);
					currentDay1 = currentDay;
					cellX = -1;
					cellY = -1;
					break;

				}
				else//如果单击的不是上月或下月的日期，则在当前日期上显示一个背景图，并将日期文字设成红色
				{
					paint.setTextSize(currentDaySize);
					flag = true;
					Bitmap bitmap = BitmapFactory.decodeResource(activity
							.getResources(), R.drawable.day);
					Rect src = new Rect();
					src.left = 0;
					src.top = 0;
					src.right = bitmap.getWidth();
					src.bottom = bitmap.getHeight();
					canvas.drawBitmap(bitmap, src, dst, paint);
					paint.setColor(currentDayColor);
					currentCol = col;
					currentRow = row;
					currentDay = Integer.parseInt(text);
					currentDay1 = currentDay;
					updateMsg(today);

				}
			}

			canvas.drawText(myText, textLeft, textTop, paint);

		}

	}

}
