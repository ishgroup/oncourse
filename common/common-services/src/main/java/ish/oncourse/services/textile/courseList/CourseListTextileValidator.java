package ish.oncourse.services.textile.courseList;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.AbstractTextileValidator;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class CourseListTextileValidator extends AbstractTextileValidator {

	private ITagService tagService;

	public CourseListTextileValidator(ITagService tagService) {
		this.tagService = tagService;
	}

	@Override
	protected void initValidator() {
		textileType = TextileType.COURSE_LIST;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String tagged = tagParams.get(TextileAttrs.tag.getValue());

		if (tagged != null) {
			Tag tagEntity = tagService.getTagByFullPath(tagged);
			if (tagEntity == null) {
				errors.addFailure(getTagNotFoundByName(tagged),
						ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
	}

	@Override
	public String getFormatErrorMessage(String tag) {
		return "The courseList tag '"
				+ tag
				+ "' doesn't match {courses tag:\"/tag\" " +
				"limit:\"digit\" " +
				"sort:\"date|alphabetical|availability\" " +
				"order:\"asc|desc\" " +
				"style:\"titles|details\" " +
				"showTags:\"true|false\"}";
	}

	public String getTagNotFoundByName(String tagged) {
		return "There are no tags with such a name: " + tagged;
	}

}
