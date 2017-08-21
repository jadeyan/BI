package com.teamsun.bi.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.widget.Toast;

import com.teamsun.bi.demo.BIProjectActivity;
import com.teamsun.bi.http.parser.MarkupReader;
import com.teamsun.bi.http.parser.MarkupToken;
import com.teamsun.bi.ui.UiControl;

public abstract class HttpWorker implements Runnable, DialogInterface.OnClickListener,
						DialogInterface.OnCancelListener{

	protected MarkupReader mMarkupReader;
	protected boolean mParserStop;
	protected HttpURLConnection httpUrlConnection;
	protected OutputStream outputstream = null;
	protected InputStream streamObj = null;
	
	public boolean blnCancel;
	protected String curResponseCmd;
	protected String curRequestCmd;
	protected String curMessageCode;
	protected String errorMessage;
	
	public final String CMD_OK = "0000";
	
	public final String CMD_SERVER_BUSY = "1000";
	
	public final String CMD_PASSWORD_WRONG = "1001";
	
	public final String CMD_USERNAME_INVALID = "1002";
	
	protected String serverIp;//"172.16.140.99:8899";
	
	protected String model = "/BI/dataservice/mquery";//"/MDSS/dataservice/mquery";
	
	
	protected Handler handler;
	
	protected UiControl ui;
	public HttpWorker(Handler handler, UiControl ui){
		this.handler = handler;
		this.ui = ui;
		serverIp = BIProjectActivity.getInstance().serverIp;
	}
	
	protected abstract void parserData(InputStream inputStream) throws Exception;

	@Override
	public void run() {
		if(ui != null && ui.isConnecting){
			handler.post(new Runnable(){

				@Override
				public void run() {
					Toast mToast = Toast.makeText(ui.context, "不能同时点击", 2500);
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
	
	protected void request(){
		int result = connect();
		if(result == 1)
			return ;
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
					
					parserData(streamObj);
				}else{
					doWrongPage("服务器响应错误");//
				}
			} catch (IOException e) {
				e.printStackTrace();
				doWrongPage("网络错误");
			} catch (Exception e) {
				e.printStackTrace();
				doWrongPage("异常："+e.getMessage());
			}
		}
		
	}
	
	/**
	 * 网络连接
	 * @return 0： 连接成功    1：失败 
	 */
	protected int connect(){
		
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
			model = model.concat(dealUrl());
			if(beProxy){
				URL url = new URL("http://" + proxy + ":" + port + model);
				httpUrlConnection = (HttpURLConnection)url.openConnection();
				httpUrlConnection.setRequestProperty("X-Online-Host", serverIp);
				httpUrlConnection.setRequestProperty("User-Agent", "CMWAP Proxy");
			} else {
				URL url = new URL("http://"+ serverIp+model);
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
			
			doWrongPage("网络超时");
			
			return 1;
		}catch(Exception e){
			e.printStackTrace();
			Log.println(Log.ERROR, "HttpBean connectNetWork1",
					e.getMessage()+e.toString());
			
			doWrongPage("网络错误");
			
			return 1;
		}
		System.gc();
		return 0;
	}
	
	protected abstract String dealUrl();
	
	protected void closeConnection(){
		try {
			if(httpUrlConnection != null){
				try{
					httpUrlConnection.disconnect();
				}catch(Exception e){
					
				}
			}
			if (this.streamObj != null) {
				try {
					((InputStream) (streamObj)).close();
				} catch (Exception e) {
				}
			}
		} finally {
			httpUrlConnection = null;
			streamObj = null;
		}
	}

	/**
	 * Tag <message>
	 * @param token
	 */
	protected void onMessage(MarkupToken token){
		if(token.isStartTag()){
			curMessageCode = token.getAttribute("value");
			curResponseCmd = token.getAttribute("command");
		}else if(token.isEndTag()){
			errorMessage = token.getPrecedingText();//f7ca803d94160170f46c4249b5bca8d2
		}
	}
	
	/**
	 * 处理异常情况 
	 */
	protected void doWrongPage(String extroInfo){
	}

	public void onClick (DialogInterface dialog, int which){
		this.blnCancel = true;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		this.blnCancel = true;
	}
}
