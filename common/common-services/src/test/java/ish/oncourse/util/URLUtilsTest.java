package ish.oncourse.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class URLUtilsTest {

	@Test
	public void testIsAbsolute() {
		assertTrue(URLUtils.isAbsolute("http://www.ish.com.au"));
		assertTrue(URLUtils.isAbsolute("https://google.com/search?q=query"));
		assertFalse(URLUtils.isAbsolute("/path/sequence"));
		assertFalse(URLUtils.isAbsolute("path/sequence"));
		assertFalse(URLUtils.isAbsolute("path"));
		assertFalse(URLUtils.isAbsolute("/some:path/sequence"));
		assertFalse(URLUtils.isAbsolute("://path"));
	}
}
