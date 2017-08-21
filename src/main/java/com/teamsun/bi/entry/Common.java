package com.teamsun.bi.entry;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.teamsun.bi.demo.BIProjectActivity;

public class Common {

	public static final String preferStr = "prefer";
	
	public static Bitmap loadResource(Resources res, int id){
		Bitmap map = BitmapFactory.decodeResource(res, id);
		return map;
	}
	
	public static Bitmap scale(Bitmap srcImg, int fw, int fh) {
		Bitmap bitmap = srcImg;
		if (bitmap != null) {
			// 获取这个图片的宽和高
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			// 计算缩放率，新尺寸除原始尺寸
			float scaleWidth = ((float) fw) / width;
			float scaleHeight = ((float) fh) / height;
			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix();
			// 缩放图片动作
			matrix.postScale(scaleWidth, scaleHeight);
			// 创建新的图片
			Bitmap finalBit = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix, true);
			return finalBit;
		}
		return null;
	}
	
	public static void saveMess(String preferName, String name, String value){
		SharedPreferences preference = BIProjectActivity.getInstance().getSharedPreferences(preferName, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = preference.edit();
		edit.putString(name, value);
		edit.commit(); 
	}

	public static  String getSavedInfo(String preferstr2, String string) {
		SharedPreferences pref = BIProjectActivity.getInstance().getSharedPreferences(preferstr2, Context.MODE_PRIVATE);
		return pref.getString(string, null);
	}
	
	public static  boolean getSavedInfo2(String preferstr2, String string) {
		SharedPreferences pref = BIProjectActivity.getInstance().getSharedPreferences(preferstr2, Context.MODE_PRIVATE);
		return pref.getBoolean(string, false);
	}

	public static void saveMess2(String preferstr2, String string, boolean save) {
		SharedPreferences preference = BIProjectActivity.getInstance().getSharedPreferences(preferstr2, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = preference.edit();
		edit.putBoolean(string, save);
		edit.commit(); 
	}
}
