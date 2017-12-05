/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages;

import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import ish.oncourse.ui.services.AppModule;
import ish.oncourse.ui.services.UIModule;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.test.PageTester;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * User: akoiro
 * Date: 20/11/17
 */
public class CoursesTest {
	private TestContext testContext;

	@Before
	public void before() {
		testContext = new TestContext();
		testContext.open();
		LoadDataSet loadDataSet = new LoadDataSet().dataSetFile("ish/oncourse/ui/pages/CoursesTest.xml").load(testContext.getDS());
	}

	@Test
	public void test() {
		new PageTester("ish.oncourse.ui", "common-ui", "/", AppModule.class).renderPage("/courses");
	}
}
