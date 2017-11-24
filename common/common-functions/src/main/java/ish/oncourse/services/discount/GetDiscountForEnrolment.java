/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.discount;

import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.utils.MembershipDiscountHelper;
import ish.util.DiscountUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.math.BigDecimal;
import java.util.*;

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
	
	private List<DiscountCourseClass> classDiscounts;
	private List<Discount> addedPromos;
	private CorporatePass corporatePass;
	Map<Contact, List<CourseClass>> enrolmentsToProceed;
	private Money totalInvoicesAmount;
	private Student currentStudent;
	private CourseClass currentCourseClass;
	private BigDecimal taxRateOverridden;


	private List<DiscountCourseClass> applicableDiscounts = new LinkedList<>();

	private DiscountCourseClass chosenDiscount;

	private GetDiscountForEnrolment() {}
	
	public static GetDiscountForEnrolment valueOf(List<DiscountCourseClass> classDiscounts, List<Discount> addedPromos, CorporatePass corporatePass, List<Enrolment> enabledEnrolments, Money totalInvoicesAmount, Enrolment currentEnrolment, BigDecimal taxRateOverridden) {
		Map<Contact, List<CourseClass>> enrolmentsToProceed = new HashMap<>();
		for (Enrolment enrolment : enabledEnrolments) {
			Contact contact = enrolment.getStudent().getContact();
			List<CourseClass> classes = enrolmentsToProceed.get(contact);
			if (classes == null) {
				classes = new ArrayList<>();
				enrolmentsToProceed.put(contact, classes);
			}
			classes.add(enrolment.getCourseClass());
		}
		
		return valueOf(classDiscounts, addedPromos, corporatePass, enrolmentsToProceed, totalInvoicesAmount, currentEnrolment.getStudent(), currentEnrolment.getCourseClass(), taxRateOverridden);
	}

	public static GetDiscountForEnrolment valueOf(List<DiscountCourseClass> classDiscounts, List<Discount> addedPromos, CorporatePass corporatePass, Map<Contact, List<CourseClass>> enrolmentsToProceed, Money totalInvoicesAmount, Student currentStudent, CourseClass currentCourseClass, BigDecimal taxRateOverridden) {

		GetDiscountForEnrolment get = new GetDiscountForEnrolment();
		get.classDiscounts = classDiscounts;
		get.addedPromos = addedPromos;
		get.corporatePass = corporatePass;
		get.enrolmentsToProceed = enrolmentsToProceed;
		get.totalInvoicesAmount = totalInvoicesAmount;
		get.currentStudent = currentStudent;
		get.currentCourseClass = currentCourseClass;
		get.taxRateOverridden = taxRateOverridden;
		return get;
	}

	public GetDiscountForEnrolment get() {
		
		for (DiscountCourseClass discountCourseClass : classDiscounts) {
			if (discountCourseClass.getDiscount().isPromotion() && !addedPromos.contains(discountCourseClass.getDiscount())) {
				continue;
			} else if (isStudentEligibile(currentStudent, discountCourseClass.getDiscount()) && isDiscountEligibile(discountCourseClass.getDiscount())) {
				applicableDiscounts.add(discountCourseClass);
			}
		}
	
		if (!applicableDiscounts.isEmpty()) {
			chosenDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(applicableDiscounts, currentCourseClass.getFeeExGst(), taxRateOverridden != null ? taxRateOverridden : currentCourseClass.getTaxRate());
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

		List<CorporatePassDiscount> passDiscounts = discount.getCorporatePassDiscounts();
		
		if (passDiscounts != null && !passDiscounts.isEmpty() 
				&& (corporatePass == null || CorporatePassDiscount.CORPORATE_PASS.eq(corporatePass).filterObjects(passDiscounts).isEmpty())) {
			return false;
		}
		
		boolean minAmountCondition = totalInvoicesAmount.compareTo(discount.getMinValue()) >= 0;
		boolean minEnrolmentsCondition = satisfiesMinEnrolmentCount(discount);
		
		return minAmountCondition && minEnrolmentsCondition;
	}


	private boolean satisfiesMinEnrolmentCount(Discount discount) {
		int enrolmentsCount = 0;
		
		for (Map.Entry<Contact, List<CourseClass>> entry : enrolmentsToProceed.entrySet()) {
			for (CourseClass courseClass : entry.getValue()) {
				if (courseClass.getDiscountCourseClassBy(discount) != null) {
					enrolmentsCount++;
				}
			}
		}

		return enrolmentsCount >= discount.getMinEnrolments();
	}
	
	public List<DiscountCourseClass> getApplicableDiscounts() {
		return applicableDiscounts;
	}

	public DiscountCourseClass getChosenDiscount() {
		return chosenDiscount;
	}
}
