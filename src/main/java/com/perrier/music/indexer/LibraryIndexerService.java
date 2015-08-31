package com.perrier.music.indexer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class LibraryIndexerService extends AbstractIdleService implements ILibraryIndexerService {

	private static final Logger log = LoggerFactory.getLogger(LibraryIndexerService.class);

	private final ExecutorService executor;

	public LibraryIndexerService() {
		ThreadFactoryBuilder builder = new ThreadFactoryBuilder().setNameFormat(LibraryIndexerService.class.getSimpleName()
				+ " %1$d");
		this.executor = new SingletonThreadPoolExecutor(2, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), builder
				.build());
	}

	@Override
	public Future<Boolean> submit(LibraryIndexerTask task) {
		try {
			Future<Boolean> f = this.executor.submit(task);
			return f;
		} catch (RejectedExecutionException e) {
			// TODO Consider throwing exception so caller can handle rejected executions
			log.warn("Unable to submit task: already running: {}", task);
			return Futures.immediateFailedFuture(e);
		} catch (Exception e) {
			log.warn("Unable to submit task", e);
			return Futures.immediateFailedFuture(e);
		}
	}

	@Override
	protected void startUp() throws Exception {
	}

	@Override
	protected void shutDown() throws Exception {
		this.executor.shutdownNow();
	}
}
