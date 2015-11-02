/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.Enrolment;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class DiscountConditionsTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		super.setup("ish/oncourse/enrol/checkout/DiscountConditionsTest.xml");
	}

	@Test
	public void testTheSameCourseClass() {
		purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001l);

		assertEquals(1, purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(Money.ZERO, purchaseController.getTotalDiscountAmountIncTax());
		
		addContact(1002l);

		assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(new Money("22.00"), purchaseController.getTotalDiscountAmountIncTax());
		
		for (Enrolment enrolment : purchaseController.getModel().getAllEnabledEnrolments()) {
			assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
			assertEquals(new Money("10.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
			assertEquals("name_1", enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
		}

		addContact(1003l);

		assertEquals(3, purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(new Money("66.00"), purchaseController.getTotalDiscountAmountIncTax());

		for (Enrolment enrolment : purchaseController.getModel().getAllEnabledEnrolments()) {
			assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
			assertEquals(new Money("20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
			assertEquals("name_2", enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
		}
		
		proceedToPayment();

		assertEquals(new Money("66.00"), purchaseController.getTotalDiscountAmountIncTax());
	}

	@Test
	public void testDifferentCourseClasses() {
		purchaseController = init(Arrays.asList(1001L, 1002L, 1003L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001l);

		assertEquals(3, purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(new Money("22.00"), purchaseController.getTotalDiscountAmountIncTax());

		for (Enrolment enrolment : purchaseController.getModel().getAllEnabledEnrolments()) {
			if (enrolment.getCourseClass().getId().equals(1001L)) {
				assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
				assertEquals(new Money("20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
				assertEquals("name_2", enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
			} else {
				assertEquals(0, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
				assertEquals(Money.ZERO, enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
			}
		}
	}

	@Test
	public void testAddNegativeCorporatePassDiscount() {
		purchaseController = init(Arrays.asList(1001L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001l);
		addContact(1002l);
		addContact(1003l);
		
		assertEquals(new Money("66.00"), purchaseController.getTotalDiscountAmountIncTax());

		proceedToPayment();
		
		selectCorporatePassEditor();
		addCorporatePass("password1");

		for (Enrolment enrolment : purchaseController.getModel().getAllEnabledEnrolments()) {
			assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
			assertEquals(new Money("-20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
			assertEquals("negative discount", enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
		}
		assertEquals(new Money("-66.00"), purchaseController.getTotalDiscountAmountIncTax());
	}
}
