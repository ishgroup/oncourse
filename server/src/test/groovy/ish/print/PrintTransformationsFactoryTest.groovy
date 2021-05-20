/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.print

import groovy.transform.CompileStatic
import ish.print.transformations.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static ish.print.PrintTransformationsFactory.getPrintTransformationFor
import static org.apache.commons.lang3.StringUtils.EMPTY

@CompileStatic
class PrintTransformationsFactoryTest {
    @Test
    void testTransformationsMap() {

        //Account
        Assertions.assertEquals(AccountAccountTransactionTransformation.class, getPrintTransformationFor("Account", "AccountTransaction", EMPTY).getClass())

        //AccountTransaction
        Assertions.assertEquals(AccountTransactionTransformation.class, getPrintTransformationFor("AccountTransaction", "AccountTransaction", EMPTY).getClass())

        //Contact
        Assertions.assertEquals(ContactAccountTransactionTransformation.class, getPrintTransformationFor("Contact", "AccountTransaction", EMPTY).getClass())
        Assertions.assertEquals(StatementLineReportTransformation.class, getPrintTransformationFor("Contact", "Contact", StatementLineReportTransformation.STATEMENT_REPORT_CODE).getClass())

        //CourseClass
        Assertions.assertEquals(CourseClassEnrolmentTransformation.class, getPrintTransformationFor("CourseClass", "Enrolment", EMPTY).getClass())
        Assertions.assertEquals(CourseClassOutcomeTransformation.class, getPrintTransformationFor("CourseClass", "Outcome", EMPTY).getClass())
        Assertions.assertEquals(CourseClassSessionTransformation.class, getPrintTransformationFor("CourseClass", "Session", EMPTY).getClass())
        Assertions.assertEquals(CourseClassTutorAttendanceTransformation.class, getPrintTransformationFor("CourseClass", "TutorAttendance", EMPTY).getClass())

        //Discount
        Assertions.assertEquals(DiscountDiscountTransformation.class, getPrintTransformationFor("Discount", "Discount", DiscountDiscountTransformation.REPORT_CODES[0]).getClass())
        Assertions.assertEquals(DiscountDiscountTransformation.class, getPrintTransformationFor("Discount", "Discount", DiscountDiscountTransformation.REPORT_CODES[1]).getClass())

        //PaymentIn
        Assertions.assertEquals(PaymentInterfaceTransformation.class, getPrintTransformationFor("PaymentIn", "PaymentInterface", EMPTY).getClass())

        //PaymentOut
        Assertions.assertEquals(PaymentInterfaceTransformation.class, getPrintTransformationFor("PaymentOut", "PaymentInterface", EMPTY).getClass())

        //Payslip
        Assertions.assertEquals(PayslipPayLineTransformation.class, getPrintTransformationFor("Payslip", "PayLine", EMPTY).getClass())

        //Tag
        Assertions.assertEquals(TagContactTransformation.class, getPrintTransformationFor("Tag", "Contact", EMPTY).getClass())

        //Site
        Assertions.assertEquals(SiteSessionTransformation.class, getPrintTransformationFor("Site", "Session", EMPTY).getClass())


        //Room
        Assertions.assertEquals(RoomSessionTransformation.class, getPrintTransformationFor("Room", "Session", EMPTY).getClass())

        //Banking
        Assertions.assertEquals(BankingPaymentInterfaceTransformation.class, getPrintTransformationFor("Banking", "PaymentInterface", EMPTY).getClass())
    }
}

