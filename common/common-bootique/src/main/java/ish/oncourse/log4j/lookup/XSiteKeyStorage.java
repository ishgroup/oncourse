package ish.oncourse.log4j.lookup;

/**
 * Thread-safe request storage
 */
class XSiteKeyStorage {

    private static ThreadLocal<String> threadLocalRequest = new ThreadLocal<>();

    static void put(String key) {
        threadLocalRequest.set(key);
    }

    static String get() {
        return threadLocalRequest.get();
    }
}
