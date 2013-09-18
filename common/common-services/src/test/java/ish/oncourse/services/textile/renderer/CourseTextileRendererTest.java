package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.IPageRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
	private CourseTextileRenderer renderer;

	private Course course;

	private Tag tag;

	private static final String TEST_COURSE_CODE = "realCode";

	private static final String SUCCESSFULLY_RENDERED = "success";

	private static final String TAG_NAME = "tagName";

	@Before
	public void init() {
		course = new Course();
		tag = new Tag();
		renderer = new CourseTextileRenderer(courseService,
				pageRenderer, tagService);
		when(courseService.getCourse(Course.CODE_PROPERTY, TEST_COURSE_CODE))
				.thenReturn(course);
		when(tagService.getTagByFullPath(TAG_NAME)).thenReturn(tag);
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
		String result = renderer.render("{course code:\""
				+ TEST_COURSE_CODE + "\"}");
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}

	/**
	 * Emulates the situation when random choice of the course is restricted
	 * with attributes, ie {course tag:&quot;tag name&quot;
	 * showclasses:&quot;true&quot;} is rendered
	 */
	@Test
	public void renderRandomRestrictedCourse() {
		String result = renderer.render("{course tag:\""
				+ TAG_NAME + "\" showclasses:\"true\"}");
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
}
