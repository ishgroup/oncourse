package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;

import java.util.List;

import static org.junit.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class AssertEnabledEnrolments {
	private Contact contact;
	private int count;
	private PurchaseController purchaseController;

	public void assertValue(){
		List<Enrolment> enrolments = purchaseController.getModel().getAllEnrolments(contact);
		assertEquals(count, enrolments.size());
		for (Enrolment enrolment : enrolments) {
			AssertEnabledEnrolment.valueOf(enrolment).assertValue();
		}
	}

	public static AssertEnabledEnrolments valueOf(Contact contact, int count,PurchaseController purchaseController) {
		AssertEnabledEnrolments result = new AssertEnabledEnrolments();
		result.contact = contact;
		result.count = count;
		result.purchaseController = purchaseController;
		return result;
	}
}
