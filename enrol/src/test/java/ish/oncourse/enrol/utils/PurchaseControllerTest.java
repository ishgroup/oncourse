package ish.oncourse.enrol.utils;

import ish.math.Money;
import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.*;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.voucher.IVoucherService;
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

import static ish.oncourse.enrol.utils.PurchaseController.State;
import static org.junit.Assert.*;

public class PurchaseControllerTest extends ServiceTest {

	private IInvoiceProcessingService invoiceProcessingService;
	private IVoucherService voucherService;
	private IDiscountService discountService;
	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.enrol.services", "enrol", EnrolTestModule.class);
		InputStream st = EnrolCoursesControllerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/utils/purchaseControllerTestDataSet.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);


		this.invoiceProcessingService = getService(IInvoiceProcessingService.class);
		this.voucherService = getService(IVoucherService.class);
		this.discountService = getService(IDiscountService.class);
		this.cayenneService = getService(ICayenneService.class);
	}

	private PurchaseController createPurchaseController(ObjectContext context) {
		College college = Cayenne.objectForPK(context, College.class, 1);
		Contact contact = Cayenne.objectForPK(context, Contact.class, 1189157);

		CourseClass cc1 = Cayenne.objectForPK(context, CourseClass.class, 1186958);
		CourseClass cc2 = Cayenne.objectForPK(context, CourseClass.class, 1186959);
		CourseClass cc3 = Cayenne.objectForPK(context, CourseClass.class, 1186960);


		Product p1 = Cayenne.objectForPK(context, VoucherProduct.class, 7);
		Product p2 = Cayenne.objectForPK(context, VoucherProduct.class, 8);

		Discount d = Cayenne.objectForPK(context, Discount.class, 2);

		PurchaseModel model = new PurchaseModel();
		model.setObjectContext(context);
		model.setClasses(Arrays.asList(cc1, cc2, cc3));
		model.setProducts(Arrays.asList(p1, p2));
		model.setCollege(college);
		model.setPayer(contact);
		model.addContact(contact);
		model.addDiscount(d);

		PurchaseController purchaseController = new PurchaseController();
		purchaseController.setInvoiceProcessingService(invoiceProcessingService);
		purchaseController.setDiscountService(discountService);
		purchaseController.setVoucherService(voucherService);
		purchaseController.setModel(model);

		purchaseController.performAction(new ActionParameter(Action.INIT));

		return purchaseController;
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
	public void testInit() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);

		PurchaseModel model = controller.getModel();

		assertNotNull(model);
		assertNotNull(model.getInvoice());
		assertNotNull(model.getPayment());
		assertEquals(1, model.getContacts().size());
		assertTrue(model.getContacts().contains(model.getPayer()));

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(State.EDIT_CHECKOUT, controller.getState());
	}

	@Test
	public void testChangePayer() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);

		PurchaseModel model = controller.getModel();

		Contact originalPayer = model.getPayer();
		Contact newPayer = Cayenne.objectForPK(context, Contact.class, 1189158);

		PurchaseController.ActionParameter param = new ActionParameter(Action.ADD_STUDENT);
		param.setValue(newPayer);
		performAction(controller, param);
		assertEquals(originalPayer, model.getPayer());
		assertTrue(controller.getModel().getContacts().contains(newPayer));

		param = new PurchaseController.ActionParameter(Action.CHANGE_PAYER);
		param.setValue(newPayer);

		performAction(controller, param);

		assertFalse(controller.isIllegalState());
		assertFalse(controller.isIllegalModel());

		assertEquals(newPayer, model.getPayer());
	}

	@Test
	public void testAddStudent() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		Contact newContact = Cayenne.objectForPK(context, Contact.class, 1189158);

		ActionParameter param = new ActionParameter(Action.ADD_STUDENT);
		param.setValue(newContact);

		performAction(controller, param);


		assertEquals(2, model.getContacts().size());
		assertTrue(model.getContacts().contains(newContact));

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(3, model.getEnabledEnrolments(newContact).size());
	}

	@Test
	public void testEnableEnrolment() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		Enrolment enrolmentToDisable = model.getEnabledEnrolments(model.getPayer()).iterator().next();

		ActionParameter param = new ActionParameter(Action.DISABLE_ENROLMENT);
		param.setValue(enrolmentToDisable);

		performAction(controller, param);


		assertEquals(2, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(1, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(enrolmentToDisable, model.getDisabledEnrolments(model.getPayer()).iterator().next());
		assertEquals(new Money("350.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		param = new ActionParameter(Action.ENABLE_ENROLMENT);
		param.setValue(enrolmentToDisable);

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testDisableEnrolment() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		Enrolment enrolmentToDisable = model.getEnabledEnrolments(model.getPayer()).iterator().next();

		ActionParameter param = new ActionParameter(Action.DISABLE_ENROLMENT);
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
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		ProductItem productToDisable = model.getEnabledProductItems(model.getPayer()).iterator().next();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ActionParameter param = new ActionParameter(Action.DISABLE_PRODUCT_ITEM);
		param.setValue(productToDisable);

		performAction(controller, param);


		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(1, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(1, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		param = new ActionParameter(Action.ENABLE_PRODUCT_ITEM);
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
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		ProductItem productToDisable = model.getEnabledProductItems(model.getPayer()).iterator().next();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ActionParameter param = new ActionParameter(Action.DISABLE_PRODUCT_ITEM);
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
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		String promocode = "code";

		ActionParameter param = new ActionParameter(Action.ADD_DISCOUNT);
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
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());

		assertEquals(2, model.getEnabledProductItems(model.getPayer()).size());
		assertEquals(0, model.getDisabledProductItems(model.getPayer()).size());

		String voucherCode = "test";

		ActionParameter param = new ActionParameter(Action.ADD_VOUCHER);
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
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ConcessionType ct = Cayenne.objectForPK(context, ConcessionType.class, 1);
		StudentConcession sc = createStudentConcession(context, model.getPayer().getStudent(), ct, model.getPayer().getCollege());

		ActionParameter param = new ActionParameter(Action.ADD_CONCESSION);
		param.setValue(sc);
		performAction(controller, param);


		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}

	@Test
	public void testRemoveConcession() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();

		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		ConcessionType ct = Cayenne.objectForPK(context, ConcessionType.class, 1);
		StudentConcession sc = createStudentConcession(context, model.getPayer().getStudent(), ct, model.getPayer().getCollege());

		ActionParameter param = new ActionParameter(Action.ADD_CONCESSION);
		param.setValue(sc);
		performAction(controller, param);


		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));

		param = new ActionParameter(Action.REMOVE_CONCESSION);
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
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);

		ActionParameter param = new ActionParameter(Action.PROCEED_TO_PAYMENT);
		performAction(controller, param);


		Contact newContact = Cayenne.objectForPK(context, Contact.class, 1189158);

		param = new ActionParameter(Action.ADD_STUDENT);
		param.setValue(newContact);

		controller.performAction(param);
		assertTrue("No actions should be allowed when controller is in finalized state.", controller.isIllegalState());
	}

	@Test
	public void test_START_CONCESSION_EDITOR() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);

		ActionParameter param = new ActionParameter(Action.START_CONCESSION_EDITOR);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		assertEDIT_CONCESSION(controller);
	}

	@Test
	public void test_CANCEL_CONCESSION_EDITOR() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);

		ActionParameter param = new ActionParameter(Action.START_CONCESSION_EDITOR);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		param = new ActionParameter(Action.CANCEL_CONCESSION_EDITOR);
		param.setValue(controller.getModel().getPayer());
		performAction(controller, param);

		assertEquals(State.EDIT_CHECKOUT, controller.getState());
		assertNull(controller.getConcessionDelegate());
	}

	@Test
	public void test_ConcessionEditorController() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		ActionParameter param = new ActionParameter(Action.START_CONCESSION_EDITOR);
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
			assertEquals(concessionType.getId(), concessionEditorController.getStudentConcession().getConcessionType().getId());
		}
		concessionEditorController.changeConcessionTypeBy(-1);
		assertNull(concessionEditorController.getStudentConcession());

		concessionEditorController.cancelEditing(concessionEditorController.getContact().getId());
		assertFalse(concessionEditorController.getObjectContext().hasChanges());
		assertEquals(State.EDIT_CHECKOUT,controller.getState());
	}


	private void assertEDIT_CONCESSION(PurchaseController controller) {
		assertEquals(State.EDIT_CONCESSION, controller.getState());
		assertNotNull(controller.getConcessionDelegate());
		assertEquals(controller.getModel().getPayer(), controller.getConcessionDelegate().getContact());
		assertEquals(controller.getModel().getPayer().getStudent(), controller.getConcessionDelegate().getContact().getStudent());

		for (Action action : Action.values()) {
			switch (action) {
				case CANCEL_CONCESSION_EDITOR:
				case ADD_CONCESSION:
				case REMOVE_CONCESSION:
					assertTrue(action.name(), controller.validateState(action));
					break;
				default:
					assertFalse(action.name(), controller.validateState(action));
					break;
			}
		}

	}

}
