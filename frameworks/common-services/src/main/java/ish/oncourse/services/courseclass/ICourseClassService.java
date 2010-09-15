package ish.oncourse.services.courseclass;

import ish.oncourse.model.CourseClass;

public interface ICourseClassService {

	CourseClass getCourseClassByFullCode(String code);
}
