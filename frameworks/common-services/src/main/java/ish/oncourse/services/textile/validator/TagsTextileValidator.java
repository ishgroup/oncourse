package ish.oncourse.services.textile.validator;

import java.util.List;
import java.util.Map;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.TagsTextileEntityTypes;

public class TagsTextileValidator implements IValidator {

	private ITagService tagService;

	public TagsTextileValidator(ITagService tagService) {
		this.tagService = tagService;
	}

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.TAGS_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}

		List<String> attrValues = TagsTextileAttributes.getAttrValues();
		TextileUtil.checkParamsUniquence(tag, errors, attrValues);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				attrValues);
		String name = tagParams.get(TagsTextileAttributes.TAGS_PARAM_NAME
				.getValue());
		String entityType = tagParams
				.get(TagsTextileAttributes.TAGS_ENTITY_TYPE_PARAM.getValue());
		if (name != null) {
			Tag tagObj = tagService.getSubTagByName(name);
			if (tagObj == null) {
				errors.addFailure(getTagNotFoundByName(name));
			}
		}
		if (entityType != null) {
			try {
				TagsTextileEntityTypes.valueOf(entityType);
			} catch (IllegalArgumentException e) {
				errors.addFailure(getEntityTypeNotFoundByName(entityType));
			}
		}
	}

	public String getFormatErrorMessage(String tag) {
		return "The {tags} tag '"
				+ tag
				+ "' doesn't match {tags entityType:\"Course\" maxLevels:\"digit\" "
				+ "showtopdetail:\"true|false\" isHidingTopLevelTags:\"true|false\""
				+ " isFiltered:\"true|false\" name:\"name\" }";
	}

	public String getTagNotFoundByName(String name) {
		return "There are no tag with such a name:" + name;
	}

	public String getEntityTypeNotFoundByName(String entityType) {
		return "There are no entities with such an entiry type: " + entityType;
	}

}
