package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class TagsTextileValidator extends AbstractTextileValidator {

	private ITagService tagService;

	public TagsTextileValidator(ITagService tagService) {
		this.tagService = tagService;
	}

	@Override
	protected void initValidator() {
		textileType = TextileType.TAGS;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String name = tagParams.get(TagsTextileAttributes.TAGS_PARAM_NAME.getValue());

		if (name != null) {
			Tag tagObj = tagService.getTagByFullPath(name);
			if (tagObj == null) {
				errors.addFailure(getTagNotFoundByName(name),
						ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
	}

	public String getFormatErrorMessage(String tag) {
		return "The {tags} tag '" + tag + "' doesn't match {tags maxLevels:\"digit\" "
				+ "showDetail:\"true|false\" hideTopLevel:\"true|false\""
				+ " name:\"name\" "
				+ " multiSelect:\"true|false\""
				+ "}";
	}

	public String getTagNotFoundByName(String name) {
		return "There are no tag with such a name:" + name;
	}

	public String getEntityTypeNotFoundByName(String entityType) {
		return "There are no entities with such an entiry type: " + entityType;
	}

}
