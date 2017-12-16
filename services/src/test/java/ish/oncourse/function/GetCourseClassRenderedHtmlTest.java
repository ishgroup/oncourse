package ish.oncourse.function;

import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.test.TestContext;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.webservices.services.AppModule;
import org.apache.tapestry5.internal.services.HeartbeatImpl;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.test.PageTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

public class GetCourseClassRenderedHtmlTest {

	private PageTester tester;
	private IPageRenderer pageRenderer;

	private Environment environment;

	private ICayenneService cayenneService;

	private IDiscountService discountService;
	private PreferenceController preferenceController;

	private TestContext testContext;


	@Before
	public void setup() throws Exception {
		testContext = new TestContext().open();

		tester = new PageTester("ish.oncourse.ui", "", PageTester.DEFAULT_CONTEXT_PATH, AppModule.class);

		environment = tester.getRegistry().getService(Environment.class);
		environment.push(Heartbeat.class, new HeartbeatImpl());

		pageRenderer = tester.getRegistry().getService(IPageRenderer.class);

		cayenneService = tester.getRegistry().getService(ICayenneService.class);

		discountService = tester.getRegistry().getService(IDiscountService.class);
		preferenceController = tester.getRegistry().getService(PreferenceController.class);

		InputStream st = GetCourseClassRenderedHtmlTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/function/GetCourseClassRenderedHtmlTestDataSet.xml");


		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(testContext.getDS().getConnection(), null), dataSet);
	}

	private void prepareRequestGlobals() {
		Request request = tester.getRegistry().getService(TestableRequest.class);
		Response response = tester.getRegistry().getService(TestableResponse.class);

		RequestGlobals requestGlobals = tester.getRegistry().getService(RequestGlobals.class);
		requestGlobals.storeRequestResponse(request, response);
	}

	@After
	public void tearDown() throws Exception {
		testContext.close();
		if (tester != null && tester.getRegistry() != null) {
			tester.getRegistry().shutdown();
		}
	}

	@Test
	@Ignore
	public void testPageLoad() {
//		CourseClass courseClass = ObjectSelect.query(CourseClass.class)
//				.where(ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 5137172))
//				.selectOne(cayenneService.newContext());
//
//		CourseContext courseContext = CourseContext.valueOf(discountService.getPromotions(), new CheckClassAge().classAge(preferenceController.getHideClassOnWebsiteAge()));
//
//		prepareRequestGlobals();
//
//		String result = GetCourseClassRenderedHtml.valueOf(pageRenderer, courseClass, true, false, false, Money.ZERO, courseContext).get();
//		assertTrue(StringUtils.isNotBlank(result));
	}
}
