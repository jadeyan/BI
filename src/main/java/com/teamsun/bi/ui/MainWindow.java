package com.teamsun.bi.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamsun.bi.demo.R;
import com.teamsun.bi.model.KPIDataManager;
import com.teamsun.bi.model.PerArearKPI;

@SuppressWarnings("deprecation")
public class MainWindow {

	public Context context;

	// private ScrollView wholeLayout;

	protected LinearLayout midLayout;

	protected LinearLayout titleBar;

	protected Button backButton;

	private String title = "";

	TextView dateView;
	
	DateView custom;
	
	RelativeLayout menuBar;
	
	PopupWindow mPopupWindow;
	
	public Button arearBtn;
	
	String[] arearNames1;

	public KPIItemList itemList;
	
	public KPIChartPage chartPage;

	UiControl ui;
	
	public PerArearKPI curArear;
	
	
	public MainWindow(final Context context, final UiControl ui) {
		this.context = context;
		this.ui = ui;
		midLayout = new LinearLayout(context);
		midLayout.setOrientation(LinearLayout.VERTICAL);
		midLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		midLayout.setBackgroundColor(0xffEEF2F9);

		
		titleBar = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.title_bar, null);
		backButton = (Button)titleBar.findViewById(R.id.backup);
		backButton.setVisibility(View.INVISIBLE);
		backButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ui.doBackup();
			}});
		
		TextView tv = (TextView) titleBar.findViewById(R.id.title);
		tv.setText(title);
		

		final String dateTime = KPIDataManager.getInstance().getDateTime();
		dateView = (TextView) titleBar.findViewById(R.id.date);
		dateView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//暂时注释此处代码
//				String dateTime = dateView.getText().toString();
//				if(dateTime == null)
//					dateTime = KPIDataManager.getInstance().getDateTime();
//				if(dateTime.indexOf("-")>-1){
//					dateTime = dateTime.replaceAll("-", "");
//				}
//				showCalendar(context, dateTime, dateTime.length() == 6?
//							MyDatePickerDialog.model_month:MyDatePickerDialog.model_day);
			}
			});
		int model = 0;
		if (dateTime != null){
			dateView.setText(dateTime);
			model = (dateTime.length() == 8)?MyDatePickerDialog.model_day:MyDatePickerDialog.model_month;
		}
		custom = (DateView)titleBar.findViewById(R.id.customDateView);
		custom.setModel(model);
		midLayout.addView(titleBar);

		
		menuBar = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.menu_bar, null);
		arearBtn = (Button) menuBar.findViewById(R.id.arear);
		arearBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (mPopupWindow == null || !mPopupWindow.isShowing())
//					popMenuView();
//				else if (mPopupWindow.isShowing())
//					mPopupWindow.dismiss();
				ui.context.showDialog(ui.context.DIALOG_AREAR);
			}
		});
		
		Button logout = (Button)menuBar.findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ui.logout();
			}
		});
		midLayout.addView(menuBar);
	}

	public void showCalendar(final Context context,
			final String dateTime, final int dateModel) {
		int year = 0;
		int month = 0;
		int day = 0;
		if(dateTime != null){
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			if(dateTime.length() == 6)
				format = new SimpleDateFormat("yyyyMM");
			Date d = null;
			try {
				d = format.parse(dateTime);
				year = d.getYear() + 1900;
				month = d.getMonth();
				day = d.getDate();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			Calendar c = Calendar.getInstance(Locale.CHINA);
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		}
		DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				String date = null;
				String d_tmp = ""+dayOfMonth;
				if(dayOfMonth < 10)
					d_tmp = "0"+dayOfMonth;
				monthOfYear++;
				String m_tmp = ""+(monthOfYear);
				if(monthOfYear < 10)
					m_tmp = "0"+monthOfYear;
				if (dateModel == MyDatePickerDialog.model_day)
					date = new String(year+ m_tmp
							+ d_tmp);
				else if (dateModel == MyDatePickerDialog.model_month)
					date = new String(year + m_tmp);
				if(!dateTime.equals(date))
					ui.changeDate(date);
			}
		};
		MyDatePickerDialog dia = new MyDatePickerDialog(context, d, year, 
				month, day, dateModel);
		dia.show();
		if(dateModel == MyDatePickerDialog.model_month){
			DatePicker dp = findDatePicker((ViewGroup) dia.getWindow().getDecorView());
	        if (dp != null) {
	            ((ViewGroup) dp.getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
	            
	        }
	    } 
	}
	
	/**
     * 从当前Dialog中查找DatePicker子控件
     * 
     * @param group
     * @return
     */
    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;

    } 
	public void popMenuView() {
		ArearGrid grid = new ArearGrid(context, arearNames1, this);
		mPopupWindow = new PopupWindow(grid.getView(),
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		grid.getView().setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mPopupWindow.isShowing())
						mPopupWindow.dismiss();
					return true;
				} else {
					return false;
				}
			}
		});

		mPopupWindow.showAtLocation(midLayout, Gravity.CENTER | Gravity.BOTTOM,
				0, menuBar.getHeight());
		// mPopupWindow.setAnimationStyle(android.R.style.PopupAnimation);
		mPopupWindow.setOutsideTouchable(true);
		// mPopupWindow.setTouchable(true);
		// mPopupWindow.set
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();

	}

	public void updateArearStr(String arearStr) {
		arearBtn.setText(arearStr);
		arearBtn.postInvalidate();
		this.arearNames1 = KPIDataManager.getInstance().getArearStrs();
	}
	
	public void setTitle(String title){
		TextView tv = (TextView) titleBar.findViewById(R.id.title);
		tv.setText(title);
	}

	public void changeDateView(String date) {
		if(date != null && date.indexOf("-") < 0){
			StringBuffer sbf = new StringBuffer(date);
			if(date.length() == 8){
				sbf.insert(6, '-');
			}
			sbf.insert(4, '-');
 			date = sbf.toString();
		}
		if(date != null)
			KPIDataManager.getInstance().setDateTime(date);
		this.dateView.setText(date);
		if(date.length() >= 10)
			custom.setModel(DateView.MODEL_DAY);
		else if(date.length()<= 7)
			custom.setModel(DateView.MODEL_MONTH);
		dateView.postInvalidate();
		
	}

	public void setFramePage(ViewGroup framePage) {
		if (midLayout.getChildCount() > 2)
			midLayout.removeViewAt(1);
		// layout.weight = 25;
		framePage.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, 1));
		midLayout.addView(framePage, 1);
		midLayout.postInvalidate();
	}

	public ViewGroup getView() {
		return midLayout;
	}

	public void closePopupWindow(String arear) {
		mPopupWindow.dismiss();
		CharSequence text = arearBtn.getText();
		if(!text.toString().equals(arear)){
			PerArearKPI per = KPIDataManager.getInstance().getPerArearByStr(arear);
			ui.updataList(per);
		}
	}

	public boolean  onKeyBack() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			return true;
		} else {
			return false;
		}
	}
	
	public void showErrorPage(){
		
	}
}
