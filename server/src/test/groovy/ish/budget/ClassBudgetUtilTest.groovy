/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.budget

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.CourseClass
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

@CompileStatic
class ClassBudgetUtilTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @BeforeEach
    void setup() throws Exception {
		wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)
        PreferenceController pref = injector.getInstance(PreferenceController.class)

        InputStream st = ClassBudgetUtilTest.class.getClassLoader().getResourceAsStream("ish/budget/classBudgetTest.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        executeDatabaseOperation(dataSet)
    }

	@Test
    void testGetClassCostExTax() {

		// class has one tutor with wage $50 per timetable hour,
		// class has 3 sessions 1 hour each

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cc = SelectById.query(CourseClass.class, 220).selectOne(context)

        Money actualCost = ClassBudgetUtil.getClassCostsExTax(cc, ClassBudgetUtil.ACTUAL)
        assertEquals(new Money("150.00"), actualCost)

        Money budgetedCost = ClassBudgetUtil.getClassCostsExTax(cc, ClassBudgetUtil.BUDGETED)
        assertEquals(new Money("150.00"), budgetedCost)

        Money maximumCost = ClassBudgetUtil.getClassCostsExTax(cc, ClassBudgetUtil.MAXIMUM)
        assertEquals(new Money("150.00"), maximumCost)
    }

	@Test
    void testGetClassIncomeExTax() {

		// class enrolment fee is $100.00
		// class has 3 discounts:
		// 1. Percent discount 10%, predicted usage 50%
		// 2. Dollar discount for $20.00, predicted usage 50%
		// 3. Fee discount $50.00 fixed fee, predicted usage 10%
		// class has 3 active enrolments using each discount listed
		// class enrolments: minimum 0, maximum 10, projected 5

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cc = SelectById.query(CourseClass.class, 220).selectOne(context)

        Money actualIncome = ClassBudgetUtil.getClassIncomeExTax(cc, ClassBudgetUtil.ACTUAL)
        assertEquals(new Money("720.00"), actualIncome)

        Money budgetedIncome = ClassBudgetUtil.getClassIncomeExTax(cc, ClassBudgetUtil.BUDGETED)
        assertEquals(new Money("600.00"), budgetedIncome)

        Money maximumIncome = ClassBudgetUtil.getClassIncomeExTax(cc, ClassBudgetUtil.MAXIMUM)
        assertEquals(new Money("1000.00"), maximumIncome)
    }

	@Test
    void testGetClassRunningCostsExTax() {

		// class has one tutor with wage $50 per timetable hour,
		// class has 3 sessions 1 hour each

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cc = SelectById.query(CourseClass.class, 220).selectOne(context)

        Money actualRunningCost = ClassBudgetUtil.getClassRunningCostsExTax(cc, ClassBudgetUtil.ACTUAL)
        assertEquals(new Money("150.00"), actualRunningCost)

        Money budgetedRunningCost = ClassBudgetUtil.getClassRunningCostsExTax(cc, ClassBudgetUtil.BUDGETED)
        assertEquals(new Money("150.00"), budgetedRunningCost)

        Money maximumRunningCost = ClassBudgetUtil.getClassRunningCostsExTax(cc, ClassBudgetUtil.MAXIMUM)
        assertEquals(new Money("150.00"), maximumRunningCost)
    }

	@Test
    void testGetClassProfitExTax() {

		// class enrolment fee is $100.00
		// class has 3 discounts:
		// 1. Percent discount 10%, predicted usage 50%
		// 2. Dollar discount for $20.00, predicted usage 50%
		// 3. Fee discount $50.00 fixed fee, predicted usage 10%
		// class has 3 active enrolments using each discount listed
		// class enrolments: minimum 0, maximum 10, projected 5

		ObjectContext context = injector.getInstance(ICayenneService.class).getNewContext()

        CourseClass cc = SelectById.query(CourseClass.class, 220).selectOne(context)

        Money actualProfit = ClassBudgetUtil.getClassProfitExTax(cc, ClassBudgetUtil.ACTUAL)
        assertEquals(new Money("570.00"), actualProfit)

        Money budgetedProfit = ClassBudgetUtil.getClassProfitExTax(cc, ClassBudgetUtil.BUDGETED)
        assertEquals(new Money("450.00"), budgetedProfit)

        Money maximumProfit = ClassBudgetUtil.getClassProfitExTax(cc, ClassBudgetUtil.MAXIMUM)
        assertEquals(new Money("850.00"), maximumProfit)
    }
}
