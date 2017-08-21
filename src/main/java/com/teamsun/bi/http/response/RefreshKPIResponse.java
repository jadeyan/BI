package com.teamsun.bi.http.response;

import java.io.InputStream;

import android.os.Handler;

import com.teamsun.bi.http.parser.MarkupReader;
import com.teamsun.bi.http.parser.MarkupToken;
import com.teamsun.bi.http.parser.Tag;
import com.teamsun.bi.model.KPIDataManager;
import com.teamsun.bi.model.PerArearKPI;
import com.teamsun.bi.ui.UiControl;

public class RefreshKPIResponse extends LoginResponse{

	public static final String update_month = "108";
	
	public static final String update_day= "109";
	
	String v_time, v_org;

	public RefreshKPIResponse(Handler handler, UiControl ui, String reqCode, String v_time, String v_org) {
		super(handler, ui);
		this.requestCode = reqCode;
		this.v_org = v_org;
		this.v_time = v_time;
	}

	@Override
	protected void parserData(InputStream inputStream) throws Exception {
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
			handler.post(new Runnable(){

				@Override
				public void run() {
					ui.updataList(arear);
				}});
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("解析异常:"+e.getMessage());
		}
	}
	
	protected void onError(MarkupToken token){
		if(token.isStartTag()){
			String code = token.getAttribute("errorCode");
			String mess = token.getAttribute("errorMsg");
			doWrongPage("错误代码："+code+"  "+mess);
		}
	}
	
	protected void doWrongPage(final String extroInfo){
		handler.post(new Runnable(){
			public void run(){
				ui.refreshError(extroInfo);
			}
		});
	}
	
	protected void onOrg(MarkupToken token) {
		if(token.isStartTag()){
			String name = token.getAttribute("orgName");
			String cd = token.getAttribute("orgCd");
			arear = new PerArearKPI(name, cd);
		}else if(token.isEndTag()){
			manager.addArear(arear);
		}
		
	}
	
	protected void onProperty(MarkupToken token) {
		if(token.isStartTag()){
			manager.clearProperty();	
		}
	}
	
	@Override
	protected String dealUrl() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("?instance=").append(requestCode)
			.append("&otype=xml&para={v_time:\'")
			.append(v_time)
			.append("\',v_org:\'")
			.append(v_org)
			.append("\'}");
		return sbf.toString();
	}

}
