/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.ApplicationStatus;
import ish.common.types.ConfirmationStatus;
import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Application;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Student;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.applicationAlreadyApplyed;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.applicationReceived;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.noEnabledItemForPurchase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApplicationProcessTest extends ACheckoutTest {
	
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/ApplicationProcessTest.xml");
	}
	
	@Test
	public void createNewApplicationTest() {
		//abb classes from single course which has ENROLMENT_BY_APPLICATION type
		init(Arrays.asList(1005l, 1006l), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		
		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001L);
		PurchaseController.ActionParameter param;
		param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(param);

		//check that only one application (course-student) was added
		assertTrue(purchaseController.getModel().getAllDisabledEnrolments().isEmpty());
		assertTrue(purchaseController.getModel().getAllEnabledEnrolments().isEmpty());
		assertEquals(1, purchaseController.getModel().getAllEnabledApplications().size());
		assertEquals(purchaseController.getModel().getClasses().get(0).getCourse(), purchaseController.getModel().getAllEnabledApplications().get(0).getCourse());
		assertEquals(purchaseController.getModel().getContacts().get(0).getStudent(), purchaseController.getModel().getAllEnabledApplications().get(0).getStudent());
		assertEquals(ApplicationStatus.NEW, purchaseController.getModel().getAllEnabledApplications().get(0).getStatus());
		
		//no invoiceLines here
		assertTrue(purchaseController.getModel().getInvoice().getInvoiceLines().isEmpty());
		proceedToPayment();
		
		//check payment step
		assertTrue(purchaseController.getPaymentEditorDelegate().isZeroPayment());
		assertTrue(purchaseController.getPaymentEditorDelegate().isEmptyInvoice());
		assertTrue(purchaseController.isEditPayment());

		//do not populate CC fields - it is not needed - zero payment here
		purchaseController.getPaymentEditorDelegate().makePayment();

		assertTrue(purchaseController.getErrors().isEmpty());
		assertTrue(purchaseController.isPaymentResult());
		assertTrue(purchaseController.getPaymentEditorDelegate().isFinalState());

		
		// check that NEW Application for course-student was created properly
		ObjectContext context = purchaseController.getModel().getObjectContext();
		Expression expression = ExpressionFactory.matchExp(Application.STUDENT_PROPERTY +"."+ Student.CONTACT_PROPERTY, contact)
				.andExp(ExpressionFactory.inExp(Application.COURSE_PROPERTY + "+." + Course.COURSE_CLASSES_PROPERTY, purchaseController.getModel().getClasses()))
				.andExp(ExpressionFactory.matchExp(Application.STATUS_PROPERTY, ApplicationStatus.NEW))
				.andExp(ExpressionFactory.matchExp(Application.CONFIRMATION_STATUS_PROPERTY, ConfirmationStatus.NOT_SENT));

		assertEquals(1, context.performQuery(new SelectQuery(Application.class, expression)).size());

		//check that no PaymentIn and Invoice for this transaction
		expression  = ExpressionFactory.matchExp(PaymentIn.CONTACT_PROPERTY, contact);
		assertEquals(0, context.performQuery(new SelectQuery(PaymentIn.class, expression)).size());

		expression  = ExpressionFactory.matchExp(Invoice.CONTACT_PROPERTY, contact);
		assertEquals(0, context.performQuery(new SelectQuery(Invoice.class, expression)).size());
	}
	
	
	@Test
	public void duplicateApplicationTest() {
		init(Arrays.asList(1003l), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		
		//add contact which already has NEW Application for this Course 
		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1000L);
		PurchaseController.ActionParameter param;
		param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(param);

		//check that only one application (course-student) was created
		assertTrue(purchaseController.getModel().getAllDisabledEnrolments().isEmpty());
		assertTrue(purchaseController.getModel().getAllEnabledEnrolments().isEmpty());
		assertEquals(0, purchaseController.getModel().getAllEnabledApplications().size());
		
		//check that application is disabled
		assertEquals(1, purchaseController.getModel().getDisabledApplications(contact).size());
		
		//check error message
		Application application = purchaseController.getModel().getApplicationBy(contact, purchaseController.getModel().getClasses().get(0).getCourse());
		assertEquals(applicationReceived.getMessage(purchaseController.getMessages()),
				purchaseController.getModel().getErrorBy(application));
		
		//try to select this item 
		ActionEnableApplication action = PurchaseController.Action.enableApplication.createAction(purchaseController);
		action.setApplication(application);
		assertFalse(action.action());

		// and check again
		assertEquals(1, purchaseController.getModel().getDisabledApplications(contact).size());
		assertEquals(applicationReceived.getMessage(purchaseController.getMessages()),
				purchaseController.getModel().getErrorBy(application));

		//try proceed to payment
		ActionProceedToPayment proceedToPayment = PurchaseController.Action.proceedToPayment.createAction(purchaseController);
		
		param = new PurchaseController.ActionParameter(PurchaseController.Action.proceedToPayment);
		param.setValue(purchaseController.getModel().getPayment());
		proceedToPayment.setParameter(param);
		
		//can not proceed because no selected item
		assertFalse(proceedToPayment.action());
		
		assertFalse(purchaseController.getErrors().isEmpty());
		assertNotNull(purchaseController.getErrors().get(noEnabledItemForPurchase.getMessage(purchaseController.getMessages())));
	}
	
	@Test
	public void applyOfferedTest() throws InterruptedException {
		//both classes from single course 
		init(Arrays.asList(1003l, 1004l), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
		
		//add contact which already has Offered Application for this Course 
		Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001L);
		PurchaseController.ActionParameter param;
		param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(param);

		//check that two enrolments was created, no applications here
		assertEquals(2, purchaseController.getModel().getAllEnrolments(contact).size());
		assertTrue(purchaseController.getModel().getAllEnabledApplications().isEmpty());
		assertTrue(purchaseController.getModel().getDisabledApplications(contact).isEmpty());
		
		//Offered application can be applied only for one Enrolment
		assertEquals(1,purchaseController.getModel().getAllEnabledEnrolments().size());
		assertEquals(1,purchaseController.getModel().getAllDisabledEnrolments().size());

		//check error message
		Enrolment disableEnrolment = purchaseController.getModel().getAllDisabledEnrolments().get(0);
		assertEquals(applicationAlreadyApplyed.getMessage(purchaseController.getMessages()), purchaseController.getModel().getErrorBy(disableEnrolment));

		//try to select this item 
		ActionEnableEnrolment action = PurchaseController.Action.enableEnrolment.createAction(purchaseController);
		action.setEnrolment(disableEnrolment);
		assertFalse(action.action());
		
		//check invoice
		Enrolment enabledEnrolment = purchaseController.getModel().getAllEnabledEnrolments().get(0);
		assertEquals(1, purchaseController.getModel().getInvoice().getInvoiceLines().size());
		InvoiceLine invoiceLine = purchaseController.getModel().getInvoice().getInvoiceLines().get(0);
		Application application = purchaseController.getApplicationService().findOfferedApplicationBy(enabledEnrolment.getCourseClass().getCourse(), enabledEnrolment.getStudent());
		
		//check that the cheapest and not expired application selected
		assertEquals(46L, application.getId().longValue());
		
		assertEquals(invoiceLine.getPriceEachExTax(), application.getFeeOverride());
		assertEquals(invoiceLine.getTaxEach(), application.getFeeOverride().multiply(enabledEnrolment.getCourseClass().getTaxRate()));
		
		proceedToPayment();
		makeValidPayment();

		//check a result
		ObjectContext context = purchaseController.getModel().getObjectContext();
		application = Cayenne.objectForPK(context, Application.class, 46L);
		
		assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
		
		Expression expression = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, enabledEnrolment.getStudent())
				.andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, enabledEnrolment.getCourseClass()))
				.andExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS));
		
		assertEquals(1, context.performQuery(new SelectQuery(Enrolment.class, expression)).size());
	}
}
