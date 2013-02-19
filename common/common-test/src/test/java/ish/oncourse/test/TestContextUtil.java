package ish.oncourse.test;

public class TestContextUtil {
	public static final String TEST_IS_QUERY_CACHE_DISABLED = "test.isQueryCacheDisabled";

	public static boolean isQueryCacheEnabled() {
		return !Boolean.valueOf(System.getProperty(TEST_IS_QUERY_CACHE_DISABLED));
	}

}
