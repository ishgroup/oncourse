package ish.oncourse.services.course;

import java.util.Date;
import java.util.List;

import ish.oncourse.model.Course;

public interface ICourseService {
	int START_DEFAULT = 0;
	int ROWS_DEFAULT = 30;
	
	List<Course> getCourses(Integer startDefault, Integer rowsDefault);
	List<Course> loadByIds(Object... ids);
	Course getCourse(String searchProperty, Object value);
	List<Course> getCourses(boolean enrollable);
	Integer getCoursesCount();
	Date getLatestModifiedDate();
}
