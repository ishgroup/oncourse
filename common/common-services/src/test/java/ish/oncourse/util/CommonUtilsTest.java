package ish.oncourse.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommonUtilsTest {

	@Test
	public void test()
	{
		assertTrue(0 == CommonUtils.compare("4.0", "4.0"));
		assertTrue(0 < CommonUtils.compare("4.1", "4.0"));
		assertTrue(0 > CommonUtils.compare("4.0b1", "4.0"));
		assertTrue(0 > CommonUtils.compare("4.0a1", "4.0b1"));
		assertTrue(0 < CommonUtils.compare("5.1", "4.0"));
		assertTrue(0 > CommonUtils.compare("3.1", "4.0"));
		assertTrue(0 > CommonUtils.compare("4.0-SNAPSHOT", "4.0"));
		assertTrue(0 < CommonUtils.compare("development", "4.0"));
	}
}
