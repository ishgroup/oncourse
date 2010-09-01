package ish.oncourse.services.textile.validator;

import java.util.Map;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;

public class CourseTextileValidator implements IValidator {

	private ICourseService courseService;
	
	public CourseTextileValidator(ICourseService courseService) {
		this.courseService = courseService;
	}

	public void validate(String tag, ValidationErrors errors) {
		if(!tag.matches(TextileUtil.COURSE_REGEXP)){
			errors
			.addFailure("The image tag '"
					+ tag
					+ "' doesn't match {course code:\"code\" tag:\"tag\"}");
		}
		
		TextileUtil.checkParamsUniquence(tag, errors,TextileUtil.COURSE_PARAM_CODE, TextileUtil.PARAM_TAG);
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TextileUtil.COURSE_PARAM_CODE);
		String code = tagParams.get(TextileUtil.COURSE_PARAM_CODE);
		if(code!=null){
			Course course = courseService.getCurrentCourseByCode(code);
			if(course==null){
				errors
				.addFailure("There are no course with such a code:"+code);
			}
		}
	}

}
