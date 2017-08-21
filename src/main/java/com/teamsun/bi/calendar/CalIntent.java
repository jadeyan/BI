package com.teamsun.bi.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamsun.bi.demo.R;

public class CalIntent extends Activity implements OnClickListener{

	private CalendarView calendarView;	
	private Button btnOk;
	private Button btnCancel;
	private Button btnYearA;
	private Button btnYearR;
	private Button btnMonthA;
	private Button btnMonthR;
	private String calText;
	
	private int type;//

	TextView tvMsg1 ;
	TextView tvMsg2 ;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout mainLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.calendar_top, null);
		setContentView(mainLayout);
		btnYearA = (Button)findViewById(R.id.btnYearA);
		btnYearR = (Button)findViewById(R.id.btnYearR);
		btnMonthA = (Button)findViewById(R.id.btnMonthA);
		btnMonthR = (Button)findViewById(R.id.btnMonthR);
		btnYearA.setOnClickListener(this);
		btnYearR.setOnClickListener(this);
		btnMonthA.setOnClickListener(this);
		btnMonthR.setOnClickListener(this);
		calendarView = new CalendarView(this);
		int year = 0;
		int month = 0;
		int day = 0;
		calText = getIntent().getExtras().getString("calText");
		try{
			if(calText!= null && !"".equals(calText.trim()))
			{
				DateFormat format = new SimpleDateFormat("yyyyMMdd");
				Date d = format.parse(calText.trim());
			    year = d.getYear() + 1900;
			    month = d.getMonth();
			    day = d.getDate();
			}else{
				Calendar c = Calendar.getInstance(Locale.CHINA);
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}
		}catch(Exception e){
			Calendar c = Calendar.getInstance(Locale.CHINA);
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		}
		calendarView.ce.grid.currentYear = year;
		calendarView.ce.grid.currentMonth = month;
		calendarView.ce.grid.currentDay = day;

		type = getIntent().getExtras().getInt("dateType");
		if(type==0){//month

			tvMsg1 = (TextView)findViewById(R.id.tvMsg1);
			tvMsg2 = (TextView)findViewById(R.id.tvMsg2);
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(calendarView.ce.grid.currentYear, calendarView.ce.grid.currentMonth,
					calendarView.ce.grid.currentDay);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			String dateString = sdf.format(calendar.getTime());
			tvMsg2.setText(dateString);	
		}else{

			mainLayout.addView(calendarView);
		}
		
		LinearLayout myLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.calendar_bottom, null);
		mainLayout.addView(myLayout);
		btnOk = (Button)myLayout.findViewById(R.id.btnOk);
		btnCancel = (Button)myLayout.findViewById(R.id.btnCancel);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}


	// 更新当前日期的信息
	private void updateMsg()
	{
		String dateString = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(calendarView.ce.grid.currentYear, calendarView.ce.grid.currentMonth,
				calendarView.ce.grid.currentDay);
		dateString = sdf.format(calendar.getTime());
		tvMsg2.setText(dateString);
	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btnOk:
			String date;
			if(type == 0){
				date = tvMsg2.getText().toString();
			}else{
				date = calendarView.ce.grid.tvMsg2.getText().toString();
			}
			getIntent().putExtra("calText", date);
			setResult(1,getIntent());
			finish();
			break;
		case R.id.btnCancel:
			getIntent().putExtra("calText", calText);
			setResult(2,getIntent());
			finish();
			break;
		case R.id.btnYearA:
			calendarView.ce.grid.currentYear += 1;
			if(calendarView.ce.grid.currentMonth == 1 && calendarView.ce.grid.currentDay == 29){
				calendarView.ce.grid.currentDay = 28;
			}
			if(type == 0){
				updateMsg();
			}else{
				calendarView.invalidate();
			}
			break;
		case R.id.btnYearR:
			calendarView.ce.grid.currentYear -= 1;
			if(calendarView.ce.grid.currentMonth == 1 && calendarView.ce.grid.currentDay == 29){
				calendarView.ce.grid.currentDay = 28;
			}
			if(type==0){
				updateMsg();
			}else{
				calendarView.invalidate();
			}
			break;
		case R.id.btnMonthA:
			if(calendarView.ce.grid.currentMonth == 11){
				calendarView.ce.grid.currentYear += 1;
				calendarView.ce.grid.currentMonth = 0;
			}else{
				calendarView.ce.grid.currentMonth += 1;
			}
			if(calendarView.ce.grid.currentMonth != 1){
				if(calendarView.ce.grid.currentDay == 31){
					calendarView.ce.grid.currentDay = getMonthDays(calendarView.ce.grid.currentDay,calendarView.ce.grid.currentMonth);
			    }
		    }else{
		    	if(calendarView.ce.grid.currentDay == 29 || calendarView.ce.grid.currentDay == 30 || calendarView.ce.grid.currentDay == 31){
		    		calendarView.ce.grid.currentDay = getMonthDays(calendarView.ce.grid.currentYear,calendarView.ce.grid.currentMonth);
		    	}
		    }
			if(type == 0){
				updateMsg();
			}else{
				calendarView.invalidate();
			}
			break;
		case R.id.btnMonthR:
			if(calendarView.ce.grid.currentMonth == 0){
				calendarView.ce.grid.currentYear -= 1;
				calendarView.ce.grid.currentMonth = 11;
			}else{
				calendarView.ce.grid.currentMonth -= 1;
			}
			if(calendarView.ce.grid.currentMonth != 1){
				if(calendarView.ce.grid.currentDay == 31){
					calendarView.ce.grid.currentDay = getMonthDays(calendarView.ce.grid.currentDay,calendarView.ce.grid.currentMonth);
			    }
		    }else{
		    	if(calendarView.ce.grid.currentDay == 29 || calendarView.ce.grid.currentDay == 30 || calendarView.ce.grid.currentDay == 31){
		    		calendarView.ce.grid.currentDay = getMonthDays(calendarView.ce.grid.currentYear,calendarView.ce.grid.currentMonth);
		    	}
		    }
			if(type == 0){
				updateMsg();
			}else{
				calendarView.invalidate();
			}
		
			break;
		}
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		calendarView.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
}
