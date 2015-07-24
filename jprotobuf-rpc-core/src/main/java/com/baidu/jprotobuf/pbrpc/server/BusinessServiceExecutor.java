package com.baidu.jprotobuf.pbrpc.server;

import java.util.concurrent.Callable;

import com.google.common.util.concurrent.ListenableFuture;

public interface BusinessServiceExecutor {

	<V> ListenableFuture<V> submit(Callable<V> task);

	void shutdown();
}
