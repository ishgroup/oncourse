package ish.oncourse.enrol.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.CayenneDataObject;
import org.apache.tapestry5.services.Session;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ish.common.types.CreditCardType;
import ish.common.types.DiscountType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.enrol.services.EnrolTestModule;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Student;
import ish.oncourse.services.courseclass.CourseClassService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
//import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;

@RunWith(MockitoJUnitRunner.class)
public class EnrolCoursesControllerTest extends ServiceTest{
	
	private ICayenneService cayenneService;
	private IInvoiceProcessingService invoiceProcessingService;
	private IPaymentGatewayService gatewayService;
	private IDiscountService discountService;
	private ICourseClassService courseClassService;
	private IWebSiteService webSiteService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.enrol.services", "enrol", EnrolTestModule.class);
		InputStream st = EnrolCoursesControllerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/utils/enrolCoursesControllerDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		this.cayenneService = getService(ICayenneService.class);
		this.gatewayService = new TestPaymentGatewayService/*PaymentExpressGatewayService*/(cayenneService);
		this.discountService = getService(IDiscountService.class);
		this.invoiceProcessingService = new InvoiceProcessingService(discountService);
		webSiteService = mock(IWebSiteService.class);
		College currentCollege = Cayenne.objectForPK(cayenneService.sharedContext(), College.class, 10);
		when(webSiteService.getCurrentCollege()).thenReturn(currentCollege);
		this.courseClassService = new CourseClassService(cayenneService, webSiteService);
	}
	
	private EnrolCoursesController prepareNewController() {
		EnrolCoursesController controller = new EnrolCoursesController(invoiceProcessingService, gatewayService);
		controller.setContext(cayenneService.newContext());
		return controller;
	}
	
	private void initClassesToEnrol(EnrolCoursesController controller, List<Long> orderedClassesIds) {
		List<Enrolment> enrolments = controller.getEnrolmentsList();
		controller.deleteNotUsedEnrolments(enrolments, orderedClassesIds);
		if (orderedClassesIds == null || orderedClassesIds.size() < 1) {
			controller.getModel().setClassesToEnrol(null);
            return;
        }
		if (controller.shouldReloadClassesToEnrol(orderedClassesIds)){
			controller.getModel().setClassesToEnrol(courseClassService.loadByIds(orderedClassesIds));
			controller.orderClassesToEnrol();
        }
	}
	
	private IStudentService prepareStudentService(EnrolCoursesController controller) {
		IStudentService studentService = mock(IStudentService.class);
		Contact contact1 = Cayenne.objectForPK(controller.getContext(), Contact.class, 11891600);
		Contact contact2 = Cayenne.objectForPK(controller.getContext(), Contact.class, 11891590);
			//(Contact) controller.getContext().performQuery(new SelectQuery(Contact.class, 
			//ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, 11891590L))).get(0);
		if (contact1.getStudent() == null) {
			Student student1 = Cayenne.objectForPK(controller.getContext(), Student.class, 11891500);
			student1.setContact(contact1);
			controller.getContext().commitChanges();
			assertNotNull("student for this contact should not be null", contact1.getStudent());
		}
		if (contact2.getStudent() == null) {
			Student student2 = Cayenne.objectForPK(controller.getContext(), Student.class, 11891490);
			student2.setContact(contact2);
			controller.getContext().commitChanges();
			assertNotNull("student for this contact should not be null", contact2.getStudent());
		}
		List<Contact> contacts = Arrays.asList(contact1, contact2);
		when(studentService.getStudentsFromShortList()).thenReturn(contacts);
		return studentService;
	}
	
	private IStudentService prepareStudentServiceForSingleUser(EnrolCoursesController controller) {
		IStudentService studentService = mock(IStudentService.class);
		Contact contact1 = Cayenne.objectForPK(controller.getContext(), Contact.class, 11891600);
		if (contact1.getStudent() == null) {
			Student student1 = Cayenne.objectForPK(controller.getContext(), Student.class, 11891500);
			student1.setContact(contact1);
			controller.getContext().commitChanges();
			assertNotNull("student for this contact should not be null", contact1.getStudent());
		}
		List<Contact> contacts = Arrays.asList(contact1);
		when(studentService.getStudentsFromShortList()).thenReturn(contacts);
		return studentService;
	}
	
	private void fillContacts(EnrolCoursesController controller, IStudentService studentService) {
		College currentCollege = webSiteService.getCurrentCollege();
		Session session = mock(Session.class);
		Discount discount = controller.getContext().newObject(Discount.class);
		discount.setAngelId(1L);
		discount.setCode("qwerty");
		discount.setCombinationType(true);
		discount.setDetail("details");
		discount.setDiscountAmount(new Money("10.00"));
		discount.setDiscountType(DiscountType.DOLLAR);
		discount.setHideOnWeb(true);
		discount.setIsAvailableOnWeb(true);
		discount.setMaximumDiscount(new Money("50.00"));
		discount.setName("discount name");
		discount.setRoundingMode(MoneyRounding.ROUNDING_NONE);
		discount.setValidFrom(studentService.getStudentsFromShortList().get(0).getCreated());
		discount.setCollege((College) controller.getContext().localObject(currentCollege.getObjectId(), null));
		controller.getContext().commitChanges();
		List<Discount> actualPromotions = Arrays.asList(discount);
		if (controller.getModel().getClassesToEnrol() != null) {
			controller.getModel().setContacts(studentService.getStudentsFromShortList());
            if (!controller.getModel().getContacts().isEmpty()) {
                // init the payment and the related entities only if there
                // exist
                // shortlisted classes and students
            	controller.initPayment(session, currentCollege, actualPromotions);
            } else {
            	controller.setModel(new EnrolCoursesModel());
            }
		}
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testIsAllEnrolmentsAvailable() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L, 11869600L);
		IStudentService studentService = prepareStudentService(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds);
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		assertTrue("All enrollments should be available for enrol", controller.isAllEnrolmentsAvailable(controller.getModel().getEnrolmentsList()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBeforeRenderInit() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L, 11869600L);
		IStudentService studentService = prepareStudentService(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds);
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		assertFalse("No discounts should be applied", controller.isHasDiscount());
		assertEquals("No discounts should be applied", Money.ZERO, controller.getTotalDiscountAmountIncTax());
		assertFalse("No discounts should be applied", controller.hasSuitableClasses(null));
	}
	
	private EnrolCoursesController testBeforeRenderInitPreparement(IStudentService studentService, EnrolCoursesController controller, 
		List<Long> orderedClassesIds) {
		initClassesToEnrol(controller, orderedClassesIds);
		assertNotNull("On init " + orderedClassesIds.size() + " classes to enroll should be loaded", controller.getModel().getClassesToEnrol());
		assertEquals("On init " + orderedClassesIds.size() + " classes to enroll should be loaded", orderedClassesIds.size(), 
			controller.getModel().getClassesToEnrol().size());
		fillContacts(controller, studentService);
		return controller;
	}
	
	private EnrolCoursesController setupThePaymentInDataBeforeProcessPayment(EnrolCoursesController controller) {
		controller.getModel().getPayment().setContact(controller.getModel().getContacts().get(0));
		controller.getModel().getPayment().setCreditCardName("john smith");
		controller.getModel().getPayment().setCreditCardNumber("5431111111111111");
		controller.getModel().getPayment().setCreditCardCVV("1234");
		controller.getModel().getPayment().setCreditCardExpiry("12" + "/" + "2019");
		controller.setCheckoutResult(true);
		return controller;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessPaymentWithFailedCardDeclinedResult() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L, 11869600L);
		IStudentService studentService = prepareStudentService(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		setupThePaymentInDataBeforeProcessPayment(testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds));
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		controller.getModel().getPayment().setCreditCardType(CreditCardType.VISA);
		controller.processPayment(Collections.EMPTY_LIST);
		
		assertFalse("After payment processing finiahed we should not checkout the result", controller.isCheckoutResult());
		assertEquals("Payment should fail", PaymentStatus.FAILED_CARD_DECLINED, controller.getModel().getPayment().getStatus());
		assertEquals("Failed payment amount should be equial to invoice amount owing", controller.getModel().getPayment().getAmount(), 
			controller.getModel().getInvoice().getAmountOwing());
		assertEquals("Failed payment amount should be 1200$", new Money("1200.00"), controller.getModel().getPayment().getAmount());
		assertTrue("When payment fails this mean that at least the payment will have not-commited modification", 
			!controller.getContext().uncommittedObjects().isEmpty());
		assertEquals("11 entities should have uncommited local modifications", 11, controller.getContext().uncommittedObjects().size());
		int enrollmentsCount = 0, invoiceLinesCount = 0,invoicesCount = 0, paymentInLinesCount = 0, paymentsCount = 0;
		for (Object object : controller.getContext().uncommittedObjects()) {
			CayenneDataObject dataObject = (CayenneDataObject) object;
			if (dataObject instanceof PaymentIn) {
				paymentsCount++;
			} else if (dataObject instanceof PaymentInLine) {
				paymentInLinesCount++;
			} else if (dataObject instanceof Invoice) {
				invoicesCount++;
			} else if (dataObject instanceof InvoiceLine) {
				invoiceLinesCount++;
			} else if (dataObject instanceof Enrolment && EnrolmentStatus.IN_TRANSACTION.equals(((Enrolment) dataObject).getStatus())) {
				enrollmentsCount++;
			}
		}
		assertEquals("4 in transaction enrollments should be uncommited", 4, enrollmentsCount);
		assertEquals("4 invoicelines should be uncommited", 4, invoiceLinesCount);
		assertEquals("1 invoice should be uncommited", 1, invoicesCount);
		assertEquals("1 paymentinline should be uncommited", 1, paymentInLinesCount);
		assertEquals("1 paymentin should be uncommited", 1, paymentsCount);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessPaymentWithFailedCardDeclinedResultForSingleUser() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L, 11869600L);
		IStudentService studentService = prepareStudentServiceForSingleUser(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		setupThePaymentInDataBeforeProcessPayment(testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds));
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		controller.getModel().getPayment().setCreditCardType(CreditCardType.VISA);
		controller.processPayment(Collections.EMPTY_LIST);
		
		assertFalse("After payment processing finiahed we should not checkout the result", controller.isCheckoutResult());
		assertEquals("Payment should fail", PaymentStatus.FAILED_CARD_DECLINED, controller.getModel().getPayment().getStatus());
		assertEquals("Failed payment amount should be equial to invoice amount owing", controller.getModel().getPayment().getAmount(), 
			controller.getModel().getInvoice().getAmountOwing());
		assertEquals("Failed payment amount should be 600$", new Money("600.00"), controller.getModel().getPayment().getAmount());
		assertTrue("When payment fails this mean that at least the payment will have not-commited modification", 
			!controller.getContext().uncommittedObjects().isEmpty());
		assertEquals("7 entities should have uncommited local modifications", 7, controller.getContext().uncommittedObjects().size());
		int enrollmentsCount = 0, invoiceLinesCount = 0,invoicesCount = 0, paymentInLinesCount = 0, paymentsCount = 0;
		for (Object object : controller.getContext().uncommittedObjects()) {
			CayenneDataObject dataObject = (CayenneDataObject) object;
			if (dataObject instanceof PaymentIn) {
				paymentsCount++;
			} else if (dataObject instanceof PaymentInLine) {
				paymentInLinesCount++;
			} else if (dataObject instanceof Invoice) {
				invoicesCount++;
			} else if (dataObject instanceof InvoiceLine) {
				invoiceLinesCount++;
			} else if (dataObject instanceof Enrolment && EnrolmentStatus.IN_TRANSACTION.equals(((Enrolment) dataObject).getStatus())) {
				enrollmentsCount++;
			}
		}
		assertEquals("2 in transaction enrollments should be uncommited", 2, enrollmentsCount);
		assertEquals("2 invoicelines should be uncommited", 2, invoiceLinesCount);
		assertEquals("1 invoice should be uncommited", 1, invoicesCount);
		assertEquals("1 paymentinline should be uncommited", 1, paymentInLinesCount);
		assertEquals("1 paymentin should be uncommited", 1, paymentsCount);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessPaymentWithFailedCardDeclinedResultForSingleUserAndSingleClass() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L);
		IStudentService studentService = prepareStudentServiceForSingleUser(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		setupThePaymentInDataBeforeProcessPayment(testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds));
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		controller.getModel().getPayment().setCreditCardType(CreditCardType.VISA);
		controller.processPayment(Collections.EMPTY_LIST);
		
		assertFalse("After payment processing finiahed we should not checkout the result", controller.isCheckoutResult());
		assertEquals("Payment should fail", PaymentStatus.FAILED_CARD_DECLINED, controller.getModel().getPayment().getStatus());
		assertEquals("Failed payment amount should be equial to invoice amount owing", controller.getModel().getPayment().getAmount(), 
			controller.getModel().getInvoice().getAmountOwing());
		assertEquals("Failed payment amount should be 500$", new Money("500.00"), controller.getModel().getPayment().getAmount());
		assertTrue("When payment fails this mean that at least the payment will have not-commited modification", 
			!controller.getContext().uncommittedObjects().isEmpty());
		assertEquals("5 entities should have uncommited local modifications", 5, controller.getContext().uncommittedObjects().size());
		int enrollmentsCount = 0, invoiceLinesCount = 0,invoicesCount = 0, paymentInLinesCount = 0, paymentsCount = 0;
		for (Object object : controller.getContext().uncommittedObjects()) {
			CayenneDataObject dataObject = (CayenneDataObject) object;
			if (dataObject instanceof PaymentIn) {
				paymentsCount++;
			} else if (dataObject instanceof PaymentInLine) {
				paymentInLinesCount++;
			} else if (dataObject instanceof Invoice) {
				invoicesCount++;
			} else if (dataObject instanceof InvoiceLine) {
				invoiceLinesCount++;
			} else if (dataObject instanceof Enrolment && EnrolmentStatus.IN_TRANSACTION.equals(((Enrolment) dataObject).getStatus())) {
				enrollmentsCount++;
			}
		}
		assertEquals("1 in transaction enrollments should be uncommited", 1, enrollmentsCount);
		assertEquals("1 invoicelines should be uncommited", 1, invoiceLinesCount);
		assertEquals("1 invoice should be uncommited", 1, invoicesCount);
		assertEquals("1 paymentinline should be uncommited", 1, paymentInLinesCount);
		assertEquals("1 paymentin should be uncommited", 1, paymentsCount);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessPaymentWithSuccessResult() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L, 11869600L);
		IStudentService studentService = prepareStudentService(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		setupThePaymentInDataBeforeProcessPayment(testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds));
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		controller.getModel().getPayment().setCreditCardType(CreditCardType.MASTERCARD);
		controller.processPayment(Collections.EMPTY_LIST);
		
		assertFalse("After payment processing finiahed we should not checkout the result", controller.isCheckoutResult());
		assertEquals("Payment should fail", PaymentStatus.SUCCESS, controller.getModel().getPayment().getStatus());
		assertEquals("Success payment amount should be 1200$", new Money("1200.00"), controller.getModel().getPayment().getAmount());
		assertEquals("Success payment amount should be zero", Money.ZERO, controller.getModel().getInvoice().getAmountOwing());
		assertTrue("When payment success this mean that everything should be commited", controller.getContext().uncommittedObjects().isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessPaymentWithSuccessResultForSingleUser() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869580L, 11869600L);
		IStudentService studentService = prepareStudentServiceForSingleUser(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		setupThePaymentInDataBeforeProcessPayment(testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds));
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		controller.getModel().getPayment().setCreditCardType(CreditCardType.MASTERCARD);
		controller.processPayment(Collections.EMPTY_LIST);
		
		assertFalse("After payment processing finiahed we should not checkout the result", controller.isCheckoutResult());
		assertEquals("Payment should fail", PaymentStatus.SUCCESS, controller.getModel().getPayment().getStatus());
		assertEquals("Success payment amount should be 600$", new Money("600.00"), controller.getModel().getPayment().getAmount());
		assertEquals("Success payment amount should be zero", Money.ZERO, controller.getModel().getInvoice().getAmountOwing());
		assertTrue("When payment success this mean that everything should be commited", controller.getContext().uncommittedObjects().isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testProcessPaymentWithSuccessResultForSingleUserAndSingleClass() {
		EnrolCoursesController controller = prepareNewController();
		List<Long> orderedClassesIds = Arrays.asList(11869600L);
		IStudentService studentService = prepareStudentServiceForSingleUser(controller);
		int studentsCount = studentService.getStudentsFromShortList().size();
		setupThePaymentInDataBeforeProcessPayment(testBeforeRenderInitPreparement(studentService, controller, orderedClassesIds));
		assertNotNull("Payment for controller should be not null", controller.getModel().getPayment());
		assertNotNull("Invoice for controller should be not null", controller.getModel().getInvoice());
		assertNotNull(studentsCount + " contacts should be linked with this controller", controller.getModel().getContacts());
		assertEquals(studentsCount + " contacts should be linked with this controller", studentsCount, controller.getModel().getContacts().size());
		assertNotNull("InvoiceLines should not be empty for this controller", controller.getModel().getInvoiceLines());
		assertNotNull("Enrolments should not be empty for this controller", controller.getModel().getEnrolments());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be linked with this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsList().size());
		assertEquals(studentsCount * orderedClassesIds.size() + " enrollments should be ready to persist for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getEnrolmentsToPersist(controller.getEnrolmentsList()).size());
		assertEquals(studentsCount * orderedClassesIds.size() + " invoicelenes should be linked to the invoice for this controller", 
			studentsCount * orderedClassesIds.size(), 
			controller.getInvoiceLinesToPersist(Collections.EMPTY_LIST).size());
		controller.getModel().getPayment().setCreditCardType(CreditCardType.MASTERCARD);
		controller.processPayment(Collections.EMPTY_LIST);
		
		assertFalse("After payment processing finiahed we should not checkout the result", controller.isCheckoutResult());
		assertEquals("Payment should fail", PaymentStatus.SUCCESS, controller.getModel().getPayment().getStatus());
		assertEquals("Success payment amount should be 100$", new Money("100.00"), controller.getModel().getPayment().getAmount());
		assertEquals("Success payment amount should be zero", Money.ZERO, controller.getModel().getInvoice().getAmountOwing());
		assertTrue("When payment success this mean that everything should be commited", controller.getContext().uncommittedObjects().isEmpty());
	}
}
