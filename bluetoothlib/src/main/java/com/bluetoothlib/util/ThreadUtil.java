package com.bluetoothlib.util;

import android.content.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池工具类
 * */
public class ThreadUtil {

	private static ThreadUtil instance;
	private static Context context;

	/** 线程池中的线程的数目 **/
	private static final int DEFAULT_THREAD_POOL_SIZE = 10;

	/** 定义线程池 **/
	public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors // 线程池
			.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

	public ThreadUtil(Context context) {
		this.context = context;
	}

	/**
	 * 使用线程池来运行线程,每一个线程都需要等待线程池中的线程有空闲才会执行
	 * */
	public static void executeThread(Runnable runnable) {
		executor.execute(runnable);
	}

	/**
	 * 获得实体
	 * */
	public static ThreadUtil getInstance(Context context) {
		instance = new ThreadUtil(context);
		return instance;
	}

}
