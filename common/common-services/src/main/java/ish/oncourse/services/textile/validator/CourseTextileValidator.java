package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.CourseTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class CourseTextileValidator extends AbstractTextileValidator {

	private ICourseService courseService;

	private ITagService tagService;

	public CourseTextileValidator(ICourseService courseService, ITagService tagService) {
		this.courseService = courseService;
		this.tagService = tagService;
	}

	@Override
	protected void initValidator() {
		textileType = TextileType.COURSE;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String code = tagParams.get(CourseTextileAttributes.COURSE_PARAM_CODE.getValue());
		String tagged = tagParams.get(CourseTextileAttributes.COURSE_PARAM_TAG.getValue());
		if (code != null) {
			Course course = courseService.getCourse(Course.CODE_PROPERTY, code);
			if (course == null) {
				errors.addFailure(getCourseNotFoundByCode(code),
						ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
		if (tagged != null) {
			Tag tagEntity = tagService.getTagByFullPath(tagged);
			if (tagEntity == null) {
				errors.addFailure(getTagNotFoundByName(tagged),
						ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
	}

	public String getTagNotFoundByName(String tagged) {
		return "There are no tags with such a name: " + tagged;
	}

	public String getFormatErrorMessage(String tag) {
		return "The course tag '" + tag + "' doesn't match {course code:\"code\" tag:\"tag\" "
				+ "showclasses:\"true|false\"}";
	}

	public String getCourseNotFoundByCode(String code) {
		return "There are no course with such a code:" + code;
	}

}
