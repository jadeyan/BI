package com.teamsun.bi.http.response;

import java.io.InputStream;

import android.os.Handler;
import android.widget.Toast;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.http.HttpWorker;
import com.teamsun.bi.http.parser.MarkupReader;
import com.teamsun.bi.http.parser.MarkupToken;
import com.teamsun.bi.http.parser.Tag;
import com.teamsun.bi.model.KPIData;
import com.teamsun.bi.model.KPIDataManager;
import com.teamsun.bi.model.KPIDataProperty;
import com.teamsun.bi.model.PerArearKPI;
import com.teamsun.bi.ui.UiControl;

public class LoginResponse extends HttpWorker{

	String requestCode = "110";
	
	KPIDataManager manager;

	PerArearKPI arear;
	
	String error = null;
	public LoginResponse(Handler handler, UiControl ui) {
		super(handler, ui);
	}
	
	@Override
	public void run() {
		if(ui != null && ui.isConnecting){
			handler.post(new Runnable(){

				@Override
				public void run() {
					Toast mToast = Toast.makeText(ui.context, "不能同时点击", Toast.LENGTH_LONG);
					mToast.show();
				}});
			return;
		}
		ui.isConnecting = true;
		ui.context.setClickListener(this);
		ui.context.setCancelListener(this);
		try{
			handler.post(new Runnable(){

				public void run() {
					ui.context.showDialog(ui.context.DIALOG_HTTP);
					if(ui.context.dialog != null)
						ui.context.dialog.setTitle("正在登录验证");
				}
					
			});
			request();
		}catch(Exception e){
			e.printStackTrace();
			doWrongPage("网络错误");
		}finally{
			handler.post(new Runnable() {
				public void run() {
					ui.context.removeDialog(0);
					ui.context.setClickListener(null);
				}
			});
			mMarkupReader = null;
			ui.isConnecting = false;
		}
	}

	@Override
	protected void parserData(InputStream inputStream)throws Exception {
		manager = KPIDataManager.getInstance();
		mMarkupReader = new MarkupReader(inputStream, "UTF-8");
		boolean endofStream = false;
		try {
			do {
				final MarkupToken token = mMarkupReader.readToken();
				endofStream = token.getType() == 6;
				switch (token.getTag()) {
				case Tag.chart:{
					onChart(token);
					break;
				}
				case Tag.metricProperty:{
					onProperty(token);
					break;
				}
				case Tag.metric:{
					onMetric(token);
					break;
				}
				case Tag.org:{
					onOrg(token);
					break;
				}
				case Tag.set:{
					onSet(token);
					break;
				}
				case Tag.message:{
					break;
				}
				case Tag.error:{
					onError(token);
					break;
				}
				}
				
				if (blnCancel) {
					closeConnection();
					return;
				}
				
				
			} while (!endofStream && !mParserStop);
			
			if(error == null){
				handler.post(new Runnable(){

					@Override
					public void run() {
						ui.showWindow();
					}});
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("解析异常:"+e.getMessage());
		}
	}
	
	protected void onError(MarkupToken token){
		if(token.isStartTag()){
			String code = token.getAttribute("errorCode");
			error = token.getAttribute("errorMsg");
//			controler.onError(code, mess);
			doWrongPage("错误代码："+code+"  "+error);
		}
	}
	
	protected void doWrongPage(final String extroInfo){
		handler.post(new Runnable(){
			public void run(){
				ui.loginError(extroInfo);
			}
		});
	}

	protected void onSet(MarkupToken token) {
		if(token.isStartTag()){
			String id = token.getAttribute("metricId");
			String curVar = token.getAttribute("curValue");
			String ringVar = token.getAttribute("ringValue");
			String likeVar = token.getAttribute("likeValue");
			String sumVar = token.getAttribute("sumValue");
			KPIData data = new KPIData(id, curVar, ringVar, likeVar, sumVar);
			KPIDataProperty prop = manager.getProperty(id);
			data.setProperty(prop);
			arear.addData(data);
		}
	}

	protected void onOrg(MarkupToken token) {
		if(token.isStartTag()){
			String name = token.getAttribute("orgName");
			String cd = token.getAttribute("orgCd");
			arear = new PerArearKPI(name, cd);
			arear.setCurDate(manager.getDateTime());
			manager.addArearStr(name);
		}else if(token.isEndTag()){
			manager.addArear(arear);
		}
		
	}

	protected void onMetric(MarkupToken token) {
		if(token.isStartTag()){
			String name = token.getAttribute("name");
			String unit = token.getAttribute("unit");
			String id = token.getAttribute("id");
			KPIDataProperty prop = new KPIDataProperty(id, unit, name);
			manager.addProperty(prop);
		}
	}

	protected void onProperty(MarkupToken token) {
	}

	protected void onChart(MarkupToken token) {
		if(token.isStartTag()){
			String title = token.getAttribute("caption");
			manager.setCapTitle(title);
			String date = token.getAttribute("dataTime");
			manager.setDateTime(date);
			String org = token.getAttribute("orgCd");
			manager.setDefOrgCd(org);
		}
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
		KPIDataProperty prop = new KPIDataProperty("ysz", "元", "本月应收账款");
		manager.addProperty(prop);

		KPIData data = new KPIData("ysz", "-15%", "10.0%", "15043.00", "98000.00");
		data.setProperty(prop);
		arear.addData(data);
	}

	@Override
	protected String dealUrl() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("?instance=").append(requestCode)
			.append("&otype=xml&para={v_username:\'")
			.append(BIProjectActivity.getInstance().userName)
			.append("\',v_password:\'")
			.append(BIProjectActivity.getInstance().password)
			.append("\'}");
		return sbf.toString();
	}

}
