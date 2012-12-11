package ish.oncourse.enrol.checkout;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.utils.EnrolCoursesControllerTest;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.State;
import static org.junit.Assert.*;

public class PurchaseControllerTest extends ServiceTest {

	private ICayenneService cayenneService;
	private IPurchaseControllerBuilder purchaseControllerBuilder;

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
	public void testIsCreditAvailable()
	{
		PurchaseController controller = init(false);

		Contact contact = Cayenne.objectForPK(controller.getModel().getObjectContext(), Contact.class, 1189160);
		//add contact/payer
		ActionParameter param = new ActionParameter(Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(controller, param);
		assertTrue(controller.isCreditAvailable());
		assertEquals(new Money("-200"),controller.getPreviousOwing());

		param = new ActionParameter(Action.proceedToPayment);
		param.setValue(controller.getModel().getPayment());
		performAction(controller, param);
		assertTrue(controller.isCreditAvailable());
		assertEquals(new Money("-200"),controller.getPreviousOwing());

	}

	@Test
	public void allActionsTests() throws InterruptedException {
		PurchaseController controller = init(false);

		Contact contact = Cayenne.objectForPK(controller.getModel().getObjectContext(), Contact.class, 1189158);

		//add contact/payer
		ActionParameter param = new ActionParameter(Action.addContact);
		param.setValue(createContactCredentialsBy(contact));
		performAction(controller, param);

		assertEquals(State.editCheckout, controller.getState());
		assertEquals(1, controller.getModel().getContacts().size());
		assertNotNull(controller.getModel().getPayer());
		assertEquals(controller.getModel().getContacts().get(0),controller.getModel().getPayer());
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
		param = new ActionParameter(Action.proceedToPayment);
		param.setValue(controller.getModel().getPayment());
		performAction(controller, param);

		assertEquals(State.editPayment, controller.getState());
		assertNotNull(controller.getModel().getPayer());
		assertEquals(controller.getModel().getContacts().get(0),controller.getModel().getPayer());
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
		delegate.getPaymentIn().setCreditCardCVV("1111");
		delegate.getPaymentIn().setCreditCardExpiry("12/2020");
		delegate.getPaymentIn().setCreditCardName("NAME NAME");
		delegate.getPaymentIn().setCreditCardNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.VISA);
		delegate.makePayment();
        assertEquals(State.paymentProgress, controller.getState());

        delegate.updatePaymentStatus();
        Thread.sleep(20000);
        delegate.updatePaymentStatus();


        assertEquals(State.paymentResult, controller.getState());
		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertEquals(delegate.getPaymentIn(), controller.getModel().getPayment());
		assertEquals(PaymentStatus.FAILED_CARD_DECLINED,delegate.getPaymentIn().getStatus());

		delegate.tryAgain();
		assertEquals(State.editPayment, controller.getState());
		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertEquals(delegate.getPaymentIn(), controller.getModel().getPayment());
		assertEquals(PaymentStatus.IN_TRANSACTION, delegate.getPaymentIn().getStatus());

		//press makePayment
		delegate.getPaymentIn().setCreditCardCVV("1111");
		delegate.getPaymentIn().setCreditCardExpiry("12/2020");
		delegate.getPaymentIn().setCreditCardName("NAME NAME");
		delegate.getPaymentIn().setCreditCardNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.MASTERCARD);
		delegate.makePayment();
        assertEquals(State.paymentProgress, controller.getState());

        delegate.updatePaymentStatus();
        Thread.sleep(20000);
        delegate.updatePaymentStatus();

        assertEquals(State.paymentResult, controller.getState());
		assertEquals(2, controller.getModel().getAllEnrolments(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getAllProductItems(controller.getModel().getPayer()).size());
		assertEquals(1, controller.getModel().getPayment().getPaymentInLines().size());
		assertEquals(3, controller.getModel().getInvoice().getInvoiceLines().size());
		assertEquals(delegate.getPaymentIn(), controller.getModel().getPayment());
		assertEquals(PaymentStatus.SUCCESS ,delegate.getPaymentIn().getStatus());

	}


	@Test
	public void testErrorNoSelectedItemForPurchase() {
		ObjectContext context = cayenneService.newContext();
		College college = Cayenne.objectForPK(context, College.class, 1);
		PurchaseModel model = createModel(context, college, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null, null);
		PurchaseController purchaseController = purchaseControllerBuilder.build(model);
		purchaseController.performAction(new ActionParameter(Action.init));
		assertEquals(State.paymentResult, purchaseController.getState());
		assertNotNull(purchaseController.getErrors().get(PurchaseController.Message.noSelectedItemForPurchase.name()));
	}

	private PurchaseModel createModel(ObjectContext context, College college,
									  List<CourseClass> classes, List<Product> products,
									  Discount discount, WebSite webSite) {
		PurchaseModel model = new PurchaseModel();
		model.setObjectContext(context);
		model.setCollege(college);
		model.setClasses(classes);
		model.setProducts(products);
		model.setWebSite(webSite);
		model.addDiscount(discount);
		return model;
	}

	private PurchaseController init()
	{
		return init(true);
	}

	private PurchaseController init(boolean addPayer) {
		ObjectContext context = cayenneService.newContext();
		College college = Cayenne.objectForPK(context, College.class, 1);
		WebSite webSite = Cayenne.objectForPK(context,WebSite.class, 1);

		CourseClass cc1 = Cayenne.objectForPK(context, CourseClass.class, 1186958);
		CourseClass cc2 = Cayenne.objectForPK(context, CourseClass.class, 1186959);
		CourseClass cc3 = Cayenne.objectForPK(context, CourseClass.class, 1186960);


		Product p1 = Cayenne.objectForPK(context, VoucherProduct.class, 7);
		Product p2 = Cayenne.objectForPK(context, VoucherProduct.class, 8);

		Discount d = Cayenne.objectForPK(context, Discount.class, 2);

		PurchaseModel model = createModel(context, college,
				Arrays.asList(cc1,cc2,cc3),
				Arrays.asList(p1, p1),
				d,
				webSite);
		model.setObjectContext(context);
		model.setClasses(Arrays.asList(cc1, cc2, cc3));
		model.setProducts(Arrays.asList(p1, p2));
		model.setCollege(college);
		model.addDiscount(d);

		PurchaseController purchaseController = purchaseControllerBuilder.build(model);

		assertPurchaseController(purchaseController);

		purchaseController.performAction(new ActionParameter(Action.init));

		assertNotNull(model);
		assertNotNull(model.getInvoice());
		assertNotNull(model.getPayment());
		assertTrue(model.getContacts().isEmpty());
		assertNull(model.getPayer());
		assertEquals(3, model.getClasses().size());
		assertEquals(2, model.getProducts().size());
		assertTrue(model.getAllEnabledEnrolments().isEmpty());
		assertNotNull(purchaseController.getVoucherRedemptionHelper().getInvoice());
		assertEquals(State.addContact, purchaseController.getState());

		if (addPayer)
		{
				Contact contact = Cayenne.objectForPK(context, Contact.class, 1189157);
				ActionParameter actionParameter = new ActionParameter(Action.addContact);
				ContactCredentials contactCredentials = createContactCredentialsBy(contact);
				actionParameter.setValue(contactCredentials);
				purchaseController.performAction(actionParameter);

				assertEquals(State.editCheckout, purchaseController.getState());
				assertEquals(1, purchaseController.getModel().getContacts().size());
				assertNotNull(purchaseController.getModel().getPayer());
		}

		return purchaseController;
	}

	private void assertPurchaseController(PurchaseController purchaseController) {
		assertNotNull(purchaseController.getModel().getInvoice());
		assertNotNull(purchaseController.getModel().getInvoice().getWebSite());
		assertNotNull(purchaseController.getModel().getInvoice().getCollege());
		assertEquals(PaymentSource.SOURCE_WEB, purchaseController.getModel().getInvoice().getSource());

		assertNotNull(purchaseController.getModel().getPayment());
		assertNotNull(purchaseController.getModel().getPayment().getCollege());
		assertEquals(PaymentSource.SOURCE_WEB, purchaseController.getModel().getPayment().getSource());
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

	private ContactCredentials createContactCredentialsBy(Contact newPayer) {
		ContactCredentials contactCredentials = new ContactCredentials();
		contactCredentials.setFirstName(newPayer.getGivenName());
		contactCredentials.setLastName(newPayer.getFamilyName());
		contactCredentials.setEmail(newPayer.getEmailAddress());
		return contactCredentials;
	}

	@Test
	public void testAddStudent() {
		PurchaseController controller = init();
		PurchaseModel model = controller.getModel();

		Contact newContact = Cayenne.objectForPK(model.getObjectContext(), Contact.class, 1189158);


		addContact(controller, newContact);


		assertEquals(2, model.getContacts().size());
		assertTrue(model.getContacts().contains(newContact));

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(3, model.getEnabledEnrolments(newContact).size());
	}

	private void addContact(PurchaseController controller, Contact newContact) {
		ActionParameter param = new ActionParameter(Action.startAddContact);
		performAction(controller, param);

		param = new ActionParameter(Action.addContact);
		param.setValue(createContactCredentialsBy(newContact));

		performAction(controller, param);
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

		param = new ActionParameter(Action.enableEnrolment);
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

		addConcession(controller,sc);


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

		addConcession(controller,sc);

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

	private void performAction(PurchaseController controller, ActionParameter param) {
		controller.performAction(param);
		assertFalse("State is valid", controller.isIllegalState());
		assertFalse("Model is valid", controller.isIllegalModel());
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

		ConcessionEditorController concessionEditorController =  (ConcessionEditorController) controller.getConcessionDelegate();
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
		assertEquals(State.editCheckout,controller.getState());
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
