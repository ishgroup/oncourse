/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.messaging;

import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Tag;
import ish.oncourse.server.cayenne.Module;

import java.util.Date;
import java.util.List;

/**
 */
public interface ICourse extends PersistentObjectI {

	int COURSE_CODE_MAX_LENGTH = 32;
	int COURSE_NAME_MAX_LENGTH = 200;

    String CURRENT_CLASS_COUNT_PROPERTY = "currentClassesCount";

	String getCode();

	String getName();

	List<? extends Tag> getTags();

	List<CourseClass> getCourseClasses();

	List<Module> getModules();

	void setModifiedOn(Date modifiedOn);
}
