package org.delmesoft.crazyblocks.utils.threads;

import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.utils.datastructure.LinkedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {

	//public static boolean debug = true;
	//public int maxRunningThreads;
	//public long startTime;

	private final LinkedList<Runnable> runnables;

	private final int maxThreads;
	private int runningThreads;

	private ExecutorService executor;
	private boolean clear;

	private final ReentrantLock reentrantLock;

	private ExceptionListener exceptionListener;

	ThreadFactory threadFactory=new ThreadFactory()
  {
    @Override
    public Thread newThread(Runnable r)
    {
      Thread t = Executors.defaultThreadFactory().newThread(r);
      t.setDaemon(true);
      return t;
    }
  };
	
	
	public ThreadPool(int maxThreads) {
		this.maxThreads = Math.max(maxThreads, 1);
		runnables = new LinkedList<Runnable>();
		executor = Executors.newFixedThreadPool(maxThreads, threadFactory);

		reentrantLock = new ReentrantLock(true);
	}

	public void execute(Array<MyRunnable> runnables) {

		runnables.sort();

		try {

			reentrantLock.lock();
			for(int i = 0; i < runnables.size; i++) {
				this.runnables.add(runnables.get(i));
			}

			while (runningThreads < maxThreads) {
				runTask();
			}

		} finally {
			reentrantLock.unlock();
		}

	}

	public void execute(Runnable runnable) {

		try {

			reentrantLock.lock();

			runnables.add(runnable);

			if (runningThreads < maxThreads) {
				runTask();
			}

		} finally {
			reentrantLock.unlock();
		}

	}

	private void runTask() {

				/*if(debug) {
					if(runningThreads == 0)
						startTime = System.currentTimeMillis();
					if(maxRunningThreads < runningThreads + 1) {
						maxRunningThreads = runningThreads + 1;
					}
				}*/

		executor.execute(new Runnable() {

			@Override
			public void run() {

				try {

					Runnable runnable;

					reentrantLock.lock();
					while (clear == false) {

						if (runnables.size() > 0) {
							runnable = runnables.poll(); // FIFO
						} else {
							return;
						}

						reentrantLock.unlock();
						try {
							runnable.run(); // task
						} finally {
							reentrantLock.lock();
						}

					} // while

				} catch (Exception e) {
					if(exceptionListener != null) {
						exceptionListener.onException(e);
					}
				} finally {
					--runningThreads;

							/*if(debug && runningThreads == 0) {
								System.out.printf("ThreadPool time: %d (ms), Concurrent tasks: %d\n", (System.currentTimeMillis() - startTime), maxRunningThreads);
								maxRunningThreads = 0;
							}*/

					if (runningThreads == 0 && clear) {
						clear = false;
						synchronized (runnables) {
							runnables.notifyAll();
						}
					}
					reentrantLock.unlock();
				} // finally

			} // run()

		});

		++runningThreads;

	}

	public int getThreadCount() {
		return runningThreads;
	}

	public int getPendingTasks() {
		return runnables.size();
	}

	public void clear(boolean sync) {

		try {

			reentrantLock.lock();

			runnables.clear();
			if (runningThreads > 0) {
				clear = true;
				if (sync) {
					synchronized (runnables) {
						reentrantLock.unlock();
						runnables.wait(0);
					}
					reentrantLock.lock();
				}
			} // if

		} catch (InterruptedException e) {
			// ignore
		} finally {
			reentrantLock.unlock();
		}

	}

	public ExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public void setExceptionListener(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

	public void dispose() {
		clear(false);
		executor.shutdown();
	}

	public interface ExceptionListener {
		void onException(Exception e);
	}

	public static abstract class MyRunnable implements Runnable, Comparable<MyRunnable> {

		public int priority;

		public MyRunnable(int priority) {
			this.priority = priority;
		}

		@Override
		public int compareTo(MyRunnable o) {
			return (o.priority > priority) ? -1 : ((priority == o.priority) ? 0 : 1);
		}

	}

}
