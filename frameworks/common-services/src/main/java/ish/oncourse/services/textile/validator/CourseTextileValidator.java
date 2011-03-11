package ish.oncourse.services.textile.validator;

import java.util.List;
import java.util.Map;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseTextileAttributes;
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

		List<String> attrValues = CourseTextileAttributes.getAttrValues();
		TextileUtil.checkParamsUniquence(tag, errors, attrValues);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				attrValues);
		String code = tagParams.get(CourseTextileAttributes.COURSE_PARAM_CODE
				.getValue());
		String tagged = tagParams.get(CourseTextileAttributes.COURSE_PARAM_TAG
				.getValue());
		if (code != null) {
			Course course = courseService.getCourse(Course.CODE_PROPERTY, code);
			if (course == null) {
				errors.addFailure(getCourseNotFoundByCode(code));
			}
		}
		if (tagged != null) {
			Tag tagEntity = tagService.getTagByFullPath(tagged);
			if (tagEntity == null) {
				errors.addFailure(getTagNotFoundByName(tagged));
			}
		}

	}

	public String getTagNotFoundByName(String tagged) {
		return "There are no tags with such a name: "
				+ tagged;
	}

	public String getFormatErrorMessage(String tag) {
		return "The course tag '" + tag
				+ "' doesn't match {course code:\"code\" tag:\"tag\" "
				+ "showclasses:\"true|false\"}";
	}

	public String getCourseNotFoundByCode(String code) {
		return "There are no course with such a code:" + code;
	}

}
