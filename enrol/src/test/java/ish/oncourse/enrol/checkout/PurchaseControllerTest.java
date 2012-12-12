package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.utils.EnrolCoursesControllerTest;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.util.InvoiceUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
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
		initTest("ish.oncourse.enrol.services", "enrol", EnrolTestModule.class);
		InputStream st = EnrolCoursesControllerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/enrol/checkout/purchaseControllerTestDataSet.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);


		this.cayenneService = getService(ICayenneService.class);
		this.purchaseControllerBuilder = getService(IPurchaseControllerBuilder.class);
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
				setVoucherPrice, addDiscount, addVoucher,
				startConcessionEditor, startAddContact,
				owingApply, creditAccess, changePayer), COMMON_ACTIONS);
		assertEquals(State.init.getAllowedActions(), Arrays.asList(init, startAddContact));
		ArrayList<Action> actions = new ArrayList<Action>(COMMON_ACTIONS);
		actions.add(proceedToPayment);
		actions.add(addCourseClass);
		assertEquals(State.editCheckout.getAllowedActions(), actions);
		assertEquals(State.editConcession.getAllowedActions(), Arrays.asList(addConcession, removeConcession, cancelConcessionEditor));
		assertEquals(State.addContact.getAllowedActions(), Arrays.asList(addContact, cancelAddContact));
		assertEquals(State.editContact.getAllowedActions(), Arrays.asList(addContact, cancelAddContact));
		assertEquals(State.editPayment.getAllowedActions(), Arrays.asList(makePayment, backToEditCheckout));
		assertEquals(State.paymentProgress.getAllowedActions(), Arrays.asList(showPaymentResult));
		assertEquals(State.paymentResult.getAllowedActions(), Arrays.asList(proceedToPayment, showPaymentResult));
	}

	@Test
	public void testIsCreditAvailable() {
		PurchaseController controller = init(false);

		Contact contact = Cayenne.objectForPK(controller.getModel().getObjectContext(), Contact.class, 1189160);
		//add contact/payer
		ActionParameter param = new ActionParameter(Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(controller, param);
		assertTrue(controller.isCreditAvailable());
		assertEquals(new Money("-200"), controller.getPreviousOwing());

		param = new ActionParameter(Action.proceedToPayment);
		param.setValue(controller.getModel().getPayment());
		performAction(controller, param);
		assertTrue(controller.isCreditAvailable());
		assertEquals(new Money("-200"), controller.getPreviousOwing());

	}

	@Test
	public void allActionsTests() throws InterruptedException {
		PurchaseController controller = init(false);

		Contact contact = Cayenne.objectForPK(controller.getModel().getObjectContext(), Contact.class, 1189158);

		//add contact/payer
		ActionParameter param = new ActionParameter(Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(controller, param);

		List<Enrolment> enrolments = controller.getModel().getAllEnrolments(controller.getModel().getPayer());
		assertEquals(State.editCheckout, controller.getState());
		for (Enrolment enrolment : enrolments) {
			assertNull(controller.getModel().getErrorBy(enrolment));
		}
		assertEquals(1, controller.getModel().getContacts().size());
		assertNotNull(controller.getModel().getPayer());
		assertEquals(controller.getModel().getContacts().get(0), controller.getModel().getPayer());
		assertEquals(3, controller.getModel().getAllEnabledEnrolments().size());
		assertEquals(3, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(2, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(3, controller.getModel().getEnabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(0, controller.getModel().getDisabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(2, controller.getModel().getEnabledProductItems(controller.getModel().getPayer()).size());
		assertEquals(0, controller.getModel().getDisabledProductItems(controller.getModel().getPayer()).size());


		//disable one Enrolment
		param = new ActionParameter(Action.disableEnrolment);
		Enrolment enrolment = controller.getModel().getEnabledEnrolments(controller.getModel().getPayer()).get(0);
		param.setValue(enrolment);
		performAction(controller, param);
		assertEquals(State.editCheckout, controller.getState());
		assertEquals(2, controller.getModel().getEnabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getDisabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(2, controller.getModel().getEnabledProductItems(controller.getModel().getPayer()).size());
		assertEquals(0, controller.getModel().getDisabledProductItems(controller.getModel().getPayer()).size());


		//disable one ProductItem
		param = new ActionParameter(Action.disableProductItem);
		ProductItem productItem = controller.getModel().getEnabledProductItems(controller.getModel().getPayer()).get(0);
		param.setValue(productItem);
		performAction(controller, param);
		assertEquals(State.editCheckout, controller.getState());
		assertEquals(2, controller.getModel().getEnabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getDisabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getEnabledProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getDisabledProductItems(controller.getModel().getPayer()).size());

		//press proceedToPayment
		proceedToPayment(controller);

		assertEquals(State.editPayment, controller.getState());
		assertNotNull(controller.getModel().getPayer());
		assertEquals(controller.getModel().getContacts().get(0), controller.getModel().getPayer());
		assertEquals(2, controller.getModel().getAllEnabledEnrolments().size());
		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(2, controller.getModel().getEnabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(0, controller.getModel().getDisabledEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getEnabledProductItems(controller.getModel().getPayer()).size());
		assertEquals(0, controller.getModel().getDisabledProductItems(controller.getModel().getPayer()).size());
		assertFalse(controller.getModel().getPayment().getObjectId().isTemporary());
		assertFalse(controller.getModel().getInvoice().getObjectId().isTemporary());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertFalse(controller.getModel().getPayment().getPaymentInLines().get(0).getObjectId().isTemporary());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertFalse(controller.getModel().getInvoice().getInvoiceLines().get(0).getObjectId().isTemporary());

		//press makePayment
		PaymentEditorDelegate delegate = controller.getPaymentEditorDelegate();
		makeInvalidPayment(controller);

		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertEquals(delegate.getPaymentIn(), controller.getModel().getPayment());
		assertEquals(PaymentStatus.FAILED_CARD_DECLINED, delegate.getPaymentIn().getStatus());

		delegate.tryAgain();
		assertEquals(State.editPayment, controller.getState());
		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertEquals(delegate.getPaymentIn(), controller.getModel().getPayment());
		assertEquals(PaymentStatus.IN_TRANSACTION, delegate.getPaymentIn().getStatus());

		//press makePayment
		makeValidPayment(controller);

		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertEquals(delegate.getPaymentIn(), controller.getModel().getPayment());
		assertEquals(PaymentStatus.SUCCESS, delegate.getPaymentIn().getStatus());

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
		PurchaseController purchaseController = super.createPurchaseController(model);
		assertEquals(3, model.getClasses().size());
		assertEquals(2, model.getProducts().size());

		if (addPayer) {
			Contact contact = Cayenne.objectForPK(context, Contact.class, 1189157);
			addContactAction(purchaseController, contact);
			assertEquals(1, purchaseController.getModel().getContacts().size());
			assertNotNull(purchaseController.getModel().getPayer());
		}

		return purchaseController;
	}

	@Test
	public void testChangePayer() {
		PurchaseController controller = init(true);

		PurchaseModel model = controller.getModel();
		ObjectContext context = model.getObjectContext();

		Contact originalPayer = model.getPayer();
		Contact newPayer = Cayenne.objectForPK(context, Contact.class, 1189158);
		addContact(controller, newPayer);

		assertEquals(originalPayer, model.getPayer());
		assertTrue(controller.getModel().getContacts().contains(newPayer));

		ActionParameter param = new PurchaseController.ActionParameter(Action.changePayer);
		param.setValue(newPayer);

		performAction(controller, param);

		assertFalse(controller.isIllegalState());
		assertFalse(controller.isIllegalModel());

		assertEquals(newPayer, model.getPayer());
	}

	@Test
	public void testAddStudent() {
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		CourseClass onePlaceClass = Cayenne.objectForPK(model.getObjectContext(),
				CourseClass.class, 1186959);

		Contact newContact = Cayenne.objectForPK(model.getObjectContext(), Contact.class, 1189158);


		addContact(controller, newContact);


		assertEquals(2, model.getContacts().size());
		assertTrue(model.getContacts().contains(newContact));

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());

		assertEquals("One course has only one place", 2, model.getEnabledEnrolments(newContact).size());
		assertNotNull("Error for this class", model.getErrorBy(model.getEnrolmentBy(newContact, onePlaceClass)));
	}


	@Test
	public void testEnableEnrolment() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		Enrolment enrolmentToDisable = model.getEnabledEnrolments(model.getPayer()).iterator().next();

		ActionParameter param = new ActionParameter(Action.disableEnrolment);
		param.setValue(enrolmentToDisable);

		performAction(controller, param);


		assertEquals(2, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(1, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(enrolmentToDisable, model.getDisabledEnrolments(model.getPayer()).iterator().next());
		assertEquals(new Money("350.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		param = new ActionParameter(enableEnrolment);
		param.setValue(enrolmentToDisable);

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testDisableEnrolment() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		Enrolment enrolmentToDisable = model.getEnabledEnrolments(model.getPayer()).iterator().next();

		ActionParameter param = new ActionParameter(Action.disableEnrolment);
		param.setValue(enrolmentToDisable);

		performAction(controller, param);


		assertEquals(2, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(1, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(enrolmentToDisable, model.getDisabledEnrolments(model.getPayer()).iterator().next());
		assertEquals(new Money("350.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testEnableProduct() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		ProductItem productToDisable = model.getEnabledProductItems(model.getPayer()).iterator().next();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ActionParameter param = new ActionParameter(Action.disableProductItem);
		param.setValue(productToDisable);

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(1, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(1, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		param = new ActionParameter(Action.enableProductItem);
		param.setValue(productToDisable);

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testDisableProduct() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		ProductItem productToDisable = model.getEnabledProductItems(model.getPayer()).iterator().next();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ActionParameter param = new ActionParameter(Action.disableProductItem);
		param.setValue(productToDisable);

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(1, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(1, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testAddPromocode() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		String promocode = "code";

		ActionParameter param = new ActionParameter(Action.addDiscount);
		param.setValue(promocode);

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testAddVoucherCode() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

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

		performAction(controller, param);


		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		paidByVoucher = Money.ZERO;
		for (PaymentIn p : model.getVoucherPayments()) {
			paidByVoucher = paidByVoucher.add(p.getAmount());
		}
		assertEquals(new Money("100.0"), paidByVoucher);
	}

	@Test //TODO add/remove concession should be adjusted
	public void testAddConcession() {
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();
		ObjectContext context = model.getObjectContext();

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ConcessionType ct = Cayenne.objectForPK(context, ConcessionType.class, 1);
		StudentConcession sc = createStudentConcession(context, model.getPayer().getStudent(), ct, model.getPayer().getCollege());

		addConcession(controller, sc);


		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	private void addConcession(PurchaseController controller, StudentConcession sc) {
		ActionParameter param = new ActionParameter(Action.startConcessionEditor);
		param.setValue(sc.getStudent().getContact());
		performAction(controller, param);

		param = new ActionParameter(Action.addConcession);
		param.setValue(sc);
		performAction(controller, param);
	}

	@Test
	public void testRemoveConcession() {
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();
		ObjectContext context = model.getObjectContext();

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ConcessionType ct = Cayenne.objectForPK(context, ConcessionType.class, 1);
		StudentConcession sc = createStudentConcession(context, model.getPayer().getStudent(), ct, model.getPayer().getCollege());

		addConcession(controller, sc);

		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ActionParameter param = new ActionParameter(Action.startConcessionEditor);
		param.setValue(model.getPayer());
		performAction(controller, param);

		param = new ActionParameter(Action.removeConcession);
		param.setValue(ct);
		param.setValue(model.getPayer());

		performAction(controller, param);


		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testProceedToPayment() {
		PurchaseController controller = init();

		ActionParameter param = new ActionParameter(Action.proceedToPayment);
		param.setValue(controller.getModel().getPayment());
		performAction(controller, param);

		Contact newContact = Cayenne.objectForPK(controller.getModel().getObjectContext(), Contact.class, 1189158);

		param = new ActionParameter(Action.addContact);
		param.setValue(newContact);

		controller.performAction(param);
		assertTrue("No actions should be allowed when controller is in finalized state.", controller.isIllegalState());
	}

	@Test
	public void testStartConcessionEditor() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();

		ActionParameter param = new ActionParameter(Action.startConcessionEditor);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		assertEditConcession(controller);
	}

	@Test
	public void testCancelConcessionEditor() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();

		ActionParameter param = new ActionParameter(Action.startConcessionEditor);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		param = new ActionParameter(Action.cancelConcessionEditor);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		assertEquals(State.editCheckout, controller.getState());
		assertNull(controller.getConcessionDelegate());
	}

	@Test
	public void testConcessionEditorController() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = init();
		ActionParameter param = new ActionParameter(Action.startConcessionEditor);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		ConcessionEditorController concessionEditorController = (ConcessionEditorController) controller.getConcessionDelegate();
		assertNotNull(concessionEditorController);
		assertTrue(concessionEditorController.getObjectContext() != context);
		assertNotNull(concessionEditorController.getContact());
		assertNotNull(concessionEditorController.getStudent());
		assertNull(concessionEditorController.getStudentConcession());
		assertEquals(3, concessionEditorController.getConcessionTypes().size());

		for (int i = 0; i < concessionEditorController.getConcessionTypes().size(); i++) {
			ConcessionType concessionType = concessionEditorController.getConcessionTypes().get(i);
			concessionEditorController.changeConcessionTypeBy(i);
			assertNotNull(concessionEditorController.getStudentConcession());
			//student will be set only before commit.
			assertNull(concessionEditorController.getStudentConcession().getStudent());
			assertEquals(concessionType.getId(), concessionEditorController.getStudentConcession().getConcessionType().getId());
		}
		concessionEditorController.changeConcessionTypeBy(-1);
		assertNull(concessionEditorController.getStudentConcession());

		concessionEditorController.cancelEditing();
		assertNull(concessionEditorController.getObjectContext());
		assertNull(concessionEditorController.getStudentConcession());
		assertEquals(State.editCheckout, controller.getState());
	}

	@Test
	public void testAbandan() throws InterruptedException {
		PurchaseController controller = init(true);
		proceedToPayment(controller);
		makeInvalidPayment(controller);
		PaymentEditorDelegate delegate = controller.getPaymentEditorDelegate();
		delegate.abandon();
		assertEquals(PaymentStatus.FAILED_CARD_DECLINED, controller.getModel().getPayment().getStatus());
		List<Enrolment> enrolments = controller.getModel().getAllEnabledEnrolments();
		for (Enrolment enrolment : enrolments) {
			assertEquals(EnrolmentStatus.FAILED, enrolment.getStatus());
		}
		assertFalse(controller.getModel().getObjectContext().hasChanges());
		assertTrue(controller.isFinished());
		assertTrue(controller.getPaymentEditorDelegate().isProcessFinished());
		assertFalse(controller.getPaymentEditorDelegate().isPaymentSuccess());
		assertFalse(controller.getPaymentEditorDelegate().isEnrolmentFailedNoPlaces());
	}


	private void assertEditConcession(PurchaseController controller) {
		assertEquals(State.editConcession, controller.getState());
		assertNotNull(controller.getConcessionDelegate());
		assertEquals(controller.getModel().getPayer().getId(), controller.getConcessionDelegate().getContact().getId());
		assertEquals(controller.getModel().getPayer().getStudent().getId(), controller.getConcessionDelegate().getContact().getStudent().getId());

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
