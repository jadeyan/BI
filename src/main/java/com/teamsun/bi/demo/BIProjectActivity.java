package com.teamsun.bi.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.teamsun.bi.entry.Common;
import com.teamsun.bi.http.response.LoginResponse;
import com.teamsun.bi.model.KPIData;
import com.teamsun.bi.model.KPIDataManager;
import com.teamsun.bi.model.KPIDataProperty;
import com.teamsun.bi.model.PerArearKPI;
import com.teamsun.bi.ui.LoadingPage;
import com.teamsun.bi.ui.MainWindow;
import com.teamsun.bi.ui.UiControl;

public class BIProjectActivity extends Activity {

	private static BIProjectActivity instance;

	public Handler handler = new Handler();

	MainWindow window;

	public String userName, password;

	public UiControl uiControl;

	public final int DIALOG_HTTP = 0;

	public final int DIALOG_ERROR = 1;

	public final int DIALOG_QUIT = 2;

	public final int DIALOG_AREAR = 3;

	public ProgressDialog dialog;

	public String errorInfo;

	public String serverIp = "60.247.68.10:8090";//"221.130.253.137:8071";

	// public

	public static BIProjectActivity getInstance() {
		return instance;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		instance = this;

		LoadingPage lp = new LoadingPage(this, handler);
		setContentView(lp);
		Thread t = new Thread(lp);
		t.start();

		uiControl = new UiControl(this);
	}

	PopupWindow mPopup;

	public void showLoginPage() {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View main = inflater.inflate(R.layout.main, null);
		setContentView(main);
		String n = Common.getSavedInfo(Common.preferStr, "userName");
		String p = Common.getSavedInfo(Common.preferStr, "password");
		boolean ch = Common.getSavedInfo2(Common.preferStr, "isSave");
		final EditText et = (EditText) this.findViewById(R.id.login_name);
		et.setText(n);
		final EditText et1 = (EditText) this.findViewById(R.id.login_password);
		final CheckBox isSave = (CheckBox) findViewById(R.id.save_password);
		if (ch) {
			isSave.setChecked(ch);
			et1.setText(p);
		}
		Button b = (Button) this.findViewById(R.id.login);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				userName = et.getText().toString();
//				if (userName == null || userName.trim().equals("")) {
//					Toast mToast = Toast.makeText(BIProjectActivity.this,
//							"用户名为空", Toast.LENGTH_LONG);
//					mToast.show();
//					return;
//				}
//
//				password = et1.getText().toString();
//
//				boolean save = isSave.isChecked();
//
//				Common.saveMess(Common.preferStr, "userName", userName);
//				if (save) {
//					Common.saveMess(Common.preferStr, "password", password);
//				}
//				Common.saveMess2(Common.preferStr, "isSave", save);
//				login();
				// test();
				rangTestData();
				uiControl.showWindow();
			}
		});
		b = (Button) this.findViewById(R.id.setting);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(mPopup != null && mPopup.isShowing())
					return;
				else{
					LayoutInflater inflater = LayoutInflater.from(BIProjectActivity.this);
					View v = inflater.inflate(R.layout.text_input, null);
					mPopup = new PopupWindow(v,
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					mPopup.showAtLocation(main, Gravity.CENTER, 0, 0);
					mPopup.setFocusable(true);
					mPopup.update();
					final EditText et = (EditText)v.findViewById(R.id.editText1);
//					et.setOnClickListener(new OnClickListener(){
//
//						@Override
//						public void onClick(View v) {
//							InputMethodManager imm = (InputMethodManager)BIProjectActivity.this.getSystemService(INPUT_METHOD_SERVICE);
//
//							imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//
//
//						}});
					et.setText(serverIp);
					Button b = (Button)v.findViewById(R.id.ok);
					b.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							serverIp = et.getText().toString();
							mPopup.dismiss();
						}});
					b = (Button)v.findViewById(R.id.cancel);
					b.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							mPopup.dismiss();
						}});
				}
			}
		});
	}

	private void login() {
		LoginResponse loginR = new LoginResponse(handler, uiControl);
		Thread t = new Thread(loginR);
		t.start();
	}

	private void rangTestData(){
		KPIDataManager manager = KPIDataManager.getInstance();
		manager.setCapTitle("移动集团报表");
		manager.setDateTime("2015-06-01");
		manager.setDefOrgCd("bj");

		PerArearKPI arear = new PerArearKPI("北京", "bj");
		arear.setCurDate(manager.getDateTime());
		manager.addArearStr("北京");
		manager.addArear(arear);
		KPIDataProperty prop = new KPIDataProperty("ysz", "万元", "本月应收账款");
		manager.addProperty(prop);

		KPIData data = new KPIData("ysz", "504.00", "10.0%", "15%", "1800.00");
		data.setProperty(prop);
		arear.addData(data);

		prop = new KPIDataProperty("tysz", "万元", "全年应收账款");
		manager.addProperty(prop);
		data = new KPIData("tysz", "5430.00", "10.0%", "15%", "98000.00");
		data.setProperty(prop);
		arear.addData(data);

		prop = new KPIDataProperty("nqxf", "元", "内勤消费");
		manager.addProperty(prop);
		data = new KPIData("nqxf", "150430.00", "-10.0%", "15%", "6300000.00");
		data.setProperty(prop);
		arear.addData(data);

		prop = new KPIDataProperty("ysz", "元", "金融部门支出");
		manager.addProperty(prop);
		data = new KPIData("ysz", "15043.00", "-7.00%", "35%", "98000.00");
		data.setProperty(prop);
		arear.addData(data);
	}

	DialogInterface.OnClickListener listener;

	OnCancelListener cancelListener;

	public void setCancelListener(DialogInterface.OnCancelListener listener) {
		cancelListener = listener;
	}

	public void setClickListener(DialogInterface.OnClickListener listener) {
		this.listener = listener;
	}

	public Dialog onCreateDialog(int id) {
		switch (id) {

		case DIALOG_HTTP: {
			dialog = new ProgressDialog(this) {

				@Override
				public boolean onKeyDown(int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							|| keyCode == KeyEvent.KEYCODE_SEARCH)
						return true;
					return super.onKeyDown(keyCode, event);
				}

			};
			dialog.setTitle("请稍等");
			dialog.setMessage("数据加载中");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.setButton2("取消", listener);
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					Log.e("cancel", "onCancel");
				}
			});
			return dialog;
		}
		case DIALOG_ERROR: {
			return new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage(errorInfo)
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							BIProjectActivity.this.removeDialog(DIALOG_ERROR);
						}
					})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									BIProjectActivity.this
											.removeDialog(DIALOG_ERROR);
								}
							}).create();
		}
		case DIALOG_QUIT: {
			return new AlertDialog.Builder(this)
					.setMessage("将要退出程序？")
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							BIProjectActivity.this.removeDialog(DIALOG_ERROR);
						}
					})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									BIProjectActivity.this
											.dismissDialog(DIALOG_QUIT);
									quit();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									BIProjectActivity.this
											.dismissDialog(DIALOG_QUIT);
								}
							}).create();
		}
		case DIALOG_AREAR: {
			return new AlertDialog.Builder(this)
					.setTitle("")
					.setItems(uiControl.getArears(),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									/* User clicked so do some stuff */
									String item = uiControl.getArears()[which];
									CharSequence text = uiControl.window.arearBtn
											.getText();
									if (!text.toString().equals(item)) {
										PerArearKPI per = KPIDataManager
												.getInstance()
												.getPerArearByStr(item);
										uiControl.updataList(per);
									}
								}
							}).create();
		}
		default:
			return null;
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		//
		// } else if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_PORTRAIT) {
		//
		// }5280522911015887
	}

	int MOA_CALENDAR_REQUEST = 0;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MOA_CALENDAR_REQUEST) {
			if (resultCode == 1) {
				String calText = data.getStringExtra("calText");
				uiControl.changeDate(calText);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			uiControl.doBackup();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void quit() {
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}