/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.math.Money;
import ish.oncourse.model.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TaxOverriddenTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/TaxOverriddenTest.xml");
    }

    @Test
    public void taxOverriddenTest() throws InterruptedException {
        init(Arrays.asList(1005L, 1006L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1000L);
        PurchaseController.ActionParameter param;
        param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
        param.setValue(createContactCredentialsBy(contact));
        performAction(param);
        
        CourseClass courseClass1 = Cayenne.objectForPK(getModel().getObjectContext(), CourseClass.class, 1005L);
        CourseClass courseClass2 = Cayenne.objectForPK(getModel().getObjectContext(), CourseClass.class, 1006L);
        
        Enrolment enrolment1 = getModel().getEnrolmentBy(contact, courseClass1);
        Enrolment enrolment2 = getModel().getEnrolmentBy(contact, courseClass2);


        assertEquals(new Money("454.55"), enrolment1.getInvoiceLines().get(0).getPriceEachExTax());
        assertEquals(new Money("68.18"), enrolment1.getInvoiceLines().get(0).getTotalTax());
        assertEquals(new Money("522.73"), enrolment1.getInvoiceLines().get(0).getPriceTotalIncTax());

        
        assertEquals(new Money("100.00"), enrolment2.getInvoiceLines().get(0).getPriceEachExTax());
        assertEquals(new Money("15.00"), enrolment2.getInvoiceLines().get(0).getTotalTax());
        assertEquals(new Money("115.00"), enrolment2.getInvoiceLines().get(0).getPriceTotalIncTax());

        assertEquals(new Money("637.73"), getModel().getPayment().getAmount());

        proceedToPayment();
        makeValidPayment();

        Invoice invoice = ObjectSelect.query(Invoice.class).selectOne(getModel().getObjectContext());

        assertEquals(new Money("0.00"), invoice.getAmountOwing());
        assertEquals(new Money("637.73"), invoice.getTotalGst());
        assertEquals(new Money("554.55"), invoice.getTotalExGst());

    }

    @Test
    public void changePayerTest() throws InterruptedException {
        init(Arrays.asList(1005L, 1006L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1001L);
        
        PurchaseController.ActionParameter param;
        param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
        param.setValue(createContactCredentialsBy(contact));
        performAction(param);

        assertEquals(new Money("610.00"), getModel().getPayment().getAmount());
        proceedToPayment();

        Contact contact2 = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1000L);

        purchaseController.setState(PurchaseController.State.editContact);
        param = new PurchaseController.ActionParameter(PurchaseController.Action.addPersonPayer);
        param.setValue(contact2);
        performAction(param);
        

        assertEquals(new Money("637.73"), getModel().getPayment().getAmount());
        assertEquals(new Money("637.73"), getModel().getInvoice().getTotalGst());
        
        makeValidPayment();

    }


    @Test
    public void voucherWithOverriddenTax() throws InterruptedException {
        init(Collections.EMPTY_LIST, Arrays.asList(7L), Collections.EMPTY_LIST, false);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1000L);

        PurchaseController.ActionParameter param;
        param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
        param.setValue(createContactCredentialsBy(contact));
        performAction(param);

        assertEquals(new Money("10.00"), getModel().getPayment().getAmount());
        assertEquals(new Money("10.00"), getModel().getInvoice().getTotalGst());
        assertEquals(1, getModel().getInvoice().getInvoiceLines().get(0).getProductItems().size());
        assertEquals(new Money("10.00"), getModel().getInvoice().getInvoiceLines().get(0).getPriceTotalIncTax());
        assertEquals(new Money("0.00"), getModel().getInvoice().getInvoiceLines().get(0).getTaxEach());

        proceedToPayment();
        makeValidPayment();
    }
    
    @Test
    public void productsWithOverriddenTax() throws InterruptedException {
        init(Collections.EMPTY_LIST, Arrays.asList(8L, 9L), Collections.EMPTY_LIST, false);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1000L);

        PurchaseController.ActionParameter param;
        param = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
        param.setValue(createContactCredentialsBy(contact));
        performAction(param);

        assertEquals(new Money("23.00"), getModel().getPayment().getAmount());
        assertEquals(new Money("23.00"), getModel().getInvoice().getTotalGst());
        
        assertEquals(1, getModel().getInvoice().getInvoiceLines().get(0).getProductItems().size());
        assertEquals(new Money("11.50"), getModel().getInvoice().getInvoiceLines().get(0).getPriceTotalIncTax());
        assertEquals(new Money("1.50"), getModel().getInvoice().getInvoiceLines().get(0).getTaxEach());

        assertEquals(1, getModel().getInvoice().getInvoiceLines().get(1).getProductItems().size());
        assertEquals(new Money("11.50"), getModel().getInvoice().getInvoiceLines().get(1).getPriceTotalIncTax());
        assertEquals(new Money("1.50"), getModel().getInvoice().getInvoiceLines().get(1).getTaxEach());

        proceedToPayment();
        makeValidPayment();
    }

    
    
}
