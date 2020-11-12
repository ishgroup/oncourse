/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.print

import static ish.print.PrintTransformationsFactory.getPrintTransformationFor
import ish.print.transformations.AccountAccountTransactionTransformation
import ish.print.transformations.AccountTransactionTransformation
import ish.print.transformations.BankingPaymentInterfaceTransformation
import ish.print.transformations.ContactAccountTransactionTransformation
import ish.print.transformations.ContactAmountOwingSqlTransformation
import ish.print.transformations.CourseClassEnrolmentTransformation
import ish.print.transformations.CourseClassOutcomeTransformation
import ish.print.transformations.CourseClassSessionTransformation
import ish.print.transformations.CourseClassTutorAttendanceTransformation
import ish.print.transformations.DiscountDiscountTransformation
import ish.print.transformations.PaymentInterfaceTransformation
import ish.print.transformations.PayslipPayLineTransformation
import ish.print.transformations.RoomSessionTransformation
import ish.print.transformations.SiteSessionTransformation
import ish.print.transformations.StatementLineReportTransformation
import ish.print.transformations.TagContactTransformation
import static org.apache.commons.lang3.StringUtils.EMPTY
import static org.junit.Assert.assertEquals
import org.junit.Test

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
		assertEquals(ContactAmountOwingSqlTransformation.class, getPrintTransformationFor("Contact", "ContactDataRowDelegator", ContactAmountOwingSqlTransformation.REPORT_CODE).getClass())

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

