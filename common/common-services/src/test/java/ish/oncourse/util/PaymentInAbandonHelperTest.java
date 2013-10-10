package ish.oncourse.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.VoucherPaymentStatus;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.model.VoucherProductCourse;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.util.ProductUtil;

public class PaymentInAbandonHelperTest extends ServiceTest {
	private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);
		InputStream st = PaymentInAbandonHelperTest.class.getClassLoader().getResourceAsStream("ish/oncourse/util/paymentInAbandonHelperDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void testCreateHelper() {
		PaymentInAbandonHelper helper = null;
		//try to init helper with incorrect arguments
		boolean illegalArgumentThrown = false;
		try {
			helper = new PaymentInAbandonHelper(null, false);
		} catch (Exception e) {
			illegalArgumentThrown = true;
		}
		assertTrue("Create helper without payment should throw an exception!", illegalArgumentThrown);
		assertNull("Helper should not be created after exception thrown", helper);
		
		//init with correct arguments for abandon payment 
		illegalArgumentThrown = false;
		try {
			helper = new PaymentInAbandonHelper(mock(PaymentIn.class), false);
		} catch (Exception e) {
			illegalArgumentThrown = true;
		}
		assertFalse("Create helper with payment should not throw an exception!", illegalArgumentThrown);
		assertNotNull("Helper should be inited", helper);
		
		//init with correct arguments for abandon payment keep invoice
		illegalArgumentThrown = false;
		try {
			helper = new PaymentInAbandonHelper(mock(PaymentIn.class), true);
		} catch (Exception e) {
			illegalArgumentThrown = true;
		}
		assertFalse("Create helper with payment should not throw an exception!", illegalArgumentThrown);
		assertNotNull("Helper should be inited", helper);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testValidatePaymentInForAbandon() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		//create helper for abandon
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		
		//re-create the helper with success paymentIn for abandon
		paymentIn.setStatus(PaymentStatus.SUCCESS);
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("Success payment should not pass the validation", helper.validatePaymentInForAbandon());
		context.rollbackChanges();
		//re-load payment because we have set status validation
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		paymentIn = paymentIns.get(0);
		paymentIn.setStatus(PaymentStatus.CARD_DETAILS_REQUIRED);
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("Card details required payment should not pass the validation", helper.validatePaymentInForAbandon());
		context.rollbackChanges();
		paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("Failed no places payment should not pass the validation", helper.validatePaymentInForAbandon());
		context.rollbackChanges();
		//re-load payment because we have set status validation
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		paymentIn = paymentIns.get(0);
		paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("Failed card declined payment should not pass the validation", helper.validatePaymentInForAbandon());
		context.rollbackChanges();
		//re-load payment because we have set status validation
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		paymentIn = paymentIns.get(0);
		paymentIn.setStatus(PaymentStatus.FAILED);
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("Failed payment should not pass the validation", helper.validatePaymentInForAbandon());
		context.rollbackChanges();
		//check that paymentIn have paymentInLines
		paymentIn = mock(PaymentIn.class);
		when(paymentIn.getPaymentInLines()).thenReturn(Collections.EMPTY_LIST);
		when(paymentIn.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("Payment without paymentInLines should not pass the validation", helper.validatePaymentInForAbandon());
		
		//check the state when paymentIn in 'in transaction' status and have no invoice amount owing
		Invoice in1 = mock(Invoice.class);
		InvoiceLine il1 = mock(InvoiceLine.class);
		when(il1.getFinalPriceToPayIncTax()).thenReturn(Money.ZERO);
		when(in1.getInvoiceLines()).thenReturn(Arrays.asList(il1));
		PaymentInLine pil1 = mock(PaymentInLine.class);
		when(pil1.getInvoice()).thenReturn(in1);
		when(in1.getPaymentInLines()).thenReturn(Arrays.asList(pil1));
		when(in1.getAmountOwing()).thenReturn(Money.ZERO);
		paymentIn = mock(PaymentIn.class);
		when(paymentIn.getPaymentInLines()).thenReturn(Arrays.asList(pil1));
		when(pil1.getPaymentIn()).thenReturn(paymentIn);
		//here can be only valid status = in transaction, 
		//all other cases already checked before 
		when(paymentIn.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
		helper = new PaymentInAbandonHelper(paymentIn, false) {
			@Override
			boolean canMakeRevertInvoice() {
				//should be false for this case
				return false;
			}
			
		};
		assertFalse("Payment without invoice amount owing should not pass the validation", helper.validatePaymentInForAbandon());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testContainInvoicesWithoutEnrolOrProductLinks() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		//load actual invoiceLine
		List<InvoiceLine> invoiceLines = context.performQuery(new SelectQuery(InvoiceLine.class,
			ExpressionFactory.matchDbExp(InvoiceLine.ID_PK_COLUMN, 5L)));
		assertFalse("InvoiceLines list should not be empty", invoiceLines.isEmpty());
		assertEquals("InvoiceLines list should have 1 record", 1, invoiceLines.size());
		InvoiceLine invoiceLine = invoiceLines.get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Invoice invoice = invoiceLine.getInvoice();
		invoice.updateAmountOwing();
		context.commitChanges();
		
		//create helper for abandon
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("This payment contain invoices with not 0 amount owing", helper.containInvoicesWithoutEnrolOrProductLinks());
		
		invoiceLine.setPriceEachExTax(Money.ZERO);
		invoiceLine.setTaxEach(Money.ZERO);
		invoice.updateAmountOwing();
		context.commitChanges();
		
		assertFalse("This payment not contain invoices with not 0 amount owing", helper.containInvoicesWithoutEnrolOrProductLinks());
		
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
				
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked productitems in new status", helper.containNewProductItems());
		assertFalse("This payment not contain invoices with not 0 amount owing", helper.containInvoicesWithoutEnrolOrProductLinks());
		
		//update voucher for test
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 4L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
		
		voucher.setStatus(ProductStatus.NEW);
		voucher.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), voucher.getVoucherProduct().getExpiryType(), 
				voucher.getVoucherProduct().getExpiryDays()));
		context.commitChanges();
		
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("This payment now contain linked productitems in new status", helper.containNewProductItems());
		assertFalse("This payment not contain invoices with not 0 amount owing", helper.containInvoicesWithoutEnrolOrProductLinks());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testContainEnrollmentsInTransactionStatus1() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			
		//create helper for abandon
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
		
		//load actual invoiceLine
		List<InvoiceLine> invoiceLines = context.performQuery(new SelectQuery(InvoiceLine.class,
			ExpressionFactory.matchDbExp(InvoiceLine.ID_PK_COLUMN, 5L)));
		assertFalse("InvoiceLines list should not be empty", invoiceLines.isEmpty());
		assertEquals("InvoiceLines list should have 1 record", 1, invoiceLines.size());
		InvoiceLine invoiceLine = invoiceLines.get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load payment with already linked enrollment in transaction
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("This payment now contain linked enrollment", helper.containEnrollmentsInTransactionStatus());
		
		//and now change the enrollment status to check that result will be different
		enrolment.setStatus(EnrolmentStatus.SUCCESS);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
		
		enrolment.setStatus(EnrolmentStatus.CANCELLED);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testContainEnrollmentsInTransactionStatus2() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			
		//create helper for abandon
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
		
		//load actual invoiceLine
		List<InvoiceLine> invoiceLines = context.performQuery(new SelectQuery(InvoiceLine.class,
			ExpressionFactory.matchDbExp(InvoiceLine.ID_PK_COLUMN, 5L)));
		assertFalse("InvoiceLines list should not be empty", invoiceLines.isEmpty());
		assertEquals("InvoiceLines list should have 1 record", 1, invoiceLines.size());
		InvoiceLine invoiceLine = invoiceLines.get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load payment with already linked enrollment in transaction
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("This payment now contain linked enrollment", helper.containEnrollmentsInTransactionStatus());
		
		//and now change the enrollment status to check that result will be different
		enrolment.setStatus(EnrolmentStatus.SUCCESS);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
		
		enrolment.setStatus(EnrolmentStatus.REFUNDED);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testContainEnrollmentsInTransactionStatus3() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			
		//create helper for abandon
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());
		
		//load actual invoiceLine
		List<InvoiceLine> invoiceLines = context.performQuery(new SelectQuery(InvoiceLine.class,
			ExpressionFactory.matchDbExp(InvoiceLine.ID_PK_COLUMN, 5L)));
		assertFalse("InvoiceLines list should not be empty", invoiceLines.isEmpty());
		assertEquals("InvoiceLines list should have 1 record", 1, invoiceLines.size());
		InvoiceLine invoiceLine = invoiceLines.get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		Enrolment enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		//re-load payment with already linked enrollment in transaction
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("This payment now contain linked enrollment", helper.containEnrollmentsInTransactionStatus());
		
		//and now change the enrollment status to check that result will be different
		enrolment.setStatus(EnrolmentStatus.FAILED);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked enrollments", helper.containEnrollmentsInTransactionStatus());		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testContainNewProductItems() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			
		//create helper for abandon
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked productitems in new status", helper.containNewProductItems());
		
		//update voucher for test
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 4L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
		
		voucher.setStatus(ProductStatus.NEW);
		voucher.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), voucher.getVoucherProduct().getExpiryType(), 
				voucher.getVoucherProduct().getExpiryDays()));
		context.commitChanges();
		
		paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 1L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("This payment now contain linked productitems in new status", helper.containNewProductItems());
		
		//and now change the voucher status to check that result will be different
		//update voucher for test
		vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 4L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		voucher.setStatus(ProductStatus.ACTIVE);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked productitems in new status", helper.containNewProductItems());
		
		vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 4L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		voucher.setStatus(ProductStatus.CANCELLED);
		context.commitChanges();
		
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked productitems in new status", helper.containNewProductItems());
		
		vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 4L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		voucher.setStatus(ProductStatus.CREDITED);
		context.commitChanges();
			
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked productitems in new status", helper.containNewProductItems());
		
		vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 4L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		voucher.setStatus(ProductStatus.REDEEMED);
		context.commitChanges();
				
		//create helper for abandon
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertFalse("This payment not contain linked productitems in new status", helper.containNewProductItems());
	}
	
	@Test
	public void testCanMakeRevertInvoice() {
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(mock(PaymentIn.class), false) {
			@Override
			boolean containEnrollmentsInTransactionStatus() {return false;}
			@Override
			boolean containNewProductItems() {return false;}
			@Override
			boolean containInvoicesWithoutEnrolOrProductLinks() {return false;}
			
		};
		assertFalse("We should not be able to create reverse invoice if no linked enrollments or vouchers in new status or invoice with not 0 owing", helper.canMakeRevertInvoice());
		
		helper = new PaymentInAbandonHelper(mock(PaymentIn.class), false) {
			@Override
			boolean containEnrollmentsInTransactionStatus() {return true;}
			@Override
			boolean containNewProductItems() {return false;}
			@Override
			boolean containInvoicesWithoutEnrolOrProductLinks() {return false;}
			
		};
		assertTrue("We should be able to create reverse invoice if linked enrollments in new status exist", helper.canMakeRevertInvoice());
		
		helper = new PaymentInAbandonHelper(mock(PaymentIn.class), false) {
			@Override
			boolean containEnrollmentsInTransactionStatus() {return false;}
			@Override
			boolean containNewProductItems() {return true;}
			@Override
			boolean containInvoicesWithoutEnrolOrProductLinks() {return false;}
			
		};
		assertTrue("We should be able to create reverse invoice if linked product items in new status exist", helper.canMakeRevertInvoice());
		
		helper = new PaymentInAbandonHelper(mock(PaymentIn.class), false) {
			@Override
			boolean containEnrollmentsInTransactionStatus() {return true;}
			@Override
			boolean containNewProductItems() {return true;}
			@Override
			boolean containInvoicesWithoutEnrolOrProductLinks() {return false;}
			
		};
		assertTrue("We should be able to create reverse invoice if linked product items and enrollments in new status exist", 
			helper.canMakeRevertInvoice());
		
		helper = new PaymentInAbandonHelper(mock(PaymentIn.class), false) {
			@Override
			boolean containEnrollmentsInTransactionStatus() {return false;}
			@Override
			boolean containNewProductItems() {return false;}
			@Override
			boolean containInvoicesWithoutEnrolOrProductLinks() {return true;}
			
		};
		
		assertTrue("We should be able to create reverse invoice if unbalanced invoices exist", 
				helper.canMakeRevertInvoice());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAbandonPaymentKeepInvoice() {
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
		
		//create helper for abandon keep invoice
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, true);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertNull("After abandon keep invoice no reverse invoice should be created", helper.abandonPaymentKeepInvoice());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("No additional paymentInlines should appears", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		//rollback this keep invoice to check that with enrollment result will be different
		context.rollbackChanges();
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
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
		
		//create helper for abandon keep invoice
		helper = new PaymentInAbandonHelper(paymentIn, true);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertNull("After abandon keep invoice no reverse invoice should be created", helper.abandonPaymentKeepInvoice());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("No additional paymentInlines should appears", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Now enrollment status should be success", EnrolmentStatus.SUCCESS, enrolment.getStatus());
		//rollback this keep invoice to check also the vouchers chnages
		context.rollbackChanges();
		
		//link voucher for invoiceline
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
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
		
		//create helper for abandon keep invoice
		helper = new PaymentInAbandonHelper(paymentIn, true);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertNull("After abandon keep invoice no reverse invoice should be created", helper.abandonPaymentKeepInvoice());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("No additional paymentInlines should appears", 1, paymentIn.getPaymentInLines().size());
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
		assertEquals("Voucher status should be active after abandon keep invoice", ProductStatus.ACTIVE, voucher.getStatus());
		
		context.rollbackChanges();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAbandonPayment() {
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
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		//create helper for abandon 
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertFalse("We can't make reverse invoice for this payment because no linked enrollments or productItems!", helper.canMakeRevertInvoice());
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
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
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		PaymentIn reversePaymentIn = helper.abandonPayment();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", paymentIn.getSessionId(), reversePaymentIn.getSessionId());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
		//rollback the changes to check also the abandon for product items
		context.rollbackChanges();
		
		//link voucher for invoiceline
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
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
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		reversePaymentIn = helper.abandonPayment();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", paymentIn.getSessionId(), reversePaymentIn.getSessionId());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
		
		context.rollbackChanges();
	}
	
	@Test
	public void testFindActiveInvoice() {
		Invoice in1 = mock(Invoice.class);
		when(in1.getInvoiceNumber()).thenReturn(1L);
		when(in1.getId()).thenReturn(10L);
		Invoice in2 = mock(Invoice.class);
		when(in2.getInvoiceNumber()).thenReturn(2L);
		when(in2.getId()).thenReturn(9L);
		InvoiceLine il1 = mock(InvoiceLine.class);
		when(il1.getFinalPriceToPayIncTax()).thenReturn(Money.ZERO);
		when(in1.getInvoiceLines()).thenReturn(Arrays.asList(il1));
		InvoiceLine il2 = mock(InvoiceLine.class);
		when(il2.getFinalPriceToPayIncTax()).thenReturn(Money.ZERO);
		when(in2.getInvoiceLines()).thenReturn(Arrays.asList(il2));
		
		PaymentInLine pil1 = mock(PaymentInLine.class);
		when(pil1.getInvoice()).thenReturn(in1);
		when(in1.getPaymentInLines()).thenReturn(Arrays.asList(pil1));
		PaymentInLine pil2 = mock(PaymentInLine.class);
		when(pil2.getInvoice()).thenReturn(in2);
		when(in2.getPaymentInLines()).thenReturn(Arrays.asList(pil2));
		
		PaymentIn paymentIn = mock(PaymentIn.class);
		when(paymentIn.getPaymentInLines()).thenReturn(Arrays.asList(pil1, pil2));
		when(pil1.getPaymentIn()).thenReturn(paymentIn);
		when(pil2.getPaymentIn()).thenReturn(paymentIn);
		//here can be only valid status = in transaction, 
		//all other cases already checked before 
		when(paymentIn.getStatus()).thenReturn(PaymentStatus.IN_TRANSACTION);
		when(paymentIn.getSource()).thenReturn(PaymentSource.SOURCE_ONCOURSE);
		
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertEquals("Second invoice should be the result because it have higher invoice number", in2, helper.findActiveInvoice());
		
		when(paymentIn.getSource()).thenReturn(PaymentSource.SOURCE_WEB);
		
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertEquals("First invoice should be the result because it have higher id", in1, helper.findActiveInvoice());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateRefundInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		PaymentInLine paymentInLine = paymentIn.getPaymentInLines().get(0);
		Invoice invoice = paymentInLine.getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		//create helper for abandon 
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertFalse("We can't make reverse invoice for this payment because no linked enrollments or productItems!", helper.canMakeRevertInvoice());
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
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
		paymentInLine = paymentIn.getPaymentInLines().get(0);
		invoice = paymentInLine.getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Initial enrollment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		PaymentIn reversePaymentIn = PaymentInAbandonHelper.createRefundPaymentIn(paymentInLine, new Date());
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", paymentIn.getSessionId(), reversePaymentIn.getSessionId());
		assertEquals("Payment status should not change here", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after create refund invoice should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Enrollment status after create refund invoice should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
		//rollback the changes to check also the create refund invoice for product items
		context.rollbackChanges();
		
		//link voucher for invoiceline
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
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
		paymentInLine = paymentIn.getPaymentInLines().get(0);
		invoice = paymentInLine.getInvoice();
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
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		reversePaymentIn = PaymentInAbandonHelper.createRefundPaymentIn(paymentInLine, new Date());
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", paymentIn.getSessionId(), reversePaymentIn.getSessionId());
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
		
		context.rollbackChanges();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRevertTheVoucherRedemption() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		PaymentInLine paymentInLine = paymentIn.getPaymentInLines().get(0);
		Invoice invoice = paymentInLine.getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
		//add enough money for voucher and commit changes
		Money voucherAmount = paymentIn.getAmount();//.add(Money.ONE);
		voucher.setRedemptionValue(voucherAmount);
		context.commitChanges();
		//now we should create the voucher payment in structure and change the payment type
		paymentIn.setType(PaymentType.VOUCHER);
		VoucherPaymentIn voucherPaymentIn = context.newObject(VoucherPaymentIn.class);
		voucherPaymentIn.setPayment(paymentIn);
		voucherPaymentIn.setVoucher(voucher);
		voucherPaymentIn.setCollege(paymentIn.getCollege());
		voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED);
		if (voucherPaymentIn.getEnrolmentsCount() == null && voucher.getVoucherProduct().getMaxCoursesRedemption() != null) {
			voucherPaymentIn.setEnrolmentsCount(0);
		}
		voucher.setRedemptionValue(voucher.getRedemptionValue().subtract(paymentIn.getAmount()));
		if (voucher.isFullyRedeemed()) {
			voucher.setStatus(ProductStatus.REDEEMED);
		}
		
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
		vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher should be redeemed", ProductStatus.REDEEMED, voucher.getStatus());
		assertTrue("The amount on redeemed voucher should be 0", voucher.getRedemptionValue().isZero());
		
		//create helper for abandon keep invoice
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, true);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		helper.revertTheVoucherRedemption();
		assertEquals("Voucher should be still active", ProductStatus.ACTIVE, voucher.getStatus());
		assertEquals("Voucher amount should be the same as before the payment attempt", voucherAmount, voucher.getRedemptionValue());
		//clean relationship for course voucher revert
		context.deleteObjects(voucherPaymentIn);
		context.commitChanges();
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		vouchers = context.performQuery(new SelectQuery(Voucher.class, ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 3L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
			
		VoucherProductCourse voucherProductCourse = context.newObject(VoucherProductCourse.class);
		voucherProductCourse.setCollege(paymentIn.getCollege());
		voucherProductCourse.setCourse(courseClass.getCourse());
		voucherProductCourse.setVoucherProduct(voucher.getVoucherProduct());
		
		assertNull("Voucher should not be linked to any contact because the payer and owner equal", voucher.getContact());
		
		voucherPaymentIn = context.newObject(VoucherPaymentIn.class);
		voucherPaymentIn.setPayment(paymentIn);
		voucherPaymentIn.setVoucher(voucher);
		voucherPaymentIn.setCollege(paymentIn.getCollege());
		voucherPaymentIn.setStatus(VoucherPaymentStatus.APPROVED);
		if (voucherPaymentIn.getEnrolmentsCount() == null && voucher.getVoucherProduct().getMaxCoursesRedemption() != null) {
			voucherPaymentIn.setEnrolmentsCount(0);
		}
		voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() + 1);
		if (voucher.isFullyRedeemed()) {
			voucher.setStatus(ProductStatus.REDEEMED);
		}
		voucherPaymentIn.setEnrolmentsCount(1);
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
		
		vouchers = context.performQuery(new SelectQuery(Voucher.class, ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 3L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be redeemed", ProductStatus.REDEEMED, voucher.getStatus());
		assertNotNull("This voucher should be the course voucher", voucher.getVoucherProduct().getRedemptionCourses());
		assertEquals("This voucher should be the course voucher and have 1 linked course", 1, 
			voucher.getVoucherProduct().getRedemptionCourses().size());
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		helper.revertTheVoucherRedemption();
		assertEquals("Voucher should be still active", ProductStatus.ACTIVE, voucher.getStatus());
		assertEquals("Voucher redeemed course count should be the same as before the payment attempt", Integer.valueOf(0), voucher.getRedeemedCoursesCount());
		//clean relationship for course voucher revert
		context.deleteObjects(voucherPaymentIn);
		context.commitChanges();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMakeAbandonForKeepInvoice() {
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
		
		//create helper for abandon keep invoice
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, true);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertNull("After abandon keep invoice no reverse invoice should be created", helper.makeAbandon());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("No additional paymentInlines should appears", 1, paymentIn.getPaymentInLines().size());
		invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 120$", new Money("120.00"),invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		context.rollbackChanges();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMakeAbandon() {
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
		
		//create helper for abandon 
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		PaymentIn reversePaymentIn = helper.makeAbandon();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", paymentIn.getSessionId(), reversePaymentIn.getSessionId());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
		//rollback the changes to check also the abandon for product items
		context.rollbackChanges();
		
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
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		reversePaymentIn = helper.abandonPayment();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", paymentIn.getSessionId(), reversePaymentIn.getSessionId());
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		assertEquals("Reverse payment should be 0 amount", Money.ZERO, reversePaymentIn.getAmount());
		assertEquals("Reverse payment should be internal", PaymentType.REVERSE, reversePaymentIn.getType());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
		
		context.rollbackChanges();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAbandonFreeInvoice() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		List<PaymentIn> paymentIns = context.performQuery(new SelectQuery(PaymentIn.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Payments list should not be empty", paymentIns.isEmpty());
		assertEquals("Payments list should have 1 record", 1, paymentIns.size());
		PaymentIn paymentIn = paymentIns.get(0);
		assertNotNull("Payment for test should not be empty", paymentIn);
		assertEquals("Payment status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		assertEquals("Only one paymentInline should exist", 1, paymentIn.getPaymentInLines().size());
		//set the zero payment amount
		paymentIn.setAmount(Money.ZERO);
		paymentIn.getPaymentInLines().get(0).setAmount(Money.ZERO);
		
		Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
		//set the zero invoice price
		invoice.setTotalExGst(Money.ZERO);
		invoice.setTotalGst(Money.ZERO);
		
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		invoiceLine.setPriceEachExTax(Money.ZERO);
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoiceLine.setTaxEach(Money.ZERO);
		
		invoice.updateAmountOwing();
		assertEquals("Amount owing for invoice should be 0$", Money.ZERO,invoice.getAmountOwing());
		Enrolment enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		
		//link enrollment to the invoice line
		//load courseclass for enrolment
		CourseClass courseClass = (CourseClass) context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1L))).get(0);
		//prepare and add the enrollment to the invoiceLine
		enrolment = paymentIn.getObjectContext().newObject(Enrolment.class);
		enrolment.setCollege(paymentIn.getCollege());
		enrolment.setCourseClass(courseClass);
		enrolment.setSource(paymentIn.getSource());
		enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrolment.setStudent(paymentIn.getStudent());
		enrolment.setReasonForStudy(1);
		invoiceLine.setEnrolment(enrolment);
		
		context.commitChanges();
		
		//create helper for abandon 
		PaymentInAbandonHelper helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We can make reverse invoice for this payment because no linked enrollments or productItems!", helper.canMakeRevertInvoice());
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
		assertEquals("Amount owing for invoice should be 0$", Money.ZERO,invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNotNull("Now enrollment should be linked to invoice line", enrolment);
		assertEquals("Initial enrollment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		PaymentIn reversePaymentIn = helper.abandonPayment();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be the same as original", paymentIn, reversePaymentIn);
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Enrollment status after abandon should be failed", EnrolmentStatus.FAILED, enrolment.getStatus());
		//rollback the changes to check also the abandon for product items
		context.rollbackChanges();
		
		invoiceLine.setPriceEachExTax(new Money("1.00"));
		invoiceLine.setEnrolment(null);
		invoice.updateAmountOwing();
		paymentIn.setAmount(invoiceLine.getPriceEachExTax());
		paymentIn.getPaymentInLines().get(0).setAmount(paymentIn.getAmount());
		context.commitChanges();
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		reversePaymentIn = helper.abandonPayment();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment status should be success", PaymentStatus.SUCCESS, reversePaymentIn.getStatus());
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		//rollback the changes to check also the abandon for product items
		context.rollbackChanges();
		
		invoiceLine.setPriceEachExTax(Money.ZERO);
		paymentIn.setAmount(invoiceLine.getPriceEachExTax());
		paymentIn.getPaymentInLines().get(0).setAmount(paymentIn.getAmount());
		invoice.updateAmountOwing();
		context.commitChanges();
		
		//link voucher for invoiceline
		List<Voucher> vouchers = context.performQuery(new SelectQuery(Voucher.class, 
			ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, 2L)));
		assertFalse("Vouchers list should not be empty", vouchers.isEmpty());
		assertEquals("Vouchers list should have 1 record", 1, vouchers.size());
		Voucher voucher = vouchers.get(0);
		assertNotNull("Voucher for test should not be empty", voucher);
		assertEquals("Voucher status should be active", ProductStatus.ACTIVE, voucher.getStatus());
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
		assertEquals("Amount owing for invoice should be 0$", Money.ZERO,invoice.getAmountOwing());
		assertEquals("InvoiceLines list should have 1 record", 1, invoice.getInvoiceLines().size());
		invoiceLine = invoice.getInvoiceLines().get(0);
		assertNotNull("InvoiceLine for test should not be empty", invoiceLine);
		enrolment = invoiceLine.getEnrolment();
		assertNull("No enrollment should be linked to invoice line", enrolment);
		assertEquals("Only 1 voucher should be linked with this invoiceline", 1, invoiceLine.getVouchers().size());
		voucher = invoiceLine.getVouchers().get(0);
		assertNotNull("Voucher should be linked with this invoiceLine", voucher);
		assertEquals("Voucher status should be new", ProductStatus.NEW, voucher.getStatus());
		
		//create helper for abandon 
		helper = new PaymentInAbandonHelper(paymentIn, false);
		assertTrue("In transaction payment with correct structure should pass the validation", helper.validatePaymentInForAbandon());
		assertTrue("We should be able to make reverse invoice for this payment!", helper.canMakeRevertInvoice());
		reversePaymentIn = helper.abandonPayment();
		assertNotNull("Reverse invoice should not be null", reversePaymentIn);
		assertEquals("Payment should be failed", PaymentStatus.FAILED, paymentIn.getStatus());
		assertEquals("Reverse payment should be the same as original", paymentIn, reversePaymentIn);
		invoice.updateAmountOwing();
		assertEquals("Amount owing after abandon should be 0", Money.ZERO, invoice.getAmountOwing());
		assertEquals("Voucher status after abandon should be failed", ProductStatus.CANCELLED, voucher.getStatus());
		
		context.rollbackChanges();
	}
	
}
