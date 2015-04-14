package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
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
public class TagsTextileValidatorTest extends CommonValidatorTest {

	private static final String EXISTING_TAG_NAME = "tag1";
	private static final String NOT_EXISTING_TAG_NAME = "tag2";

	@Mock
	private ITagService tagService;
	private Tag tag;

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<>();
		for (TagsTextileAttributes attr : TagsTextileAttributes.values()) {
			switch (attr) {
			case TAGS_HIDE_TOP_LEVEL:
				data.put(TagsTextileAttributes.TAGS_HIDE_TOP_LEVEL.getValue(),
						"{tags hideTopLevel:\"true\" hideTopLevel:\"false\"}");
				break;
			case TAGS_MAX_LEVELS_PARAM:
				data.put(TagsTextileAttributes.TAGS_MAX_LEVELS_PARAM.getValue(),
						"{tags maxLevels:\"2\" maxLevels:\"3\"}");
				break;
			case TAGS_PARAM_NAME:
				data.put(TagsTextileAttributes.TAGS_PARAM_NAME.getValue(), "{tags name:\"tagName1\" name:\"tagName2\"}");
				break;
			case TAGS_SHOW_DETAIL_PARAM:
				data.put(TagsTextileAttributes.TAGS_SHOW_DETAIL_PARAM.getValue(),
						"{tags showDetail:\"true\" showDetail:\"false\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{tags id:\"1\"}";
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{tags}";
	}

	@Override
	public void init() {
		errors = new ValidationErrors();
		validator = new TagsTextileValidator(tagService);
		tag = new Tag();
		when(tagService.getTagByFullPath(EXISTING_TAG_NAME)).thenReturn(tag);
		when(tagService.getTagByFullPath(NOT_EXISTING_TAG_NAME)).thenReturn(null);
	}

	/**
	 * Emulates the situations when there's tag with given name and when there's
	 * no tag with such a name
	 */
	@Test
	public void testNameAttribute() {
		String tag = "{tags name:\"" + EXISTING_TAG_NAME + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{tags name:\"" + NOT_EXISTING_TAG_NAME + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(
				((TagsTextileValidator) validator).getTagNotFoundByName(NOT_EXISTING_TAG_NAME)));
	}

	/**
	 * Emulates the situation when there is a new line in {tags}, shouldn't be
	 * any errors.
	 */
	@Test
	public void tagsWithNewLineTest() {
		String tag = "{tags \n name:\"" + EXISTING_TAG_NAME + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}
}
