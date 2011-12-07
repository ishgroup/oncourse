package ish.oncourse.model;

import ish.oncourse.model.auto._Course;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.TimestampUtilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course extends _Course implements Queueable {
	private static final long serialVersionUID = 254942637990278217L;
	public static final String COURSE_TAG = "courseTag";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public List<CourseClass> getEnrollableClasses() {
		List<CourseClass> currentClasses = getCurrentClasses();
		List<CourseClass> list = new ArrayList<CourseClass>(currentClasses.size());

		for (CourseClass courseClass : currentClasses) {
			if (courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}

	public List<CourseClass> getCurrentClasses() {
		List<CourseClass> courseClasses = getCourseClasses();
		List<CourseClass> list = new ArrayList<CourseClass>(courseClasses.size());

		for (CourseClass courseClass : courseClasses) {
			if (Boolean.TRUE.equals(courseClass.getIsWebVisible())
					&& !courseClass.isCancelled()
					&& (courseClass.getEndDate() == null || courseClass.getEndDate().after(
							TimestampUtilities.normalisedDate(new Date())))) {
				list.add(courseClass);
			}

		}

		return list;
	}
	
	public List<CourseClass> getFullClasses() {
		List<CourseClass> currentClasses = getCurrentClasses();
		List<CourseClass> list = new ArrayList<CourseClass>(currentClasses.size());

		for (CourseClass courseClass : currentClasses) {
			if (!courseClass.isHasAvailableEnrolmentPlaces()) {
				list.add(courseClass);
			}
		}

		return list;
	}
}
