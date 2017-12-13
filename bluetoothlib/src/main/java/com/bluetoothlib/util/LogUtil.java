package com.bluetoothlib.util;

import android.util.Log;

/**
 * 打印的工具类
 * 
 * @author zhxiang
 */
public class LogUtil {

//	public static  boolean DEBUG = true;
	 public static  boolean DEBUG = false;

	public static void i(String TAG, String i) {
		if (DEBUG) {
			Log.i(TAG, i);
		}
	}

	public static void e(String TAG, String i) {
		if (DEBUG) {
			try {
				Log.e(TAG, i);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void v(String TAG, String i) {
		if (DEBUG) {
			Log.v(TAG, i);
		}
	}

	public static void d(String TAG, String i) {
		if (DEBUG) {
			Log.d(TAG, i);
		}
	}

	public static void w(String TAG, String i) {
		if (DEBUG) {
			Log.w(TAG, i);
		}
	}
}
