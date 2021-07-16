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
package ish.budget;

import ish.common.types.ClassCostFlowType;
import ish.common.types.ClassCostRepetitionType;
import ish.math.Money;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.cayenne.DiscountInterface;
import ish.oncourse.server.cayenne.*;
import ish.util.DiscountUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 */
public class ClassCostUtil {

    /**
	 * Unit count is stored only for FIXED, DISCOUNT and PER HOUR class cost types, for other uses it is left null
	 *
	 * @param classCost
	 */
	public static void resetUnitCount(ClassCost classCost) {
		ClassCostRepetitionType type = classCost.getRepetitionType();

		// fixed type always has count of 1
		if (ClassCostRepetitionType.FIXED.equals(type)) {
			classCost.setUnitCount(BigDecimal.ONE);
		} else if (ClassCostRepetitionType.DISCOUNT.equals(type) || ClassCostRepetitionType.PER_UNIT.equals(type)) {
			// check if it is null, set to 1 in this case, but leave alone any other values
			if (classCost.getUnitCount() == null) {
				classCost.setUnitCount(BigDecimal.ONE);
			}
		} else {
			// all other types have null count (inherit from class)
			classCost.setUnitCount(null);
		}
	}

	/**
	 * The budgeted cost for a ClassCost for the entire duration of the class. (Convenience method)
	 *
	 * @param classCost
	 * @return the money value of the cost
	 */
	public static Money getBudgetedCost(ClassCost classCost) {
		if (classCost.getCourseClass() != null) {
			return getBudgetedCost(classCost, null, classCost.getCourseClass().getBudgetedPlaces());
		}
		return getBudgetedCost(classCost, null, null);
	}

	/**
	 * The budgeted cost is the predicted value based on two values: until date and plannedEnrolmentsCount. (until date affects number of sessions, enrolment
	 * count is a field in the class)
	 *
	 * @param classCost the class cost
	 * @param until (when null defaults to CourseClass end date)
	 * @param plannedEnrolmentsCount (when null defaults to current valid enrolment count for the class)
	 * @return Money value
	 */
	public static Money getCost(ClassCost classCost, Date until, Integer plannedEnrolmentsCount, String type) {
		Money cost = Money.ZERO;
		if (classCost.getCourseClass() == null) {
			return cost;
		}

		if (getPerUnitAmountExTax(classCost) == null && !ClassCostFlowType.DISCOUNT.equals(classCost.getFlowType())) {
			return cost;
		}

		ClassCostRepetitionType repetitionType = classCost.getRepetitionType();
		Money perUnitAmountExTax = getPerUnitAmountExTax(classCost);
		BigDecimal unitCount = null;

		if (until == null) {
			until = classCost.getCourseClass().getEndDateTime();
		}

		if (plannedEnrolmentsCount == null) {
			switch (type) {
				case ClassBudgetUtil.ACTUAL:
					plannedEnrolmentsCount = classCost.getCourseClass().getValidEnrolmentCount();
					break;
				case ClassBudgetUtil.MAXIMUM:
					plannedEnrolmentsCount = classCost.getCourseClass().getMaximumPlaces();
					break;
				case ClassBudgetUtil.BUDGETED:
					plannedEnrolmentsCount = classCost.getCourseClass().getBudgetedPlaces();
					break;
			}
		}

		// if maximum/budgeted places is not set then default to 0
		if (plannedEnrolmentsCount == null) {
			plannedEnrolmentsCount = 0;
		}

		if (ClassCostRepetitionType.DISCOUNT.equals(repetitionType) || ClassCostFlowType.DISCOUNT.equals(classCost.getFlowType())) {
			Money feeExGst = classCost.getCourseClass().getFeeExGst();
			BigDecimal rate = null;

			for (ClassCost costInterface : classCost.getCourseClass().getCosts()) {
				if (ClassCostFlowType.INCOME.equals(costInterface.getFlowType())) {
					rate = costInterface.getTax().getRate();
				}
			}
			if (rate == null) {
				rate = classCost.getCourseClass().getTax().getRate();
			}
			BigDecimal predictedStudentsPercentage = classCost.getDiscountCourseClass().getPredictedStudentsPercentage() != null
					? classCost.getDiscountCourseClass().getPredictedStudentsPercentage()
					: classCost.getDiscountCourseClass().getDiscount().getPredictedStudentsPercentage();
			switch (type) {
				case ClassBudgetUtil.ACTUAL:
					return getActualDiscountedAmount(classCost);
				case ClassBudgetUtil.BUDGETED:
				case ClassBudgetUtil.MAXIMUM:
					return DiscountUtils.discountValue(classCost.getDiscountCourseClass(), feeExGst, rate)
							.multiply(plannedEnrolmentsCount).multiply(predictedStudentsPercentage);
			}
		} else if (ClassCostRepetitionType.FIXED.equals(repetitionType) || ClassCostRepetitionType.PER_UNIT.equals(repetitionType)) {
			// PER_HOUR and FIXED class costs use the unitCount field directly
			unitCount = classCost.getUnitCount();
			if (unitCount == null) {
				unitCount = BigDecimal.ONE;
			}
			// for other class cost types calculate unit count
		} else if (ClassCostRepetitionType.PER_SESSION.equals(repetitionType)) {
			unitCount = new BigDecimal(classCost.getSessionCount(until));
		} else if (ClassCostRepetitionType.PER_ENROLMENT.equals(repetitionType)) {
			switch (type) {
				case ClassBudgetUtil.ACTUAL:
					if (ClassCostFlowType.INCOME.equals(classCost.getFlowType()) && classCost.getInvoiceToStudent()) {
						Money result = Money.ZERO;
						if (classCost.getCourseClass().getSuccessAndQueuedEnrolments() != null) {
							for (Enrolment e : classCost.getCourseClass().getSuccessAndQueuedEnrolments()) {
                                result = result.add(e.getOriginalInvoiceLine().getPriceTotalExTax());
							}
						}
						return result;
					}
					// else, if not enrolment
					unitCount = new BigDecimal(classCost.getCourseClass().getValidEnrolmentCount());
					break;
				case ClassBudgetUtil.BUDGETED:
					unitCount = new BigDecimal(plannedEnrolmentsCount);
					break;
				case ClassBudgetUtil.MAXIMUM:
					if (classCost.getCourseClass().getMaximumPlaces() != null) {
						unitCount = new BigDecimal(classCost.getCourseClass().getMaximumPlaces());
					} else {
						unitCount = BigDecimal.ZERO;
					}
					break;
			}
		} else if (ClassCostRepetitionType.PER_TIMETABLED_HOUR.equals(repetitionType)) {
			unitCount = classCost.getSessionPayableHours(until);
		} else if (ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR.equals(repetitionType)) {
			switch (type) {
				case ClassBudgetUtil.ACTUAL:
					unitCount = new BigDecimal(classCost.getCourseClass().getValidEnrolmentCount()).multiply(classCost.getSessionPayableHours(classCost
							.getCourseClass().getEndDateTime()));
					break;
				case ClassBudgetUtil.BUDGETED:
					unitCount = classCost.getSessionPayableHours(until).multiply(new BigDecimal(plannedEnrolmentsCount));
					break;
				case ClassBudgetUtil.MAXIMUM:
					unitCount = new BigDecimal(classCost.getCourseClass().getMaximumPlaces()).multiply(classCost.getSessionPayableHours(classCost.getCourseClass()
							.getEndDateTime()));
					break;
			}

		}

		return getCost(perUnitAmountExTax, unitCount, classCost.getMinimumCost(), classCost.getMaximumCost(), classCost.getCurrentOncostRate());
	}

	/**
	 * The budgeted cost is the predicted value based on two values: until date and plannedEnrolmentsCount. (until date affects number of sessions, enrolment
	 * count is a field in the class)
	 *
	 * @param classCost the class cost
	 * @param until (when null defaults to CourseClass end date)
	 * @param plannedEnrolmentsCount (when null defaults to current valid enrolment count for the class)
	 * @return Money value
	 */
	public static Money getBudgetedCost(ClassCost classCost, Date until, Integer plannedEnrolmentsCount) {
		return getCost(classCost, until, plannedEnrolmentsCount, ClassBudgetUtil.BUDGETED);
	}

	/**
	 * Actual class cost, as opposed to the budgeted, is the actual cost/income which incured. This value is not based on any other params then the class cost
	 * itself, and all the multipliers are calculated on the fly from the data.
	 *
	 * @param classCost
	 * @return Money value
	 */
	public static Money getActualCost(ClassCost classCost) {
		return getCost(classCost, null, null, ClassBudgetUtil.ACTUAL);
	}

	/**
	 * @param classCost
	 * @return Money value
	 */
	public static Money getMaximumCost(ClassCost classCost) {
		return getCost(classCost, null, null, ClassBudgetUtil.MAXIMUM);
	}

	/**
	 * default method to calculate the cost.
	 *
	 * @param perUnitAmountExTax
	 * @param units
	 * @param minimumCost
	 * @param maximumCost
	 * @param onCostRate
	 * @return Money value
	 */
	public static Money getCost(Money perUnitAmountExTax, BigDecimal units, Money minimumCost, Money maximumCost, BigDecimal onCostRate) {
		if (perUnitAmountExTax == null) {
			throw new IllegalArgumentException("the per unit amount ex tax has to be specified");
		}

		if (units == null) {
			throw new IllegalArgumentException("the number of units have to be specified");
		}

		Money cost = perUnitAmountExTax.multiply(units);

		return applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate);
	}

	/**
	 * Utility method to add onCostRate and apply minimum and maximum cost constraints to the cost.
	 *
	 * @param cost
	 * @param minimumCost
	 * @param maximumCost
	 * @param onCostRate
	 * @return Money value
	 */
	public static Money applyOnCostRateAndMinMaxCost(Money cost, Money minimumCost, Money maximumCost, BigDecimal onCostRate) {
		if (cost == null) {
			throw new IllegalArgumentException("the per cost has to be specified");
		}

		// apply the oncost rate
		cost = applyOnCostRate(cost, onCostRate);

		// now apply cost limits
		if (minimumCost != null && cost.compareTo(minimumCost) < 0) {
			cost = minimumCost;
		} else if (maximumCost != null && !maximumCost.isZero() && cost.compareTo(maximumCost) > 0) {
			cost = maximumCost;
		}

		return cost;
	}

	/**
	 * applies onCostrate to a money cost.
	 *
	 * @param cost
	 * @param onCostRate
	 * @return
	 */
	public static Money applyOnCostRate(Money cost, BigDecimal onCostRate) {
		if (cost == null) {
			throw new IllegalArgumentException("the per cost has to be specified");
		}

		// apply the oncost rate
		if (onCostRate != null && !BigDecimal.ZERO.equals(onCostRate)) {
			cost = cost.multiply(onCostRate.add(BigDecimal.ONE));
		}

		return cost;
	}

	/**
	 * calculates class fee based on a list of class costs
	 *
	 * @param costs
	 * @return calculated fee
	 */
	public static Money calculateFee(List<ClassCost> costs) {
		Money fee = Money.ZERO;
		if (costs != null && costs.size() != 0) {
			for (ClassCost cc : costs) {
				if (ClassCostFlowType.INCOME.equals(cc.getFlowType()) && Boolean.TRUE.equals(cc.getInvoiceToStudent())) {
					fee = fee.add(getPerUnitAmountExTax(cc));
				}
			}
		}
		return fee;
	}

	/**
	 * calculates class deposit based on a list of class costs
	 *
	 * @param costs
	 * @return calculated deposit
	 */
	public static Money calculateDeposit(List<ClassCost> costs) {
		Money deposit = Money.ZERO;
		if (costs != null && costs.size() != 0) {
			for (ClassCost cc : costs) {
				if (ClassCostFlowType.INCOME.equals(cc.getFlowType()) && Boolean.TRUE.equals(cc.getPayableOnEnrolment())) {
					deposit = deposit.add(getPerUnitAmountExTax(cc));
				}
			}
		}
		return deposit;
	}

	private static Money getActualDiscountedAmount(ClassCost cost) {
		if (ClassCostFlowType.DISCOUNT.equals(cost.getFlowType()) && (ClassCostRepetitionType.DISCOUNT.equals(cost.getRepetitionType()))) {

			Money discounted = Money.ZERO;
			List<Enrolment> enrolments = cost.getCourseClass().getSuccessAndQueuedEnrolments();
			for (Enrolment e : enrolments) {
				for (InvoiceLine invoiceLine : e.getInvoiceLines()) {
					List<Discount> discounts = invoiceLine.getDiscounts();
					Money currentTotalDiscountAmount = Money.ZERO;
					Money currentDiscountAmount = Money.ZERO;
					for (DiscountInterface discount : discounts) {

						DiscountCourseClassInterface classDiscount = null;
						for (DiscountCourseClassInterface dcc : cost.getCourseClass().getDiscountCourseClasses()) {
							if (dcc.getDiscount().equals(discount)) {
								classDiscount = dcc;
								break;
							}
						}

						Money discountValue = DiscountUtils.discountValue(classDiscount, invoiceLine.getPriceTotalExTax(), invoiceLine.getTax().getRate());
						currentTotalDiscountAmount = currentTotalDiscountAmount.add(discountValue);

						if (discount.equals(cost.getDiscountCourseClass().getDiscount())) {
							currentDiscountAmount = discountValue;
						}
					}

					if (currentTotalDiscountAmount.compareTo(Money.ZERO) != 0) {
						discounted = discounted.add(invoiceLine.getDiscountTotalExTax()
								.multiply(currentDiscountAmount.divide(currentTotalDiscountAmount)));
					}
				}
			}

			return discounted;
		}
		return Money.ZERO;
	}

	public static Money getPerUnitAmountExTax(ClassCost cost) {

		if (ClassCostFlowType.DISCOUNT.equals(cost.getFlowType()) && ClassCostRepetitionType.DISCOUNT.equals(cost.getRepetitionType())) {
			Money feeExGst = cost.getCourseClass().getFeeExGst();
			BigDecimal rate = null;

			for (ClassCost classCost : cost.getCourseClass().getCosts()) {
				if (ClassCostFlowType.INCOME.equals(classCost.getFlowType()) && classCost.getTax() != null) {
					rate = classCost.getTax().getRate();
					break;
				}
			}
			if (rate == null) {
				rate = cost.getCourseClass().getTax().getRate();
			}

			return DiscountUtils.discountValue(cost.getDiscountCourseClass(), feeExGst, rate);
		}

		Money result = cost.getPerUnitAmountExTax();

		// if the value was not established, then try to return inherited values from the pay rate (wages only)
		if (result == null && ClassCostFlowType.WAGES.equals(cost.getFlowType())) {
			result = calculateWageCost(cost);
		}

		return result;
	}

	public static Money calculateWageCost(ClassCost cost) {
		if (ClassCostFlowType.WAGES.equals(cost.getFlowType()) && cost.getTutorRole() !=null && cost.getTutorRole().getDefinedTutorRole() != null && cost.getCourseClass() != null) {

			// if class has no start date assigned then calculating pay rate for current date
			Date payRateDate = cost.getCourseClass().getStartDateTime() != null ? cost.getCourseClass().getStartDateTime() : new Date();

			PayRate pr = cost.getTutorRole().getDefinedTutorRole().getPayRateForDate(payRateDate);
			if (pr != null) {
				return pr.getRate();
			} else {
				// if no applicable pay rate defined then return $0
				return Money.ZERO;
			}
		}

		return null;
	}

	public static String getUnit2(ClassCost classCost) {
		if (ClassCostRepetitionType.PER_UNIT.equals(classCost.getRepetitionType())) {
			return "per unit";
		} else if (ClassCostRepetitionType.PER_TIMETABLED_HOUR.equals(classCost.getRepetitionType())) {
			return "per timetabled hour";
		} else if (ClassCostRepetitionType.FIXED.equals(classCost.getRepetitionType())) {
			return "fixed";
		} else if (ClassCostRepetitionType.PER_SESSION.equals(classCost.getRepetitionType())) {
			return "per session";
		} else if (ClassCostRepetitionType.PER_ENROLMENT.equals(classCost.getRepetitionType())) {
			return "per enrolment";
		} else if (ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR.equals(classCost.getRepetitionType())) {
			return "per student contact hour";
		}
		return "";
	}

	public static boolean isAmountOverride(ClassCost classCost) {
		return classCost.getPerUnitAmountExTax() != null;
	}
}
