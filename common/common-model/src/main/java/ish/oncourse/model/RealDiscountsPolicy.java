package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.utils.MembershipDiscountHelper;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.*;

/**
 * Use this policy to get the discounts for which the selected student is
 * eligible.
 * 
 * @author ksenia
 * 
 */
public class RealDiscountsPolicy extends DiscountPolicy {

	/**
	 * Constant for {@link Discount#getStudentAgeOperator()} comparison: "less".
	 */
	public static final String AGE_UNDER = "<";
	/**
	 * Constant for {@link Discount#getStudentAgeOperator()} comparison:
	 * "greater".
	 */
	public static final String AGE_OVER = ">";
	/**
	 * Student that tries to get discount.
	 */
	private Student student;

	/**
	 * Default constructor for the {@link RealDiscountsPolicy}. Gets the
	 * user-defined promotions, and the student to consider eligibility.
	 * 
	 * @param promotions
	 * @param student
	 */
	public RealDiscountsPolicy(List<Discount> promotions, Student student) {
		this(promotions);
		this.student = student;
	}

	public RealDiscountsPolicy(List<Discount> promotions) {
		super(promotions);
	}

	/**
	 * Check if the potential promotions are defined by user and if the student
	 * given is eligible for discount. {@inheritDoc}
	 * 
	 * @see ish.oncourse.model.DiscountPolicy#getApplicableByPolicy(java.util.List)
	 */
	@Override
	public List<Discount> getApplicableByPolicy(List<Discount> discounts) {
		List<Discount> result = new ArrayList<>();
		if (discounts != null) {
			for (Discount discount : discounts) {
				if (discount.isPromotion() && !isPromotionAdded(discount)) {
					continue;
				}
				if (isStudentEligibile(student, discount)) {
					result.add(discount);
				}
			}
		}
		return result;
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
	public boolean isStudentEligibile(Student student, Discount discount) {
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

}
