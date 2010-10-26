package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class CourseTextileValidator implements IValidator {

	private ICourseService courseService;

	private ITagService tagService;

	public CourseTextileValidator(ICourseService courseService,
			ITagService tagService) {
		this.courseService = courseService;
		this.tagService = tagService;
	}

	public void validate(String tag, ValidationErrors errors) {
		if (!tag.matches(TextileUtil.COURSE_REGEXP)) {
			errors.addFailure(getFormatErrorMessage(tag));
		}

		TextileUtil.checkParamsUniquence(tag, errors,
				TextileUtil.COURSE_PARAM_CODE, TextileUtil.PARAM_TAG,
				TextileUtil.COURSE_PARAM_ENROLLABLE,
				TextileUtil.COURSE_PARAM_CURRENT_SEARCH);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.COURSE_PARAM_CODE, TextileUtil.PARAM_TAG);
		String code = tagParams.get(TextileUtil.COURSE_PARAM_CODE);
		String tagged = tagParams.get(TextileUtil.PARAM_TAG);
		if (code != null) {
			Course course = courseService.getCourse(Course.CODE_PROPERTY, code);
			if (course == null) {
				errors.addFailure("There are no course with such a code:"
						+ code);
			}
		}
		if (tagged != null) {
			Tag tagEntity = tagService.getSubTagByName(tagged);
			if (tagEntity == null) {
				errors.addFailure("There are no tags with such a name: "
						+ tagged);
			}
		}

	}

	public String getFormatErrorMessage(String tag) {
		return "The course tag '" + tag
				+ "' doesn't match {course code:\"code\" tag:\"tag\" "
				+ "enrollable:\"true|false\" currentsearch:\"true|false\"}";
	}

}
