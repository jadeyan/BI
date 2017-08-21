package com.teamsun.bi.http.response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.http.HttpWorker;
import com.teamsun.bi.http.parser.MarkupReader;
import com.teamsun.bi.http.parser.MarkupToken;
import com.teamsun.bi.http.parser.Tag;
import com.teamsun.bi.model.ChartData;
import com.teamsun.bi.model.KPIData;
import com.teamsun.bi.model.KPIDataManager;
import com.teamsun.bi.model.PerArearKPI;
import com.teamsun.bi.model.PulseChartData;
import com.teamsun.bi.ui.UiControl;

public class KPIChartResponse extends HttpWorker{

	String metric_id, v_time, v_org;
	
	PulseChartData pulse;
	
	ChartData chart;

	String error = null;
	
	public KPIChartResponse(Handler handler, UiControl ui, String metric_id, String v_time, String v_org) {
		super(handler, ui);
		this.metric_id = metric_id;
		this.v_org = v_org;

		if(v_time!= null)
			v_time = v_time.replaceAll("-", "");
		this.v_time = v_time;
	}

	@Override
	protected void parserData(InputStream inputStream) throws Exception {
	}
	
	private void parserPulseData(InputStream inputStream)throws Exception{
		mMarkupReader = new MarkupReader(inputStream, "UTF-8");
		boolean endofStream = false;
		try {
			do {
				final MarkupToken token = mMarkupReader.readToken();
				endofStream = token.getType() == 6;
				switch (token.getTag()) {
				case Tag.chart:{
					onPulseChart(token);
					break;
				}
				case Tag.valueRange:{
					onValueRange(token);
					break;
				}
				case Tag.range:{
					onRange(token);
					break;
				}
				case Tag.curret:{
					break;
				}
				case Tag.cursor:{
					onCursor(token);
					break;
				}
				case Tag.message:{
					onPulseMessage(token);
					break;
				}
				case Tag.error:{
					onPulseError(token);
					break;
				}
				}
			}while(!endofStream && !mParserStop);

			if(error != null){
				final String s = error;
				handler.post(new Runnable(){

					@Override
					public void run() {
						ui.showErrorPulse(s);
					}});
				error = null;
			}else{
				handler.post(new Runnable(){
					public void run(){
						ui.showPulse(pulse);
					}
				});
			}
		}catch(Exception e){
			throw new Exception("解析错误："+e.getMessage());
		}
	}
	
	private void onPulseError(MarkupToken token){
		if(token.isStartTag()){
			this.error = token.getAttribute("errorMsg");
		}
	}
	
	private void onPulseMessage(MarkupToken token){}
	
	private void onCursor(MarkupToken token){
		if(token.isStartTag()){
			String v = token.getAttribute("value");
			float var = Float.parseFloat(v);
			pulse.curVar = var;
		}
	}
	
	private void onRange(MarkupToken token){
		if(token.isStartTag()){
			String value = token.getAttribute("value","0");
			String color = token.getAttribute("color", "#FFFFFF");
			pulse.addValue(value, color);
		}
	}
	
	private void onValueRange(MarkupToken token){
		if(token.isEndTag()){
			pulse.setMaxValue();
		}
	}
	
	private void onPulseChart(MarkupToken token){
		if(token.isStartTag()){
			String title = token.getAttribute("caption","");
			String metricId = token.getAttribute("metricId","");
			String unit = token.getAttribute("unit","");
			String dateTime = token.getAttribute("dataTime","");
			String orgName = token.getAttribute("orgName","");
			String orgCd = token.getAttribute("orgCd");
			pulse = new PulseChartData(title, metricId, unit,dateTime, orgName, orgCd);
		}
	}
	
	private void parserLineData(InputStream inputStream)throws Exception{
		mMarkupReader = new MarkupReader(inputStream, "UTF-8");
		boolean endofStream = false;
		try {
			do {
				final MarkupToken token = mMarkupReader.readToken();
				endofStream = token.getType() == 6;
				switch (token.getTag()) {
				case Tag.chart:{
					onLineChart(token);
					break;
				}
				case Tag.categories:{
					break;
				}
				case Tag.category:{
					onCategory(token);
					break;
				}
				case Tag.dataset:{
					onDataset(token);
					break;
				}
				case Tag.set:{
					onSet(token);
					break;
				} 	
				case Tag.vertline:{
					onVerLine(token);
					break;
				}
				case Tag.line:{
					onLine(token);
					break;
				}
				case Tag.message:{
					onLineMessage(token);
					break;
				}
				case Tag.error:{
					onLineError(token);
					break;
				}
				}
			}while(!endofStream && !mParserStop);

			if(error !=null){
				final String s = error;
				handler.post(new Runnable(){
					public void run(){
						ui.showErrorColumn(s);
					}
				});
				error = null;
			}else{
				
				handler.post(new Runnable(){
					public void run(){
						ui.showLine(chart);
					}
				});
			}
		}catch(Exception e){
			throw new Exception("解析错误："+e.getMessage());
		}
	}
	
	private void onLineError(MarkupToken token){}
	
	private void onLineMessage(MarkupToken token){}
	
	private void onLine(MarkupToken token){
		if(token.isStartTag()){
			String startValue = token.getAttribute("startValue",null);
			if(startValue != null){
				chart.setCurDataId(startValue);
			}
		}
	}
	
	private void onVerLine(MarkupToken token){}
	
	private void onColumnSet(MarkupToken token){
		if(token.isStartTag()){
			String var = token.getAttribute("value", null);
			if(var != null && !var.equals("")){
				float  v = Float.parseFloat(var);
				chart.addData(new Float(v));
			}else
				chart.addData(null);
		}
	}
	
	private void onSet(MarkupToken token){
		if(token.isStartTag()){
			String var = token.getAttribute("value",null);
			if(var != null && !var.equals("")){
				float v = Float.parseFloat(var);
				chart.addSingleData(new Float(v));
			}else 
				chart.addSingleData(null);
		}
	}
	
	private void onDataset(MarkupToken token){
		if(token.isStartTag()){
			String series = token.getAttribute("seriesName","");
			chart.addLineData(series);
		}
	}
	
	private void onCategory(MarkupToken token){
		if(token.isStartTag()){
			String label = token.getAttribute("label","");
			chart.appendLableX(label);
		}
	}
	
	private void onLineChart(MarkupToken token){
		if(token.isStartTag()){
			String title = token.getAttribute("caption",""); 
			String metricId = token.getAttribute("metricId","");
			String dataTime = token.getAttribute("dataTime","");
			String unit = token.getAttribute("unit","");
			String orgName = token.getAttribute("orgName","");
			String orgCd = token.getAttribute("orgCd","");
			chart = new ChartData(title, metricId, dataTime, unit, orgName, orgCd);
		}
	}
	
	private void parserColumnData(InputStream inputStream)throws Exception{
		mMarkupReader = new MarkupReader(inputStream, "UTF-8");
		boolean endofStream = false;
		chart = null;
		try {
			do {
				final MarkupToken token = mMarkupReader.readToken();
				endofStream = token.getType() == 6;
				switch (token.getTag()) {
				case Tag.chart:{
					onColumnChart(token);
					break;
				}
				case Tag.categories:{
					break;
				}
				case Tag.category:{
					onCategory(token);
					break;
				}
				case Tag.dataset:{
					onColumnDataset(token);
					break;
				}
				case Tag.set:{
					onColumnSet(token);
					break;
				}
				case Tag.message:{
					break;
				}
				case Tag.error:{
					onColumnError(token);
					break;
				}
				default:
					break;
				}
			}while(!endofStream && !mParserStop);
			if(error !=null){
				final String s = error;
				handler.post(new Runnable(){
					public void run(){
						ui.showErrorColumn(s);
					}
				});
				error = null;
			}else{

				handler.post(new Runnable(){
					public void run(){
						ui.showColumn(chart);
					}
				});
			}
		}catch(Exception e){
			throw new Exception("解析错误："+e.getMessage());
		}
	}
	
	private void onColumnError(MarkupToken token){
		if(token.isStartTag()){
			error = token.getAttribute("errorMsg");
		}
	}
	
	private void onColumnDataset(MarkupToken token){
		if(token.isStartTag()){
			String series = token.getAttribute("seriesName","");
			chart.addColumnDataSet(series);
		}
	}
	
	private void onColumnChart(MarkupToken token){
		if(token.isStartTag()){
			String title = token.getAttribute("caption",""); 
			String metricId = token.getAttribute("metricId","");
			String dataTime = token.getAttribute("dataTime","");
			String unit = token.getAttribute("unit","");
			PerArearKPI perArear = KPIDataManager.getInstance().getPerArear(v_org);
			chart = new ChartData(title, metricId, dataTime, unit, 
					perArear.getOrgName(), v_org);
		}
	}

	@Override
	protected String dealUrl() {
		return null;
	}
	
	private String dealUrl(String code){

		StringBuffer sbf = new StringBuffer();
		sbf.append("?instance=")
			.append(code)
			.append("&otype=xml&para={v_time:\'")
			.append(v_time)
			.append("\',v_metrics_id:\'")
			.append(metric_id)
			.append("\',v_org:\'")
			.append(v_org)
			.append("\'}");
		return sbf.toString();
	}
	
	protected void request(){
		String url = "";
		
		/***********速度计表查询******************************************************/
		if(v_time!=null){
			if(v_time.length() == 8)
				url = dealUrl("103");
			else if(v_time.length() == 6)
				url = dealUrl("102");
		}
		int result = connect(url);
		Log.e("url:pulse", "http://"+ serverIp+model+url);
		if(result != 1){
			if(httpUrlConnection != null){
				try {
					int code = httpUrlConnection.getResponseCode();
					Log.println(Log.ERROR, "HttpWork request",
					"responseCode: " + code);
					if(code == HttpURLConnection.HTTP_OK){
						streamObj = httpUrlConnection.getInputStream();
						
						if (blnCancel) {
							closeConnection();
							return;
						}
						handler.post(new Runnable(){
							public void run(){
								ui.showSecondLevelPage(metric_id);
								ui.window.changeDateView(v_time);
							}
						});
						parserPulseData(streamObj);
					}else{
						doWrongPage("服务器响应错误");//
						return;
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					doWrongPage("网络错误");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		/*******趋势查询*****************************************/
		if(v_time!=null){
			if(v_time.length() == 8)
				url = dealUrl("104");
			else if(v_time.length() == 6)
				url = dealUrl("105");
		}
		result = connect(url);
		Log.e("url:line", url);
		if(result != 1){
			if(httpUrlConnection != null){
				try {
					int code = httpUrlConnection.getResponseCode();
					Log.println(Log.ERROR, "HttpWork request",
					"responseCode: " + code);
					if(code == HttpURLConnection.HTTP_OK){
						streamObj = httpUrlConnection.getInputStream();
						
						if (blnCancel) {
							closeConnection();
							return;
						}
						
						parserLineData(streamObj);
					}else{
						doWrongPage("服务器响应错误");//
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					doWrongPage("网络错误");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		/*******地域对比查询*****************************************/
		if(v_time!=null){
			if(v_time.length() == 8)
				url = dealUrl("107");
			else if(v_time.length() == 6)
				url = dealUrl("106");
		}
		result = connect(url);
		Log.e("url:column", url);
		if(result != 1){
			if(httpUrlConnection != null){
				try {
					int code = httpUrlConnection.getResponseCode();
					Log.println(Log.ERROR, "HttpWork request",
					"responseCode: " + code);
					if(code == HttpURLConnection.HTTP_OK){
						streamObj = httpUrlConnection.getInputStream();
						
						if (blnCancel) {
							closeConnection();
							return;
						}
						
						parserColumnData(streamObj);
					}else{
						doWrongPage("服务器响应错误");//
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					doWrongPage("网络错误");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 网络连接
	 * @return 0： 连接成功    1：失败 
	 */
	protected int connect(String modelExt){
		
		if (blnCancel) {
			closeConnection();
			return 1;
		}
		
		String proxy = null;
		String port = null;
		boolean beProxy = false;
		ConnectivityManager connMgr = (ConnectivityManager)BIProjectActivity.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = connMgr.getActiveNetworkInfo();
		if (network != null) {
			int type = network.getType();
			if (type == ConnectivityManager.TYPE_MOBILE){
				ContentResolver resolver = BIProjectActivity.getInstance()
						.getContentResolver();
				Cursor c = resolver.query(Uri
						.parse("content://telephony/carriers/preferapn"),
						new String[] { "_id", "name", "apn" }, null, null,
						null);

				if (c != null) {
					c.moveToFirst();
					if (c.isAfterLast()) {
					}else{
						int pro = c.getColumnIndex("proxy");
						if (pro > -1) {
							proxy = c.getString(pro);
						}
						pro = c.getColumnIndex("port");
						if (pro > -1) {
							port = c.getString(pro);
						}
						if (proxy != null && port != null)
							beProxy = true;
						
					}
				}
			}
		}
		if (blnCancel) {
			closeConnection();
			return 1;
		}

		
		System.gc();
		if (blnCancel) {
			closeConnection();
			return 1;
		}

		try {
			String m = model.concat(modelExt);
			if(beProxy){
				URL url = new URL("http://" + proxy + ":" + port + m);
				httpUrlConnection = (HttpURLConnection)url.openConnection();
				httpUrlConnection.setRequestProperty("X-Online-Host", serverIp);
				httpUrlConnection.setRequestProperty("User-Agent", "CMWAP Proxy");
			} else {
				URL url = new URL("http://"+ serverIp+m);
				httpUrlConnection = (HttpURLConnection)url.openConnection();
			}
			if(httpUrlConnection != null){
//				httpUrlConnection.setDoOutput(true);
				httpUrlConnection.setDoInput(true);
				httpUrlConnection.setConnectTimeout(50000);
				httpUrlConnection.setRequestMethod("GET");
				httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				httpUrlConnection.setRequestProperty("Accept", "*/*,application/x-www-form-urlencoded");
				httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
				
				if (blnCancel) {
					closeConnection();
					return 1;
				}
				
				httpUrlConnection.connect();
				Log.println(Log.INFO, "HttpBean connectNetWork",
						"connect successed" );
			}
		}catch(java.net.SocketTimeoutException e){
			Log.println(Log.ERROR, "HttpBean connectNetWork",
					e.getMessage()+e.toString());
			
			if(handler != null){
				handler.post(new Runnable(){
					public void run(){
//						Toast mToast = Toast.makeText(window.context, "连接网络超时", 5000);
//						mToast.show();
					}
				});
			}else
				doWrongPage("网络超时");
			
			return 1;
		}catch(Exception e){
			e.printStackTrace();
			Log.println(Log.ERROR, "HttpBean connectNetWork1",
					e.getMessage()+e.toString());
			
			if(handler != null){
				handler.post(new Runnable(){
					public void run(){
//						Toast mToast = Toast.makeText(window.context, "网络异常,请检查网络", 5000);
//						mToast.show();
					}
				});
			}else
				doWrongPage("网络错误");
			
			return 1;
		}
		System.gc();
		return 0;
	}

}
