package ish.oncourse.services.textile.renderer;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseListSortValue;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CourseListTextileRendererTest {
	private static final String SUCCESSFULLY_RENDERED = "success";

	@Mock
	private ICourseService courseService;

	@Mock
	private IPageRenderer pageRenderer;

	@Mock
	private ITagService tagService;

	/**
	 * service under the test
	 */
	private CourseListTextileRenderer courseListTextileRenderer;
	private ValidationErrors errors;

	@Before
	public void init() {
		courseListTextileRenderer = new CourseListTextileRenderer(courseService, pageRenderer,
				tagService);
		errors = new ValidationErrors();
	}

	@Test
	public void courseListWithParameters() {

		List<Course> courses = new ArrayList<>();
		courses.add(new Course());
		String tagName = "/tag";
		CourseListSortValue sort = CourseListSortValue.DATE;
		Boolean isAscending = false;
		Integer limit = 5;
		Tag tag = new Tag();
		when(tagService.getTagByFullPath(tagName)).thenReturn(tag);
		when(courseService.getCourses(tagName, sort, isAscending, limit)).thenReturn(courses);
		Map<String, Object> params = new HashMap<>();
		params.put(TextileUtil.TEXTILE_COURSE_LIST_PAGE_PARAM, courses);
		when(pageRenderer.renderPage(TextileUtil.TEXTILE_COURSE_LIST_PAGE, params)).thenReturn(
				SUCCESSFULLY_RENDERED);
		String result = courseListTextileRenderer.render("{courses tag:\"" + tagName
				+ "\" limit:\"" + limit + "\" sort:\"date\" order:\"desc\"}", errors);
		assertFalse(errors.hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}

	@Test
	public void failedCourseListRendering() {
		String textile = "{courses broken syntax}";
		String result = courseListTextileRenderer.render(textile, errors);
		assertTrue(errors.hasFailures());
		assertEquals(textile, result);
	}

}
