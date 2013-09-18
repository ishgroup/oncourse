package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseListSortValue;
import ish.oncourse.util.IPageRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

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
	private CourseListTextileRenderer renderer;

	@Before
	public void init() {
		renderer = new CourseListTextileRenderer(courseService, pageRenderer,
				tagService);
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
		String result = renderer.render("{courses tag:\"" + tagName
				+ "\" limit:\"" + limit + "\" sort:\"date\" order:\"desc\"}");
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}

	@Test
	public void failedCourseListRendering() {
		String textile = "{courses broken syntax}";
		String result = renderer.render(textile);
		assertTrue(renderer.getErrors().hasFailures());
		assertEquals("<span class=\"richtext_error\">Syntax error in \"{courses broken syntax}\"</span><ol><li>The courseList tag '{courses broken syntax}' doesn't match {courses tag:\"/tag\" limit:\"digit\" sort:\"date|alphabetical|availability\" order:\"asc|desc\" style:\"titles|details\"}</li></ol>", result);
	}

}
