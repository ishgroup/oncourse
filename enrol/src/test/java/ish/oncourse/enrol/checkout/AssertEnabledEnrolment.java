package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

import static org.junit.Assert.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class AssertEnabledEnrolment {

	private Enrolment enrolment;

	void assertValue() {
		for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
			assertNotNull(invoiceLine);
		}
		assertFalse("Enrolment should be linked with at least 1 invoiceline", enrolment.getInvoiceLines().isEmpty());
		assertEquals(EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
	}

	public static AssertEnabledEnrolment valueOf(Enrolment enrolment) {
		AssertEnabledEnrolment result = new AssertEnabledEnrolment();
		result.enrolment = enrolment;
		return result;
	}
}
