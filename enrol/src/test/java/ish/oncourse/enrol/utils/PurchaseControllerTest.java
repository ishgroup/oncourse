package ish.oncourse.enrol.utils;

import static org.junit.Assert.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import ish.math.Money;
import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.test.ServiceTest;
import ish.util.InvoiceUtil;

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
		
		VoucherProduct p1 = Cayenne.objectForPK(context, VoucherProduct.class, 7);
		VoucherProduct p2 = Cayenne.objectForPK(context, VoucherProduct.class, 8);
		
		Discount d = Cayenne.objectForPK(context, Discount.class, 2);
		
		return new PurchaseController(invoiceProcessingService, discountService, voucherService, context, college, contact, 
				Arrays.asList(cc1, cc2, cc3), Arrays.asList(d), new ArrayList<Product>(Arrays.asList(p1, p2)));
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
	}
	
	@Test
	public void testChangePayer() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		Contact originalPayer = model.getPayer();
		
		Contact newPayer = Cayenne.objectForPK(context, Contact.class, 1189158);
		
		PurchaseController.ActionParameter param = new PurchaseController.ActionParameter(Action.CHANGE_PAYER);
		param.setValue(newPayer);
		
		controller.performAction(param);
		
		assertEquals(originalPayer, model.getPayer());
		
		param = new ActionParameter(Action.ADD_STUDENT);
		param .setValue(newPayer);
		
		controller.performAction(param);
		
		param = new PurchaseController.ActionParameter(Action.CHANGE_PAYER);
		param.setValue(newPayer);
		
		controller.performAction(param);
		
		assertEquals(newPayer, model.getPayer());
	}
	
	@Test
	public void testAddStudent() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		Contact newContact = Cayenne.objectForPK(context, Contact.class, 1189158);
		
		ActionParameter param = new ActionParameter(Action.ADD_STUDENT);
		param .setValue(newContact);
		
		controller.performAction(param);
		
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
		
		controller.performAction(param);
		
		assertEquals(2, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(1, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(enrolmentToDisable, model.getDisabledEnrolments(model.getPayer()).iterator().next());
		assertEquals(new Money("350.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		param = new ActionParameter(Action.ENABLE_ENROLMENT);
		param.setValue(enrolmentToDisable);
		
		controller.performAction(param);
		
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
		
		controller.performAction(param);
		
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
		
		ProductItem productToDisable = model.getEnabledProducts(model.getPayer()).iterator().next();
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(2, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(0, model.getDisabledProducts(model.getPayer()).size());
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		ActionParameter param = new ActionParameter(Action.DISABLE_PRODUCT);
		param.setValue(productToDisable);
		
		controller.performAction(param);
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(1, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(1, model.getDisabledProducts(model.getPayer()).size());
		
		assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		param = new ActionParameter(Action.ENABLE_PRODUCT);
		param.setValue(productToDisable);
		
		controller.performAction(param);
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(2, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(0, model.getDisabledProducts(model.getPayer()).size());
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}
	
	@Test
	public void testDisableProduct() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		ProductItem productToDisable = model.getEnabledProducts(model.getPayer()).iterator().next();
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(2, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(0, model.getDisabledProducts(model.getPayer()).size());
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		ActionParameter param = new ActionParameter(Action.DISABLE_PRODUCT);
		param.setValue(productToDisable);
		
		controller.performAction(param);
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(1, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(1, model.getDisabledProducts(model.getPayer()).size());
		
		assertEquals(new Money("840.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}
	
	@Test
	public void testAddPromocode() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(2, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(0, model.getDisabledProducts(model.getPayer()).size());
		
		String promocode = "code";
		
		ActionParameter param = new ActionParameter(Action.ADD_PROMOCODE);
		param.setValue(promocode);
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		controller.performAction(param);
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(2, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(0, model.getDisabledProducts(model.getPayer()).size());
		
		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}
	
	@Test
	public void testAddVoucherCode() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		assertEquals(3, model.getEnabledEnrolments(model.getPayer()).size());
		assertEquals(0, model.getDisabledEnrolments(model.getPayer()).size());
		
		assertEquals(2, model.getEnabledProducts(model.getPayer()).size());
		assertEquals(0, model.getDisabledProducts(model.getPayer()).size());
		
		String voucherCode = "test";
		
		ActionParameter param = new ActionParameter(Action.ADD_VOUCHER_CODE);
		param.setValue(voucherCode);
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		Money paidByVoucher = Money.ZERO;
		for (PaymentIn p : model.getVoucherPayments()) {
			paidByVoucher = paidByVoucher.add(p.getAmount());
		}
		
		assertEquals(Money.ZERO, paidByVoucher);
		
		controller.performAction(param);
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		paidByVoucher = Money.ZERO;
		for (PaymentIn p : model.getVoucherPayments()) {
			paidByVoucher = paidByVoucher.add(p.getAmount());
		}
		assertEquals(new Money("100.0"), paidByVoucher);
	}
	
	@Test
	public void testAddConcession() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		ConcessionType ct = Cayenne.objectForPK(context, ConcessionType.class, 1);
		createStudentConcession(context, model.getPayer().getStudent(), ct, model.getPayer().getCollege());
		
		ActionParameter param = new ActionParameter(Action.ADD_CONCESSION);
		controller.performAction(param);
		
		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}
	
	@Test
	public void testRemoveConcession() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		PurchaseModel model = controller.getModel();
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		ConcessionType ct = Cayenne.objectForPK(context, ConcessionType.class, 1);
		createStudentConcession(context, model.getPayer().getStudent(), ct, model.getPayer().getCollege());
		
		ActionParameter param = new ActionParameter(Action.ADD_CONCESSION);
		controller.performAction(param);
		
		assertEquals(new Money("740.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
		
		param = new ActionParameter(Action.REMOVE_CONCESSION);
		param.setValue(ct);
		param.setValue(model.getPayer());
		
		controller.performAction(param);
		
		assertEquals(new Money("850.0"), InvoiceUtil.sumInvoiceLines(model.getInvoice().getInvoiceLines()));
	}
	
	@Test
	public void testProceedToPayment() {
		ObjectContext context = cayenneService.newContext();
		PurchaseController controller = createPurchaseController(context);
		
		ActionParameter param = new ActionParameter(Action.PROCEED_TO_PAYMENT);
		controller.performAction(param);
		
		Contact newContact = Cayenne.objectForPK(context, Contact.class, 1189158);
		
		param = new ActionParameter(Action.ADD_STUDENT);
		param.setValue(newContact);
		
		try {
			controller.performAction(param);
			fail("No actions should be allowed when controller is in finalized state.");
		} catch (Exception e) {
			// expected
		}
	}

}
