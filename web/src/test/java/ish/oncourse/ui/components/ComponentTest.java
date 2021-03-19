package ish.oncourse.ui.components;

import ish.oncourse.cayenne.cache.ICacheEnabledService;
import ish.oncourse.model.Course;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.ModuleBinder;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.site.GetWebSite;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.test.TestContext;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.test.tapestry.TestModule;
import ish.oncourse.ui.utils.CourseItemModel;
import org.apache.cayenne.cache.QueryCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.test.PageTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.EDITOR;
import static ish.oncourse.ui.pages.ComponentTestPage.CURRENT_RENDERED_COMPONENT_ATTRIBUTE_NAME;

public class ComponentTest extends ServiceTest {

	private static Logger logger = LogManager.getLogger();
	private static boolean enableWriteToFile;

	@Test
	public void coursesListSkeletonComponentTest() throws IOException {
		Request request = prepateRequest("cce-main", "ui/CoursesListSkeleton");
		ICourseService courseService = getPageTester().getService(ICourseService.class);

		List<Course> courses = courseService.getCourses(0, Integer.MAX_VALUE);
		request.setAttribute("ui_test_courses", courses);

		request.setAttribute("ui_test_coursesCount", courses.size());
		request.setAttribute("ui_test_sitesParameter", "");
		request.setAttribute("ui_test_searchParams", new SearchParams());
		request.setAttribute("ui_test_coursesIds", courses.stream().map(Course::getId).collect(Collectors.toSet()));
		request.setAttribute("ui_test_debugInfoMap", new HashMap<>());

		Document document = tester.renderPage("ui/ComponentTestPage");

		writeToFile(document, "CoursesListSkeleton.html");
	}

	@Test
	public void relatedProductsComponentTest() throws IOException {
		Request request = prepateRequest("cce-main", "ui/relatedporducts");

		ICourseService courseService = getPageTester().getService(ICourseService.class);
		Course course = courseService.getCourseByCode("MSW1");

		request.setAttribute("ui_test_courseItemModel", CourseItemModel.valueOf(course, new SearchParams()));

		Document document = tester.renderPage("ui/ComponentTestPage");
		Assert.assertTrue(!document.find("html/body/div").isEmpty());

		writeToFile(document, "RelatedProducts.html");
	}

	@Test
	public void courseRelationsComponentTest() throws IOException {
		Request request = prepateRequest("cce-main", "ui/CourseRelations");

		ICourseService courseService = getPageTester().getService(ICourseService.class);
		Course course = courseService.getCourseByCode("MSW1");

		request.setAttribute("ui_test_courseItemModel", CourseItemModel.valueOf(course, new SearchParams()));
		request.setAttribute("ui_test_course", course);

		Document document = tester.renderPage("ui/ComponentTestPage");
		Assert.assertTrue(!document.find("html/body/div/ul/li/a").getAttribute("href").isEmpty());

		writeToFile(document, "CourseRelations.html");
	}

	@Test
	public void courseRelationsComponentOnlyCoursePropertyTest() throws IOException {
		Request request = prepateRequest("cce-main", "ui/CourseRelations");

		ICourseService courseService = getPageTester().getService(ICourseService.class);
		Course course = courseService.getCourseByCode("MSW1");

		request.setAttribute("ui_test_course", course);

		Document document = tester.renderPage("ui/ComponentTestPage");
		Assert.assertTrue(!document.find("html/body/div/ul/li/a").getAttribute("href").isEmpty());

		writeToFile(document, "CourseRelationsOnlyCourse.html");
	}

	@Before
	public void before() {
		setUpProperties();
		setUpTestContext(false, false, null);
		setUpTestModule();
		tester = new PageTester("ish.oncourse.ui", "ui-test", "src/main/webapp", ComponentTestModule.class);
	}

	private void setUpProperties() {
		Properties properties = new Properties();

		try {
			properties.load(new FileReader(new File(getClass().getClassLoader().getResource("context.properties").getFile())));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (Boolean.parseBoolean(properties.getProperty("oncourse.test.rewriteprops"))) {
			System.setProperty("oncourse.jdbc.url", properties.getProperty("oncourse.jdbc.url"));
			System.setProperty("oncourse.jdbc.user", properties.getProperty("oncourse.jdbc.user"));
			System.setProperty("oncourse.jdbc.password", properties.getProperty("oncourse.jdbc.password"));
		}

		ComponentTest.enableWriteToFile = Boolean.parseBoolean(properties.getProperty("oncourse.ui.yield"));
	}

	private void setUpTestModule() {
		ComponentTestModule.dataSource.set(testContext.getDS());
		ComponentTestModule.serverRuntime.set(testContext.getServerRuntime());
	}

	private void setUpTestContext(boolean shouldCleanTables, boolean shouldCreateTables, QueryCache queryCache) {
		testContext = new TestContext().queryCache(queryCache);
		testContext.shouldCleanTables(shouldCleanTables);
		testContext.shouldCreateTables(shouldCreateTables);
		testContext.open();
	}

	private Request prepateRequest(String siteKeyHeader, String componentName) {
		WebSite webSite = new GetWebSite(siteKeyHeader, testContext.getServerRuntime().newContext()).get();
		Request request = tester.getService(TestableRequest.class);
		request.setAttribute("currentWebSite", webSite);
		request.setAttribute("currentCollege", webSite.getCollege());
		request.setAttribute(CURRENT_RENDERED_COMPONENT_ATTRIBUTE_NAME, componentName);
		return request;
	}

	private void writeToFile(Document document, String fileName) throws IOException {
		if (!enableWriteToFile) {
			return;
		}
		FileWriter fileWriter = new FileWriter("./" + fileName);
		fileWriter.write(document.toString());
		fileWriter.close();
	}

	public static class ComponentTestModule extends TestModule {
		public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
			configuration.add(new LibraryMapping("ui", "ish.oncourse.ui"));
		}

		public static void bind(ServiceBinder binder) {
			new ModuleBinder()
					.dataSource(resources -> TestModule.dataSource.get())
					.serverRuntime(resources -> TestModule.serverRuntime.get())
					.cacheEnabledService(resources -> new ICacheEnabledService() {
						@Override
						public boolean isCacheEnabled() {
							return false;
						}

						@Override
						public void setCacheEnabled(Boolean enabled) {
						}

						@Override
						public CacheDisableReason getDisableReason() {
							return EDITOR;
						}

						@Override
						public void setCacheEnabled(CacheDisableReason reason, Boolean enabled) {
						}
					})
					.bind(binder);
		}
	}
}
