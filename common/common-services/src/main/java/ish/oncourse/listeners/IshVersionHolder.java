package ish.oncourse.listeners;

public class IshVersionHolder {

	private static ThreadLocal<Long> threadLocal = new InheritableThreadLocal<>();

	public static Long getIshVersion() {
		return threadLocal.get();
	}

	public static void setIshVersion(Long value) {
		threadLocal.set(value);
	}

	public static void cleanUp() {
		threadLocal.remove();
	}
}
