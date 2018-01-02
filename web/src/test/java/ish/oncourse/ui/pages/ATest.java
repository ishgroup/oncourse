/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages;

import ish.oncourse.test.TestContext;
import ish.oncourse.test.context.CCollege;
import ish.oncourse.test.context.CWebSite;
import ish.oncourse.test.context.DataContext;
import ish.oncourse.ui.services.TestModule;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;

import static ish.oncourse.test.tapestry.TestModule.dataSource;
import static ish.oncourse.test.tapestry.TestModule.serverRuntime;

/**
 * User: akoiro
 * Date: 2/1/18
 */
public abstract class ATest {
	protected TestContext context;
	protected PageTester tester;

	protected CCollege college;

	@Before
	public void before() {
		this.context = new TestContext().open();
		dataSource.set(context.getDS());
		serverRuntime.set(context.getServerRuntime());

		college = new DataContext().withObjectContext(context.getServerRuntime().newContext()).newCollege();
		CWebSite webSite = college.newWebSite();
		this.tester = new PageTester("ish.oncourse.ui", "", "/", TestModule.class);
	}

	@After
	public void after() {
		this.tester.shutdown();
		this.context.close();
	}
}
