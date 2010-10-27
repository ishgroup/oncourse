package ish.oncourse.services.textile.validator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TagsTextileValidatorTest extends CommonValidatorTest {

	private static final String NOT_EXISTING_ENTITY_TYPE = "NotExistingEntity";
	private static final String EXISTING_TAG_NAME = "tag1";
	private static final String NOT_EXISTING_TAG_NAME = "tag2";

	@Mock
	private ITagService tagService;
	private Tag tag;

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<String, String>();
		for (TagsTextileAttributes attr : TagsTextileAttributes.values()) {
			switch (attr) {
			case TAGS_ENTITY_TYPE_PARAM:
				data
						.put(TagsTextileAttributes.TAGS_ENTITY_TYPE_PARAM
								.getValue(),
								"{tags entityType:\"Course\" entityType:\"AnotherType\"");
				break;
			case TAGS_FILTERED_PARAM:
				data.put(TagsTextileAttributes.TAGS_FILTERED_PARAM.getValue(),
						"{tags isFiltered:\"true\" isFiltered:\"false\"");
				break;
			case TAGS_HIDE_TOP_LEVEL:
				data
						.put(TagsTextileAttributes.TAGS_HIDE_TOP_LEVEL
								.getValue(),
								"{tags isHidingTopLevelTags:\"true\" isHidingTopLevelTags:\"false\"");
				break;
			case TAGS_MAX_LEVELS_PARAM:
				data.put(
						TagsTextileAttributes.TAGS_MAX_LEVELS_PARAM.getValue(),
						"{tags maxLevels:\"2\" maxLevels:\"3\"");
				break;
			case TAGS_PARAM_NAME:
				data.put(TagsTextileAttributes.TAGS_PARAM_NAME.getValue(),
						"{tags name:\"tagName1\" name:\"tagName2\"");
				break;
			case TAGS_SHOW_DETAIL_PARAM:
				data.put(TagsTextileAttributes.TAGS_SHOW_DETAIL_PARAM
						.getValue(),
						"{tags showtopdetail:\"true\" showtopdetail:\"false\"");
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
		when(tagService.getSubTagByName(EXISTING_TAG_NAME)).thenReturn(tag);
		when(tagService.getSubTagByName(NOT_EXISTING_TAG_NAME))
				.thenReturn(null);
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
		assertEquals(((TagsTextileValidator) validator)
				.getTagNotFoundByName(NOT_EXISTING_TAG_NAME), errors.toString());
	}

	/**
	 * Emulates the situations when there's entityType with given name and when
	 * there's no entityType with such a name
	 */
	@Test
	public void testEntityTypeAttribute() {
		String tag = "{tags entityType:\"Course\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{tags entityType:\"" + NOT_EXISTING_ENTITY_TYPE + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertEquals(((TagsTextileValidator) validator)
				.getEntityTypeNotFoundByName(NOT_EXISTING_ENTITY_TYPE), errors
				.toString());
	}
}
