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
import ish.util.LocalDateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePaylinesPerUnit extends AbstractPaylinesCreator {

	private static final Logger logger = LogManager.getLogger(CreatePaylinesPerUnit.class);

	private CreatePaylinesPerUnit(ClassCost classCost) {
		super(classCost);
	}

	public static CreatePaylinesPerUnit valueOf(ClassCost classCost) {
		return new CreatePaylinesPerUnit(classCost);
	}

	/**
	 * Create single payline for classCost with PER_UNIT repetition type.
	 * Budgeted quantity for such line is 'unit count' which specified through tutor wage UI on creation of wage
	 * Only perform if no existing lines (has no been generated before) and quantity/rate is greater than zero.
	 * @return
	 */
	@Override
	public List<PayLine> createLines() {
		List<PayLine> payLines = new ArrayList<>();

		var eligibleRate = getPerUnitAmountExTax(classCost.getCourseClass().getStartDateTime());

		logger.debug("paylines generate perHour, hours: {}", classCost.getUnitCount());
		if (classCost.getPaylines().size() == 0
				&& eligibleRate != null
				&& eligibleRate.isGreaterThan(Money.ZERO())
				&& BigDecimal.ZERO.compareTo(classCost.getUnitCount()) < 0) {

			var pl = classCost.getObjectContext().newObject(PayLine.class);
			logger.debug("hour payline {}", pl.hashCode());
			pl.setCreatedOn(new Date());
			pl.setModifiedOn(new Date());

			pl.setClassCost(classCost);
			pl.setDescription(classCost.getDescription());

			pl.setQuantity(classCost.getUnitCount());
			pl.setBudgetedQuantity(classCost.getUnitCount());

			pl.setValue(eligibleRate);
			pl.setBudgetedValue(eligibleRate);

			var dateFor = classCost.getCourseClass().getStartDateTime();
			if (dateFor == null) {
				dateFor = classCost.getCreatedOn();
			}
			pl.setDateFor(LocalDateUtils.dateToValue(dateFor));
			payLines.add(pl);
		}
		return payLines;
	}
}
