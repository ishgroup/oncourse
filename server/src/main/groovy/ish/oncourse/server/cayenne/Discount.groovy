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

import ish.common.types.DiscountType
import ish.math.Money
import ish.math.MoneyRounding
import ish.oncourse.API
import ish.oncourse.cayenne.DiscountInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Discount
import ish.persistence.CommonExpressionFactory
import ish.util.DateTimeUtil
import ish.util.DiscountUtils
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.regex.Pattern

/**
 * A discount represents a set of rules used to determine whether a different price can be applied to an enrolments.
 * Discounts can be negative (in which case they represent a surcharge).
 */
@API
@QueueableEntity
class Discount extends _Discount implements DiscountTrait, DiscountInterface, Queueable {

	private static final Logger logger = LogManager.getLogger()

	public static final String ADD_BY_DEFAULT_PROPERTY = "addByDefault";
	public static final String VALID_FROM_PROPERTY = "validFrom";
	public static final String VALID_TO_PROPERTY = "validTo";

	public static final String AGE_UNDER = "<"
	public static final String AGE_OVER = ">"
	/**
	 *
	 */

	@Override
	void onEntityCreation() {
		super.onEntityCreation()
		if (getLimitPreviousEnrolment() == null) {
			setLimitPreviousEnrolment(false)
		}
	}

	@Override
	void validateForSave(final ValidationResult validationResult) {
		// data sanity check:
		if (getDiscountPercent() == null || BigDecimal.ZERO == getDiscountPercent()) {
			setDiscountMax(Money.ZERO)
			setDiscountMin(Money.ZERO)
		}
	}

	private static Integer studentAge(@Nullable String ageOperation) {
		String age = ageOperation == null ? "" : ageOperation.trim()
		age = age.replaceAll("[^\\d]", "")
		return age != null && age.isEmpty() ? null : Integer.parseInt(age)
	}

	private static String studentAgeOperator(@Nullable String ageOperation) {
		String op = ageOperation == null ? "" : ageOperation.trim()
		op = op.replaceAll("[\\d\\s]", "").trim()
		return op != null && op.isEmpty() ? null : op
	}

	private static String studentPostcodes(@Nullable String postcodes) {
		String codes = postcodes == null ? "" : postcodes.trim()
		codes = codes.replaceAll("\\s", "")
		return codes != null && codes.isEmpty() ? null : codes
	}

	/**
	 * Translated from willow.
	 *
	 * @param courseClass the class to determine the discount for.
	 * @return zero if the class is not assigned for discount or a discount value.
	 */
	@Nonnull
	Money valueForCourseClass(@Nullable CourseClass courseClass) {
		if (courseClass != null && getApplicableDiscounts(courseClass).contains(this)) {
			for (DiscountCourseClass discountCourseClass : courseClass.getDiscountCourseClasses()) {
				if (discountCourseClass.getDiscount().equalsIgnoreContext(this)) {
					DiscountUtils.discountValue(discountCourseClass, courseClass.getFeeExGst(), courseClass.getTax().getRate())
				}
			}
			return Money.ZERO
		} else {
			return Money.ZERO
		}
	}

	/**
	 * @return list of postcodes whose residents are eligible for this discount
	 */
	@Nonnull
	@API
	List<String> getStudentPostcodes() {
		List<String> result = new ArrayList<>()

		String postcodeString = getStudentPostcode()
		if (postcodeString == null || postcodeString.length() == 0) {
			return result
		}
		Pattern pattern = Pattern.compile(",")
		String[] postcodes = pattern.split(postcodeString)

		for (String postcode : postcodes) {
			if (postcode != null && postcode.trim().length() > 0) {
				result.add(postcode.trim())
			}
		}

		return result
	}

	@Override
	List<CorporatePassDiscount> getCorporatePassDiscount() {
		super.getCorporatePassDiscount()
	}

	@Override
	Integer getMinEnrolments() {
		super.getMinEnrolments()
	}

	@Override
	Money getMinValue() {
		super.getMinValue()
	}


	/**
	 * @param courseClass class to check
	 * @return list of applicable discounts
	 */
	@API
	static List<Discount> getApplicableDiscounts(@Nonnull CourseClass courseClass) {
		List<Discount> results = new ArrayList<>()

		results.addAll(courseClass.getDiscounts())

		Date now = new Date()
		Expression validToExp = VALID_TO.isNull().andExp(VALID_TO_OFFSET.isNull())
		validToExp = validToExp.orExp(VALID_TO.isNotNull().andExp(VALID_TO.gt(CommonExpressionFactory.previousMidnight(now))))

		Expression validFromExp = VALID_FROM.isNull().andExp(VALID_FROM_OFFSET.isNull())
		validFromExp = validFromExp.orExp(VALID_FROM.isNotNull().andExp(VALID_FROM.lt(CommonExpressionFactory.nextMidnight(now))))

		// apply discounts with offsets (valid from offset, valid to offset) only when courseClass has start date time.
		Date classStart = courseClass.getStartDateTime()
		if (classStart != null) {
			int startClassOffsetInDays = DateTimeUtil.getDaysLeapYearDaylightSafe(classStart, now)

			validToExp = validToExp.orExp(VALID_TO_OFFSET.isNotNull().andExp(VALID_TO_OFFSET.gte(startClassOffsetInDays)))
			validFromExp = validFromExp.orExp(VALID_FROM_OFFSET.isNotNull().andExp(VALID_FROM_OFFSET.lte(startClassOffsetInDays)))
		}

		Expression validExp = validToExp.andExp(validFromExp).andExp(CODE.isNull())

		results = validExp.filterObjects(results)

		return results
	}

	void setStudentPostcodes(@Nonnull List<String> postcodes) {
		String result = ""
		for (String postcode : postcodes) {
			result = result + postcode + ","
		}
		result = result.substring(0, result.lastIndexOf(","))
		setStudentPostcode(result)
	}

	/**
	 * @return true if discount is added by default when creating a new class
	 */
	@Nonnull
	@API
	@Override
	Boolean getAddByDefault() {
		return super.getAddByDefault()
	}

	/**
	 * @return promotion code for this discount
	 */
	@API
	@Override
	String getCode() {
		return super.getCode()
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
	 * @return discount's dollar value
	 */
	@API
	@Override
	Money getDiscountDollar() {
		return super.getDiscountDollar()
	}

	/**
	 * Maximum discount value sets a cap on the discount value which can be applied.
	 * For example, if invoice line amount is $100, discount is 30% and maximum discount value is set to $10,
	 * only $10 discount will be applied.
	 *
	 * @return maximum value for this discount
	 */
	@API
	@Override
	Money getDiscountMax() {
		return super.getDiscountMax()
	}

	/**
	 * Minimum discount value sets a minimal discount value for this discount
	 * For example, if invoice line amount is $100, discount is 10% and minimum discount value is set to $20,
	 * then $20 discount will be applied instead of $10.
	 *
	 * @return minimum value for this discount
	 */
	@API
	@Override
	Money getDiscountMin() {
		return super.getDiscountMin()
	}

	/**
	 * @return discount's percentage
	 */
	@API
	@Override
	BigDecimal getDiscountPercent() {
		return super.getDiscountPercent()
	}

	/**
	 * @return type of discount: percent, dollar value or fee override
	 */
	@Nonnull
	@API
	@Override
	DiscountType getDiscountType() {
		return super.getDiscountType()
	}

	/**
	 * @return true if this discount is hidden from promotions list on website
	 */
	@Nonnull
	@API
	@Override
	Boolean getHideOnWeb() {
		return super.getHideOnWeb()
	}


	/**
	 * @return true if discount is available to students enrolling on website
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsAvailableOnWeb() {
		return super.getIsAvailableOnWeb()
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
	 * @return the name of the discount. Will be displayed on the website as well.
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return predicted percentage of students using this discount
	 */
	@Nonnull
	@API
	@Override
	BigDecimal getPredictedStudentsPercentage() {
		return super.getPredictedStudentsPercentage()
	}

	/**
	 * @return a description which may be displayed to students
	 */
	@API
	@Override
	String getPublicDescription() {
		return super.getPublicDescription()
	}

	/**
	 * After the discount is applied, different rounding options can be applied to the resulting amount.
	 *
	 * @return rounding mode for this discount
	 */
	@Nonnull
	@API
	@Override
	MoneyRounding getRounding() {
		return super.getRounding()
	}

	/**
	 * The discount can be limited to students up to a certain age.
	 *
	 * @return Maximum student age in years
	 * TODO: why does this return String?
	 */
	@API
	@Override
	String getStudentAge() {
		return super.getStudentAge()
	}

	/**
	 * @return
	 * TODO: should this be removed?
	 */
	@Deprecated
	@Override
	Integer getStudentConcessionObsolete() {
		return super.getStudentConcessionObsolete()
	}

	/**
	 * @return number of days from last enrolment student should not exceed to receive the discount
	 */
	@API
	@Override
	Integer getStudentEnrolledWithinDays() {
		return super.getStudentEnrolledWithinDays()
	}

	/**
	 * The discount can be limited to students who live in certain postcodes. If more than one postcode is needed,
	 * separate them with commas ",".
	 *
	 * @return one or more postcodes for the student's address
	 */
	@API
	@Override
	String getStudentPostcode() {
		return super.getStudentPostcode()
	}

	/**
	 * If this value is null the discount doesn't have a start date.
	 *
	 * @return the date before which the discount will not be applied
	 */
	@API
	@Override
	Date getValidFrom() {
		return super.getValidFrom()
	}

	/**
	 * If this value is null the discount doesn't have an expiry date.
	 *
	 * @return the date after which the discount will not be applied
	 */
	@API
	@Override
	Date getValidTo() {
		return super.getValidTo()
	}



	/**
	 * @return list of concession types linked to this discount
	 */
	@Nonnull
	@API
	@Override
	List<DiscountConcessionType> getDiscountConcessionTypes() {
		return super.getDiscountConcessionTypes()
	}

	/**
	 * @return list of classes linked to this discount, discount can be applied only if enrolling to classes from this list
	 */
	@Nonnull
	@API
	@Override
	List<DiscountCourseClass> getDiscountCourseClasses() {
		return super.getDiscountCourseClasses()
	}

	/**
	 * @return list of memberships linked to this discount
	 */
	@Nonnull
	@API
	@Override
	List<DiscountMembership> getDiscountMemberships() {
		return super.getDiscountMemberships()
	}

	/**
	 * @return cos account for this discount
	 */
	@Nullable
	@API
	@Override
	Account getCosAccount() {
		return super.getCosAccount()
	}
}
