package ish.oncourse.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.payment.PaymentInAbandonUtil;

public class PaymentInAbandonUtilTest extends ServiceTest {
private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);
		InputStream st = PaymentInAbandonUtilTest.class.getClassLoader().getResourceAsStream("ish/oncourse/util/paymentInAbandonUtilDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMultipleEnrollmentsExpireAbandon() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class,
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 20L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 100", new Money("100.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 2 records", 2, invoice.getInvoiceLines().size());

		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class,
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		assertNotNull("Course class should be loaded", courseClass);

		for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
			assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
			//prepare and add the enrollment to the invoiceLine
			Enrolment enrolment = context.newObject(Enrolment.class);
			enrolment.setCollege(paymentIn.getCollege());
			enrolment.setCourseClass(courseClass);
			enrolment.setSource(paymentIn.getSource());
			enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
			if (invoiceLine.getId() == 20L) {
				enrolment.setStudent(paymentIn.getStudent());
				assertEquals("This student should have id=4", 4L, enrolment.getStudent().getId().longValue());
			} else if (invoiceLine.getId() == 21L) {
				Student student3 = (Student) context.performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 3L))).get(0);
				assertNotNull("Student with id 3 should be loaded", student3);
				enrolment.setStudent(student3);
			}
			enrolment.setReasonForStudy(1);
			invoiceLine.setEnrolment(enrolment);
		}
		context.commitChanges();

		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class,
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 20L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 100", new Money("100.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 2 records", 2, invoice.getInvoiceLines().size());

		//emulate run abandon by system
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, true);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class,
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn reversePaymentIn = paymentIns.get(0);
		assertNotNull("Reverse payment should exist for this abandon", reversePaymentIn);
		Invoice reverseInvoice = null;
		for (PaymentInLine line : reversePaymentIn.getPaymentInLines()) {
			if (!line.getInvoice().getId().equals(invoice.getId())) {
				reverseInvoice = line.getInvoice();
			}
		}
		assertNotNull("Reverse invoice should exist for this abandon", reverseInvoice);
		assertEquals("InvoiceLines list should have 2 records for reverse invoice", 2, reverseInvoice.getInvoiceLines().size());
		for (InvoiceLine invoiceLine : reverseInvoice.getInvoiceLines()) {
			assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
			Enrolment enrolment = invoiceLine.getEnrolment();
			assertNull("enrollment should not be linked with invoicelines for reverse invoice", enrolment);
			//TODO: update this test when reverse invoice lines became linked with reversed enrollment
		}
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.INTERNAL, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 2 records", 2, invoice.getInvoiceLines().size());
		for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
			assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
			Enrolment enrolment = invoiceLine.getEnrolment();
			assertNotNull("enrollment should be linked with invoicelines after reverse", enrolment);
			assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInTransactionEnrollManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Initial enrollment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		//emulate run abandon by user
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, true);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn reversePaymentIn = paymentIns.get(0);
		assertNotNull("Reverse payment should exist for this abandon", reversePaymentIn);
		Invoice reverseInvoice = null;
		for (PaymentInLine line : reversePaymentIn.getPaymentInLines()) {
			if (line.getInvoice() != invoice) {
				reverseInvoice = line.getInvoice();
			}
		}
		assertNotNull("Reverse invoice should exist for this abandon", reverseInvoice);
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.INTERNAL, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInTransactionEnrollNotManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Initial enrollment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		//emulate run abandon by expire job
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, false);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertTrue("Payments list should be empty", paymentIns.isEmpty());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be equal to original invoice amount", invoiceLine.getFinalPriceToPayIncTax(), 
			invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSuccessEnrollManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.SUCCESS);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Initial enrollment status should be in success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
		//emulate run abandon by user
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, true);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertTrue("Payments list should be empty", paymentIns.isEmpty());
		
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be original", new Money("120.00"), invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSuccessEnrollNotManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.SUCCESS);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Initial enrollment status should be success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
		//emulate run abandon by expire job
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, false);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertTrue("Payments list should  be empty", paymentIns.isEmpty());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be original", new Money("120.00"), invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testManuallyAbandonPaymentReverseInvoiceForManuallInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		context.commitChanges();
		
		//emulate run abandon by user
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, true);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn reversePaymentIn = paymentIns.get(0);
		assertNotNull("Reverse payment should exist for this abandon", reversePaymentIn);
		Invoice reverseInvoice = null;
		for (PaymentInLine line : reversePaymentIn.getPaymentInLines()) {
			if (line.getInvoice() != invoice) {
				reverseInvoice = line.getInvoice();
			}
		}
		assertNotNull("Reverse invoice should exist for this abandon", reverseInvoice);
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.INTERNAL, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNotManuallyAbandonPaymentReverseInvoiceForManuallInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		context.commitChanges();
		
		//emulate run abandon by expire job
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, false);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertTrue("Payments list should be empty", paymentIns.isEmpty());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 120", new Money("120.00"), invoice.getAmountOwing());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testActiveVoucherManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment = invoiceLine.getEnrolment();
		
		//link voucher for invoiceline
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Payment status should be in transaction", ProductStatus.ACTIVE, voucher.getStatus());
		voucher.setStatus(ProductStatus.NEW);
		voucher.setInvoiceLine(invoiceLine);
		//unlink the enrollment
		invoiceLine.setEnrolment(null);
		context.commitChanges();
		
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		assertEquals("Only 1 voucher should be linked with this invoiceline", 1, invoiceLine.getVouchers().size());
		voucher = invoiceLine.getVouchers().get(0);
		assertNotNull("Voucher should be linked with this invoiceLine", voucher);
		assertEquals("Voucher status should be new", ProductStatus.NEW, voucher.getStatus());
		
		//emulate run abandon by user
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, true);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn reversePaymentIn = paymentIns.get(0);
		assertNotNull("Reverse payment should exist for this abandon", reversePaymentIn);
		Invoice reverseInvoice = null;
		for (PaymentInLine line : reversePaymentIn.getPaymentInLines()) {
			if (line.getInvoice() != invoice) {
				reverseInvoice = line.getInvoice();
			}
		}
		assertNotNull("Reverse invoice should exist for this abandon", reverseInvoice);
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.INTERNAL, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
		//assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testActiveVoucherNotManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment = invoiceLine.getEnrolment();
		
		//link voucher for invoiceline
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Payment status should be in transaction", ProductStatus.ACTIVE, voucher.getStatus());
		voucher.setStatus(ProductStatus.NEW);
		voucher.setInvoiceLine(invoiceLine);
		//unlink the enrollment
		invoiceLine.setEnrolment(null);
		context.commitChanges();
		
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		assertEquals("Only 1 voucher should be linked with this invoiceline", 1, invoiceLine.getVouchers().size());
		voucher = invoiceLine.getVouchers().get(0);
		assertNotNull("Voucher should be linked with this invoiceLine", voucher);
		assertEquals("Voucher status should be new", ProductStatus.NEW, voucher.getStatus());
		
		//emulate run abandon by expire job
		PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn, false);
		//re-load data
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.lessDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertTrue("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be the equal to original invoice owing", invoiceLine.getFinalPriceToPayIncTax(), 
			invoice.getAmountOwing());
		//assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
	}

}
