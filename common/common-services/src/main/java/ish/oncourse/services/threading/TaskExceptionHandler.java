package ish.oncourse.services.threading;

public interface TaskExceptionHandler {
	void exceptionThrown(Object task, Throwable exception);
}
