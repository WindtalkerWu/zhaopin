package com.qianniu.zhaopin.app.common;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.Context;

/**
 * 线程池控制器； 1、可以执行普通的线程； 2、也可以自定义线程，在线程池里来复写execute方法，处理相同的任务；
 * 
 * @author Administrator
 * 
 */
public class ThreadPoolController {
	public static final String TAG = "ThreadPoolController";

	private static final int CORE_POOL_SIZE = 5;
	private static final int MAXIMUM_POOL_SIZE = 15;
	private static final int KEEP_ALIVE = 10;

	private static ThreadPoolController _controller = null;

	public static ThreadPoolController getInstance() {
		if (_controller == null) {
			_controller = new ThreadPoolController();
		}
		return _controller;
	}

	private ThreadPoolController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
	private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>(
			MAXIMUM_POOL_SIZE);

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {

		private AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			int count = mCount.getAndIncrement();
			DsLog.i(TAG, ":::::::::::::::create a new thread: " + count);
			return new Thread(r, count + "_thread");
		}
	};

	private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			sWorkQueue, sThreadFactory, rejectedExecutionHandler);

	/**
	 * 执行一个 RUN 任务
	 * 
	 * @param r
	 */
	public synchronized void execute(Runnable r) {

		if (true) {
			sExecutor.execute(r);
		}

	}
}
