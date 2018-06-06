/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz.job;

import io.reactivex.Observable;
import ish.oncourse.test.LoadLiquibase;
import ish.oncourse.test.MariaDB;
import ish.oncourse.test.TestContext;
import ish.oncourse.test.context.CCollege;
import ish.oncourse.test.context.DataContext;

/**
 * User: akoiro
 * Date: 5/6/18
 */
public class SolrTestData {
	private MariaDB mariaDB;

	private TestContext testContext;
	private DataContext dataContext;


	public SolrTestData schema(String schema) {
		this.mariaDB = MariaDB.valueOf(schema);
		return this;
	}


	public SolrTestData create() {
		testContext = new TestContext()
				.mariaDB(mariaDB)
				.shouldCleanTables(true)
				.shouldCreateTables(true)
				.open();

		new LoadLiquibase()
				.mariaDB(mariaDB)
				.loadPostcodes(true)
				.changeSet("322").load();

		dataContext = new DataContext().withObjectContext(testContext.getServerRuntime().newContext()).load();

		CCollege college = dataContext.newCollege("C1");

		Observable.range(1, 10)
				.map((i) -> college.newCourse("CO" + i))
				.subscribe();

		dataContext.getObjectContext().commitChanges();

		return this;
	}

	public TestContext getTestContext() {
		return testContext;
	}

	public DataContext getDataContext() {
		return dataContext;
	}

	public static void main(String[] args) {
		SolrTestData solrTestData = new SolrTestData().schema("OD9910");
		solrTestData.create();
	}
}
