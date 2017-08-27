/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap;

import ish.oncourse.model.*;

import java.util.Calendar;

/**
 * User: akoiro
 * Date: 26/8/17
 */
public interface TestConstants {
	String ENROLMENT_IDENTIFIER = Enrolment.class.getSimpleName();
	String INVOICE_LINE_IDENTIFIER = InvoiceLine.class.getSimpleName();
	String PAYMENT_LINE_IDENTIFIER = PaymentInLine.class.getSimpleName();
	String INVOICE_IDENTIFIER = Invoice.class.getSimpleName();
	String PAYMENT_IDENTIFIER = PaymentIn.class.getSimpleName();
	String CONTACT_IDENTIFIER = Contact.class.getSimpleName();
	String STUDENT_IDENTIFIER = Student.class.getSimpleName();
	String MEMBERSHIP_IDENTIFIER = Membership.class.getSimpleName();
	String VOUCHER_IDENTIFIER = Voucher.class.getSimpleName();
	String ARTICLE_IDENTIFIER = Article.class.getSimpleName();
	String VOUCHER_PAYMENT_IN_IDENTIFIER = VoucherPaymentIn.class.getSimpleName();


	String DEFAULT_COLLEGE_KEY = "345ttn44$%9";
	String CARD_HOLDER_NAME = "john smith";
	String VALID_CARD_NUMBER = "5431111111111111";
	String DECLINED_CARD_NUMBER = "9999990000000378";
	String CREDIT_CARD_CVV = "1111";
	String VALID_EXPIRY_MONTH = String.format("%d", Calendar.getInstance().get(Calendar.MONTH) + 1);
	String VALID_EXPIRY_YEAR = String.format("%d", Calendar.getInstance().get(Calendar.YEAR));

	String ID_ATTRIBUTE = "id";
}
