package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.Voucher;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.deselectVoucher;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActionAddVoucherPayerTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
        setup("ish/oncourse/enrol/checkout/ActionAddVoucherPayerTest.xml");
    }

    @Test
    public void test1() {
        createPurchaseController(1001);
        addFirstContact(1001);

		// add first voucher
        Voucher voucher1 = SelectById.query(Voucher.class, 1003L).selectOne(getModel().getObjectContext());

        ActionAddVoucher actionAddVoucher = new ActionAddVoucher();
        actionAddVoucher.setVoucher(voucher1);
        actionAddVoucher.setController(purchaseController);
        actionAddVoucher.action();

		assertEquals(17, getModel().getObjectContext().uncommittedObjects().size());
		assertEquals(10, getModel().getObjectContext().newObjects().size());
		assertEquals(2, getModel().getAllEnabledEnrolments().get(0).getPersistenceState());

		assertEquals(0,ObjectSelect.query(QueuedRecord.class).select(getModel().getObjectContext()).size());
		

		//check that contact was added, disabled enrolment was added for him, payer was changed
        assertEquals(Long.valueOf(1002l), getModel().getPayer().getId());
		assertEquals(1, getModel().getDisabledEnrolments(voucher1.getContact()).size());

		//try to add other voucher with different contact
		Voucher voucher2 = SelectById.query(Voucher.class, 1004L).selectOne(getModel().getObjectContext());

		actionAddVoucher = new ActionAddVoucher();
		actionAddVoucher.setVoucher(voucher2);
		actionAddVoucher.setController(purchaseController);
		boolean success = actionAddVoucher.action();
		
		//check that it is not passes 
		assertFalse(success);
		//payer has not been added, contacts list not changed
		assertEquals(Long.valueOf(1002l), purchaseController.getModel().getPayer().getId());
		assertEquals(2, getModel().getContacts().size());
		assertEquals(1, getModel().getVouchers().size());
		
		//proceed on payment page
		proceedToPayment();

		//try to add other voucher again
		success = actionAddVoucher.action();
		
		// check that it not passed on payment page also
		assertFalse(success);
		
		assertEquals(Long.valueOf(1002l), purchaseController.getModel().getPayer().getId());
		assertEquals(2, getModel().getContacts().size());
		assertEquals(1, getModel().getVouchers().size());

		//disable first voucher
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(deselectVoucher);
		parameter.setValue(voucher1.getId());
		purchaseController.performAction(parameter);
		//and check it
		assertEquals(0, getModel().getVoucherPayments().size());

		//try to add other voucher wit different contact
		success = actionAddVoucher.action();
		
		//should be success now
		assertTrue(success);
		
		//contact added, payer changed
		assertEquals(Long.valueOf(1004l), purchaseController.getModel().getPayer().getId());
		assertEquals(3, getModel().getContacts().size());
		assertEquals(2, getModel().getVouchers().size());
		assertEquals(1, getModel().getVoucherPayments().size());

		//try to add one more voucher without contact
		Voucher voucher3 = SelectById.query(Voucher.class, 1006L).selectOne(getModel().getObjectContext());

		actionAddVoucher = new ActionAddVoucher();
		actionAddVoucher.setVoucher(voucher3);
		actionAddVoucher.setController(purchaseController);
		success = actionAddVoucher.action();
		purchaseController.updateTotalIncGst();
		purchaseController.updateTotalDiscountAmountIncTax();
		
		//should be OK, vouchers apply together
		assertTrue(success);
		
		assertEquals(Long.valueOf(1004l), purchaseController.getModel().getPayer().getId());
		assertEquals(3, getModel().getContacts().size());
		assertEquals(3, getModel().getVouchers().size());
		assertEquals(2, getModel().getVoucherPayments().size());

	}

}
