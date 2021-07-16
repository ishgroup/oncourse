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

package ish.oncourse.server.entity.mixins

import groovy.transform.CompileDynamic
import ish.budget.ClassBudgetUtil
import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.CourseClassUtil
import ish.oncourse.entity.services.CourseClassService
import ish.oncourse.entity.services.EnrolmentService
import ish.oncourse.entity.services.InvoiceLineService
import ish.oncourse.entity.services.TagService
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.TutorAttendance
import static ish.oncourse.server.entity.mixins.MixinHelper.getService
import ish.oncourse.server.print.proxy.PrintableAssessmentClassModule
import ish.oncourse.server.print.proxy.PrintableAttendance
import ish.persistence.CommonExpressionFactory
import org.apache.cayenne.query.Ordering
import org.apache.commons.lang3.time.DateUtils

import javax.annotation.Nullable

@CompileDynamic
class CourseClassMixin {

	/**
	* @return the first child of the Subject tag applied to this class
	*/
	@API
	static getFirstSubjectTag(CourseClass self) {
		def tags = getService(TagService).getSubjectTagsForCourse(self.course)
		return tags.empty ? "" : tags[0].pathName
	}

    /**
     * Checks if CourseClass isn't finished and isn't cancelled in the moment of method call
     *
     * @param self
     * @return
     */
    @API
    static isActual(CourseClass self){
		return isActual(self.isCancelled, self.isDistantLearningCourse, self.endDateTime)
    }

	static isActual(boolean isCancelled, boolean isDistantLearningCourse, Date endDateTime) {
		return !isCancelled && (isDistantLearningCourse || endDateTime?.after(new Date()))
	}

	static getBudgetValue(CourseClass self, String key) {
		return ClassBudgetUtil.getValueForKey(key, self)
	}

	@API
	static getActualTotalIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_INCOME_EX_TAX, self)
	}

	@API
	static getBudgetedTotalIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_INCOME_EX_TAX, self)
	}

	@API
	static getActualTotalCost(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_COST_EX_TAX, self)
	}

	@API
	static getBudgetedTotalCost(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_COST_EX_TAX, self)
	}

	@API
	static getActualTotalProfit(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_PROFIT_EX_TAX, self)
	}

	@API
	static getBudgetedTotalProfit(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_PROFIT_EX_TAX, self)
	}

	@API
	static getActualFeeIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_FEE_INCOME_EX_TAX, self)
	}

	@API
	static getActualOtherIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_OTHER_INCOME_EX_TAX, self)
	}

	@API
	static getActualCustomInvoices(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_CUSTOM_INVOICE_EX_TAX, self)
	}

	@API
	static getActualDiscounts(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_TOTAL_DISCOUNTS_EX_TAX, self)
	}

	@API
	static getBudgetedFeeIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_FEE_INCOME_EX_TAX, self)
	}

	@API
	static getBudgetedOtherIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_OTHER_INCOME_EX_TAX, self)
	}

	@API
	static getBudgetedCustomInvoices(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_CUSTOM_INVOICE_EX_TAX, self)
	}

	@API
	static getBudgetedDiscounts(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_BUDGETED_DISCOUNTS_EX_TAX, self)
	}

	@API
	static getMaximumTotalProfit(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_MAXIMUM_PROFIT_EX_TAX, self)
	}

	@API
	static getMaximumTotalIncome(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_MAXIMUM_INCOME_EX_TAX, self)
	}

	@API
	static getMaximumTotalCost(CourseClass self) {
		return ClassBudgetUtil.getValueForKey(ClassBudgetUtil.CLASS_MAXIMUM_COST_EX_TAX, self)
	}

	/**
	* @return all enrolments with a CANCELLED or REFUNDED status
	*/
	@API
	static getClassTotalFeeIncomeExTaxForRefundedAndCancelledEnrolments(CourseClass self) {
		return getService(CourseClassService).getClassTotalFeeIncomeExTaxForRefundedAndCancelledEnrolments(self)
	}

	@API
	static getRefundedAndCancelledEnrolments(CourseClass self) {
		return CourseClassUtil.getRefundedAndCancelledEnrolments(self.getEnrolments())
	}

	/**
	* @return all enrolments that did not use a discount
	*/
	@API
	static getFullFeeEnrolments(CourseClass self) {
		return getService(CourseClassService).getSuccessfulAndQueuedEnrolments(self).findAll { Enrolment e ->
			e.invoiceLines.findAll { il -> il.discountTotalExTax.isGreaterThan(Money.ZERO) }.empty
		}
	}

	/**
	 * Get number of enrolments for class created in a given date range
	 *
	 * @param from start Date range
	 * @param to end Date range
	 * @return number of enrolments created within the given range
	 */
	@API
	static getEnrolmentsWithinDateRange(CourseClass self, Date from, Date to) {
		return self.successAndQueuedEnrolments.findAll {
			e -> from == null || e.createdOn >= CommonExpressionFactory.previousMidnight(from)
		}.findAll {
			e -> to == null || e.createdOn <= CommonExpressionFactory.nextMidnight(to)
		}
	}

	/**
	* @return the sum of all enrolment fees for all enrolments that did not use a discount
	*/
	@API
	static getFullFeeEnrolmentsFeesSum(CourseClass self) {
		return getFullFeeEnrolments(self)*.invoiceLines.flatten().inject(Money.ZERO) {
			Money sum, InvoiceLine il -> sum.add(il.finalPriceToPayExTax)
		}
	}

	/**
	*
	* @return all enrolments in this courseClass enrolled with a manual discount
	*/
	@API
	static getManuallyDiscountedEnrolments(CourseClass self) {
		return getService(CourseClassService).getSuccessfulAndQueuedEnrolments(self).findAll { Enrolment e ->
			!e.invoiceLines.findAll { il -> il.discountTotalExTax.isGreaterThan(Money.ZERO) && il.discounts.empty }.empty
		}
	}

	static getManuallyDiscountedEnrolmentsFeesSum(CourseClass self) {
		return getManuallyDiscountedEnrolments(self)*.invoiceLines.flatten().inject(Money.ZERO) {
			Money sum, InvoiceLine il -> sum.add(il.finalPriceToPayExTax)
		}
	}

	static getManuallyDiscountedEnrolmentsDiscountSum(CourseClass self) {
		return getManuallyDiscountedEnrolments(self)*.invoiceLines.flatten().inject(Money.ZERO) {
			Money sum, InvoiceLine il -> sum.add(il.discountTotalExTax)
		}
	}

	/**
	*
	* @return Integer number of successful enrolments in this CourseClass
	*/
	@API
	static getEnrolmentsCount(CourseClass self) {
		return self.successAndQueuedEnrolments.size()
	}

	/**
	 * Get number of enrolments for class to cover its running costs.
	 *
	 * @return required number of enrolments
	 */
	@API
	static getEnrolmentsToProceed(CourseClass self) {
		return getService(CourseClassService).getEnrolmentsToProceed(self)
	}

	/**
	 * Get number of enrolments for class to be profitable.
	 *
	 * @return required number of enrolments
	 */
	@API
	static getEnrolmentsToProfit(CourseClass self) {
		return getService(CourseClassService).getEnrolmentsToProfit(self)
	}

	static getUniqueCode(CourseClass self) {
		return getService(CourseClassService).getUniqueCode(self)
	}

	@API
	static getTutorNames(CourseClass self) {
		return getService(CourseClassService).getTutorNames(self)
	}

	@API
	static getTutorNamesAbriged(CourseClass self) {
		return getService(CourseClassService).getTutorNamesAbriged(self)
	}

	@API
	static getTimetableSummary(CourseClass self) {
		return getService(CourseClassService).getTimetableSummaryForClass(self)
	}

	static getMaleEnrolmentsCount(CourseClass self) {
		return getService(CourseClassService).getMaleCount(self)
	}

	@API
	static getSessionModules(CourseClass self) {
		return getService(CourseClassService).getSessionModules(self)
	}

	@API
	static getUniqueSessionModules(CourseClass self) {
		return getService(CourseClassService).getUniqueSessionModules(self)
	}

	/**
	* @return number of enrolments left before reaching the maxiumum number of enrolments in the courseClass
	*/
	@API
	static getPlacesLeft(CourseClass self) {
		return getService(CourseClassService).getPlacesLeft(self)
	}

	/**
	*	Returns a string with the format "discount.name discount.description discount.feeExGst" for all discounts attached to this CourseClass
	*
	* @return
	*/
	@API
	static getDiscountsDescription(CourseClass self) {
		return getService(CourseClassService).getDiscountFees(self)
	}

	/**
	* @return the Room associated with the first session of the class
	*/
	@API
	static getFirstRoom(CourseClass self) {
		return getService(CourseClassService).getFirstRoomSpecified(self)
	}

	/**
	* Collects all successful enrolments attached to this courseClass used a discount
	* @return InvoiceLines sorted by discount name
	*/
	@API
	static getDiscountedInvoiceLinesForEnrolments(CourseClass self) {
		return self.enrolments.findAll { e ->
			EnrolmentStatus.SUCCESS.equals(e.status) &&
					e.originalInvoiceLine != null && !e.originalInvoiceLine.invoiceLineDiscounts.empty
		}.collect { e -> e.originalInvoiceLine }.toSorted { il1, il2 ->
			getService(InvoiceLineService).getDiscountNames(il1) <=> getService(InvoiceLineService).getDiscountNames(il2)
		}
	}

	/**
	* Returns the first child of the 'Subject' tag of the course this courseClass belongs to
	*	Subject/[Languages]/Japanese
	*
	* @return the name of the Subject child tag applied to this CourseClass
	*/
	@API
	static getCategory(CourseClass self) {
		return getFirstSubjectTag(self).split('-').with { parts -> parts.length > 1 ? parts[1] : null }
	}

	/**
	* Returns the first child of the 'Subject' tag of the course this courseClass belongs to
	*	Subject/Languages/[Japanese]
	*
	* @return the name of the 2nd Subject child tag applied to this CourseClass
	*/
	@API
	static getSubcategory(CourseClass self) {
		return getFirstSubjectTag(self).split('-').with { parts -> parts.length > 2 ? parts[2] : null }
	}

	@API
	static getTotalIncomeAmount(CourseClass self) {
		return self.successAndQueuedEnrolments*.invoiceLines.flatten().inject(Money.ZERO) {
			Money total, InvoiceLine il -> total.add(il.finalPriceToPayExTax)
		}.add(self.invoiceLines.inject(Money.ZERO) { Money total, InvoiceLine il -> total.add(il.finalPriceToPayExTax) })
	}

	@API
	static getTotalIncomeAmountWithoutPrepaidFees(CourseClass self) {
		return self.successAndQueuedEnrolments*.invoiceLines.flatten().inject(Money.ZERO) {
			Money total,  InvoiceLine il -> total.add(il.finalPriceToPayExTax.subtract(il.prepaidFeesRemaining))
		}.add(self.invoiceLines.inject(Money.ZERO) {
			Money total, InvoiceLine il -> total.add(il.finalPriceToPayExTax.subtract(il.prepaidFeesRemaining))
		})
	}


	/**
	*
	* @return all outcomes of all enrolments in this courseCLass
	*/
	@API
	static getOutcomes(CourseClass self) {
		return getService(CourseClassService).getOutcomes(self)
	}

	// TODO: this logic is used and makes sense only for the "Income projection report", and probably should be moved there altogether
	@API
	static getPrepaidFeesForMonth(CourseClass self, int monthsCount) {
		Money totalByMonth = Money.ZERO

		Date currentDate = new Date()

		Date startOfMonth = DateUtils.truncate(DateUtils.addMonths(currentDate, monthsCount), monthsCount == 0 ? Calendar.DAY_OF_MONTH : Calendar.MONTH)
		Date endOfMonth = DateUtils.addSeconds(DateUtils.truncate(DateUtils.addMonths(currentDate, monthsCount + 1), Calendar.MONTH), -1)


		if (self.getIsDistantLearningCourse()) {
			totalByMonth = totalByMonth.add(getTotalIncomeAmount(self)).subtract(getTotalIncomeAmountWithoutPrepaidFees(self))
		} else if (self.getEndDateTime() != null && currentDate.after(self.getEndDateTime())) {
			//do nothing
		} else {

			BigDecimal start = self.getPercentageOfDeliveredScheduledHoursBeforeDate(startOfMonth)
			BigDecimal end = monthsCount == 6 ? new BigDecimal(1) : self.getPercentageOfDeliveredScheduledHoursBeforeDate(endOfMonth)

			totalByMonth = totalByMonth.add(getTotalIncomeAmount(self).multiply(end.subtract(start)))
		}

		return totalByMonth
	}

	/**
	* An alias for getAttendance(true)
	*
	* @return attendance lines for this particular courseClass including tutors
	*/
	@API
	static getAttendance(CourseClass self) {
		return getAttendance(self, true)
	}


	/**
	* An alias for getAttendance(false)
	*
	* @return attendance lines for this particular courseClass excluding tutors
	*/
	@API
	static getAttendanceWithoutTutor(CourseClass self) {
		return getAttendance(self, false)
	}

	/**
	* @param true to include tutor attendance lines in the return list
	* @return attendance lines for this particular courseClass
	*/
	static List<PrintableAttendance> getAttendance(CourseClass self, boolean addTutorLines) {

		List<Session> theSessions = self.sessions
		List<PrintableAttendance> result = new ArrayList<>()
		List<Enrolment> theEnrolments = new ArrayList<>(self.enrolments)


		// sort the students:
		List<Ordering> orderings = [Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).ascInsensitive(),
									Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).ascInsensitive()]

		Ordering.orderList(theEnrolments, orderings)

		EnrolmentService enrolmentService = getService(EnrolmentService)

		theEnrolments.findAll { Enrolment e -> e.allowedToPrint() }.each { e ->
			if (theSessions) {
				theSessions.each { s ->
					List<Attendance> attendances = Attendance.STUDENT.eq(e.student).filterObjects(s.attendance)

					result.add(PrintableAttendance.valueOf(enrolmentService, e, s, attendances[0]?.attendanceType))
				}

			} else {
				result.add(PrintableAttendance.valueOf(enrolmentService, e, null, null))
			}
		}

		if (addTutorLines) {
			String[] text = ["", "", "", "Number Attended"]
			text.each { t ->
				if (theSessions) {
					theSessions.each { s ->
						result.add(PrintableAttendance.valueOf(contactService, enrolmentService, s, t))
					}
				} else {
					result.add(PrintableAttendance.valueOf(contactService, enrolmentService, null, t))
				}
			}

			self.tutorRoles.each { tr ->
				if (theSessions) {
					theSessions.each { s ->
						List<TutorAttendance> attendances = TutorAttendance.COURSE_CLASS_TUTOR.eq(tr).filterObjects(s.sessionTutors)

						result.add(PrintableAttendance.valueOf(contactService, enrolmentService, tr.tutor, s, attendances[0]?.attendanceType))
					}
				} else {
					result.add(PrintableAttendance.valueOf(contactService, enrolmentService, tr.tutor, null, null))
				}
			}
		}

		result
	}

	static List<PrintableAssessmentClassModule> getAssessmentClassModules(CourseClass self) {
		if (self.assessmentClasses.empty || self.course.modules.empty) {
			return []
		} else {
			List<PrintableAssessmentClassModule> result = []
			self.assessmentClasses.each { ac ->
				self.course.modules.each { m ->
					result.add(PrintableAssessmentClassModule.valueOf(ac, m))
				}
			}
			return result
		}
	}

}
