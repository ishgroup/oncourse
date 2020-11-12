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
package ish.oncourse.server.payroll;

import ish.common.types.AttendanceType;
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.PayLine;
import ish.oncourse.server.cayenne.Session;
import ish.oncourse.server.cayenne.TutorAttendance;
import org.apache.cayenne.exp.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ish.common.types.AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON;
import static ish.common.types.AttendanceType.UNMARKED;

/**
 * The abstract creator, applicable to create paylines for wages which pay rate based on tutor attendances:
 * PER_SESSION & PER_TIMETABLED_HOUR.
 */
public abstract class AbstractAttendanceBasedPaylinesCreator extends AbstractPaylinesCreator {

	protected Date until;
	protected boolean confirm = false;

	protected AbstractAttendanceBasedPaylinesCreator(ClassCost classCost, Date until, boolean confirm) {
		super(classCost);
		this.until = until;
		this.confirm = confirm;
	}

	/**
	 * Filter tutor attendances in accordance with assigned date range
	 * @param until
	 * @return
	 */
	public List<TutorAttendance> getAttendedAttendanceRecords(Date until) {
		var expression = TutorAttendance.SESSION.dot(Session.END_DATETIME).lte(until);
		if (classCost.getTutorRole() != null) {
			return expression.filterObjects(classCost.getTutorRole().getSessionsTutors());
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Find paylines which already linked to corresponded attendance.session and classCost.
	 * If such paylines already exists: this means that payroll was already generated for assigned classCost/date range
	 * @param attendance
	 * @return
	 */
	public List<PayLine> findPaylinesForAttendance(TutorAttendance attendance) {
		List<PayLine> results = Collections.emptyList();
		if (attendance != null && attendance.getSession() != null) {
			return PayLine.CLASS_COST.eq(classCost).filterObjects(attendance.getSession().getPayLines());
		}
		return results;
	}

	/**
	 * Make decision to pay for the single session based on input flags and attendance state
	 * @param attendance
	 * @return
	 */
	public boolean shouldGeneratePaylineForAttendance(TutorAttendance attendance) {
		if (attendance != null) {
			var marker = attendance.getAttendanceType();
			//pass UNMARKED attendances if user press confirm button before generation
			return DID_NOT_ATTEND_WITHOUT_REASON != marker && (confirm || UNMARKED != marker);
		}
		return false;

	}
}
