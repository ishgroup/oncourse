package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.services.textile.attrs.CourseListSortValue;

import java.util.Date;
import java.util.List;

public interface ICourseService {
	int START_DEFAULT = 0;
	int ROWS_DEFAULT = 30;

	List<Course> getCourses(Integer startDefault, Integer rowsDefault);

	List<Course> getCourses(String tagName, CourseListSortValue sort, Boolean isAscending,
			Integer limit);

	List<Course> loadByIds(Object... ids);

	Course getCourse(String searchProperty, Object value);

	Course getCourse(String taggedWith);

	Integer getCoursesCount();

	Date getLatestModifiedDate();

}
