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

import ish.budget.ClassCostUtil;
import ish.math.Money;
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.PayLine;
import ish.oncourse.server.cayenne.PayRate;

import java.util.Date;
import java.util.List;

public  abstract class AbstractPaylinesCreator {

	protected ClassCost classCost;


	protected AbstractPaylinesCreator (ClassCost classCost) {
		this.classCost = classCost;
	}


	/**
	 * Return eligible pay rate for 'date' Applicable when DefinedTutorRole has specified list of rates by date
	 * and wage for tutorRole is not 'overriden'. Otherwise method return amount which was specified in tutor wage UI
	 * @param date
	 * @return
	 */
	public Money getPerUnitAmountExTax(Date date) {
		var perUnitAmountExTax = classCost.getPerUnitAmountExTax();
		if (perUnitAmountExTax != null) {
			return perUnitAmountExTax;
		}
		if (classCost.getTutorRole() != null) {
			var rate = classCost.getTutorRole().getDefinedTutorRole().getPayRateForDate(date);
			if (rate != null) {
				return rate.getRate();
			}
		}
		return null;
	}

	/**
	 * Create set of paylines in accordance with repetition type of wage.
	 * @return
	 */
	public abstract List<PayLine> createLines();
}
