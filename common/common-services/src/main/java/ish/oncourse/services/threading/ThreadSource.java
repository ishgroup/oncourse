package ish.oncourse.services.threading;

public interface ThreadSource {
	void runInThread(Runnable task);
	void runInThread(final Runnable task, final TaskExceptionHandler taskExceptionHandler);
}
