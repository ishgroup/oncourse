package ish.oncourse.model;

import ish.oncourse.model.auto._CourseClass;

public class CourseClass extends _CourseClass {

	public Long getRecordId() {
		return (Long) readProperty(ID_PK_COLUMN);
	}

	public String getUniqueIdentifier() {
		return getCourseForClass() + "-" + getCode();
	}

	public Course getCourseForClass() {
		return getCourse().get(0);
	}
}
