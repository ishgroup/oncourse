package ish.oncourse.services.html;


import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;

public interface IFacebookMetaProvider {

	/**
	 * @return content for meta 'description' for courseClass
	 */
	public String getDescriptionContent(CourseClass courseClass);

	/**
	 * @return content for meta 'description' for course
	 */
	public String getDescriptionContent(Course course);
}