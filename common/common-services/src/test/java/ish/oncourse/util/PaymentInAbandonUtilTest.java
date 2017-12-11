package ish.oncourse.util;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.payment.PaymentInAbandon;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder;
import ish.oncourse.util.payment.PaymentInSucceed;
import ish.oncourse.utils.PaymentInUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentInAbandonUtilTest extends ServiceTest {
private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/util/paymentInAbandonUtilDataSet.xml").load(testContext.getDS());
		cayenneService = getService(ICayenneService.class);
	}

	@Test
	public void testMultipleEnrollmentsExpireAbandon() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 20);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 100", new Money("100.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 2 records", 2, invoice.getInvoiceLines().size());

		//load courseclass for enrolment

		CourseClass courseClass = (CourseClass) context.performQuery(
				SelectQuery.query(CourseClass.class,ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
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
				enrolment.setStudent(paymentIn.getContact().getStudent());
				assertEquals("This student should have id=4", 4L, enrolment.getStudent().getId().longValue());
			} else if (invoiceLine.getId() == 21L) {
				Student student3 = (Student) context.performQuery(
						SelectQuery.query(Student.class,ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 3L))).get(0);
				assertNotNull("Student with id 3 should be loaded", student3);
				enrolment.setStudent(student3);
			}
			enrolment.setReasonForStudy(1);
			invoiceLine.setEnrolment(enrolment);
		}
		context.commitChanges();

		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 20);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 100", new Money("100.00"), invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 2 records", 2, invoice.getInvoiceLines().size());

		//emulate run abandon by system
		abandonPayment(paymentIn);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		PaymentIn reversePaymentIn = Cayenne.objectForPK(context, PaymentIn.class, 1);
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
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
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

	@Test
	public void testInTransactionEnrollManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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

		CourseClass courseClass = (CourseClass) context.performQuery(
				SelectQuery.query(CourseClass.class,ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getContact().getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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

		abandonPayment(paymentIn);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		PaymentIn reversePaymentIn = Cayenne.objectForPK(context, PaymentIn.class, 1);
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
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
	}

	private void abandonPayment(PaymentIn paymentIn) {
		abandonPayment(paymentIn,  keepInvoice(paymentIn));
	}

	private void abandonPayment(PaymentIn paymentIn, boolean keepInvoice) {
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(paymentIn).build().getModel();
		PaymentInAbandon.valueOf(model, keepInvoice).perform();
		paymentIn.getObjectContext().commitChanges();
	}


	@Test
	public void testInTransactionEnrollNotManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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

		CourseClass courseClass = (CourseClass) context.performQuery(
				SelectQuery.query(CourseClass.class,ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getContact().getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		abandonPayment(paymentIn, true);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		assertNull("Payments list should be empty", Cayenne.objectForPK(context, PaymentIn.class, 1));
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be equal to original invoice amount", invoiceLine.getFinalPriceToPayIncTax(), 
			invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}

	@Test
	public void testSuccessEnrollManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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

		CourseClass courseClass = (CourseClass) context.performQuery(
				SelectQuery.query(CourseClass.class,ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.SUCCESS);
		enrolment.setStudent(paymentIn.getContact().getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		abandonPayment(paymentIn);
		//re-load data

		assertNull("Payments list should be empty", Cayenne.objectForPK(context, PaymentIn.class, 1));
		
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be original", new Money("120.00"), invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}

	@Test
	public void testSuccessEnrollNotManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1);

		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = context.newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.SUCCESS);
		enrolment.setStudent(paymentIn.getContact().getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		abandonPayment(paymentIn, true);
		//re-load data
		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		assertNull("Payments list should be empty", Cayenne.objectForPK(context, PaymentIn.class, 1));
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be original", new Money("120.00"), invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}

	@Test
	public void testManuallyAbandonPaymentReverseInvoiceForManuallInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"), invoice.getAmountOwing());
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		context.commitChanges();
		
		//emulate run abandon by user
		abandonPayment(paymentIn);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		PaymentIn reversePaymentIn = Cayenne.objectForPK(context, PaymentIn.class, 1);

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
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
	}

	@Test
	public void testNotManuallyAbandonPaymentReverseInvoiceForManuallInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"), invoice.getAmountOwing());
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		context.commitChanges();
		
		//emulate run abandon by expire job
		abandonPayment(paymentIn, true);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		assertNull("Payments list should be empty", Cayenne.objectForPK(context, PaymentIn.class, 1));
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 120", new Money("120.00"), invoice.getAmountOwing());
	}

	@Test
	public void testActiveVoucherManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment;
		
		//link voucher for invoiceline
		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 2);
		assertNotNull("Voucher for test should not be empty", voucher);
		voucher.setInvoiceLine(invoiceLine);
		//unlink the enrollment
		invoiceLine.setEnrolment(null);
		context.commitChanges();
		
		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		assertEquals("Only 1 voucher should be linked with this invoiceline", 1, invoiceLine.getProductItems().size());
		voucher = (Voucher) invoiceLine.getProductItems().get(0);
		assertNotNull("Voucher should be linked with this invoiceLine", voucher);
		assertEquals("Voucher status should be new", ProductStatus.NEW, voucher.getStatus());
		
		//emulate run abandon by user
		abandonPayment(paymentIn);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		PaymentIn reversePaymentIn = Cayenne.objectForPK(context, PaymentIn.class, 1);
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
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		reverseInvoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, reverseInvoice.getAmountOwing());
		//assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
	}

	private boolean keepInvoice(PaymentIn paymentIn) {
		return PaymentInUtil.hasSuccessEnrolments(paymentIn) || PaymentInUtil.hasSuccessProductItems(paymentIn);
	}

	@Test
	public void testActiveVoucherNotManuallyAbandonPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		PaymentIn paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment;
		
		//link voucher for invoiceline
		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 2);
		assertNotNull("Voucher for test should not be empty", voucher);
		voucher.setInvoiceLine(invoiceLine);
		//unlink the enrollment
		invoiceLine.setEnrolment(null);
		context.commitChanges();
		
		//re-load data
		paymentIn = Cayenne.objectForPK(context, PaymentIn.class, 200);
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
		assertEquals("Only 1 voucher should be linked with this invoiceline", 1, invoiceLine.getProductItems().size());
		voucher = (Voucher) invoiceLine.getProductItems().get(0);
		assertNotNull("Voucher should be linked with this invoiceLine", voucher);
		assertEquals("Voucher status should be new", ProductStatus.NEW, voucher.getStatus());
		
		//emulate run abandon by expire job
		abandonPayment(paymentIn, true);
		//re-load data

		// this logic unreliably assumes that db generated PK equal to 1 for new payment
		// probably this should be replaced with something more certain
		assertNull("Payments list should be empty", Cayenne.objectForPK(context, PaymentIn.class, 1));
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be the equal to original invoice owing", invoiceLine.getFinalPriceToPayIncTax(), 
			invoice.getAmountOwing());
		//assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
	}

	@Test
	public void testFailVoucherPaymentReverseInvoice() {
		ObjectContext context = cayenneService.newContext();

		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 5);

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 4);

		PaymentIn moneyPayment = Cayenne.objectForPK(context, PaymentIn.class, 400);
		PaymentIn voucherPayment = Cayenne.objectForPK(context, PaymentIn.class, 500);

		assertEquals(ProductStatus.REDEEMED, voucher.getStatus());
		assertEquals(Money.ZERO, voucher.getValueRemaining());

		assertEquals(2, invoice.getPaymentInLines().size());
		assertEquals(1, invoice.getInvoiceLines().size());
		assertEquals(new Money("120.0"), invoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(new Money("50.0"), moneyPayment.getAmount());
		assertEquals(new Money("70.0"), voucherPayment.getAmount());

		assertEquals(PaymentStatus.IN_TRANSACTION, moneyPayment.getStatus());
		assertEquals(PaymentStatus.IN_TRANSACTION, voucherPayment.getStatus());

		abandonPayment(moneyPayment, false);

		assertEquals(ProductStatus.ACTIVE, voucher.getStatus());
		assertEquals(new Money("70.0"), voucher.getValueRemaining());

		assertEquals(3, invoice.getPaymentInLines().size());
		assertEquals(1, invoice.getInvoiceLines().size());
		assertEquals(new Money("120.0"), invoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(new Money("50.0"), moneyPayment.getAmount());
		assertEquals(new Money("70.0"), voucherPayment.getAmount());

		assertEquals(PaymentStatus.FAILED, moneyPayment.getStatus());
		assertEquals(PaymentStatus.FAILED, voucherPayment.getStatus());

		assertEquals(Money.ZERO, invoice.getAmountOwing());

		SelectQuery<Invoice> query = SelectQuery.query(Invoice.class);
		query.addOrdering(new Ordering(Invoice.INVOICE_DATE.getName(), SortOrder.DESCENDING));

		Invoice refundInvoice = (Invoice) context.performQuery(query).get(0);

		assertNotEquals(invoice, refundInvoice);

		assertEquals(1, refundInvoice.getInvoiceLines().size());
		assertEquals(new Money("-120.0"), refundInvoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(1, refundInvoice.getPaymentInLines().size());

		List<PaymentInLine> refundPaymentLines = refundInvoice.getPaymentInLines();

		Ordering.orderList(refundPaymentLines, Collections.singletonList(new Ordering(PaymentInLine.AMOUNT.getName(), SortOrder.ASCENDING)));

		assertEquals(new Money("-120.0"), refundInvoice.getPaymentInLines().get(0).getAmount());

		PaymentIn reversePayment = refundInvoice.getPaymentInLines().get(0).getPaymentIn();

		assertEquals(PaymentType.REVERSE, reversePayment.getType());

		assertEquals(PaymentStatus.SUCCESS, reversePayment.getStatus());
	}

	@Test
	public void testFailVoucherPaymentKeepInvoice() {
		ObjectContext context = cayenneService.newContext();

		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 5);

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 4);

		PaymentIn moneyPayment = Cayenne.objectForPK(context, PaymentIn.class, 400);
		PaymentIn voucherPayment = Cayenne.objectForPK(context, PaymentIn.class, 500);

		assertEquals(ProductStatus.REDEEMED, voucher.getStatus());
		assertEquals(Money.ZERO, voucher.getValueRemaining());

		assertEquals(2, invoice.getPaymentInLines().size());
		assertEquals(1, invoice.getInvoiceLines().size());
		assertEquals(new Money("120.0"), invoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(new Money("50.0"), moneyPayment.getAmount());
		assertEquals(new Money("70.0"), voucherPayment.getAmount());

		assertEquals(PaymentStatus.IN_TRANSACTION, moneyPayment.getStatus());
		assertEquals(PaymentStatus.IN_TRANSACTION, voucherPayment.getStatus());

		abandonPayment(moneyPayment, true);

		assertEquals(ProductStatus.ACTIVE, voucher.getStatus());
		assertEquals(new Money("70.0"), voucher.getValueRemaining());

		assertEquals(2, invoice.getPaymentInLines().size());
		assertEquals(1, invoice.getInvoiceLines().size());
		assertEquals(new Money("120.0"), invoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(new Money("50.0"), moneyPayment.getAmount());
		assertEquals(new Money("70.0"), voucherPayment.getAmount());

		assertEquals(PaymentStatus.FAILED, moneyPayment.getStatus());
		assertEquals(PaymentStatus.FAILED, voucherPayment.getStatus());

		assertEquals(new Money("120.0"), invoice.getAmountOwing());
	}

	@Test
	public void testSucceedVoucherPayment() {
		ObjectContext context = cayenneService.newContext();

		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 5);

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 4);

		PaymentIn moneyPayment = Cayenne.objectForPK(context, PaymentIn.class, 400);
		PaymentIn voucherPayment = Cayenne.objectForPK(context, PaymentIn.class, 500);

		assertEquals(ProductStatus.REDEEMED, voucher.getStatus());
		assertEquals(Money.ZERO, voucher.getValueRemaining());

		assertEquals(2, invoice.getPaymentInLines().size());
		assertEquals(1, invoice.getInvoiceLines().size());
		assertEquals(new Money("120.0"), invoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(new Money("50.0"), moneyPayment.getAmount());
		assertEquals(new Money("70.0"), voucherPayment.getAmount());

		assertEquals(PaymentStatus.IN_TRANSACTION, moneyPayment.getStatus());
		assertEquals(PaymentStatus.IN_TRANSACTION, voucherPayment.getStatus());

		// succeed payment
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(moneyPayment).build().getModel();
		PaymentInSucceed.valueOf(model).perform();
		assertTrue(model.getPaymentIn().getStatus() == PaymentStatus.SUCCESS);

		assertEquals(ProductStatus.REDEEMED, voucher.getStatus());
		assertEquals(Money.ZERO, voucher.getValueRemaining());

		assertEquals(2, invoice.getPaymentInLines().size());
		assertEquals(1, invoice.getInvoiceLines().size());
		assertEquals(new Money("120.0"), invoice.getInvoiceLines().get(0).getPriceTotalIncTax());

		assertEquals(new Money("50.0"), moneyPayment.getAmount());
		assertEquals(new Money("70.0"), voucherPayment.getAmount());

		assertEquals(PaymentStatus.SUCCESS, moneyPayment.getStatus());
		assertEquals(PaymentStatus.SUCCESS, voucherPayment.getStatus());

		assertEquals(Money.ZERO, invoice.getAmountOwing());
	}
}
