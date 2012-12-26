package ish.oncourse.portal.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.WebHostName;
import ish.oncourse.portal.service.TestModule;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import static org.junit.Assert.*;

public class PortalUtilsTest extends ServiceTest {
	
	public static final String APP_PACKAGE = "ish.oncourse.portal";
	public static final String CONTEXT_PATH = "src/main/resources/desktop/ish/oncourse/portal/pages";
	
	@Before
	public void setup() throws Exception {
		initTest(APP_PACKAGE, "", CONTEXT_PATH, TestModule.class);
	}
	
	@After
	public void tearDown() {}

	@Test
	public void getCourseDetailsURLByTest() {
		IWebSiteService correctWebsiteService = mock(IWebSiteService.class);
		WebHostName whn1 = mock(WebHostName.class);
		when(correctWebsiteService.getCurrentDomain()).thenReturn(whn1);
		when(whn1.getName()).thenReturn("test.domain");
		Course course = mock(Course.class);
		when(course.getCode()).thenReturn("test_code_1");
		assertEquals("Invalid course details generated", String.format(PortalUtils.URL_COURSE_TEMPLATE, whn1.getName(), course.getCode()), 
			PortalUtils.getCourseDetailsURLBy(course, correctWebsiteService));
		assertEquals("Invalid course details generated", StringUtils.EMPTY, PortalUtils.getCourseDetailsURLBy(null, correctWebsiteService));
		IWebSiteService updatedWebsiteService = mock(IWebSiteService.class);
		WebHostName whn2 = mock(WebHostName.class);
		when(whn2.getName()).thenReturn("test.domain/");
		when(updatedWebsiteService.getCurrentDomain()).thenReturn(whn2);
		assertEquals("Invalid course details generated", String.format(PortalUtils.URL_COURSE_TEMPLATE, whn1.getName(), course.getCode()), 
			PortalUtils.getCourseDetailsURLBy(course, updatedWebsiteService));
		assertEquals("Invalid course details generated", StringUtils.EMPTY, PortalUtils.getCourseDetailsURLBy(null, updatedWebsiteService));
	}
	
	@Test
	public void getClassDetailsURLByTest() {
		IWebSiteService correctWebsiteService = mock(IWebSiteService.class);
		WebHostName whn1 = mock(WebHostName.class);
		when(correctWebsiteService.getCurrentDomain()).thenReturn(whn1);
		when(whn1.getName()).thenReturn("test.domain");
		Course course = mock(Course.class);
		when(course.getCode()).thenReturn("test_code_1");
		CourseClass cClass = mock(CourseClass.class);
		when(cClass.getCode()).thenReturn("test_cc_code_2");
		when(cClass.getCourse()).thenReturn(course);
		assertEquals("Invalid class details generated", String.format(PortalUtils.URL_CLASS_TEMPLATE, whn1.getName(), course.getCode(), cClass.getCode()), 
			PortalUtils.getClassDetailsURLBy(cClass, correctWebsiteService));
		assertEquals("Invalid class details generated", StringUtils.EMPTY, PortalUtils.getClassDetailsURLBy(null, correctWebsiteService));
		IWebSiteService updatedWebsiteService = mock(IWebSiteService.class);
		WebHostName whn2 = mock(WebHostName.class);
		when(whn2.getName()).thenReturn("test.domain/");
		when(updatedWebsiteService.getCurrentDomain()).thenReturn(whn2);
		assertEquals("Invalid class details generated", String.format(PortalUtils.URL_CLASS_TEMPLATE, whn1.getName(), course.getCode(), cClass.getCode()), 
			PortalUtils.getClassDetailsURLBy(cClass, updatedWebsiteService));
		assertEquals("Invalid class details generated", StringUtils.EMPTY, PortalUtils.getClassDetailsURLBy(null, updatedWebsiteService));
	}
}
