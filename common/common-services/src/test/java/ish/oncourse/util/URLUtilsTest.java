package ish.oncourse.util;

import static org.junit.Assert.*;
import org.junit.Test;

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
