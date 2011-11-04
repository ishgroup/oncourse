package ish.oncourse.services.textile.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.attrs.CourseListTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CourseListTextileValidatorTest extends CommonValidatorTest {
	private static final String EXISTING_COURSE_TAG = "tag1";
	private static final String NOT_EXISTING_COURSE_TAG = "tag2";

	@Mock
	private ITagService tagService;

	private Tag tag;

	public void init() {
		errors = new ValidationErrors();
		validator = new CourseListTextileValidator(tagService);
		tag = new Tag();
		when(tagService.getTagByFullPath(EXISTING_COURSE_TAG)).thenReturn(tag);
		when(tagService.getTagByFullPath(NOT_EXISTING_COURSE_TAG)).thenReturn(null);
	}

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<String, String>();
		for (CourseListTextileAttributes attr : CourseListTextileAttributes.values()) {
			switch (attr) {
			case COURSE_LIST_PARAM_LIMIT:
				data.put(CourseListTextileAttributes.COURSE_LIST_PARAM_LIMIT.getValue(),
						"{courses limit:\"1\" limit:\"2\"}");
				break;
			case COURSE_LIST_PARAM_ORDER:
				data.put(CourseListTextileAttributes.COURSE_LIST_PARAM_ORDER.getValue(),
						"{course order:\"asc\" order:\"desc\"}");
				break;
			case COURSE_LIST_PARAM_SORT:
				data.put(CourseListTextileAttributes.COURSE_LIST_PARAM_SORT.getValue(),
						"{courses sort:\"date\" sort:\"alphabetical\"}");
				break;
			case COURSE_LIST_PARAM_TAG:
				data.put(CourseListTextileAttributes.COURSE_LIST_PARAM_TAG.getValue(),
						"{courses tag:\"tag1\" tag:\"tag2\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{courses}";
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{courses limit:\"bbbbbbb\"}";
	}

	/**
	 * Emulates the situations when there's tag with given name and when there's
	 * no tag with such a name
	 */
	@Test
	public void validatingCourseTagTest() {
		String tag = "{courses tag:\"" + EXISTING_COURSE_TAG + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{courses tag:\"" + NOT_EXISTING_COURSE_TAG + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(
				((CourseListTextileValidator) validator).getTagNotFoundByName(NOT_EXISTING_COURSE_TAG)));
	}
	
	/**
	 * Emulates the situation when there is a new line in {courses}, shouldn't be any errors.
	 */
	@Test
	public void coursesWithNewLineTest() {
		String tag = "{courses \n tag:\"" + EXISTING_COURSE_TAG + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}

}
