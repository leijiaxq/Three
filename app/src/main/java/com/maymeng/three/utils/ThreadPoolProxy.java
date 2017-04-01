package com.maymeng.three.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的创建,任务执行,任务提交,任务移除
 * 
 */
public class ThreadPoolProxy {

	ThreadPoolExecutor mExecutor;

	int mCorePoolSize;
	int mMaximumPoolSize;
	long mKeepAliveTime;

	public ThreadPoolProxy(int corePoolSize, int maximumPoolSize,
			long keepAliveTime) {

		this.mCorePoolSize = corePoolSize;
		this.mMaximumPoolSize = maximumPoolSize;
		this.mKeepAliveTime = keepAliveTime;

	}

	private ThreadPoolExecutor initThreadPoolExecutor() {

		if (mExecutor == null) {
			synchronized (ThreadPoolExecutor.class) {
				if (mExecutor == null) {
					TimeUnit unit = TimeUnit.MILLISECONDS;// 毫秒
					BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();// 无界队列
					ThreadFactory threadFactory = Executors
							.defaultThreadFactory();
					RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();// 丢弃任务并抛出异常
					mExecutor = new ThreadPoolExecutor(mCorePoolSize,// 核心线程池数
							mMaximumPoolSize, // 最大线程池数
							mKeepAliveTime,// 保持时间
							unit, // 保持时间单位
							workQueue,// 任务队列
							threadFactory, // 工厂
							handler);// 异常捕获器
				}
			}

		}
	
		return mExecutor;
	}

	/**
	 * 执行任务
	 */
	public void excute(Runnable task) {

		initThreadPoolExecutor();
		mExecutor.execute(task);
	}

	/**
	 * 提交任务
	 */
	public void submit(Runnable task) {

		initThreadPoolExecutor();
		mExecutor.submit(task);
	}

	/**
	 * 移除任务
	 */
	public void remove(Runnable task) {

		initThreadPoolExecutor();
		mExecutor.remove(task);
	}
}
