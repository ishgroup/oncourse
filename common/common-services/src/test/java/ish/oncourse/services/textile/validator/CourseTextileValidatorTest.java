package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.attrs.CourseTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseTextileValidatorTest extends CommonValidatorTest {
	private static final String NOT_EXISTING_COURSE_CODE = "BBBBB";
	private static final String EXISTING_COURSE_CODE = "AAAAA";
	private static final String COURSE = "{course}";
	private static final String EXISTING_COURSE_TAG = "tag1";
	private static final String NOT_EXISTING_COURSE_TAG = "tag2";
	@Mock
	private ICourseService courseService;

	@Mock
	private ITagService tagService;

	private Course course;

	private Tag tag;

	public void init() {
		errors = new ValidationErrors();
		validator = new CourseTextileValidator(courseService, tagService);
		course = new Course();
		tag = new Tag();
		when(courseService.getCourse(Course.CODE_PROPERTY, EXISTING_COURSE_CODE)).thenReturn(course);
		when(courseService.getCourse(Course.CODE_PROPERTY, NOT_EXISTING_COURSE_CODE)).thenReturn(null);

		when(tagService.getTagByFullPath(EXISTING_COURSE_TAG)).thenReturn(tag);
		when(tagService.getTagByFullPath(NOT_EXISTING_COURSE_TAG)).thenReturn(null);
	}

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<>();
		for (CourseTextileAttributes attr : CourseTextileAttributes.values()) {
			switch (attr) {
			case COURSE_PARAM_CODE:
				data.put(CourseTextileAttributes.COURSE_PARAM_CODE.getValue(), "{course code:\"code1\" code:\"code2\"}");
				break;
			case COURSE_PARAM_SHOW_CLASSES:
				data.put(CourseTextileAttributes.COURSE_PARAM_SHOW_CLASSES.getValue(),
						"{course showclasses:\"true\" showclasses:\"false\"}");
				break;
			case COURSE_PARAM_TAG:
				data.put(CourseTextileAttributes.COURSE_PARAM_TAG.getValue(), "{course tag:\"tag1\" tag:\"tag2\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getTextileForSmokeTest() {
		return COURSE;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{course showclasses:\"bbbbbbb\"}";
	}

	/**
	 * Emulates the situations when there's course with given code and when
	 * there's no course with such a code
	 */
	@Test
	public void validatingCourseCodeTest() {
		String tag = "{course code:\"" + EXISTING_COURSE_CODE + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{course code:\"" + NOT_EXISTING_COURSE_CODE + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(
				((CourseTextileValidator) validator).getCourseNotFoundByCode(NOT_EXISTING_COURSE_CODE)));
	}

	/**
	 * Emulates the situations when there's tag with given name and when there's
	 * no tag with such a name
	 */
	@Test
	public void validatingCourseTagTest() {
		String tag = "{course tag:\"" + EXISTING_COURSE_TAG + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{course tag:\"" + NOT_EXISTING_COURSE_TAG + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(
				((CourseTextileValidator) validator).getTagNotFoundByName(NOT_EXISTING_COURSE_TAG)));
	}
	
	/**
	 * Emulates the situation when there is a new line in {course}, shouldn't be any errors.
	 */
	@Test
	public void courseWithNewLineTest() {
		String tag = "{course \n tag:\"" + EXISTING_COURSE_TAG + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}
}
