package com.teamsun.bi.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class MyDatePickerDialog extends DatePickerDialog{
	
	public static final int model_day = 1, model_month = 0; 
	
	int model;
	public MyDatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth, int model) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		this.model = model;

        if(model == model_day)
        	setTitle(year+ "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
        else if(model == model_month)
        	setTitle(year+ "年" + (monthOfYear+1) + "月");
	}

	@Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        if(model == model_day)
        	setTitle(year+ "年" + (month + 1) + "月" + day + "日");
        else if(model == model_month)
        	setTitle(year+ "年" + (month+1) + "月");
    }
}
