package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebSite;

import java.util.Date;
import java.util.List;

public interface ICourseService {
	
	int START_DEFAULT = 0;
	int ROWS_DEFAULT = 30;

	/**
	 * Loads courses between start and end index.
	 * @param startDefault start index
	 * @param rowsDefault end index
	 * @return list of courses
	 */
	List<Course> getCourses(Integer startDefault, Integer rowsDefault);

	/**
	 * Load courses by tag and sort settings and limit.
	 * @param tag
	 * @param sort
	 * @param isAscending
	 * @param limit
	 * @return
	 */
	List<Course> getCourses(Tag tag, Sort sort, Boolean isAscending,
			Integer limit);

	boolean availableByRootTag(Course course);
	
	/**
	 * Load by primary keys.
	 * 
	 * @param ids
	 * @return
	 */
	List<Course> loadByIds(List<String> ids);

	/**
	 * Load by search property and search property value.
	 * 
	 * @param searchProperty
	 * @param value
	 * @return
	 */
	Course getCourse(String searchProperty, Object value);

	Course getCourseByCode(String code);

	/**
	 * Gets random course, linked to provided tag.
	 * @param taggedWith
	 * @return
	 */
	Course getCourse(String taggedWith);

	/**
	 * Get courses count for current site. 
	 * @return
	 */
	Long getCoursesCount();

	/**
	 * Get last modified date for course in current site.
	 * @return
	 */
	Date getLatestModifiedDate();

	String[] getAvailableSiteKeys(Course course);
}
