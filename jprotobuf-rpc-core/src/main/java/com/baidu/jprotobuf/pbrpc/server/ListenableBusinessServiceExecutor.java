package com.baidu.jprotobuf.pbrpc.server;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class ListenableBusinessServiceExecutor implements BusinessServiceExecutor {

	private ListeningExecutorService executor = null;

	public ListenableBusinessServiceExecutor() {
		this(MoreExecutors.listeningDecorator(Executors.newCachedThreadPool()));
	}

	public ListenableBusinessServiceExecutor(ListeningExecutorService executor) {
		this.executor = executor;
	}

	public <V> ListenableFuture<V> submit(Callable<V> task) {
		return executor.submit(task);
	}

	public void shutdown() {
		executor.shutdown();
	}
}
