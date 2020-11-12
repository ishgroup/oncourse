/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.budget

import ish.CayenneIshTestCase
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.replication.builders.IAngelStubBuilder
import ish.oncourse.server.replication.handler.OutboundReplicationHandlerTest
import ish.oncourse.server.replication.services.IAngelQueueService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ClassBudgetUtilTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @Before
    void setup() throws Exception {
		wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)
        IAngelStubBuilder stubBuilder = injector.getInstance(IAngelStubBuilder.class)
        IAngelQueueService queueService = injector.getInstance(IAngelQueueService.class)
        PreferenceController pref = injector.getInstance(PreferenceController.class)

        InputStream st = OutboundReplicationHandlerTest.class.getClassLoader().getResourceAsStream("ish/budget/classBudgetTest.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)
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
