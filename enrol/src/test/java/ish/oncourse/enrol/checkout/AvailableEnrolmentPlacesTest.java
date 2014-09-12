/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.model.*;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class AvailableEnrolmentPlacesTest extends ACheckoutTest {

	@Before
	public void setup() throws Exception {
		super.setup("ish/oncourse/enrol/checkout/AvailableEnrolmentPlacesTest.xml");
	}

	@Test
	public void availableEnrolmentPlacesTest() throws InterruptedException {

		PurchaseModel model = init(Arrays.asList(1003L), Collections.<Long>emptyList(), Collections.<Long>emptyList(), false).getModel();

		Contact contact = Cayenne.objectForPK(model.getObjectContext(), Contact.class, 1003L);

		addContact(contact);

		proceedToPayment();

		//make parallel enrolment in this courseclass
		ObjectContext context = cayenneService.newContext();
		Enrolment enrolment = context.newObject(Enrolment.class);

		CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1003L);
		Student student = Cayenne.objectForPK(context, Student.class, 1001L);
		College college = Cayenne.objectForPK(context, College.class, 1L);

		enrolment.setCourseClass(courseClass);
		enrolment.setStudent(student);
		enrolment.setCollege(college);
		enrolment.setStatus(EnrolmentStatus.SUCCESS);

		context.commitChanges();

		//make payment from web enrol
		makePayment();

		assertEquals(PurchaseController.State.editPayment, purchaseController.getState());
		assertEquals(1, purchaseController.getErrors().size());
		assertNotNull(purchaseController.getModel().getErrorBy(purchaseController.getModel().getEnrolmentBy(model.getPayer(), model.getClasses().get(0))));
		assertTrue(purchaseController.getErrors().containsKey(PurchaseController.Message.noPlacesLeft.name()));
	}

	private void makePayment() {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.getPaymentIn().setCreditCardCVV(TestPaymentGatewayService.VISA.getCvv());
		delegate.getPaymentIn().setCreditCardExpiry(TestPaymentGatewayService.VISA.getExpiry());
		delegate.getPaymentIn().setCreditCardName(TestPaymentGatewayService.VISA.getName());
		delegate.getPaymentIn().setCreditCardNumber(TestPaymentGatewayService.VISA.getNumber());
		delegate.getPaymentIn().setCreditCardType(TestPaymentGatewayService.VISA.getType());
		delegate.makePayment();
	}



    /**
     * The test verifies we stop processing the payment if MaximumPlaces for the class was changed during the process.
     * It can happen if college changes the property on angel side and the changes is replicated to willow
     * while enrol process is not finished
     */
    @Test
    public void maximumPlacesDecreasedTest() throws Exception {
        //class has only three places and one is already used
        CourseClass courseClass = createPurchaseController(1002);

        //add first contact
        Contact contact = addFirstContact(1001);
        assertEnabledEnrolments(contact, 1, true);
        assertEquals(1, purchaseController.getModel().getAllEnabledEnrolments().size());

        //add second contact
        contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1002);
        addContact(contact);
        assertEnabledEnrolments(contact, 1, true);
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());

        proceedToPayment();

        //the code updates amountOwing by JDBC request, to exclude Cayenne functionality
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        Connection connection = refDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("update CourseClass set maximumPlaces = ? where id = ?");
        statement.setLong(1, 1);
        statement.setLong(2, 1002);
        statement.execute();
        connection.commit();
        statement.close();
        connection.close();



        makePayment();

        assertEquals(PurchaseController.State.editPayment, purchaseController.getState());
        assertEquals(1, purchaseController.getErrors().size());
        assertEquals(1, purchaseController.getModel().getAllEnableEnrolmentBy(courseClass).size());
        assertTrue(purchaseController.getErrors().containsKey(PurchaseController.Message.noCourseClassPlaces.name()));
    }
}
