/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.disableEnrolment;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.enableEnrolment;
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

		//check that no one discounts available now, until other discounts conditions are not achieved
		//discount by corporate pass available after adding of corresponded  corporate pass only
		assertEquals(1, purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(new Money("0"), purchaseController.getTotalDiscountAmountIncTax());
		
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

		//check that negative discount will be selected in the first order
		for (Enrolment enrolment : purchaseController.getModel().getAllEnabledEnrolments()) {
			assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
			assertEquals(new Money("-20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
			assertEquals("negative discount", enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
		}
		assertEquals(new Money("-66.00"), purchaseController.getTotalDiscountAmountIncTax());
	}

	@Test
	public void testOverrideDiscountAmountOnClassLevel() {
		purchaseController = init(Arrays.asList(1004L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		addFirstContact(1001l);
		addContact(1002l);
		addContact(1003l);

		assertEquals(new Money("165.00"), purchaseController.getTotalDiscountAmountIncTax());

		proceedToPayment();

		for (Enrolment enrolment : purchaseController.getModel().getAllEnabledEnrolments()) {
			assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
			assertEquals(new Money("50.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
			assertEquals("rewritten discount", enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
		}
		assertEquals(new Money("165.00"), purchaseController.getTotalDiscountAmountIncTax());
	}


	@Test
	public void testDiscountViaStartDateOffsetDateRange() {
		Date now = new Date();
		purchaseController = init(Arrays.asList(1005L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		CourseClass addedClass = purchaseController.getModel().getClasses().get(0);
		addedClass.setStartDate(now);
		addFirstContact(1001l);

		Enrolment enrolment = getModel().getAllEnabledEnrolments().get(0);
		

		assertEquals(1, enrolment.getInvoiceLines().get(0).getInvoiceLineDiscounts().size());
		assertEquals(new Money("20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
		assertEquals("Discount 3  days before and 3 days after offsets", getModel().getAllEnabledEnrolments().get(0).getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
		assertEquals(new Money("22.00"), purchaseController.getTotalDiscountAmountIncTax());
		
		Calendar expiryDate = Calendar.getInstance();
		expiryDate.setTime(now);
		expiryDate.set(Calendar.HOUR_OF_DAY, 23);
		expiryDate.set(Calendar.MINUTE, 59);
		expiryDate.set(Calendar.SECOND, 59);
		expiryDate.add(Calendar.DATE, 3);
		addedClass.setStartDate(expiryDate.getTime());

		enrolment = refreshEnrolment(enrolment);
		
		assertEquals(new Money("20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
		assertEquals("Discount 3  days before and 3 days after offsets", getModel().getAllEnabledEnrolments().get(0).getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());

		expiryDate = Calendar.getInstance();
		expiryDate.setTime(now);
		expiryDate.set(Calendar.HOUR_OF_DAY, 23);
		expiryDate.set(Calendar.MINUTE, 59);
		expiryDate.set(Calendar.SECOND, 59);
		expiryDate.add(Calendar.DATE, 4);
		addedClass.setStartDate(expiryDate.getTime());

		enrolment = refreshEnrolment(enrolment);

		assertEquals(Money.ZERO, enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
		assertEquals(0, getModel().getAllEnabledEnrolments().get(0).getInvoiceLines().get(0).getInvoiceLineDiscounts().size());

		expiryDate = Calendar.getInstance();
		expiryDate.setTime(now);
		expiryDate.set(Calendar.HOUR_OF_DAY, 23);
		expiryDate.set(Calendar.MINUTE, 59);
		expiryDate.set(Calendar.SECOND, 59);
		expiryDate.add(Calendar.DATE, -4);
		addedClass.setStartDate(expiryDate.getTime());

		enrolment = refreshEnrolment(enrolment);

		assertEquals(Money.ZERO, enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
		assertEquals(0, getModel().getAllEnabledEnrolments().get(0).getInvoiceLines().get(0).getInvoiceLineDiscounts().size());

		expiryDate = Calendar.getInstance();
		expiryDate.setTime(now);
		expiryDate.set(Calendar.HOUR_OF_DAY, 23);
		expiryDate.set(Calendar.MINUTE, 59);
		expiryDate.set(Calendar.SECOND, 59);
		expiryDate.add(Calendar.DATE, -3);
		addedClass.setStartDate(expiryDate.getTime());

		enrolment = refreshEnrolment(enrolment);

		assertEquals(new Money("20.00"), enrolment.getInvoiceLines().get(0).getDiscountEachExTax());
		assertEquals("Discount 3  days before and 3 days after offsets", getModel().getAllEnabledEnrolments().get(0).getInvoiceLines().get(0).getInvoiceLineDiscounts().get(0).getDiscount().getName());
	}
	
	private Enrolment refreshEnrolment(Enrolment enrolment) {

		ActionDisableEnrolment actionDisableEnrolment =  disableEnrolment.createAction(purchaseController);
		actionDisableEnrolment.setEnrolment(enrolment);
		performAction(actionDisableEnrolment, disableEnrolment);

		enrolment = actionDisableEnrolment.getEnrolment();

		ActionEnableEnrolment actionEnableEnrolment =  enableEnrolment.createAction(purchaseController);
		actionEnableEnrolment.setEnrolment(enrolment);
		performAction(actionEnableEnrolment, enableEnrolment);
		return enrolment;
	}
}
