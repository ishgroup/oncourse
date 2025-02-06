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
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.PayLine;
import ish.oncourse.server.cayenne.TutorAttendance;
import ish.util.LocalDateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ish.common.types.AttendanceType.ATTENDED;
import static ish.common.types.AttendanceType.UNMARKED;

public class CreatePaylinesPerSession extends AbstractAttendanceBasedPaylinesCreator {

	private static final Logger logger = LogManager.getLogger(CreatePaylinesPerSession.class);

	private CreatePaylinesPerSession(ClassCost classCost, Date until, boolean confirm) {
		super(classCost, until, confirm);
	}

	public static CreatePaylinesPerSession 	valueOf(ClassCost classCost, Date until, boolean confirm) {
		return new CreatePaylinesPerSession(classCost, until, confirm);
	}

	/**
	 * Create list of paylines for classCost with PER_SESSION repetition type.
	 * Budgeted quantity for each line is always ONE.
	 * The count of lines equals count of tutor attendances for sessions from assigned date range exclude:
	 * - attendances which already has corresponded paylines (already generated for wage, session) - @see AbstractAttendanceBasedPaylinesCreator findPaylinesForAttendance()
	 * - attendances which are not eligible to generate - @see AbstractAttendanceBasedPaylinesCreator shouldGeneratePaylineForAttendance()
	 * - attendances which linked to sessions with zero payable time - @see Session getPayableDurationInHours()
	 * @return
	 */
	@Override
	public List<PayLine> createLines() {

		List<PayLine> payLines = new ArrayList<>();

		var attendanceRecords = getAttendedAttendanceRecords(until);
		logger.debug("paylines generate perSession... {}", attendanceRecords.size());
		for (var attendanceItem : attendanceRecords) {

			var alreadyGenerated = findPaylinesForAttendance(attendanceItem).size() > 0;
			var eligibleToGenerate = shouldGeneratePaylineForAttendance(attendanceItem);
			var eligibleRate = getPerUnitAmountExTax(attendanceItem.getStartDatetime());

			if (alreadyGenerated || !eligibleToGenerate) {
				logger.debug("paylines exlude per session attendance...{}", attendanceItem);
				continue;
			}
			if (eligibleRate != null && eligibleRate.isGreaterThan(Money.ZERO())
					&& attendanceItem.getActualPayableDurationHours().compareTo(new BigDecimal(0)) > 0) {

				if (UNMARKED == attendanceItem.getAttendanceType()) {
					attendanceItem.setAttendanceType(ATTENDED);
				}

				var pl = classCost.getObjectContext().newObject(PayLine.class);
				logger.debug("session payline {}", pl.hashCode());
				pl.setCreatedOn(new Date());
				pl.setModifiedOn(new Date());

				pl.setSession(attendanceItem.getSession());
				pl.setClassCost(classCost);
				pl.setDescription(classCost.getDescription());
				pl.setQuantity(BigDecimal.ONE);
				pl.setBudgetedQuantity(BigDecimal.ONE);
				pl.setValue(eligibleRate);
				pl.setBudgetedValue(pl.getValue());

				pl.setDateFor(LocalDateUtils.dateToValue(attendanceItem.getStartDatetime()));
				payLines.add(pl);
			}
		}

		return payLines;
	}
}
