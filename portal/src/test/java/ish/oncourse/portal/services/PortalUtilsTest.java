package ish.oncourse.portal.services;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.preference.PreferenceController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUtilsTest {
	
	public static final String APP_PACKAGE = "ish.oncourse.portal";
	public static final String CONTEXT_PATH = "src/main/resources/desktop/ish/oncourse/portal/pages";
	
	@Before
	public void setup() throws Exception {

	}
	
	@After
	public void tearDown() {}

	@Test
	public void getCourseDetailsURLByTest() {
		PreferenceController preferenceController = mock(PreferenceController.class);
		when(preferenceController.getCollegeURL()).thenReturn("test.domain");
		Course course = mock(Course.class);
		when(course.getCode()).thenReturn("test_code_1");
		assertEquals(String.format(PortalUtils.URL_COURSE_TEMPLATE, "test.domain", course.getCode()),
			PortalUtils.getCourseDetailsURLBy(course, PortalUtils.getDomainName(preferenceController)));
		assertNull(PortalUtils.getCourseDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        when(preferenceController.getCollegeURL()).thenReturn("test.domain/");

		assertEquals(String.format(PortalUtils.URL_COURSE_TEMPLATE, "test.domain", course.getCode()),
			PortalUtils.getCourseDetailsURLBy(course, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getCourseDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        when(preferenceController.getCollegeURL()).thenReturn("http://test.domain/");
        assertEquals(String.format(PortalUtils.URL_COURSE_TEMPLATE, "test.domain", course.getCode()),
                PortalUtils.getCourseDetailsURLBy(course, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getCourseDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        when(preferenceController.getCollegeURL()).thenReturn("https://test.domain/");
        assertEquals(String.format(PortalUtils.URL_COURSE_TEMPLATE, "test.domain", course.getCode()),
                PortalUtils.getCourseDetailsURLBy(course, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getCourseDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        assertNull(PortalUtils.getCourseDetailsURLBy(course, null));
        assertNull(PortalUtils.getClassDetailsURLBy(null, null));

    }
	
	@Test
	public void getClassDetailsURLByTest() {
        PreferenceController preferenceController = mock(PreferenceController.class);
        when(preferenceController.getCollegeURL()).thenReturn("test.domain");

		Course course = mock(Course.class);
		when(course.getCode()).thenReturn("test_code_1");
		CourseClass cClass = mock(CourseClass.class);
		when(cClass.getCode()).thenReturn("test_cc_code_2");
		when(cClass.getCourse()).thenReturn(course);

        assertEquals(String.format(PortalUtils.URL_CLASS_TEMPLATE, "test.domain", course.getCode(), cClass.getCode()),
			PortalUtils.getClassDetailsURLBy(cClass, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getClassDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        when(preferenceController.getCollegeURL()).thenReturn("test.domain/");
		assertEquals(String.format(PortalUtils.URL_CLASS_TEMPLATE, "test.domain", course.getCode(), cClass.getCode()),
			PortalUtils.getClassDetailsURLBy(cClass, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getClassDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        when(preferenceController.getCollegeURL()).thenReturn("http://test.domain/");
        assertEquals(String.format(PortalUtils.URL_CLASS_TEMPLATE, "test.domain", course.getCode(), cClass.getCode()),
                PortalUtils.getClassDetailsURLBy(cClass, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getClassDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        when(preferenceController.getCollegeURL()).thenReturn("https://test.domain/");
        assertEquals(String.format(PortalUtils.URL_CLASS_TEMPLATE, "test.domain", course.getCode(), cClass.getCode()),
                PortalUtils.getClassDetailsURLBy(cClass, PortalUtils.getDomainName(preferenceController)));
        assertNull(PortalUtils.getClassDetailsURLBy(null, PortalUtils.getDomainName(preferenceController)));

        assertNull(PortalUtils.getClassDetailsURLBy(cClass, null));
        assertNull(PortalUtils.getClassDetailsURLBy(null, null));


    }
}
