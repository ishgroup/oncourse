package ish.oncourse.portal.pages;


import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Collections;

import javax.sql.DataSource;

import ish.oncourse.portal.service.TestModule;
import ish.oncourse.portal.services.AppModule;
import ish.oncourse.test.ServiceTest;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnsubscribeTest extends ServiceTest {
	
	public static final String APP_PACKAGE = "ish.oncourse.portal";
	public static final String CONTEXT_PATH = "src/main/resources/ish/oncourse/portal/pages";
	
	@Before
	public void setup() throws Exception {
		initTest(APP_PACKAGE, "", CONTEXT_PATH, TestModule.class);
		
		InputStream st = UnsubscribeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/portal/unsubscribeDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
	}
	
	@After
	public void tearDown() {
		
	}

	@Test
	public void testSubscribedPageLoad() {
		Document doc = getPageTester().renderPage("unsubscribe/1002-tr78gh2");
		assertNotNull(doc.getElementById("unsubscribeForm"));
	}
	
	@Test
	public void testNotSubscribedPageLoad() {
		Document doc = getPageTester().renderPage("unsubscribe/1003-tr78gh2");
		assertNotNull(doc.getElementById("unsubscribed"));
	}
	
	@Test
	public void testUnsubscribe() {
		PageTester tester = getPageTester();
		Document doc = tester.renderPage("unsubscribe/1002-tr78gh2");
		
		Element unsubscribeButton = doc.getElementById("unsubscribeAction");
		assertNotNull(doc.getElementById("unsubscribeForm"));
		
		doc = tester.clickSubmit(unsubscribeButton, Collections.EMPTY_MAP);
		assertNotNull(doc.getElementById("postUnsubscribeMessage"));
		
		doc = tester.renderPage("unsubscribe/1002-tr78gh2");
		assertNotNull(doc.getElementById("unsubscribed"));
	}
	
}
