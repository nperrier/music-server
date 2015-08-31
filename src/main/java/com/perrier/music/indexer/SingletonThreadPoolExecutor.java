package com.perrier.music.indexer;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Custom thread pool executor to ensure that only a single class type is executing at a given time
 */
public class SingletonThreadPoolExecutor extends ThreadPoolExecutor {

	private static final Logger log = LoggerFactory.getLogger(SingletonThreadPoolExecutor.class);

	// tasks currently executing
	private Set<Callable<?>> executingTasks = Sets.newConcurrentHashSet();

	/**
	 * Wrapper FutureTask to hold the class of the running task
	 * 
	 * @param <V>
	 */
	private static class ClassContainerRunnableFuture<V> extends FutureTask<V> {

		private final Callable<V> callable;

		public ClassContainerRunnableFuture(Callable<V> callable) {
			super(callable);
			this.callable = callable;
		}

		public Callable<V> getContainedClass() {
			return this.callable;
		}

		@Override
		protected void done() {
			try {
				if (!isCancelled()) {
					get();
				}
			} catch (ExecutionException e) {
				// Exception occurred, deal with it
				log.warn("Exception: " + e.getCause());
			} catch (InterruptedException e) {
				throw new AssertionError(e);
			}
		}
	}

	public SingletonThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	@Override
	protected void beforeExecute(Thread thread, Runnable runnable) {
		super.beforeExecute(thread, runnable);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new ClassContainerRunnableFuture<T>(callable);
	}

	@Override
	public void execute(Runnable runnable) {
		if (runnable instanceof ClassContainerRunnableFuture) {
			ClassContainerRunnableFuture<?> target = (ClassContainerRunnableFuture<?>) runnable;
			Callable<?> task = target.getContainedClass();

			if (isCurrentlyExecuting(task)) {
				this.getRejectedExecutionHandler().rejectedExecution(runnable, this);
			} else {
				this.executingTasks.add(task);
			}
		}

		super.execute(runnable);
	}

	@Override
	public <T> Future<T> submit(Callable<T> callable) {
		return super.submit(callable);
	}

	private boolean isCurrentlyExecuting(Callable<?> callable) {
		return this.executingTasks.contains(callable);
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		if (runnable instanceof ClassContainerRunnableFuture) {
			ClassContainerRunnableFuture<?> target = (ClassContainerRunnableFuture<?>) runnable;
			Callable<?> task = target.getContainedClass();

			if (isCurrentlyExecuting(task)) {
				this.executingTasks.remove(task);
			}
		}

		super.afterExecute(runnable, throwable);
	}

}
