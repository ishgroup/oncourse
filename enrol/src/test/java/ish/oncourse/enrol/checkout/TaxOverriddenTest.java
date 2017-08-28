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
    public void createNewApplicationTest() throws InterruptedException {
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
}
