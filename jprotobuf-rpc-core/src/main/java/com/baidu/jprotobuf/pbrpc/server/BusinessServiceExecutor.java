package com.baidu.jprotobuf.pbrpc.server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Business Service Asynchronous Executor with ListenableFuture result.
 * 
 * @author LuciferYang
 * @since 3.0.3
 */
public class BusinessServiceExecutor {

	private ListeningExecutorService executor = null;

	/**
	 * default init with Cached ThreadPool.
	 */
	public BusinessServiceExecutor() {
		this(Executors.newCachedThreadPool());
	}

	/**
	 * init with a customer ExecutorService.
	 * 
	 * @param executor
	 */
	public BusinessServiceExecutor(ExecutorService executor) {
		this.executor = MoreExecutors.listeningDecorator(executor);
	}

	/**
	 * submit a callable task and retrun a ListenableFuture.
	 * 
	 * @param task
	 *            business service task
	 * @return listenable future
	 */
	public <V> ListenableFuture<V> submit(Callable<V> task) {
		return executor.submit(task);
	}

	/**
	 * shutdown executor.
	 */
	public void shutdown() {
		executor.shutdown();
	}
}
