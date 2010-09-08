package ish.oncourse.services.course;

import java.util.List;

import ish.oncourse.model.Course;

public interface ICourseService {
	List<Course> getCourses();
	List<Course> loadByIds(Object... ids);
	Course getCourse(String searchProperty, Object value);
	List<Course> getCourses(boolean enrollable);
}
