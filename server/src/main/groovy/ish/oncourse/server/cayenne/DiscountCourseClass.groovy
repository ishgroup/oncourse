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

package ish.oncourse.server.cayenne

import ish.oncourse.API
import ish.oncourse.cayenne.DiscountCourseClassInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._DiscountCourseClass

import javax.annotation.Nonnull

/**
 * Object representing relation between discount and course class.
 */
@API
@QueueableEntity
class DiscountCourseClass extends _DiscountCourseClass implements Queueable, DiscountCourseClassInterface {

	/**
	 * @return predicted percentage of students using this discount
	 */
	@API
	@Override
	BigDecimal getPredictedStudentsPercentage() {
		super.getPredictedStudentsPercentage()
	}

	@Override
	void setCourseClass(CourseClass courseClass) {
		super.setCourseClass(courseClass)
	}

	@Override
	void setDiscount(Discount discount) {
		super.setDiscount(discount)
	}

	@Override
	void setPredictedStudentsPercentage(BigDecimal predictedStudentsPercentage) {
		if (getDiscount().getPredictedStudentsPercentage() != predictedStudentsPercentage) {
			super.setPredictedStudentsPercentage(predictedStudentsPercentage)
		} else {
			super.setPredictedStudentsPercentage(null)
		}
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}



	/**
	 * @return linked class cost record linked
	 */
	@Nonnull
	@API
	@Override
	ClassCost getClassCost() {
		return super.getClassCost()
	}

	/**
	 * @return linked class
	 */
	@Nonnull
	@API
	@Override
	CourseClass getCourseClass() {
		return super.getCourseClass()
	}

	/**
	 * @return linked discount
	 */
	@Nonnull
	@API
	@Override
    Discount getDiscount() {
		return super.getDiscount()
	}
}
