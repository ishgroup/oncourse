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
import ish.oncourse.cayenne.StudentInterface;

import java.util.List;

/**
 */
public interface IStudent extends StudentInterface, PersistentObjectI {
	String CONTACT_KEY = "contact";
	String OUTCOMES = "outcomes";

	int MIN_SCHOOL_COMPLETION_YEAR = 1940;

	@Override
    IContact getContact();

	List<? extends IEnrolment> getEnrolments();

	List<? extends IPriorLearning> getPriorLearnings();

	Long getStudentNumber();
}
