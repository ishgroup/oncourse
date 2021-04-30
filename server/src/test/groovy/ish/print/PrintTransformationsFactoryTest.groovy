/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.print

import ish.print.transformations.*
import org.junit.jupiter.api.Test

import static ish.print.PrintTransformationsFactory.getPrintTransformationFor
import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.junit.Assert.assertEquals

class PrintTransformationsFactoryTest {
	@Test
	void testTransformationsMap() {

		//Account
		assertEquals(AccountAccountTransactionTransformation.class, getPrintTransformationFor("Account", "AccountTransaction", EMPTY).getClass())

		//AccountTransaction
		assertEquals(AccountTransactionTransformation.class, getPrintTransformationFor("AccountTransaction", "AccountTransaction", EMPTY).getClass())

		//Contact
		assertEquals(ContactAccountTransactionTransformation.class, getPrintTransformationFor("Contact", "AccountTransaction", EMPTY).getClass())
		assertEquals(StatementLineReportTransformation.class, getPrintTransformationFor("Contact", "Contact", StatementLineReportTransformation.STATEMENT_REPORT_CODE).getClass())

		//CourseClass
		assertEquals(CourseClassEnrolmentTransformation.class, getPrintTransformationFor("CourseClass", "Enrolment", EMPTY).getClass())
		assertEquals(CourseClassOutcomeTransformation.class, getPrintTransformationFor("CourseClass", "Outcome", EMPTY).getClass())
		assertEquals(CourseClassSessionTransformation.class, getPrintTransformationFor("CourseClass", "Session", EMPTY).getClass())
		assertEquals(CourseClassTutorAttendanceTransformation.class, getPrintTransformationFor("CourseClass", "TutorAttendance", EMPTY).getClass())

		//Discount
		assertEquals(DiscountDiscountTransformation.class, getPrintTransformationFor("Discount", "Discount", DiscountDiscountTransformation.REPORT_CODES[0]).getClass())
		assertEquals(DiscountDiscountTransformation.class, getPrintTransformationFor("Discount", "Discount", DiscountDiscountTransformation.REPORT_CODES[1]).getClass())

		//PaymentIn
		assertEquals(PaymentInterfaceTransformation.class, getPrintTransformationFor("PaymentIn", "PaymentInterface", EMPTY).getClass())

		//PaymentOut
		assertEquals(PaymentInterfaceTransformation.class, getPrintTransformationFor("PaymentOut", "PaymentInterface", EMPTY).getClass())

		//Payslip
		assertEquals(PayslipPayLineTransformation.class, getPrintTransformationFor("Payslip", "PayLine", EMPTY).getClass())

		//Tag
		assertEquals(TagContactTransformation.class, getPrintTransformationFor("Tag", "Contact", EMPTY).getClass())

		//Site
		assertEquals(SiteSessionTransformation.class, getPrintTransformationFor("Site", "Session", EMPTY).getClass())


		//Room
		assertEquals(RoomSessionTransformation.class, getPrintTransformationFor("Room", "Session", EMPTY).getClass())

		//Banking
		assertEquals(BankingPaymentInterfaceTransformation.class, getPrintTransformationFor("Banking", "PaymentInterface", EMPTY).getClass())
	}
}

