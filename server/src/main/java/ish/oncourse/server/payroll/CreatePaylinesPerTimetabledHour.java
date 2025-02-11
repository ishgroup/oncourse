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

import ish.math.Money;
import ish.oncourse.entity.services.SessionService;
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.PayLine;
import ish.oncourse.server.cayenne.Session;
import ish.oncourse.server.cayenne.TutorAttendance;
import ish.util.LocalDateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ish.common.types.AttendanceType.ATTENDED;
import static ish.common.types.AttendanceType.UNMARKED;

public class CreatePaylinesPerTimetabledHour extends AbstractAttendanceBasedPaylinesCreator {

	private static final Logger logger = LogManager.getLogger(CreatePaylinesPerTimetabledHour.class);
	private SessionService sessionService;

	private CreatePaylinesPerTimetabledHour(ClassCost classCost, Date until, SessionService sessionService, boolean confirm) {
		super(classCost, until, confirm);
		this.sessionService = sessionService;
	}

	public static CreatePaylinesPerTimetabledHour valueOf(ClassCost classCost, Date until, SessionService sessionService, boolean confirm) {
		return new CreatePaylinesPerTimetabledHour(classCost, until, sessionService, confirm);

	}

	/**
	 * Create list of paylines for classCost with PER_TIMETABLED_HOUR repetition type.
	 * Budgeted quantity: pay according to the session payable time if:
	 * - tutor attended the session
	 * - tutor did not attend the session, but with a reason (ie. sick)
	 * - tutor attendance is not marked, but user specified that the payments should be made
	 *  else:
	 * - pay for part of the class if the partial attendance is entered. adjust the payable time
	 * The count of lines equals count of tutor attendances for sessions from assigned date range exclude:
	 * - attendances which already has corresponded paylines (already generated for wage, session) - @see AbstractAttendanceBasedPaylinesCreator findPaylinesForAttendance()
	 * - attendances which are not eligible to generate - @see AbstractAttendanceBasedPaylinesCreator shouldGeneratePaylineForAttendance()
	 * @return
	 */
	@Override
	public List<PayLine> createLines() {
		List<PayLine> payLines = new ArrayList<>();

		var attendanceFromDateRange= getAttendedAttendanceRecords(until);
		logger.debug("paylines generate perTimetabledHour... {}", attendanceFromDateRange.size());

		for (var attendanceItem : attendanceFromDateRange) {

			logger.debug("looping through attendance: {} of type {} for session ending {}", attendanceItem.getCourseClassTutor().getTutor().getContact().getName(),
					attendanceItem.getAttendanceType(), attendanceItem.getEndDatetime());

			var alreadyGenerated = findPaylinesForAttendance(attendanceItem).size() > 0;
			var eligibleToGenerate = shouldGeneratePaylineForAttendance(attendanceItem);
			var eligibleRate = getPerUnitAmountExTax(attendanceItem.getStartDatetime());

			if (alreadyGenerated || !eligibleToGenerate) {
				logger.debug("paylines exlude perTimeTabledHour attendance (end date)... {}", attendanceItem.getEndDatetime());
			} else if (eligibleRate != null && eligibleRate.isGreaterThan(Money.ZERO())) {

				if (UNMARKED == attendanceItem.getAttendanceType()) {
					attendanceItem.setAttendanceType(ATTENDED);
				}

				var pl = classCost.getObjectContext().newObject(PayLine.class);
				pl.setCreatedOn(new Date());
				pl.setModifiedOn(new Date());

				pl.setSession(attendanceItem.getSession());
				pl.setClassCost(classCost);
				pl.setDescription(classCost.getDescription());

				pl.setQuantity(attendanceItem.getActualPayableDurationHours());
				
				pl.setBudgetedQuantity(attendanceItem.getBudgetedPayableDurationHours());

				pl.setValue(eligibleRate);
				pl.setBudgetedValue(pl.getValue());
				pl.setDateFor(LocalDateUtils.dateToValue(attendanceItem.getStartDatetime()));

				logger.debug("timetable payline created (end date)... {}", attendanceItem.getEndDatetime());

				payLines.add(pl);
			}
		}
		return payLines;
	}
	
}
