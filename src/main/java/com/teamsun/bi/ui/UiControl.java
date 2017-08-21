package com.teamsun.bi.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.demo.R;
import com.teamsun.bi.entry.Common;
import com.teamsun.bi.http.response.KPIChartResponse;
import com.teamsun.bi.http.response.RefreshKPIResponse;
import com.teamsun.bi.model.ChartData;
import com.teamsun.bi.model.KPIData;
import com.teamsun.bi.model.KPIDataManager;
import com.teamsun.bi.model.PerArearKPI;
import com.teamsun.bi.model.PulseChartData;

public class UiControl {
	
	public BIProjectActivity context;
	
	public MainWindow window;
	
	public boolean isConnecting;
	
	HashMap<Integer, Bitmap> resources;
	
	int curLevel;//当前第几级页面，是否需要返回按钮
	
	public UiControl(BIProjectActivity context){
		this.context = context;
	}
	
	public Bitmap getResource(int id){
		if(resources != null && resources.containsKey(new Integer(id))){
			return resources.get(id);
		}
		return null;
	}
	
	private void loadResources(){
		if(resources == null)
			resources = new HashMap<Integer, Bitmap>();
		else
			resources.clear();
		int id = R.drawable.pic00;
		Resources res = context.getResources();
		Bitmap b = Common.loadResource(res, id);
		resources.put(new Integer(id), b);
		
		id = R.drawable.pic01;
		b = Common.loadResource(res, id);
		resources.put(new Integer(id), b);
		
		id = R.drawable.column;
		b = Common.loadResource(res, id);
		resources.put(new Integer(id), b);
		
		id = R.drawable.kpi_item_background;
		b = Common.loadResource(res, id);
		resources.put(new Integer(id), b);
		
		id = R.drawable.bg;
		b = Common.loadResource(res, id);
		resources.put(new Integer(id), b);
	}

	public void showWindow(){
		window = new MainWindow(context, this);
		context.setContentView(window.getView());
		KPIDataManager manager = KPIDataManager.getInstance();
		PerArearKPI  a = manager.getPerArear(manager.getDefOrgCd());
		if(a == null){
			a = manager.getArearByIndex(0);
		}
		if(a != null){
			ArrayList<KPIData> data = a.getDatas();
			if(window.itemList == null)
				window.itemList = new KPIItemList(context, data, this);
			ViewGroup layout = window.itemList.getView();
			window.setFramePage(layout);
			window.curArear = a;
			window.updateArearStr(a.getOrgName());
			window.changeDateView(window.curArear.getCurDate());
		}
		window.setTitle(manager.getCapTitle());
		curLevel = 0;
	}
	
	public void updataList(PerArearKPI arear){
		if(arear != null && window.itemList != null){
			ArrayList<KPIData> data = arear.getDatas();
			window.itemList.setData(data);
			window.curArear = arear;
			window.updateArearStr(arear.getOrgName());
			if(this.curLevel == 1){
				window.setFramePage(window.itemList.getView());
				curLevel = 0;
			}
			String dateTime = window.curArear.getCurDate();
			window.changeDateView(dateTime);
		}
	}

	public void showPulse(PulseChartData pulse) {
		window.chartPage.addPulseView(pulse);
		window.getView().postInvalidate();
	}
	
	public void showErrorPulse(String mess){
		window.chartPage.addErrorPulseView(mess);

		window.getView().postInvalidate();
	}
	
	public void showLine(ChartData data){
		if(window.chartPage != null)
			window.chartPage.addChartView(data);
		window.getView().postInvalidate();
	}

	public void showSecondLevelPage(String metricId) {
		if(resources == null)
			this.loadResources();
		KPIChartPage page = null;
		if(window.chartPage != null){
			page = window.chartPage;
		}
		window.chartPage = new KPIChartPage(context);
		window.chartPage.curMetricId = metricId;
		window.backButton.setVisibility(View.VISIBLE);
//		window.arearBtn.setVisibility(View.INVISIBLE);
		ViewGroup vg = window.chartPage.getView();
		window.setFramePage(vg);
		curLevel = 1;
		
		if(page != null){
			final KPIChartPage p = page;
			Thread t = new Thread(){
				public void run(){
					p.clear();
				}
			};
			t.start();
			page = null;
		}	
	}

	public void doBackup() {
		if(window == null){
			context.showDialog(context.DIALOG_QUIT);
		}else{
			boolean b = window.onKeyBack();
			if(!b){
				if (curLevel == 1) {
					KPIDataManager manager = KPIDataManager.getInstance();
					if (window.itemList == null) {
						PerArearKPI a = manager.getPerArear(manager.getDefOrgCd());
						if (a == null) {
							a = manager.getArearByIndex(0);
						}
						if (a != null) {
							ArrayList<KPIData> data = a.getDatas();
							window.itemList = new KPIItemList(context, data, this);
						}
						window.curArear = a;
						window.updateArearStr(a.getOrgName());
					}
					ViewGroup layout = window.itemList.getView();
					window.setFramePage(layout);
					

					if(window.chartPage != null){
						Thread t = new Thread(){
							public void run(){

								window.chartPage.clear();
								window.chartPage = null;		
							}
						};
						t.start();
					}
					
					window.setTitle(manager.getCapTitle());
					window.backButton.setVisibility(View.INVISIBLE);
					String curDate = window.curArear.getCurDate();
					window.changeDateView(curDate);
//					window.arearBtn.setVisibility(View.VISIBLE);
					curLevel = 0;
				}else if(curLevel == 0){
					context.showDialog(context.DIALOG_QUIT);
				}
			}
		}
	}

	public void requestChartPage(KPIData kpi) {
//		KPIChartResponse chart = new KPIChartResponse(context.handler, this, kpi.metricId,
//				window.curArear.getCurDate(),window.curArear.getOrgId());
//		Thread t = new Thread(chart);
//		t.start();
		addKPIChartData();
	}

	private void addKPIChartData(){
		showSecondLevelPage("ysz");
//		ui.window.changeDateView(v_time);
		PulseChartData pulse = new PulseChartData("饼图", "ysz", "万元", "2015-06-02", "北京", "bj");
		pulse.addValue("0", "#FFFFFF");
		pulse.addValue("100", "#FFFF00");
		pulse.curVar = 70;
		pulse.setMaxValue();
		showPulse(pulse);

		ChartData chart = new ChartData("", "ysz", "2015-06-01", "万元", "北京", "bj");
		chart.appendLableX("Jan");
		chart.appendLableX("Feb");
		chart.appendLableX("Mar");
		chart.appendLableX("Apr");
		chart.appendLableX("May");
		chart.appendLableX("Jun");
		chart.appendLableX("Jul");
		chart.appendLableX("Aug");
		chart.appendLableX("Spr");
		chart.appendLableX("Oct");
		chart.appendLableX("Nov");
		chart.appendLableX("Dec");

		chart.addLineData("income");
		chart.addSingleData((float) 100.0);
		chart.addSingleData((float) 80.0);
		chart.addSingleData((float)90.0);
		chart.addSingleData((float) 110.0);
		chart.addSingleData((float)113.0);
		chart.addSingleData((float)150.0);
		chart.addSingleData((float)96.0);
		chart.addSingleData((float)88.0);
		chart.addSingleData((float)110.0);
		chart.addSingleData((float)115.0);
		chart.addSingleData((float)125.0);
		chart.addSingleData((float)95.0);
		chart.addLineData("outcome");
		chart.addSingleData((float) 30.0);
		chart.addSingleData((float) 10.0);
		chart.addSingleData((float) 45.0);
		chart.addSingleData((float)22.0);
		chart.addSingleData((float)30.0);
		chart.addSingleData((float)35.0);
		chart.addSingleData((float)55.0);
		chart.addSingleData((float)50.0);
		chart.addSingleData((float)48.0);
		chart.addSingleData((float)65.0);
		chart.addSingleData((float)70.0);
		chart.addSingleData((float)75.0);

		chart.setCurDataId("Feb");

		showLine(chart);

		chart = new ChartData("", "ysz", "2015-06-01","万元", "北京", "bj");
		chart.appendLableX("北京");
		chart.appendLableX("上海");
		chart.appendLableX("山西");
		chart.appendLableX("河北");
		chart.appendLableX("内蒙古");
		chart.appendLableX("黑龙江");
		chart.appendLableX("海南");
		chart.appendLableX("江苏");
		chart.appendLableX("安徽");
		chart.appendLableX("四川");
		chart.appendLableX("浙江");
		chart.appendLableX("广州");

		chart.addColumnDataSet("Income");
		chart.addSingleData((float) 100.0);
		chart.addSingleData((float) 80.0);
		chart.addSingleData((float)90.0);
		chart.addSingleData((float) 110.0);
		chart.addSingleData((float)113.0);
		chart.addSingleData((float)150.0);
		chart.addSingleData((float)96.0);
		chart.addSingleData((float)88.0);
		chart.addSingleData((float)110.0);
		chart.addSingleData((float)115.0);
		chart.addSingleData((float)125.0);
		chart.addSingleData((float)95.0);
		chart.addColumnDataSet("outcome");
		chart.addSingleData((float) 30.0);
		chart.addSingleData((float) 10.0);
		chart.addSingleData((float) 45.0);
		chart.addSingleData((float)22.0);
		chart.addSingleData((float)30.0);
		chart.addSingleData((float)35.0);
		chart.addSingleData((float)55.0);
		chart.addSingleData((float)50.0);
		chart.addSingleData((float)48.0);
		chart.addSingleData((float)65.0);
		chart.addSingleData((float)70.0);
		chart.addSingleData((float)75.0);

		showColumn(chart);
	}
	
	public void refreshListByDate(String time){
		if(time != null){
			time = time.replaceAll("-", "");
			String code = RefreshKPIResponse.update_day;
			if(time.length() == 6){
				code = RefreshKPIResponse.update_month;
			}else if(time.length() == 8){
				code = RefreshKPIResponse.update_day;
			}
			RefreshKPIResponse refresh = new RefreshKPIResponse(context.handler, this, 
					code, time, window.curArear.getOrgId());
			Thread t = new Thread(refresh);
			t.start();
		}
	}
	
	public void refreshChartByDate(String time){
		if(time != null){
			KPIChartResponse req = new KPIChartResponse(context.handler, this, 
					window.chartPage.curMetricId, time, window.curArear.getOrgId());
			Thread t = new Thread(req);
			t.start();
		}
	}

	public void showColumn(ChartData chart) {
		if(window.chartPage != null)
			window.chartPage.addChartView(chart);
		window.getView().postInvalidate();
	}
	
	public void showErrorColumn(String s){
		window.chartPage.addErrorChartView(s);
		window.getView().postInvalidate();
	}

	public void changeDate(String calText) {
		KPIDataManager.getInstance().setDateTime(calText);
		if(this.curLevel == 0)
			refreshListByDate(calText);
		else if(curLevel == 1){
			refreshChartByDate(calText);
		}
	}

	public void loginError(String extroInfo) {
		context.errorInfo = extroInfo;
		context.showDialog(context.DIALOG_ERROR);
	}

	public void refreshError(String extroInfo) {
		context.errorInfo = extroInfo;
		context.showDialog(context.DIALOG_ERROR);
	}

	public void logout() {
		context.showLoginPage();
		window.chartPage = null;
		window.curArear = null;
		window.itemList = null;
		window.menuBar = null;
		window.midLayout = null;
		window.titleBar = null;
		KPIDataManager.getInstance().clear();
		window = null;
	}

	public String[] getArears() {
		
		if(window != null){
			return window.arearNames1;
		}
		return null;
	}

}
