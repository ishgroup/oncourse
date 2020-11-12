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

import com.google.inject.Inject;
import ish.messaging.ICertificate;
import ish.messaging.ICertificateOutcome;
import ish.messaging.IOutcome;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Deprecated
public class OutcomeService {

	@Inject
	private CourseClassService courseClassService;

	/**
	 * Use Outcome.getName() instead
	 */
	@Deprecated
	public String getName(IOutcome outcome) {
		if (outcome.getModule() == null) {
			if (outcome.getEnrolment() != null) {
				return outcome.getEnrolment().getCourseClass().getCourse().getName();
			} else {
				return outcome.getPriorLearning().getTitle();
			}
		}
		return outcome.getModule().getTitle();
	}

	/**
	 * Use Outcome.getCode() instead
	 */
	@Deprecated
	public String getCode(IOutcome outcome) {
		if (outcome.getModule() == null) {
			if (outcome.getEnrolment() != null) {
				return outcome.getEnrolment().getCourseClass().getUniqueCode();
			} else {
				return outcome.getPriorLearning().getTitle();
			}
		}
		return outcome.getModule().getNationalCode();
	}

	@Deprecated
	/**
	 * Use Outcome.getCertificate() instead
	 */
	public List<ICertificate> getCertificates(IOutcome outcome) {
		List<ICertificate> results = null;
		if (outcome.getCertificateOutcomes() != null) {
			results = new ArrayList<>();
			for (ICertificateOutcome certificateOutcome : outcome.getCertificateOutcomes()) {
				if (certificateOutcome.getCertificate() != null) {
					results.add(certificateOutcome.getCertificate());
				}
			}
		}
		return results;
	}
}
