package ish.oncourse.ui.pages;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.site.GetWebSite;
import ish.oncourse.test.TestContext;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.ui.services.ExtendedTestModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.test.PageTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

@Ignore
public class CoursesSkeletonPageTest extends ServiceTest {

	private static Logger logger = LogManager.getLogger();

	@Before
	public void before() {

		testContext = new TestContext().queryCache(null);
		testContext.shouldCleanTables(false);
		testContext.shouldCreateTables(false);
		testContext.open();

		ExtendedTestModule.dataSource.set(testContext.getDS());
		ExtendedTestModule.serverRuntime.set(testContext.getServerRuntime());

		tester = new PageTester("ish.oncourse.ui", "ui-test", "src/main/webapp", ExtendedTestModule.class);
	}

	@Test
	public void test() throws IOException {
		WebSite webSite = new GetWebSite("cce-main", testContext.getServerRuntime().newContext()).get();
		Request request = tester.getService(TestableRequest.class);
		request.setAttribute("currentWebSite", webSite);
		request.setAttribute("currentCollege", webSite.getCollege());

		Document document = tester.renderPage("CoursesSkeleton");
		writeToFile(document);
	}

	private void writeToFile(Document document) throws IOException {
		FileWriter fileWriter = new FileWriter("./dried_courses.html");
		fileWriter.write(document.toString());
		fileWriter.close();
	}
}
