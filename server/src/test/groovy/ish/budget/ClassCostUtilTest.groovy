/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.budget

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.DiscountCourseClass
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 */
@CompileStatic
class ClassCostUtilTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @Before
    void setup() throws Exception {
		wipeTables()

        this.cayenneService = injector.getInstance(ICayenneService.class)
        PreferenceController pref = injector.getInstance(PreferenceController.class)

        InputStream st = ClassCostUtilTest.class.getClassLoader().getResourceAsStream("ish/budget/classBudgetTest.xml")
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
    void testApplyOnCostRateAndMinMaxCost() {
		Money cost = new Money("100.00")
        Money minimumCost = null
        Money maximumCost = null
        BigDecimal onCostRate = null

        // test simple calculations:
		Money result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(cost, result)

        // test minimum cost calculations
		minimumCost = new Money("200.00")
        result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(minimumCost, result)

        minimumCost = new Money("50.00")
        result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(cost, result)

        // test maximim cost calculations
		maximumCost = new Money("200.00")
        result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(cost, result)

        maximumCost = new Money("75.00")
        result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(maximumCost, result)

        // test onCostRate calculations
		onCostRate = new BigDecimal("0.1")
        maximumCost = null
        minimumCost = null

        result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(new Money("110.00"), result)

        // test onCostRate with maximum cost
		maximumCost = new Money("100.00")
        result = ClassCostUtil.applyOnCostRateAndMinMaxCost(cost, minimumCost, maximumCost, onCostRate)
        assertEquals(maximumCost, result)
    }

	@Test
    void testGetCost() {
		Money cost = new Money("100.00")
        Money minimumCost = null
        Money maximumCost = null
        BigDecimal onCostRate = null
        BigDecimal unitCount = BigDecimal.ONE

        // test simple calculations:
		Money result = ClassCostUtil.getCost(cost, unitCount, minimumCost, maximumCost, onCostRate)
        assertEquals(cost, result)

        unitCount = new BigDecimal("2")
        result = ClassCostUtil.getCost(cost, unitCount, minimumCost, maximumCost, onCostRate)
        assertEquals(new Money("200.00"), result)

        onCostRate = new BigDecimal("0.1")
        result = ClassCostUtil.getCost(cost, unitCount, minimumCost, maximumCost, onCostRate)
        assertEquals(new Money("220.00"), result)

        maximumCost = new Money("200.00")
        result = ClassCostUtil.getCost(cost, unitCount, minimumCost, maximumCost, onCostRate)
        assertEquals(maximumCost, result)

        minimumCost = new Money("300.00")
        result = ClassCostUtil.getCost(cost, unitCount, minimumCost, maximumCost, onCostRate)
        assertEquals(minimumCost, result)

    }

	@Test
    void testResetUnitCount() {

		ObjectContext context = cayenneService.getNewContext()

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setUnitCount(new BigDecimal("3.3"))

        cc.setRepetitionType(ClassCostRepetitionType.FIXED)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setRepetitionType(ClassCostRepetitionType.PER_SESSION)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setRepetitionType(ClassCostRepetitionType.PER_ENROLMENT)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setRepetitionType(ClassCostRepetitionType.PER_UNIT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setUnitCount(new BigDecimal("3.3"))
        assertEquals(new BigDecimal("3.3"), cc.getUnitCount())

        cc.setRepetitionType(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

    }

	@Test
    void testGetBudgetedCost_FIXED() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)

        cc.setCourseClass(cl)
        cc.setRepetitionType(ClassCostRepetitionType.FIXED)
        ClassCostUtil.resetUnitCount(cc)
        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("20.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("20.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("20.00"), result)
    }

	@Test
    void testGetBudgetedCost_PER_SESSION() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setRepetitionType(ClassCostRepetitionType.PER_SESSION)
        ClassCostUtil.resetUnitCount(cc)

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetBudgetedCost_PER_ENROLMENT() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setRepetitionType(ClassCostRepetitionType.PER_ENROLMENT)
        ClassCostUtil.resetUnitCount(cc)

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getBudgetedCost(cc, null, 10)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getBudgetedCost(cc, null, 10)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getBudgetedCost(cc, null, 10)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetBudgetedCost_PER_HOUR() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setRepetitionType(ClassCostRepetitionType.PER_UNIT)
        ClassCostUtil.resetUnitCount(cc)
        cc.setUnitCount(new BigDecimal("10"))

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetBudgetedCost_DISCOUNT() {
		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ClassCostUtil.resetUnitCount(cc)
        cc.setUnitCount(new BigDecimal("10"))

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        // all total costs equal $0, because the total cost depends on the class projected enrolments.
		// and the min(unitCount,projectedEnrolments) is used to calculate the budgeted value.
		// TODO write the tests for discosunts
	}

	@Test
    void testGetBudgetedCost_PER_TIMETABLED_HOUR() {
		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.PER_TIMETABLED_HOUR)
        ClassCostUtil.resetUnitCount(cc)

        // cc.setSessionPayableHours(new BigDecimal("10.2"));

		cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getBudgetedCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetBudgetedCost_PER_STUDENT_CONTACT_HOUR() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        ClassCostUtil.resetUnitCount(cc)

        // cc.setSessionPayableHours(new BigDecimal("10.2"));

		cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getBudgetedCost(cc, null, 10)
        assertEquals(new Money("2000.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getBudgetedCost(cc, null, 10)
        assertEquals(new Money("2000.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getBudgetedCost(cc, null, 10)
        assertEquals(new Money("2000.00"), result)
    }

	@Test
    void testGetActualCost_FIXED() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.FIXED)
        ClassCostUtil.resetUnitCount(cc)
        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("20.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("20.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("20.00"), result)
    }

	@Test
    void testGetActualCost_PER_SESSION() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.PER_SESSION)
        ClassCostUtil.resetUnitCount(cc)

        cc.setPerUnitAmountExTax(new Money("20.00"))
        // cc.setSessionCount(10);
		cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetActualCost_PER_ENROLMENT() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setInvoiceToStudent(Boolean.FALSE)
        cc.setRepetitionType(ClassCostRepetitionType.PER_ENROLMENT)
        ClassCostUtil.resetUnitCount(cc)

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setInvoiceToStudent(Boolean.TRUE)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("500.00"), result)

        cc.setInvoiceToStudent(Boolean.FALSE)
        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetActualCost_PER_HOUR() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.PER_UNIT)
        ClassCostUtil.resetUnitCount(cc)
        cc.setUnitCount(new BigDecimal("10"))

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetActualCost_DISCOUNT() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ClassCostUtil.resetUnitCount(cc)
        cc.setUnitCount(new BigDecimal("10"))

        cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        // this cannot be tested at the moment
		if (true) {
			return
        }
		Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetActualCost_PER_TIMETABLED_HOUR() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.PER_TIMETABLED_HOUR)
        ClassCostUtil.resetUnitCount(cc)

        // cc.setSessionPayableHours(new BigDecimal("10.2"));

		cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("200.00"), result)
    }

	@Test
    void testGetActualCost_PER_STUDENT_CONTACT_HOUR() {

		ObjectContext context = cayenneService.getNewContext()

        CourseClass cl = SelectById.query(CourseClass.class, 200).selectOne(context)
        Contact c = SelectById.query(Contact.class, 2).selectOne(context)

        ClassCost cc = context.newObject(ClassCost.class)
        cc.setCourseClass(cl)
        cc.setContact(c)
        cc.setRepetitionType(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        ClassCostUtil.resetUnitCount(cc)

        // ccsetSessionPayableHours(new BigDecimal("10.2"));

		cc.setPerUnitAmountExTax(new Money("20.00"))
        cc.setFlowType(ClassCostFlowType.EXPENSE)

        Money result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("2000.00"), result)

        cc.setFlowType(ClassCostFlowType.INCOME)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("2000.00"), result)

        cc.setFlowType(ClassCostFlowType.WAGES)
        result = ClassCostUtil.getActualCost(cc)
        assertEquals(new Money("2000.00"), result)
    }

	@Test
    void testUnitCount() {

		ObjectContext context = cayenneService.getNewContext()

        ClassCost cc = context.newObject(ClassCost.class)

        // test if starting from null count
		cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.FIXED)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.PER_ENROLMENT)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.PER_SESSION)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.PER_TIMETABLED_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setUnitCount(null)
        cc.setRepetitionType(ClassCostRepetitionType.PER_UNIT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        // test if starting from count of 1
		cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.FIXED)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.PER_ENROLMENT)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.PER_SESSION)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.PER_TIMETABLED_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setUnitCount(BigDecimal.ONE)
        cc.setRepetitionType(ClassCostRepetitionType.PER_UNIT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        // test if starting from count of 10
		cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.FIXED)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.ONE, cc.getUnitCount())

        cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.PER_ENROLMENT)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.PER_SESSION)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.PER_STUDENT_CONTACT_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.PER_TIMETABLED_HOUR)
        ClassCostUtil.resetUnitCount(cc)
        assertNull(cc.getUnitCount())

        cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.TEN, cc.getUnitCount())

        cc.setUnitCount(BigDecimal.TEN)
        cc.setRepetitionType(ClassCostRepetitionType.PER_UNIT)
        ClassCostUtil.resetUnitCount(cc)
        assertEquals(BigDecimal.TEN, cc.getUnitCount())

    }

	@Test
    void testActualCostDiscount() {
		ObjectContext context = cayenneService.getNewContext()

        DiscountCourseClass percDiscountCC = SelectById.query(DiscountCourseClass.class, 201).selectOne(context)
        DiscountCourseClass dollarDiscountCC = SelectById.query(DiscountCourseClass.class, 203).selectOne(context)
        DiscountCourseClass feeDiscountCC = SelectById.query(DiscountCourseClass.class, 205).selectOne(context)

        CourseClass courseClass = SelectById.query(CourseClass.class, 220).selectOne(context)

        ClassCost ccPerc = context.newObject(ClassCost.class)
        ccPerc.setCourseClass(courseClass)
        ccPerc.setDiscountCourseClass(percDiscountCC)
        ccPerc.setInvoiceToStudent(false)
        ccPerc.setFlowType(ClassCostFlowType.DISCOUNT)
        ccPerc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccPerc.setIsSunk(false)
        ccPerc.setPayableOnEnrolment(true)
        ccPerc.setTaxAdjustment(Money.ZERO)

        // calculating actual amount for 10% discount, class price excluding tax $100.00,
		// 1 active enrolment with this discount applied
		Money actualCostPerc = ClassCostUtil.getActualCost(ccPerc)
        assertEquals(new Money("10.00"), actualCostPerc)

        percDiscountCC.getDiscount().setDiscountPercent(new BigDecimal('20.0'))

        ClassCost ccPercChanged = context.newObject(ClassCost.class)
        ccPercChanged.setCourseClass(courseClass)
        ccPercChanged.setDiscountCourseClass(percDiscountCC)
        ccPercChanged.setInvoiceToStudent(false)
        ccPercChanged.setFlowType(ClassCostFlowType.DISCOUNT)
        ccPercChanged.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccPercChanged.setIsSunk(false)
        ccPercChanged.setPayableOnEnrolment(true)
        ccPercChanged.setTaxAdjustment(Money.ZERO)

        // calculating actual amount for 10% discount which had been changed to 20% after it was applied,
		// class price excluding tax $100.00, should receive same value as for 10% discount applied originally
		// 1 active enrolment with this discount applied
		Money actualCostPercChanged = ClassCostUtil.getActualCost(ccPercChanged)
        assertEquals(new Money("10.00"), actualCostPercChanged)

        ClassCost ccDollar = context.newObject(ClassCost.class)
        ccDollar.setCourseClass(courseClass)
        ccDollar.setDiscountCourseClass(dollarDiscountCC)
        ccDollar.setInvoiceToStudent(false)
        ccDollar.setFlowType(ClassCostFlowType.DISCOUNT)
        ccDollar.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccDollar.setIsSunk(false)
        ccDollar.setPayableOnEnrolment(true)
        ccDollar.setTaxAdjustment(Money.ZERO)

        // calculating actual amount for $20.00 discount, class price excluding tax $100.00,
		// 1 active enrolment with this discount applied
		Money actualCostDollar = ClassCostUtil.getActualCost(ccDollar)
        assertEquals(new Money("20.00"), actualCostDollar)

        ClassCost ccFee = context.newObject(ClassCost.class)
        ccFee.setCourseClass(courseClass)
        ccFee.setDiscountCourseClass(feeDiscountCC)
        ccFee.setInvoiceToStudent(false)
        ccFee.setFlowType(ClassCostFlowType.DISCOUNT)
        ccFee.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccFee.setIsSunk(false)
        ccFee.setPayableOnEnrolment(true)
        ccFee.setTaxAdjustment(Money.ZERO)

        // calculating actual amount for $50.00 fixed price discount, class price excluding tax $100.00,
		// 1 active enrolment with this discount applied
		Money actualCostFee = ClassCostUtil.getActualCost(ccFee)
        assertEquals(new Money("50.00"), actualCostFee)
    }

	@Test
    void testBudgetedCostDiscount() {
		ObjectContext context = cayenneService.getNewContext()

        DiscountCourseClass percDiscountCC = SelectById.query(DiscountCourseClass.class, 201).selectOne(context)
        DiscountCourseClass dollarDiscountCC = SelectById.query(DiscountCourseClass.class, 203).selectOne(context)
        DiscountCourseClass feeDiscountCC = SelectById.query(DiscountCourseClass.class, 205).selectOne(context)

        CourseClass courseClass = SelectById.query(CourseClass.class, 220).selectOne(context)

        ClassCost ccPerc = context.newObject(ClassCost.class)
        ccPerc.setCourseClass(courseClass)
        ccPerc.setDiscountCourseClass(percDiscountCC)
        ccPerc.setInvoiceToStudent(false)
        ccPerc.setFlowType(ClassCostFlowType.DISCOUNT)
        ccPerc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccPerc.setIsSunk(false)
        ccPerc.setPayableOnEnrolment(true)
        ccPerc.setTaxAdjustment(Money.ZERO)

        // calculating budgeted amount for 10% discount, class price excluding tax $100.00,
		// projected enrolments count 5, predicted discount usage 50%
		Money budgetedCostPerc = ClassCostUtil.getBudgetedCost(ccPerc)
        assertEquals(new Money("25.00"), budgetedCostPerc)

        ClassCost ccDollar = context.newObject(ClassCost.class)
        ccDollar.setCourseClass(courseClass)
        ccDollar.setDiscountCourseClass(dollarDiscountCC)
        ccDollar.setInvoiceToStudent(false)
        ccDollar.setFlowType(ClassCostFlowType.DISCOUNT)
        ccDollar.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccDollar.setIsSunk(false)
        ccDollar.setPayableOnEnrolment(true)
        ccDollar.setTaxAdjustment(Money.ZERO)

        // calculating budgeted amount for $20.00 discount, class price excluding tax $100.00,
		// projected enrolments count 5, predicted discount usage 50%
		Money budgetedCostDollar = ClassCostUtil.getBudgetedCost(ccDollar)
        assertEquals(new Money("50.00"), budgetedCostDollar)

        ClassCost ccFee = context.newObject(ClassCost.class)
        ccFee.setCourseClass(courseClass)
        ccFee.setDiscountCourseClass(feeDiscountCC)
        ccFee.setInvoiceToStudent(false)
        ccFee.setFlowType(ClassCostFlowType.DISCOUNT)
        ccFee.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccFee.setIsSunk(false)
        ccFee.setPayableOnEnrolment(true)
        ccFee.setTaxAdjustment(Money.ZERO)

        // calculating budgeted amount for $50.00 fixed price discount, class price excluding tax $100.00,
		// projected enrolments count 5, predicted discount usage 10%
		Money budgetedCostFee = ClassCostUtil.getBudgetedCost(ccFee)
        assertEquals(new Money("25.00"), budgetedCostFee)
    }

	@Test
    void testMaximumCostDiscount() {
		ObjectContext context = cayenneService.getNewContext()

        DiscountCourseClass percDiscountCC = SelectById.query(DiscountCourseClass.class, 201).selectOne(context)
        DiscountCourseClass dollarDiscountCC = SelectById.query(DiscountCourseClass.class, 203).selectOne(context)
        DiscountCourseClass feeDiscountCC = SelectById.query(DiscountCourseClass.class, 205).selectOne(context)

        CourseClass courseClass = SelectById.query(CourseClass.class, 220).selectOne(context)

        ClassCost ccPerc = context.newObject(ClassCost.class)
        ccPerc.setCourseClass(courseClass)
        ccPerc.setDiscountCourseClass(percDiscountCC)
        ccPerc.setInvoiceToStudent(false)
        ccPerc.setFlowType(ClassCostFlowType.DISCOUNT)
        ccPerc.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccPerc.setIsSunk(false)
        ccPerc.setPayableOnEnrolment(true)
        ccPerc.setTaxAdjustment(Money.ZERO)

        // calculating maximum amount for 10% discount, class price excluding tax $100.00,
		// maximum enrolments count 10, predicted discount usage 50%
		Money maximumCostPerc = ClassCostUtil.getMaximumCost(ccPerc)
        assertEquals(new Money("50.00"), maximumCostPerc)

        ClassCost ccDollar = context.newObject(ClassCost.class)
        ccDollar.setCourseClass(courseClass)
        ccDollar.setDiscountCourseClass(dollarDiscountCC)
        ccDollar.setInvoiceToStudent(false)
        ccDollar.setFlowType(ClassCostFlowType.DISCOUNT)
        ccDollar.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccDollar.setIsSunk(false)
        ccDollar.setPayableOnEnrolment(true)
        ccDollar.setTaxAdjustment(Money.ZERO)

        // calculating maximum amount for $20.00 discount, class price excluding tax $100.00,
		// maximum enrolments count 10, predicted discount usage 50%
		Money maximumCostDollar = ClassCostUtil.getMaximumCost(ccDollar)
        assertEquals(new Money("100.00"), maximumCostDollar)

        ClassCost ccFee = context.newObject(ClassCost.class)
        ccFee.setCourseClass(courseClass)
        ccFee.setDiscountCourseClass(feeDiscountCC)
        ccFee.setInvoiceToStudent(false)
        ccFee.setFlowType(ClassCostFlowType.DISCOUNT)
        ccFee.setRepetitionType(ClassCostRepetitionType.DISCOUNT)
        ccFee.setIsSunk(false)
        ccFee.setPayableOnEnrolment(true)
        ccFee.setTaxAdjustment(Money.ZERO)

        // calculating maximum amount for $50.00 fixed price discount, class price excluding tax $100.00,
		// maximum enrolments count 10, predicted discount usage 10%
		Money maximumCostFee = ClassCostUtil.getMaximumCost(ccFee)
        assertEquals(new Money("50.00"), maximumCostFee)
    }
}