/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages;

import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.test.tapestry.TestModule;
import org.junit.Before;
import org.junit.Test;

/**
 * User: akoiro
 * Date: 20/11/17
 */
public class CoursesTest extends ServiceTest {
	@Before
	public void before() {
		initTest("ish.oncourse.ui", "app", TestModule.class);
	}

	@Test
	public void test() {
	}
}
