package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.course.Sort;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.courseList.CourseListTextileRenderer;
import ish.oncourse.util.IPageRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseListTextileRendererTest {
	private static final String SUCCESSFULLY_RENDERED = "success";

	private static final  String ERRORS_RESULT = "<span class=\"richtext_error\">Syntax error in \"{courses broken syntax}\"</span><ol><li>The courseList tag '{courses broken syntax}' doesn't match {courses tag:\"/tag\" limit:\"digit\" sort:\"date|alphabetical|availability\" order:\"asc|desc\" style:\"titles|details\" showTags:\"true|false\"}</li></ol>";

	@Mock
	private ICourseService courseService;

	@Mock
	private IPageRenderer pageRenderer;

	@Mock
	private ITagService tagService;

	@Mock
	private ISearchService searchService;

	/**
	 * service under the test
	 */
	private CourseListTextileRenderer renderer;

	@Before
	public void init() {
		renderer = new CourseListTextileRenderer(courseService, pageRenderer,
				tagService, searchService);
	}

	@Test
	public void courseListWithParameters() {

		List<Course> courses = new ArrayList<>();
		courses.add(new Course());
		String tagName = "/tag";
		Sort sort = Sort.date;
		Boolean isAscending = false;
		Integer limit = 5;
		Tag tag = mock(Tag.class);
		Tag subject = mock(Tag.class);
		when(tagService.getTagByFullPath(tagName)).thenReturn(tag);
		when(tagService.getSubjectsTag()).thenReturn(subject);
		when(courseService.getCourses(tag, sort, isAscending, limit)).thenReturn(courses);
		when(pageRenderer.renderPage(eq(TextileUtil.TEXTILE_COURSE_LIST_PAGE), Matchers.any(HashMap.class))).thenReturn(
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
		assertEquals(ERRORS_RESULT, result);
	}

}
