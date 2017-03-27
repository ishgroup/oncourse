package ish.oncourse.function;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.PaymentInSuccessFailAbandonTest;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.courseclass.functions.GetCourseClassByFullCode;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ContextUtils;
import ish.oncourse.util.IPageRenderer;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

/**
 * Created by anarut on 3/24/17.
 */
public class GetCourseClassRenderedHtmlTest {
	
	private PageTester tester;
	private IPageRenderer pageRenderer;

	private ICayenneService cayenneService;
	private IWebSiteService webSiteService;
	
	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		
		tester = new PageTester("ish.oncourse.ui", "", PageTester.DEFAULT_CONTEXT_PATH, ServiceModule.class);
		
		pageRenderer = tester.getRegistry().getService(IPageRenderer.class);

		cayenneService = tester.getRegistry().getService(ICayenneService.class);
		webSiteService = tester.getRegistry().getService(IWebSiteService.class);

		InputStream st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/function/GetCourseClassRenderedHtmlTestDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
		DataSource refDataSource = ContextUtils.getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);
	}

	private void prepareRequestGlobals() {
		Request request = tester.getRegistry().getService(TestableRequest.class);
		Response response = tester.getRegistry().getService(TestableResponse.class);

		RequestGlobals requestGlobals = tester.getRegistry().getService(RequestGlobals.class);
		requestGlobals.storeRequestResponse(request, response);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testPageLoad() {
		CourseClass courseClass = GetCourseClassByFullCode.valueOf(cayenneService.sharedContext(), webSiteService.getCurrentCollege(), "DJPLF-1").get();


		prepareRequestGlobals();

		String result2 = GetCourseClassRenderedHtml.valueOf(pageRenderer, courseClass, true, false, false, Money.ZERO).get();
		System.out.println(result2);
	}
}
