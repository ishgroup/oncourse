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
package ish.oncourse.entity.services;

import ish.common.types.EnrolmentStatus;
import ish.messaging.IEnrolment;
import ish.messaging.IOutcome;
import ish.messaging.IPriorLearning;
import ish.oncourse.server.cayenne.Student;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class StudentService {

	public List<? extends IEnrolment> getValidAndCancelledEnrolments(Student student) {
		List<IEnrolment> result = new ArrayList<>();
		if (student.getEnrolments() != null) {
			for (IEnrolment e : student.getEnrolments()) {
				if (EnrolmentStatus.STATUSES_LEGIT.contains(e.getStatus()) || EnrolmentStatus.STATUSES_CANCELLATIONS.contains(e.getStatus())) {
					result.add(e);
				}
			}
		}
		return result;
	}

	/**
	 * @param vetOnly
	 * @return Outcomes
	 */
	public List<? extends IOutcome> getOutcomes(Student student, boolean vetOnly) {
		List<IOutcome> result = new ArrayList<>();
		for (IEnrolment e : getValidAndCancelledEnrolments(student)) {
			if (e.getCourseClass() != null && !e.getCourseClass().getIsCancelled()) {
				result.addAll(e.getOutcomes());
			}
		}

		for (IPriorLearning pl : student.getPriorLearnings()) {
			result.addAll(pl.getOutcomes());
		}

		if (vetOnly) {
			List<IOutcome> finalresult = new ArrayList<>();
			for (IOutcome o : result) {
				if (o.getModule() != null) {
					finalresult.add(o);
				}
			}
			return finalresult;
		}
		return result;
	}

}
