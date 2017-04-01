package com.maymeng.three.utils;

/**
 * 
 * 创建线程池的工厂
 *
 */
public class ThreadPoolFactory {
	private static ThreadPoolProxy mNormalThreadPool;
	private static ThreadPoolProxy mDownLoadThreadPool;
	
	/**
	 * 获得普通的线程池
	 * @return
	 */
	public static ThreadPoolProxy getNormalThreadPool(){
		
		if (mNormalThreadPool == null) {
			synchronized (ThreadPoolProxy.class) {
				if (mNormalThreadPool == null) {
					mNormalThreadPool = new ThreadPoolProxy(5, 5, 3000);
				}
			}
		}
		
		return mNormalThreadPool;
	}
	
	/**
	 * 获得下载的线程池
	 * @return
	 */
	public static ThreadPoolProxy getDownLoadThreadPool(){
		
		if (mDownLoadThreadPool == null) {
			synchronized (ThreadPoolProxy.class) {
				if (mDownLoadThreadPool == null) {
					mDownLoadThreadPool = new ThreadPoolProxy(3, 3, 3000);
				}
			}
		}
		
		return mDownLoadThreadPool;
	}
}
