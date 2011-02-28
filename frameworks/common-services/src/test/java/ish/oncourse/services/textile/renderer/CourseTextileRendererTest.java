package ish.oncourse.services.textile.renderer;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CourseTextileRendererTest {

	@Mock
	private ICourseService courseService;

	@Mock
	private IPageRenderer pageRenderer;

	@Mock
	private ITagService tagService;

	/**
	 * service under the test
	 */
	private CourseTextileRenderer courseTextileRenderer;

	private ValidationErrors errors;

	private Course course;

	private Tag tag;

	private static final String TEST_COURSE_CODE = "realCode";

	private static final String SUCCESSFULLY_RENDERED = "success";

	private static final String TAG_NAME = "tagName";

	@Before
	public void init() {
		errors = new ValidationErrors();
		course = new Course();
		tag = new Tag();
		courseTextileRenderer = new CourseTextileRenderer(courseService,
				pageRenderer, tagService);
		when(courseService.getCourse(Course.CODE_PROPERTY, TEST_COURSE_CODE))
				.thenReturn(course);
		when(tagService.getSubTagByName(TAG_NAME)).thenReturn(tag);
		when(courseService.getCourse(TAG_NAME)).thenReturn(course);
		when(
				pageRenderer.renderPage(eq(TextileUtil.TEXTILE_COURSE_PAGE),
						anyMap())).thenReturn(SUCCESSFULLY_RENDERED);
	}

	/**
	 * Emulates the situation when {course code:"realCode"} is rendered
	 */
	@Test
	public void renderCourseWithGivenCode() {
		String result = courseTextileRenderer.render("{course code:\""
				+ TEST_COURSE_CODE + "\"}", errors);
		assertFalse(errors.hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}

	/**
	 * Emulates the situation when random choice of the course is restricted
	 * with attributes, ie {course tag:&quot;tag name&quot;
	 * showclasses:&quot;true&quot;} is rendered
	 */
	@Test
	public void renderRandomRestrictedCourse() {
		String result = courseTextileRenderer.render("{course tag:\""
				+ TAG_NAME + "\" showclasses:\"true\"}",
				errors);
		assertFalse(errors.hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
}
