package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseListTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import java.util.List;
import java.util.Map;

public class CourseListTextileValidator implements IValidator {

	private ITagService tagService;

	public CourseListTextileValidator(ITagService tagService) {
		this.tagService = tagService;
	}

	@Override
	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.COURSE_LIST_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}

		List<String> attrValues = CourseListTextileAttributes.getAttrValues();
		TextileUtil.checkParamsUniquence(tag, errors, attrValues);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, attrValues);
		String tagged = tagParams.get(CourseListTextileAttributes.COURSE_LIST_PARAM_TAG.getValue());

		if (tagged != null) {
			Tag tagEntity = tagService.getTagByFullPath(tagged);
			if (tagEntity == null) {
				errors.addFailure(getTagNotFoundByName(tagged));
			}
		}

	}

	@Override
	public String getFormatErrorMessage(String tag) {
		return "The courseList tag '"
				+ tag
				+ "' doesn't match {courses tag:\"/tag\" limit:\"digit\" sort:\"date|alphabetical|availability\" order:\"asc|desc\"}";
	}

	public String getTagNotFoundByName(String tagged) {
		return "There are no tags with such a name: " + tagged;
	}

}
