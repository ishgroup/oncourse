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
package ish.oncourse.server.scripting.api;

import ish.math.Money;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.List;

public class PaymentPlan {

	private CourseClass courseClass;
	private List<PaymentLine> payments = new ArrayList<>();

	public static TemporalAdjuster firstDayOfWeek() {
		return new TemporalAdjuster() {
			@Override
			public Temporal adjustInto(Temporal temporal) {
				return temporal.with(ChronoField.DAY_OF_WEEK, 1);
			}
		};
	}

	public static PaymentPlan newPlanFor(CourseClass courseClass) {
		return new PaymentPlan(courseClass);
	}

	public PaymentPlan(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	public PaymentLine pay(double amount) {
		return pay(Money.of(BigDecimal.valueOf(amount)));
	}

	public PaymentLine pay(Money amount) {
		var line = new PaymentLine();
		line.setAmount(amount);

		this.payments.add(line);

		return line;
	}

	public void schedule() {
		var context = courseClass.getObjectContext();

		for (var line : payments) {
			var paymentPlanLine = context.newObject(CourseClassPaymentPlanLine.class);

			paymentPlanLine.setCourseClass(courseClass);
			paymentPlanLine.setAmount(line.amount);
			paymentPlanLine.setDayOffset(line.offsetDays);
		}

		context.commitChanges();
	}

	private class PaymentLine {

		private Money amount = Money.ZERO;
		private Integer offsetDays;

		public void setAmount(Money amount) {
			this.amount = amount;
		}

		public void on(int offsetDays) {
			this.offsetDays = offsetDays;
		}

		public void on(LocalDate date) {
			this.offsetDays = (int) ChronoUnit.DAYS.between(courseClass.getStart(), date);
		}

		public void on(PaymentDate scheduledDate) {
			switch (scheduledDate) {
				case ENROLMENT:
					this.offsetDays = null;
					break;
				case START:
					this.offsetDays = 0;
					break;
				case COMPLETION:
					on(courseClass.getEnd());
					break;
				default:
					throw new IllegalArgumentException("Unknown schedule date.");
			}
		}
	}

	private enum PaymentDate {
		ENROLMENT, START, COMPLETION
	}
}
