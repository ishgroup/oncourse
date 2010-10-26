package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class TagsTextileValidator implements IValidator {

	private ITagService tagService;

	public TagsTextileValidator(ITagService tagService) {
		this.tagService = tagService;
	}

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.TAGS_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}

		TextileUtil.checkParamsUniquence(tag, errors,
				TextileUtil.TAGS_ENTITY_TYPE_PARAM,
				TextileUtil.TAGS_MAX_LEVELS_PARAM,
				TextileUtil.TAGS_SHOW_DETAIL_PARAM,
				TextileUtil.TAGS_HIDE_TOP_LEVEL,
				TextileUtil.TAGS_FILTERED_PARAM, TextileUtil.PARAM_NAME);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.PARAM_NAME, TextileUtil.TAGS_ENTITY_TYPE_PARAM);
		String name = tagParams.get(TextileUtil.PARAM_NAME);
		String entityType = tagParams.get(TextileUtil.TAGS_ENTITY_TYPE_PARAM);
		if (name != null) {
			Tag tagObj = tagService.getSubTagByName(name);
			if (tagObj == null) {
				errors.addFailure("There are no tag with such a name:" + name);
			}
		}
		if (entityType != null) {
			try {
				Class<?> clazz = Class.forName(entityType);
				// TODO add the check for only the taggable entity types
				clazz.getName();
			} catch (ClassNotFoundException e) {
				errors
						.addFailure("There are no entities with such an entiry type: "
								+ entityType);
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

}
