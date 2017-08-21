package com.teamsun.bi.ui;

import com.teamsun.bi.demo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton1 extends Button{

	public CustomButton1(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundResource(R.drawable.btn_bg);
	}

}
