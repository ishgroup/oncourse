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

import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._ClassCost
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Object representing income or expense associated with running particular class.
 * This includes enrolment fees, tutor wages, discounts and other incomes or expenses.
 */
@API
class ClassCost extends _ClassCost implements ClassCostTrait {

	public static final String TYPE_SUMMARY = "type_summary"

	@Override
	void postAdd() {
		super.postAdd()

		if (super.getRepetitionType() == null) {
			setRepetitionType(ClassCostRepetitionType.FIXED)
		}

		if (super.getFlowType() == null) {
			setFlowType(ClassCostFlowType.EXPENSE)
		}

		if (super.getInvoiceToStudent() == null) {
			setInvoiceToStudent(false)
		}

		if (super.getPayableOnEnrolment() == null) {
			setPayableOnEnrolment(true)
		}

		if (super.getIsSunk() == null) {
			setIsSunk(false)
		}

		if (super.getTaxAdjustment() == null) {
			setTaxAdjustment(Money.ZERO)
		}

	}

	/**
	 * @return
	 */
	@API
	@Override
	Object getValueForKey(String key) {
		if (TAX.getName() == key) {
			return getTax()
		}

		return super.getValueForKey(key)
	}

	private List<Session> getSessions(@Nullable Date until) {
		List<Session> results = new ArrayList<>()

		if (ClassCostFlowType.WAGES == getFlowType() && getTutorRole() != null) {

			for (TutorAttendance attendance : getTutorRole().getSessionsTutors()) {
				if (attendance.getSession().getPayableDurationInHours() > new BigDecimal(0)) {
					results.add(attendance.getSession())
				}
			}

		} else if (getCourseClass() != null) {
			results = getCourseClass().getSessions()
		}

		if (until != null) {
			Expression expression = ExpressionFactory.noMatchExp(Session.END_DATETIME.getName(), null).andExp(
					ExpressionFactory.lessOrEqualExp(Session.END_DATETIME.getName(), until))

			results = expression.filterObjects(results)
		}

		return Collections.unmodifiableList(results)
	}

	/**
	 * @return number of sessions if this class cost record has "per session" repetition type
	 */
	@API
	Integer getSessionCount(Date until) {
		return getSessions(until).size()
	}


	/**
	 *
	 * @return the tutor role for this class cost. This may determine pay rates based on the date of the class or session.
	 */
	@API @Nullable @Override
    CourseClassTutor getTutorRole() {
		return super.getTutorRole()
	}

	/**
	 * @return amoount of payable hours if this class cost record has "per timetabled hour" or "per student contact" hour repetition type
	 */
	@API
	BigDecimal getSessionPayableHours(@Nonnull Date until) {
		BigDecimal result = BigDecimal.ZERO

		if (ClassCostFlowType.WAGES != getFlowType() || getTutorRole() == null) {
			return getCourseClass().getClassroomHours()
		}

		for (TutorAttendance ta : getTutorRole().getSessionsTutors()) {
			if (!ta.getSession().getEndDatetime().after(until)) {
				result = result.add(ta.getSession().getPayableDurationInHours())
			}
		}

		return result
	}

	/**
	 * @return
	 */
	int getSessionsCountForTutor() {
		if (getContact() != null && getContact().getIsTutor()) {
			return getCourseClass().getSessionsCountForTutor(getContact().getTutor())
		}
		return 0
	}

	@Override
	void preUpdate() {
		super.preUpdate()
		if (getCourseClass() != null && ClassCostFlowType.INCOME == getFlowType()) {
			getCourseClass().updateClassFee()
		}
	}

	@Override
	void postPersist() {
		super.postPersist()
		if (getCourseClass() != null && ClassCostFlowType.INCOME == getFlowType()) {
			getCourseClass().updateClassFee()
		}
	}

	//TODO docs
	/**
	 * @return
	 */
	@Nullable
	@API
	BigDecimal getCurrentOncostRate() {
		BigDecimal result = getOnCostRate()
		if (result == null && getTutorRole() != null && getTutorRole().getDefinedTutorRole() != null && getCourseClass() != null) {
			PayRate pRate = getTutorRole().getDefinedTutorRole().getPayRateForCourseClass(getCourseClass())
			if (pRate != null) {
				return pRate.getOncostRate()
			}
		}
		return result
	}

	/**
	 * @return tax linked to this class cost record
	 */
	@Nonnull
	@API
	@Override
	Tax getTax() {
		// if there is no tax defined for ClassCost inherit from class
		if (super.getTax() == null && getCourseClass() != null) {
			return getCourseClass().getTax()
		}
		return super.getTax()
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
	 * @return description of this class cost record
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * @return flow type of this class cost record: expense, income, wages or discount
	 */
	@Nonnull
	@API
	@Override
	ClassCostFlowType getFlowType() {
		return super.getFlowType()
	}


	/**
	 * @return true if class cost is invoiced to student
	 */
	@Nonnull
	@API
	@Override
	Boolean getInvoiceToStudent() {
		return super.getInvoiceToStudent()
	}

	/**
	 * @return true if this class cost is sunk
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsSunk() {
		return super.getIsSunk()
	}

	/**
	 * @return minimum amount for this class cost
	 */
	@API
	@Override
	Money getMinimumCost() {
		return super.getMinimumCost()
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
	 * @return
	 */
	@API
	@Override
	BigDecimal getOnCostRate() {
		return super.getOnCostRate()
	}

	/**
	 * @return true if this class cost is payable on student enrolment
	 */
	@Nonnull
	@API
	@Override
	Boolean getPayableOnEnrolment() {
		return super.getPayableOnEnrolment()
	}

	/**
	 * @return per unit amount excluding tax
	 */
	@API
	@Override
	Money getPerUnitAmountExTax() {
		return super.getPerUnitAmountExTax()
	}

	/**
	 * @return repetition type for this class cost: fixed, per session, per enrolment, per unit, discount, per timetabled hour or per student contact hour
	 */
	@Nonnull
	@API
	@Override
	ClassCostRepetitionType getRepetitionType() {
		return super.getRepetitionType()
	}

	/**
	 * @return tax adjustment value (used for rounding)
	 */
	@Nonnull
	@API
	@Override
	Money getTaxAdjustment() {
		return super.getTaxAdjustment()
	}

	/**
	 * @return number of units for this class cost
	 */
	@API
	@Override
	BigDecimal getUnitCount() {
		return super.getUnitCount()
	}

	/**
	 * @return contact linked to this class cost (e.g. tutor for wage cost types)
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return class linked to this class cost
	 */
	@Nonnull
	@API
	@Override
	CourseClass getCourseClass() {
		return super.getCourseClass()
	}

	@Override
	void setCourseClass(CourseClass courseClass) {
		super.setCourseClass(courseClass)
	}

	/**
	 * @return onCourse user who created this class cost
	 */
	@Nonnull
	@API
	@Override
	SystemUser getCreatedBy() {
		return super.getCreatedBy()
	}

	/**
	 * @return discount record linked to this class cost (applies for flow type "discount")
	 */
	@Nonnull
	@API
	@Override
    DiscountCourseClass getDiscountCourseClass() {
		return super.getDiscountCourseClass()
	}

	@Override
	void setDiscountCourseClass(DiscountCourseClass discountCourseClass) {
		super.setDiscountCourseClass(discountCourseClass)
	}

	/**
	 * @return paylines generated from this class cost record
	 */
	@Nonnull
	@API
	@Override
	List<PayLine> getPaylines() {
		return super.getPaylines()
	}

	@Override
	String getSummaryDescription() {
		return getDescription()
	}
}
