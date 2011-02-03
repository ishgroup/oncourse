package ish.oncourse.services.courseclass;

import java.util.List;

import ish.oncourse.model.CourseClass;

public interface ICourseClassService {
	CourseClass getCourseClassByFullCode(String code);
	List<CourseClass> loadByIds(Object... ids);
	List<CourseClass> loadByIds(List<Long> ids);
}
