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
import ish.messaging.ICourseClass;
import ish.messaging.IEnrolment;
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.InvoiceLine;
import org.apache.cayenne.PersistenceState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 */
public class ClassBudgetUtil {
    public static final String PREFIX = "classBudget";

	public static final String MAXIMUM = PREFIX + "_maximum_";
	public static final String BUDGETED = PREFIX + "_budgeted_";
	public static final String ACTUAL = PREFIX + "_actual_";

	public static final String FEE_INCOME_EX_TAX = "fee_income";
	public static final String DISCOUNTS_EX_TAX = "discounts";
	public static final String OTHER_INCOME_EX_TAX = "other_income";
	public static final String TOTAL_INCOME_EX_TAX = "total_income";
	public static final String CUSTOM_INVOICES_EX_TAX = "custom_invoices";

	public static final String SUNK_COST_EX_TAX = "sunk_cost";
	public static final String RUNNING_COST_EX_TAX = "running_cost";
	public static final String TOTAL_COST_EX_TAX = "total_cost";

	public static final String TOTAL_PROFIT_EX_TAX = "total_profit";

	public static final String CLASS_TOTAL_COST_EX_TAX = ACTUAL + TOTAL_COST_EX_TAX;
	public static final String CLASS_TOTAL_SUNK_COST_EX_TAX = ACTUAL + SUNK_COST_EX_TAX;
	public static final String CLASS_TOTAL_RUNNING_COST_EX_TAX = ACTUAL + RUNNING_COST_EX_TAX;
	public static final String CLASS_TOTAL_FEE_INCOME_EX_TAX = ACTUAL + FEE_INCOME_EX_TAX;
	public static final String CLASS_TOTAL_OTHER_INCOME_EX_TAX = ACTUAL + OTHER_INCOME_EX_TAX;
	public static final String CLASS_TOTAL_DISCOUNTS_EX_TAX = ACTUAL + DISCOUNTS_EX_TAX;
	public static final String CLASS_TOTAL_PROFIT_EX_TAX = ACTUAL + TOTAL_PROFIT_EX_TAX;
	public static final String CLASS_TOTAL_INCOME_EX_TAX = ACTUAL + TOTAL_INCOME_EX_TAX;
	public static final String CLASS_TOTAL_CUSTOM_INVOICE_EX_TAX = ACTUAL + CUSTOM_INVOICES_EX_TAX;

	public static final String CLASS_BUDGETED_COST_EX_TAX = BUDGETED + TOTAL_COST_EX_TAX;
	public static final String CLASS_BUDGETED_SUNK_COST_EX_TAX = BUDGETED + SUNK_COST_EX_TAX;
	public static final String CLASS_BUDGETED_RUNNING_COST_EX_TAX = BUDGETED + RUNNING_COST_EX_TAX;
	public static final String CLASS_BUDGETED_FEE_INCOME_EX_TAX = BUDGETED + FEE_INCOME_EX_TAX;
	public static final String CLASS_BUDGETED_OTHER_INCOME_EX_TAX = BUDGETED + OTHER_INCOME_EX_TAX;
	public static final String CLASS_BUDGETED_DISCOUNTS_EX_TAX = BUDGETED + DISCOUNTS_EX_TAX;
	public static final String CLASS_BUDGETED_PROFIT_EX_TAX = BUDGETED + TOTAL_PROFIT_EX_TAX;
	public static final String CLASS_BUDGETED_INCOME_EX_TAX = BUDGETED + TOTAL_INCOME_EX_TAX;
	public static final String CLASS_BUDGETED_CUSTOM_INVOICE_EX_TAX = BUDGETED + CUSTOM_INVOICES_EX_TAX;

	public static final String CLASS_MAXIMUM_COST_EX_TAX = MAXIMUM + TOTAL_COST_EX_TAX;
	public static final String CLASS_MAXIMUM_SUNK_COST_EX_TAX = MAXIMUM + SUNK_COST_EX_TAX;
	public static final String CLASS_MAXIMUM_RUNNING_COST_EX_TAX = MAXIMUM + RUNNING_COST_EX_TAX;
	public static final String CLASS_MAXIMUM_FEE_INCOME_EX_TAX = MAXIMUM + FEE_INCOME_EX_TAX;
	public static final String CLASS_MAXIMUM_OTHER_INCOME_EX_TAX = MAXIMUM + OTHER_INCOME_EX_TAX;
	public static final String CLASS_MAXIMUM_DISCOUNTS_EX_TAX = MAXIMUM + DISCOUNTS_EX_TAX;
	public static final String CLASS_MAXIMUM_PROFIT_EX_TAX = MAXIMUM + TOTAL_PROFIT_EX_TAX;
	public static final String CLASS_MAXIMUM_INCOME_EX_TAX = MAXIMUM + TOTAL_INCOME_EX_TAX;
	public static final String CLASS_MAXIMUM_CUSTOM_INVOICE_EX_TAX = MAXIMUM + CUSTOM_INVOICES_EX_TAX;

	public static final List<String> BudgetPropertyKeys;
	static {

		List<String> budgetingKeys = new ArrayList<>();
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_SUNK_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_RUNNING_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_FEE_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_OTHER_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_DISCOUNTS_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_PROFIT_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_TOTAL_CUSTOM_INVOICE_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_FEE_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_OTHER_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_DISCOUNTS_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_PROFIT_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_RUNNING_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_SUNK_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_BUDGETED_CUSTOM_INVOICE_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_FEE_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_OTHER_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_DISCOUNTS_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_PROFIT_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_INCOME_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_RUNNING_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_SUNK_COST_EX_TAX);
		budgetingKeys.add(ClassBudgetUtil.CLASS_MAXIMUM_CUSTOM_INVOICE_EX_TAX);

		BudgetPropertyKeys = Collections.unmodifiableList(budgetingKeys);

	}

	private static String getKeyType(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}

		if (key.startsWith(ACTUAL)) {
			return ACTUAL;
		} else if (key.startsWith(BUDGETED)) {
			return BUDGETED;
		} else if (key.startsWith(MAXIMUM)) {
			return MAXIMUM;
		}
		throw new IllegalArgumentException("key not reckognised " + key);
	}

	public static Object getValueForKey(String key, ICourseClass cclass) {
		if (key == null) {
			return null;
		} else if (key.endsWith(SUNK_COST_EX_TAX)) {
			return getClassSunkCostsExTax(cclass, getKeyType(key));
		} else if (key.endsWith(RUNNING_COST_EX_TAX)) {
			return getClassRunningCostsExTax(cclass, getKeyType(key));
		} else if (key.endsWith(TOTAL_COST_EX_TAX)) {
			return getClassCostsExTax(cclass, getKeyType(key));
		} else if (key.endsWith(FEE_INCOME_EX_TAX)) {
			return getClassFeeIncomeExTax(cclass, getKeyType(key));
		} else if (key.endsWith(OTHER_INCOME_EX_TAX)) {
			return getClassOtherIncomeExTax(cclass, getKeyType(key));
		} else if (key.endsWith(TOTAL_INCOME_EX_TAX)) {
			return getClassIncomeExTax(cclass, getKeyType(key));
		} else if (key.endsWith(TOTAL_PROFIT_EX_TAX)) {
			return getClassProfitExTax(cclass, getKeyType(key));
		} else if (key.endsWith(DISCOUNTS_EX_TAX)) {
			return getClassDiscountsExTax(cclass, getKeyType(key));
		} else if (key.endsWith(CUSTOM_INVOICES_EX_TAX)) {
			return getClassCustomInvoicesExTax(cclass);
		}
		return null;
	}

	public static Money getClassIncomeExTax(ICourseClass cclass, String type) {
		Money feeIncome = getClassFeeIncomeExTax(cclass, type);
		Money otherIncome = getClassOtherIncomeExTax(cclass, type);
		Money discounts = getClassDiscountsExTax(cclass, type);
		Money customInvoices = getClassCustomInvoicesExTax(cclass);

		return feeIncome.add(otherIncome).add(customInvoices).subtract(discounts);
	}

	private static Money getClassDiscountsExTax(ICourseClass cclass, String type) {
		Money result = Money.ZERO;
		if (cclass.getCosts() == null || cclass.getCosts().size() == 0) {
			return result;
		}

		// for actual discounted amount calculation need to consider discounts entered manually in QE. The only
		// way to calculate it is by summing up all discount amounts in invoice lines for successful enrolments.
		if (ACTUAL.equals(type)) {
			List<? extends IEnrolment> enrolments = cclass.getSuccessAndQueuedEnrolments();
			return enrolments.stream().flatMap(e -> e.getInvoiceLines().stream()).map(InvoiceLine::getDiscountTotalExTax).reduce(Money.ZERO, (a, b) -> a.add(b));
		}

		return calculateCostsFor(cclass, false, Collections.singletonList(ClassCostRepetitionType.DISCOUNT), false, type);

	}

	/**
	 * @return the projected income the class will give based on budgetedPlaces field
	 */
	private static Money getClassFeeIncomeExTax(ICourseClass cclass, String type) {
		switch (type) {
			case BUDGETED:
				if (cclass.getBudgetedPlaces() == null) {
					return Money.ZERO;
				}
				return cclass.getFeeExGst().multiply(cclass.getBudgetedPlaces());

			case ACTUAL:
				List<? extends IEnrolment> enrolments = cclass.getSuccessAndQueuedEnrolments();
				if (enrolments != null) {
					return enrolments.stream().map(e -> e.getOriginalInvoiceLine().getPriceTotalExTax()).reduce(Money.ZERO, Money::add);
				} else {
					return Money.ZERO;
				}

			default:
				if (cclass.getMaximumPlaces() == null) {
					return Money.ZERO;
				}
				return cclass.getFeeExGst().multiply(cclass.getMaximumPlaces());
		}
	}

	public static Money getClassProfitExTax(ICourseClass cclass, String type) {
		Money income = getClassIncomeExTax(cclass, type);
		Money costs = getClassCostsExTax(cclass, type);

		return income.subtract(costs);
	}

	private static Money getClassOtherIncomeExTax(ICourseClass cclass, String type) {
		Money result = Money.ZERO;
		if (cclass.getCosts() == null || cclass.getCosts().size() == 0) {
			return result;
		}
		if (BUDGETED.equals(type) && cclass.getBudgetedPlaces() == null) {
			return Money.ZERO;
		}

		result = calculateCostsFor(cclass, true, Arrays.asList(ClassCostRepetitionType.values()), false, type);
		return result;
	}

	private static Money getClassSunkCostsExTax(ICourseClass cclass, String type) {
		Money result = Money.ZERO;
		if (cclass.getCosts() == null || cclass.getCosts().size() == 0) {
			return result;
		}

		if (BUDGETED.equals(type) && cclass.getBudgetedPlaces() == null) {
			return result;
		}

		return calculateCostsFor(
				cclass,
				false,
				Arrays.asList(ClassCostRepetitionType.FIXED,
                        ClassCostRepetitionType.PER_ENROLMENT,
                        ClassCostRepetitionType.PER_SESSION,
                        ClassCostRepetitionType.PER_UNIT,
                        ClassCostRepetitionType.PER_TIMETABLED_HOUR,
                        ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR), true, type);

	}

	/**
	 * @return the projected cost of the class based on budgetedPlaces field
	 */
	public static Money getClassCostsExTax(ICourseClass cclass, String type) {
		Money result = Money.ZERO;

		if (cclass.getCosts() == null || cclass.getCosts().size() == 0) {
			return result;
		}
		if (BUDGETED.equals(type) && cclass.getBudgetedPlaces() == null) {
			return result;
		}

		return getClassSunkCostsExTax(cclass, type).add(getClassRunningCostsExTax(cclass, type));
	}

	public static Money getClassRunningCostsExTax(ICourseClass cclass, String type) {
		Money result = Money.ZERO;
		if (cclass.getCosts() == null || cclass.getCosts().size() == 0) {
			return result;
		}
		if (BUDGETED.equals(type) && cclass.getBudgetedPlaces() == null) {
			return result;
		}

		return calculateCostsFor(
				cclass,
				false,
				Arrays.asList(ClassCostRepetitionType.FIXED,
                        ClassCostRepetitionType.PER_ENROLMENT,
                        ClassCostRepetitionType.PER_SESSION,
                        ClassCostRepetitionType.PER_UNIT,
                        ClassCostRepetitionType.PER_TIMETABLED_HOUR,
                        ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR), false, type);

	}

	/**
	 * Calculates sum of class costs of the class based on the actual enrolments for the given selection of ClassCostTypes
	 *
	 * @param funding
	 * @param costsTypes
	 * @param flag - for expense and wages it is sunk?, for income is it invoiceToSstudent?
	 * @param type - whether to calculate ACTUAL, BUDGETED or MAXIMUM
	 * @return
	 */
	private static Money calculateCostsFor(ICourseClass cclass, boolean funding, List<ClassCostRepetitionType> costsTypes, boolean flag, String type) {
		Money result = Money.ZERO;
		if (cclass.getCosts() == null || cclass.getCosts().size() == 0) {
			return result;
		}
		if (BUDGETED.equals(type) && cclass.getBudgetedPlaces() == null) {
			return result;
		}

		for (ClassCost cost : cclass.getCosts()) {
			if (cost.getPersistenceState() == PersistenceState.DELETED) {
				continue;
			}

			if (funding && cost.getInvoiceToStudent() != null && cost.getInvoiceToStudent() == flag) {
				if (ClassCostFlowType.INCOME.equals(cost.getFlowType())) {
					if (costsTypes.contains(cost.getRepetitionType())) {
						result = result.add(ClassCostUtil.getCost(cost, null, null, type));
					}
				}

			} else if (!funding && cost.getIsSunk() != null && cost.getIsSunk() == flag) {
				if (!ClassCostFlowType.INCOME.equals(cost.getFlowType())) {
					if (costsTypes.contains(cost.getRepetitionType())) {
						result = result.add(ClassCostUtil.getCost(cost, null, null, type));
					}
				}

			}

		}
		return result;
	}

	private static Money getClassCustomInvoicesExTax(ICourseClass cclass) {
		Money result = Money.ZERO;

		List<InvoiceLine> invoiceLines = cclass.getInvoiceLines();
		if (invoiceLines != null && invoiceLines.size() != 0) {
			for (InvoiceLine invoiceLine : invoiceLines) {
				result = result.add(invoiceLine.getPriceTotalExTax());
			}
		}
		return result;
	}
}
