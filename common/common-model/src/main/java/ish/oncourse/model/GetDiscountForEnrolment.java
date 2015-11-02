/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.oncourse.utils.MembershipDiscountHelper;
import ish.oncourse.utils.WebDiscountUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.collections.ListUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class GetDiscountForEnrolment {

	/**
	 * Constant for {@link Discount#getStudentAgeOperator()} comparison: "less".
	 */
	public static final String AGE_UNDER = "<";
	/**
	 * Constant for {@link Discount#getStudentAgeOperator()} comparison:
	 * "greater".
	 */
	public static final String AGE_OVER = ">";
	
	private List<Discount> classDiscounts;
	private List<Discount> addedPromos;
	private List<Discount> corporatePassDiscounts;
	private int enabledEnrolmentsCount;
	private Money totalInvoicesAmount;
	private Enrolment currentEnrolment;

	private List<Discount> applicableDiscounts = new LinkedList<>();

	private List<Discount> bestDiscountsVariant = new LinkedList<>();

	private GetDiscountForEnrolment() {}
	
	public static GetDiscountForEnrolment valueOf(List<Discount> classDiscounts, List<Discount> addedPromos, List<Discount> corporatePassDiscounts, int enabledEnrolmentsCount, Money totalInvoicesAmount, Enrolment currentEnrolment) {

		GetDiscountForEnrolment get = new GetDiscountForEnrolment();
		get.setClassDiscounts(classDiscounts);
		get.setAddedPromos(addedPromos);
		get.setCorporatePassDiscounts(corporatePassDiscounts);
		get.setEnabledEnrolmentsCount(enabledEnrolmentsCount);
		get.setTotalInvoicesAmount(totalInvoicesAmount);
		get.setCurrentEnrolment(currentEnrolment);
		return get;
	}

	public GetDiscountForEnrolment get() {
		
		List<Discount> avalibleDiscounts = new LinkedList<>();
		if (corporatePassDiscounts != null && !corporatePassDiscounts.isEmpty()) {
			avalibleDiscounts.addAll(ListUtils.intersection(corporatePassDiscounts, classDiscounts));
		} else {
			avalibleDiscounts.addAll(classDiscounts);
		}
		
		for (Discount discount : avalibleDiscounts) {
			if (discount.isPromotion() && !addedPromos.contains(discount)) {
				continue;
			} else if (isStudentEligibile(currentEnrolment.getStudent(), discount) && isDiscountEligibile(discount)) {
				applicableDiscounts.add(discount);
			}
		}
	
		if (!applicableDiscounts.isEmpty()) {
			if (applicableDiscounts.size() == 1) {
				bestDiscountsVariant.add(applicableDiscounts.get(0));
			} else {
				bestDiscountsVariant.addAll(WebDiscountUtils.chooseBestDiscountsVariant(applicableDiscounts, currentEnrolment.getCourseClass().getFeeExGst(), currentEnrolment.getCourseClass().getTaxRate()));
			}
		} 
		
		return this;
	}


	/**
	 * Determines if the given student is eligible for this Discount, based on:
	 * enrolled within X days; student age; postcode; and concessions (test not
	 * performed if this Discount is not related to any ConcessionTypes). If the
	 * student has ANY of the concessions related to this Discount, they will be
	 * eligible. If the student has none of them (and one or more are related to
	 * this discount), they will NOT be eligible.
	 *
	 * the implementation of this method is brought from
	 * angel/client/ish.oncourse.cayenne.Discount#isStudentEligibile(Student
	 * student)[97]
	 *
	 * @param student
	 * @return
	 */
	public static boolean isStudentEligibile(Student student, Discount discount) {
		if (student == null || discount == null) {
			return false;
		}

		if (discount.getStudentEnrolledWithinDays() != null) {
			if (student.getEnrolments() == null || student.getEnrolments().size() == 0) {
				return false;// not eligibile
			}

			Calendar tresholdDate = new GregorianCalendar();
			tresholdDate.add(Calendar.DATE, 0 - discount.getStudentEnrolledWithinDays());

			Expression enrolledWithinDaysQualifier = ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS)
					.andExp(ExpressionFactory.greaterExp(Enrolment.CREATED_PROPERTY, tresholdDate.getTime()));

			if (enrolledWithinDaysQualifier.filterObjects(student.getEnrolments()).isEmpty()) {
				return false;
			}
		}
		if (discount.getStudentAge() != null) {
			if (student.getContact().getDateOfBirth() == null) {
				return false;// not eligibile
			}

			Calendar studentBirthDay = new GregorianCalendar();
			studentBirthDay.setTime(student.getContact().getDateOfBirth());

			Calendar thresholdYear = new GregorianCalendar();

			int age = discount.getStudentAge();

			thresholdYear.add(Calendar.YEAR, 0 - age);

			if (AGE_UNDER.equals(discount.getStudentAgeOperator()) && studentBirthDay.before(thresholdYear)) {
				return false;// not eligibile
			} else if (AGE_OVER.equals(discount.getStudentAgeOperator()) && studentBirthDay.after(thresholdYear)) {
				return false;// not eligibile
			} else if (!AGE_OVER.equals(discount.getStudentAgeOperator())
					&& !AGE_UNDER.equals(discount.getStudentAgeOperator())) {
				return false;// not eligibile
			}
		}
		if (discount.getDiscountConcessionTypes() != null && !discount.getDiscountConcessionTypes().isEmpty()) {
			boolean notEligibile = true;

			for (DiscountConcessionType dct : discount.getDiscountConcessionTypes()) {
				for (StudentConcession concession : student.getStudentConcessions()) {
					if (concession.getConcessionType().getId().equals(dct.getConcessionType().getId())) {
						if (!Boolean.TRUE.equals(concession.getConcessionType().getHasExpiryDate())
								|| (concession.getExpiresOn() != null && concession.getExpiresOn().after(new Date()))) {
							notEligibile = false;
							break;
						}
					}
				}
				if (!notEligibile) {
					break;
				}
			}
			if (notEligibile)
				return false; // does not have any of the concession types that
			// give this discount
		}
		if (discount.getStudentPostcodes() != null) {
			List<String> postcodes = Arrays.asList(discount.getStudentPostcodes().split("\\s*,\\s"));
			if (!postcodes.isEmpty()) {
				if (student.getContact().getPostcode() == null) {
					return false;// not eligibile
				}
				if (!postcodes.contains(student.getContact().getPostcode())) {
					return false;// not eligibile
				}
			}
		}

		MembershipDiscountHelper membershipDiscountHelper = new MembershipDiscountHelper();
		membershipDiscountHelper.setContact(student.getContact());
		membershipDiscountHelper.setDiscount(discount);
		return membershipDiscountHelper.isEligibile();
	}


	public boolean isDiscountEligibile(Discount discount) {

		boolean minAmountCondition = totalInvoicesAmount.compareTo(discount.getMinValue()) >= 0;
		boolean minEnrolmentsCondition = enabledEnrolmentsCount >= discount.getMinEnrolments();
		
		return minAmountCondition && minEnrolmentsCondition;
	}

	public void setCorporatePassDiscounts(List<Discount> corporatePassDiscounts) {
		this.corporatePassDiscounts = corporatePassDiscounts;
	}

	public void setClassDiscounts(List<Discount> classDiscounts) {
		this.classDiscounts = classDiscounts;
	}

	public void setCurrentEnrolment(Enrolment currentEnrolment) {
		this.currentEnrolment = currentEnrolment;
	}

	public void setTotalInvoicesAmount(Money totalInvoicesAmount) {
		this.totalInvoicesAmount = totalInvoicesAmount;
	}

	public void setEnabledEnrolmentsCount(int enabledEnrolmentsCount) {
		this.enabledEnrolmentsCount = enabledEnrolmentsCount;
	}

	public void setAddedPromos(List<Discount> addedPromos) {
		this.addedPromos = addedPromos;
	}

	public List<Discount> getApplicableDiscounts() {
		return applicableDiscounts;
	}

	public List<Discount> getBestDiscountsVariant() {
		return bestDiscountsVariant;
	}
}
