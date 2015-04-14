package ish.oncourse.services.threading;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.slf4j.Logger;

public class ThreadSourceImpl implements ThreadSource {

	private final PerthreadManager perthreadManager;
	private final Logger logger;

	@Inject
	public ThreadSourceImpl(PerthreadManager perthreadManager, Logger logger) {
		super();
		this.perthreadManager = perthreadManager;
		this.logger = logger;
	}

	/*
	 * Executes task in separate thread.
	 * 
	 * @see
	 * ish.oncourse.services.threading.ThreadSource#runInThread(java.lang.Runnable
	 * )
	 */
	@Override
	public void runInThread(Runnable task) {
		runInThread(task, defaultTaskExceptionHandler);
	}

	/*
	 * Executes task in separate thread.
	 * 
	 * @see
	 * ish.oncourse.services.threading.ThreadSource#runInThread(java.lang.Runnable
	 * , ish.oncourse.services.threading.TaskExceptionHandler)
	 */
	@Override
	public void runInThread(final Runnable task, final TaskExceptionHandler taskExceptionHandler) {
		new Thread(new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Throwable e) {
					taskExceptionHandler.exceptionThrown(task, e);
				} finally {
					perthreadManager.cleanup();
				}
			}
		}).start();
	}

	/** default exception handler that writes exception to the log */
	private final TaskExceptionHandler defaultTaskExceptionHandler = new TaskExceptionHandler() {
		public void exceptionThrown(Object task, Throwable exception) {
			logger.error("Task failed: {}", task, exception);
		}
	};
}
