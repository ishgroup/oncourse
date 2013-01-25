package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.model.*;
import ish.util.InvoiceUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;
import static ish.oncourse.enrol.checkout.PurchaseController.COMMON_ACTIONS;
import static ish.oncourse.enrol.checkout.PurchaseController.State;
import static org.junit.Assert.*;

public class PurchaseControllerTest extends ACheckoutTest {

    @Before
    public void setup() throws Exception {
		setup("ish/oncourse/enrol/checkout/purchaseControllerTestDataSet.xml");
    }


    private StudentConcession createStudentConcession(ObjectContext context, Student student, ConcessionType ct, College college) {
        StudentConcession sc = context.newObject(StudentConcession.class);

        sc.setCollege(college);
        sc.setConcessionType(ct);
        sc.setStudent(student);
        sc.setConcessionNumber("1");

        return sc;
    }

    @Test
    public void testActionsAndStates() {
        assertEquals(Arrays.asList(
                enableEnrolment, enableProductItem,
                disableEnrolment, disableProductItem,
                setVoucherPrice, addVoucher,
                startConcessionEditor, startAddContact),
                COMMON_ACTIONS);
        assertEquals(State.init.getAllowedActions(), Arrays.asList(init, startAddContact));
        ArrayList<Action> actions = new ArrayList<Action>(COMMON_ACTIONS);
        actions.add(addDiscount);
        actions.add(removeDiscount);
        actions.add(proceedToPayment);
        actions.add(addCourseClass);
        assertEquals(State.editCheckout.getAllowedActions(), actions);
        assertEquals(State.editConcession.getAllowedActions(), Arrays.asList(addConcession, removeConcession, cancelConcessionEditor));
        assertEquals(State.addContact.getAllowedActions(), Arrays.asList(addContact, cancelAddContact));
        assertEquals(State.editContact.getAllowedActions(), Arrays.asList(addContact, cancelAddContact));
        assertEquals(State.editPayment.getAllowedActions(), Arrays.asList(makePayment, backToEditCheckout,addDiscount, creditAccess, owingApply,changePayer));
        assertEquals(State.paymentProgress.getAllowedActions(), Arrays.asList(showPaymentResult));
        assertEquals(State.paymentResult.getAllowedActions(), Arrays.asList(proceedToPayment, showPaymentResult));
    }

    @Test
    public void testIsCreditAvailable() {
        init(false);

        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189160);
        //add contact/payer
        ActionParameter param = new ActionParameter(Action.addContact);
        param.setValue(createContactCredentialsBy(contact));
        performAction(param);
        assertTrue(purchaseController.isCreditAvailable());
        assertFalse(purchaseController.hasPreviousOwing());
        assertEquals(new Money("-200"), purchaseController.getPreviousOwing());

        param = new ActionParameter(Action.proceedToPayment);
        param.setValue(purchaseController.getModel().getPayment());
        performAction(param);
        assertTrue(purchaseController.isCreditAvailable());
        assertFalse(purchaseController.hasPreviousOwing());
        assertEquals(new Money("-200"), purchaseController.getPreviousOwing());


    }

    @Test
    public void allActionsTests() throws InterruptedException {
        init(false);

        assertTrue(purchaseController.isAddContact());
        assertFalse(purchaseController.isFinished());
        Contact contact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189158);

        //add contact/payer
        ActionParameter param = new ActionParameter(Action.addContact);
        param.setValue(createContactCredentialsBy(contact));
        performAction(param);
        assertFalse(purchaseController.isFinished());


        List<Enrolment> enrolments = purchaseController.getModel().getAllEnrolments(purchaseController.getModel().getPayer());
        assertTrue(purchaseController.isEditCheckout());
        assertEquals(State.editCheckout, purchaseController.getState());
        for (Enrolment enrolment : enrolments) {
            assertNull(purchaseController.getModel().getErrorBy(enrolment));
        }
        assertEquals(1, purchaseController.getModel().getContacts().size());
        assertNotNull(purchaseController.getModel().getPayer());
        assertEquals(purchaseController.getModel().getContacts().get(0), purchaseController.getModel().getPayer());
        assertEquals(3, purchaseController.getModel().getAllEnabledEnrolments().size());
        assertEquals(3, purchaseController.getModel().getAllEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(2, purchaseController.getModel().getAllProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(3, purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(0, purchaseController.getModel().getDisabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(2, purchaseController.getModel().getEnabledProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(0, purchaseController.getModel().getDisabledProductItems(purchaseController.getModel().getPayer()).size());


        //disable one Enrolment
        param = new ActionParameter(Action.disableEnrolment);
        Enrolment enrolment = purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).get(0);
        param.setValue(enrolment);
        performAction(param);
        assertFalse(purchaseController.isFinished());
        assertTrue(purchaseController.isEditCheckout());
        assertEquals(State.editCheckout, purchaseController.getState());
        assertEquals(2, purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(2, purchaseController.getModel().getEnabledProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(0, purchaseController.getModel().getDisabledProductItems(purchaseController.getModel().getPayer()).size());


        //disable one ProductItem
        param = new ActionParameter(Action.disableProductItem);
        ProductItem productItem = purchaseController.getModel().getEnabledProductItems(purchaseController.getModel().getPayer()).get(0);
        param.setValue(productItem);
        performAction(param);
        assertFalse(purchaseController.isFinished());
        assertTrue(purchaseController.isEditCheckout());
        assertEquals(State.editCheckout, purchaseController.getState());
        assertEquals(2, purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getDisabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getEnabledProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getDisabledProductItems(purchaseController.getModel().getPayer()).size());

        //press proceedToPayment
        proceedToPayment();

        assertFalse(purchaseController.isFinished());
        assertTrue(purchaseController.isEditPayment());
        assertEquals(State.editPayment, purchaseController.getState());
        assertNotNull(purchaseController.getModel().getPayer());
        assertEquals(purchaseController.getModel().getContacts().get(0), purchaseController.getModel().getPayer());
        assertEquals(2, purchaseController.getModel().getAllEnabledEnrolments().size());
        assertEquals(2, purchaseController.getModel().getAllEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getAllProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(2, purchaseController.getModel().getEnabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(0, purchaseController.getModel().getDisabledEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getEnabledProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(0, purchaseController.getModel().getDisabledProductItems(purchaseController.getModel().getPayer()).size());
        assertFalse(purchaseController.getModel().getPayment().getObjectId().isTemporary());
        assertFalse(purchaseController.getModel().getInvoice().getObjectId().isTemporary());
        assertEquals(1, purchaseController.getModel().getPayment().getPaymentInLines().size());
        assertFalse(purchaseController.getModel().getPayment().getPaymentInLines().get(0).getObjectId().isTemporary());
        assertEquals(3, purchaseController.getModel().getInvoice().getInvoiceLines().size());
        assertFalse(purchaseController.getModel().getInvoice().getInvoiceLines().get(0).getObjectId().isTemporary());

        //press makePayment
        PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
        makeInvalidPayment();

        assertEquals(2, purchaseController.getModel().getAllEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getAllProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getPayment().getPaymentInLines().size());
        assertEquals(3, purchaseController.getModel().getInvoice().getInvoiceLines().size());
        assertEquals(delegate.getPaymentIn(), purchaseController.getModel().getPayment());
        assertEquals(PaymentStatus.FAILED_CARD_DECLINED, delegate.getPaymentIn().getStatus());

        delegate.tryAgain();
        assertEquals(State.editPayment, purchaseController.getState());
        assertEquals(2, purchaseController.getModel().getAllEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getAllProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getPayment().getPaymentInLines().size());
        assertEquals(3, purchaseController.getModel().getInvoice().getInvoiceLines().size());
        assertEquals(delegate.getPaymentIn(), purchaseController.getModel().getPayment());
        assertEquals(PaymentStatus.IN_TRANSACTION, delegate.getPaymentIn().getStatus());

        //press makePayment
        makeValidPayment();

        enrolments = purchaseController.getModel().getAllEnabledEnrolments();
        for (Enrolment e : enrolments) {
            assertEquals(EnrolmentStatus.SUCCESS, e.getStatus());
        }

        assertEquals(2, purchaseController.getModel().getAllEnrolments(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getAllProductItems(purchaseController.getModel().getPayer()).size());
        assertEquals(1, purchaseController.getModel().getPayment().getPaymentInLines().size());
        assertEquals(3, purchaseController.getModel().getInvoice().getInvoiceLines().size());
        assertEquals(delegate.getPaymentIn(), purchaseController.getModel().getPayment());
        assertEquals(PaymentStatus.SUCCESS, delegate.getPaymentIn().getStatus());
        assertFalse(purchaseController.getModel().getObjectContext().hasChanges());
        assertTrue(purchaseController.isFinished());
        assertTrue(purchaseController.getPaymentEditorDelegate().isProcessFinished());
        assertTrue(purchaseController.getPaymentEditorDelegate().isPaymentSuccess());
    }

    @Test
    public void testErrorNoSelectedItemForPurchase() {
        ObjectContext context = cayenneService.newContext();
        PurchaseModel model = createModel(context, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
        PurchaseController purchaseController = purchaseControllerBuilder.build(model);
        purchaseController.performAction(new ActionParameter(Action.init));
        assertEquals(State.paymentResult, purchaseController.getState());
        assertNotNull(purchaseController.getErrors().get(PurchaseController.Message.noSelectedItemForPurchase.name()));
    }

    private PurchaseController init() {
        return init(true);
    }

    private PurchaseController init(boolean addPayer) {
        ObjectContext context = cayenneService.newContext();
        CourseClass cc1 = Cayenne.objectForPK(context, CourseClass.class, 1186958);
        CourseClass cc2 = Cayenne.objectForPK(context, CourseClass.class, 1186959);
        CourseClass cc3 = Cayenne.objectForPK(context, CourseClass.class, 1186960);


        Product p1 = Cayenne.objectForPK(context, VoucherProduct.class, 7);
        Product p2 = Cayenne.objectForPK(context, VoucherProduct.class, 8);

        Discount d = Cayenne.objectForPK(context, Discount.class, 2);

        PurchaseModel model = createModel(context,
                Arrays.asList(cc1, cc2, cc3),
                Arrays.asList(p1, p2),
                d);
        super.createPurchaseController(model);
        assertEquals(3, model.getClasses().size());
        assertEquals(2, model.getProducts().size());

        if (addPayer) {
			addFirstContact(1189157);
            assertEquals(1, purchaseController.getModel().getContacts().size());
            assertNotNull(purchaseController.getModel().getPayer());
        }

        return purchaseController;
    }

    @Test
    public void testChangePayer() {
        PurchaseController purchaseController = init(true);

        PurchaseModel model = purchaseController.getModel();
        ObjectContext context = model.getObjectContext();

        Contact originalPayer = model.getPayer();
        Contact newPayer = Cayenne.objectForPK(context, Contact.class, 1189158);
        addContact(newPayer);

        assertEquals(originalPayer, model.getPayer());
        assertTrue(purchaseController.getModel().getContacts().contains(newPayer));

        proceedToPayment();

        ActionParameter param = new ActionParameter(Action.changePayer);
        param.setValue(newPayer);

        performAction(param);

        assertFalse(purchaseController.isIllegalState());
        assertFalse(purchaseController.isIllegalModel());

        assertEquals(newPayer, model.getPayer());
    }

    @Test
    public void testAddStudent() {
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        CourseClass onePlaceClass = Cayenne.objectForPK(model.getObjectContext(),
                CourseClass.class, 1186959);

        Contact newContact = Cayenne.objectForPK(model.getObjectContext(), Contact.class, 1189158);


        addContact(newContact);


        assertEquals(2, model.getContacts().size());
        assertTrue(model.getContacts().contains(newContact));

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());

        assertEquals("One course has only one place", 2, model.getEnabledEnrolments(newContact).size());
        assertNotNull("Error for this class", model.getErrorBy(model.getEnrolmentBy(newContact, onePlaceClass)));
    }


    @Test
    public void testEnableEnrolment() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        Enrolment enrolmentToDisable = model.getEnabledEnrolments(model.getPayer()).iterator().next();

        ActionParameter param = new ActionParameter(Action.disableEnrolment);
        param.setValue(enrolmentToDisable);

        performAction(param);


        assertEquals(2, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(1, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(enrolmentToDisable, model.getDisabledEnrolments(model.getPayer()).iterator().next());
        assertEquals(new Money("350.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        param = new ActionParameter(enableEnrolment);
        param.setValue(enrolmentToDisable);

        performAction(param);


        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    @Test
    public void testDisableEnrolment() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        Enrolment enrolmentToDisable = model.getEnabledEnrolments(model.getPayer()).iterator().next();

        ActionParameter param = new ActionParameter(Action.disableEnrolment);
        param.setValue(enrolmentToDisable);

        performAction(param);


        assertEquals(2, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(1, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(enrolmentToDisable, model.getDisabledEnrolments(model.getPayer()).iterator().next());
        assertEquals(new Money("350.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    @Test
    public void testEnableProduct() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        ProductItem productToDisable = model.getEnabledProductItems(model.getPayer()).iterator().next();

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        ActionParameter param = new ActionParameter(Action.disableProductItem);
        param.setValue(productToDisable);

        performAction(param);


        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(1, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(1, model.getDisabledProductItems(model.getPayer()).size());

        assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        param = new ActionParameter(Action.enableProductItem);
        param.setValue(productToDisable);

        performAction(param);


        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    @Test
    public void testDisableProduct() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        ProductItem productToDisable = model.getEnabledProductItems(model.getPayer()).iterator().next();

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        ActionParameter param = new ActionParameter(Action.disableProductItem);
        param.setValue(productToDisable);

        performAction(param);


        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(1, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(1, model.getDisabledProductItems(model.getPayer()).size());

        assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    @Test
    public void testAddPromocode() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

        String promocode = "code";

        ActionParameter param = new ActionParameter(Action.addDiscount);
        param.setValue(promocode);

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        performAction(param);


        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

        assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    @Test
    public void testAddVoucherCode() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();

        assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
        assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

        assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
        assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

        String voucherCode = "test";

        ActionParameter param = new ActionParameter(Action.addVoucher);
        param.setValue(voucherCode);

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        Money paidByVoucher = Money.ZERO;
        for (PaymentIn p : model.getVoucherPayments()) {
            paidByVoucher = paidByVoucher.add(p.getAmount());
        }

        assertEquals(Money.ZERO, paidByVoucher);

        performAction(param);


        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        paidByVoucher = Money.ZERO;
        for (PaymentIn p : model.getVoucherPayments()) {
            paidByVoucher = paidByVoucher.add(p.getAmount());
        }
        assertEquals(new Money("100.0"), paidByVoucher);
    }

    @Test //TODO add/remove concession should be adjusted
    public void testAddConcession() {
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();
        ObjectContext context = model.getObjectContext();

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ObjectContext cContext = context.createChildContext();
        ConcessionType ct = Cayenne.objectForPK(cContext, ConcessionType.class, 1);
        StudentConcession sc = createStudentConcession(cContext,
				(Student) cContext.localObject(model.getPayer().getStudent().getObjectId(),null),
				ct,
				(College) cContext.localObject(model.getPayer().getCollege().getObjectId(),null));

        addConcession(purchaseController, sc);


        assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    private void addConcession(PurchaseController purchaseController, StudentConcession sc) {
        ActionParameter param = new ActionParameter(Action.startConcessionEditor);
        param.setValue(sc.getStudent().getContact());
        performAction(param);

        param = new ActionParameter(Action.addConcession);
        param.setValue(sc);
        performAction(param);
    }

    @Test
    public void testRemoveConcession() {
        PurchaseController purchaseController = init();
        PurchaseModel model = purchaseController.getModel();
        ObjectContext context = model.getObjectContext();

        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ObjectContext cContext = context.createChildContext();
        ConcessionType ct = Cayenne.objectForPK(cContext, ConcessionType.class, 1);
        StudentConcession sc = createStudentConcession(cContext, (Student)cContext.localObject(model.getPayer().getStudent().getObjectId(), null),
				ct, (College) cContext.localObject(model.getPayer().getCollege().getObjectId(), null));

        addConcession(purchaseController, sc);

        assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

        ActionParameter param = new ActionParameter(Action.startConcessionEditor);
        param.setValue(model.getPayer());
        performAction(param);

        param = new ActionParameter(Action.removeConcession);
        param.setValue(model.localizeObject(ct));
        param.setValue(model.getPayer());

        performAction(param);


        assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
    }

    @Test
    public void testProceedToPayment() {
        PurchaseController purchaseController = init();

        ActionParameter param = new ActionParameter(Action.proceedToPayment);
        param.setValue(purchaseController.getModel().getPayment());
        performAction(param);

        Contact newContact = Cayenne.objectForPK(purchaseController.getModel().getObjectContext(), Contact.class, 1189158);

        param = new ActionParameter(Action.addContact);
        param.setValue(newContact);

        purchaseController.performAction(param);
        assertTrue("No actions should be allowed when purchaseController is in finalized state.", purchaseController.isIllegalState());
    }

    @Test
    public void testStartConcessionEditor() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();

        ActionParameter param = new ActionParameter(Action.startConcessionEditor);
        param.setValue(purchaseController.getModel().getPayer());
        performAction(param);

        assertEditConcession(purchaseController);
    }

    @Test
    public void testCancelConcessionEditor() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();

        ActionParameter param = new ActionParameter(Action.startConcessionEditor);
        param.setValue(purchaseController.getModel().getPayer());
        performAction(param);

        param = new ActionParameter(Action.cancelConcessionEditor);
        param.setValue(purchaseController.getModel().getPayer());
        performAction(param);

        assertEquals(State.editCheckout, purchaseController.getState());
        assertNull(purchaseController.getConcessionDelegate());
    }

    @Test
    public void testConcessionEditorController() {
        ObjectContext context = cayenneService.newContext();
        PurchaseController purchaseController = init();
        ActionParameter param = new ActionParameter(Action.startConcessionEditor);
        param.setValue(purchaseController.getModel().getPayer());
        performAction(param);

        ConcessionEditorController concessionEditorController = (ConcessionEditorController) purchaseController.getConcessionDelegate();
        assertNotNull(concessionEditorController);
        assertTrue(concessionEditorController.getObjectContext() != context);
        assertNotNull(concessionEditorController.getContact());
        assertNotNull(concessionEditorController.getStudent());
        assertNotNull(concessionEditorController.getStudentConcession());
        assertEquals(3, concessionEditorController.getConcessionTypes().size());

        for (int i = 0; i < concessionEditorController.getConcessionTypes().size(); i++) {
            ConcessionType concessionType = concessionEditorController.getConcessionTypes().get(i);
            concessionEditorController.changeConcessionTypeBy(i);
            assertNotNull(concessionEditorController.getStudentConcession());
            assertEquals(concessionType.getId(), concessionEditorController.getStudentConcession().getConcessionType().getId());
        }
        concessionEditorController.changeConcessionTypeBy(-1);
        assertNotNull(concessionEditorController.getStudentConcession());
        assertNull(concessionEditorController.getStudentConcession().getConcessionType());

        concessionEditorController.cancelEditing();
        assertNull(concessionEditorController.getObjectContext());
        assertEquals(State.editCheckout, purchaseController.getState());
    }

    @Test
    public void testAbandan() throws InterruptedException {
        PurchaseController purchaseController = init(true);
        proceedToPayment();
        makeInvalidPayment();
        PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
        delegate.abandon();
        assertEquals(PaymentStatus.FAILED_CARD_DECLINED, purchaseController.getModel().getPayment().getStatus());
        List<Enrolment> enrolments = purchaseController.getModel().getAllEnabledEnrolments();
        for (Enrolment enrolment : enrolments) {
            assertEquals(EnrolmentStatus.FAILED, enrolment.getStatus());
        }
        assertFalse(purchaseController.getModel().getObjectContext().hasChanges());
        assertTrue(purchaseController.isFinished());
        assertTrue(purchaseController.getPaymentEditorDelegate().isProcessFinished());
        assertFalse(purchaseController.getPaymentEditorDelegate().isPaymentSuccess());
        assertFalse(purchaseController.getPaymentEditorDelegate().isEnrolmentFailedNoPlaces());
    }


    private void assertEditConcession(PurchaseController purchaseController) {
        assertEquals(State.editConcession, purchaseController.getState());
        assertNotNull(purchaseController.getConcessionDelegate());
        assertEquals(purchaseController.getModel().getPayer().getId(), purchaseController.getConcessionDelegate().getContact().getId());
        assertEquals(purchaseController.getModel().getPayer().getStudent().getId(), purchaseController.getConcessionDelegate().getContact().getStudent().getId());

        for (Action action : Action.values()) {
            switch (action) {
                case cancelConcessionEditor:
                case addConcession:
                case removeConcession:
                    assertTrue(State.editConcession.getAllowedActions().contains(action));
                    break;
                default:
                    assertFalse(State.editConcession.getAllowedActions().contains(action));
                    break;
            }
        }

    }

}
