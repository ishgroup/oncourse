package ish.oncourse.services.course;

import java.util.List;

import ish.oncourse.model.Course;

public interface ICourseService {
	List<Course> getCourses();
	List<Course> loadByIds(List<String> ids);
	Course getCurrentCourseByCode(String code);
}
