package ish.oncourse.webservices.soap.v4;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentPortTypeTest extends ServiceTest {

	private Date dueDate;
	private Date today;

	private DataSource onDataSource;

	private ICayenneService cayenneService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);

		cayenneService = getService(ICayenneService.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/paymentDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 3);
		this.dueDate = cal.getTime();
		this.today = new Date();
	}

	@Test
	public void testV4ProcessCreditCardPayment() throws Exception {
		testV4CreditCardPaymentProcessing();
	}
	
	@Test
	public void testV5ProcessCreditCardPayment() throws Exception {
		testV5CreditCardPaymentProcessing();
	}
	
	private GenericTransactionGroup prepareV4CreditCardPaymentData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private GenericTransactionGroup prepareV5CreditCardPaymentData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub = enrolmentV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = paymentInV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV5();
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub = contactV5();
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub = studentV5();
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private void testV4CreditCardPaymentProcessing() throws InternalReplicationFault {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV4CreditCardPaymentData());
		testCreditCardPaymentProcessing(respGroup);
	}
	
	private void testCreditCardPaymentProcessing(GenericTransactionGroup respGroup) {
		assertNotNull("Check Response Group is not null", respGroup);
		GenericEnrolmentStub respEnrolStub = null;
		GenericPaymentInStub respPaymentInStub = null;
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (GenericEnrolmentStub) stub;
				assertEquals("Check enrolment status.", "IN_TRANSACTION", respEnrolStub.getStatus());
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
				assertEquals("Check payment status.", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), respPaymentInStub.getStatus());
				assertNotNull("Check if sessionId is set.", respPaymentInStub.getSessionId());
			}
		}
		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		//assertEquals("Check enrolment status.", "IN_TRANSACTION", respEnrolStub.getStatus());
		//assertEquals("Check payment status.", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), respPaymentInStub.getStatus());
		//assertNotNull("Check if sessionId is set.", respPaymentInStub.getSessionId());
	}
	
	private void testV5CreditCardPaymentProcessing() throws InternalReplicationFault {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV5CreditCardPaymentData());
		testCreditCardPaymentProcessing(respGroup);
	}

	@Test
	public void testV4ProcessPaymentNoPlaces() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV4ProcessPaymentNoPlacesData());
		testProcessPaymentNoPlaces(respGroup);
	}
	
	private GenericTransactionGroup prepareV4ProcessPaymentNoPlacesData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub1 = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub1 = studentV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub2 = contactV4();
		contactStub2.setAngelId(2l);
		contactStub2.setStudentId(2l);
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub2 = studentV4();
		studentStub2.setAngelId(2l);
		studentStub2.setContactId(2l);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub1 = enrolmentV4();
		enrolStub1.setCourseClassId(201l);
		enrolStub1.setStudentId(1l);
		enrolStub1.setInvoiceLineId(4l);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV4();
		enrolStub1.setAngelId(2l);
		enrolStub2.setCourseClassId(201l);
		enrolStub2.setStudentId(2l);
		enrolStub2.setInvoiceLineId(3l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		//NOTE: that invoicelines with collegeid 1 + angelid 1 and 2 already used by replication test and may not be cleanup.
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub1 = invoiceLineV4();
		invLineStub1.setAngelId(4l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV4();
		invLineStub2.setAngelId(3l);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(contactStub1);
		stubs.add(studentStub1);
		stubs.add(contactStub2);
		stubs.add(studentStub2);
		stubs.add(invoiceStub);
		stubs.add(invLineStub1);
		stubs.add(invLineStub2);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(enrolStub1);
		stubs.add(enrolStub2);
		return group;
	}
	
	private GenericTransactionGroup prepareV5ProcessPaymentNoPlacesData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub1 = contactV5();
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub1 = studentV5();
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub2 = contactV5();
		contactStub2.setAngelId(2l);
		contactStub2.setStudentId(2l);
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub2 = studentV5();
		studentStub2.setAngelId(2l);
		studentStub2.setContactId(2l);
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub1 = enrolmentV5();
		enrolStub1.setCourseClassId(201l);
		enrolStub1.setStudentId(1l);
		enrolStub1.setInvoiceLineId(4l);
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV5();
		enrolStub1.setAngelId(2l);
		enrolStub2.setCourseClassId(201l);
		enrolStub2.setStudentId(2l);
		enrolStub2.setInvoiceLineId(3l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		//NOTE: that invoicelines with collegeid 1 + angelid 1 and 2 already used by replication test and may not be cleanup.
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub1 = invoiceLineV5();
		invLineStub1.setAngelId(4l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV5();
		invLineStub2.setAngelId(3l);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = paymentInV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV5();
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(contactStub1);
		stubs.add(studentStub1);
		stubs.add(contactStub2);
		stubs.add(studentStub2);
		stubs.add(invoiceStub);
		stubs.add(invLineStub1);
		stubs.add(invLineStub2);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(enrolStub1);
		stubs.add(enrolStub2);
		return group;
	}
	
	private void testProcessPaymentNoPlaces(GenericTransactionGroup respGroup) {
		assertNotNull("Check Response Group is not null", respGroup);
		final List<GenericEnrolmentStub> enrolStubs = new ArrayList<GenericEnrolmentStub>(2);
		final List<GenericPaymentInStub> paymentStubs = new ArrayList<GenericPaymentInStub>(2);
		final List<GenericInvoiceStub> invoiceStubs = new ArrayList<GenericInvoiceStub>();
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				GenericEnrolmentStub enrol = (GenericEnrolmentStub) stub;
				assertEquals("Check enrolment status.", "FAILED", enrol.getStatus());
				enrolStubs.add(enrol);
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub p = (GenericPaymentInStub) stub;
				assertNull("Check that sessionId wasn't set.", p.getSessionId());
				assertTrue("Check payment status. Expecting SUCESS or FAILED_NO_PLACES.", p.getStatus().equals(Integer.valueOf(3))
						|| p.getStatus().equals(Integer.valueOf(7)));
				paymentStubs.add(p);
			} else if ("Invoice".equals(stub.getEntityIdentifier())) {
				GenericInvoiceStub inv = (GenericInvoiceStub) stub;
				invoiceStubs.add(inv);
			}
		}
		assertEquals("Check if two enrolments present in response.", 2, enrolStubs.size());
		assertEquals("Check if two payments present in response.", 2, paymentStubs.size());
		assertEquals("Check if two invoices present in response.", 2, invoiceStubs.size());
		// Check amount of invoices.
		long sum = invoiceStubs.get(0).getAmountOwing().add(invoiceStubs.get(1).getAmountOwing()).longValue();
		assertEquals("Check that the amounts are opposite.", 0, sum);
	}
	
	@Test
	public void testV5ProcessPaymentNoPlaces() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV5ProcessPaymentNoPlacesData());
		testProcessPaymentNoPlaces(respGroup);
	}

	@Test
	public void testV4ProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPaymentV4(true, false, false);
	}

	@Test
	public void testV4ProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPaymentV4(false, false, false);
	}
	
	@Test
	public void testV5ProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPaymentV5(true, false, false);
	}

	@Test
	public void testV5ProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPaymentV5(false, false, false);
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV4WithTheSameInvoiceData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV4();
		enrolStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV4();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub2 = paymentInV4();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV4();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();

		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invLineStub2);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV5WithTheSameInvoiceData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV5();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(null);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub2 = paymentInV5();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV5();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub = enrolmentV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = paymentInV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV5();
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub = contactV5();
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub = studentV5();

		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();

		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invLineStub2);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private void conflictPaymentProcessingWithTheSameInvoice(GenericTransactionGroup respGroup) {
		ICayenneService service = getService(ICayenneService.class);
		ObjectContext context = service.newContext();
		assertNotNull("Check Response Group is not null", respGroup);
		GenericEnrolmentStub respEnrolStub = null;
		GenericPaymentInStub respPaymentInStub = null;
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (GenericEnrolmentStub) stub;
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
		}
		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check enrolment status.", "IN_TRANSACTION", respEnrolStub.getStatus());
		
		@SuppressWarnings("unchecked")
		List<PaymentIn> payments = context.performQuery(new SelectQuery(PaymentIn.class, ExpressionFactory.inDbExp(PaymentIn.ID_PK_COLUMN, 1l,2l)));
		for (PaymentIn paymentIn : payments) {
			if (paymentIn.getAngelId().equals(1l)) {
				assertEquals("Check payment status. ", PaymentStatus.FAILED, paymentIn.getStatus());
				assertEquals("Only 1 paymentin line for failed payment should exist", 1, paymentIn.getPaymentInLines().size());
				Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
				for (PaymentInLine paymentInLine : invoice.getPaymentInLines()) {
					//check that there are no reverse invoices linked
					assertFalse("Invoice should not be linked with any success payment", 
						PaymentStatus.SUCCESS.equals(paymentInLine.getPaymentIn().getStatus()));
				}
				for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
					assertTrue("InvoiceLine price is positive", invoiceLine.getPriceEachIncTax().isGreaterThan(Money.ZERO));
					Enrolment enrolment = invoiceLine.getEnrolment();
					if (invoiceLine.getAngelId().equals(2l)) {
						assertNull("This positive invoice have no enrolment", enrolment);
					} else if (invoiceLine.getAngelId().equals(1l)) {
						assertNotNull("This positive invoice have enrolment", enrolment);
						assertEquals("Enrolment should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
					} else {
						assertFalse("Unexpected invoiceline", true);
					}
				}
			} else if (paymentIn.getAngelId().equals(2l)) {
				assertEquals("Check payment status. ", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			}
		}
	}
	
	@Test
	public void conflictPaymentProcessingV4WithTheSameInvoice() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(conflictPaymentProcessingV4WithTheSameInvoiceData());
		conflictPaymentProcessingWithTheSameInvoice(respGroup);
	}
	
	@Test
	public void conflictPaymentProcessingV5WithTheSameInvoice() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(conflictPaymentProcessingV5WithTheSameInvoiceData());
		conflictPaymentProcessingWithTheSameInvoice(respGroup);
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV4WithManyInvoicesFirstPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV4();
		enrolStub2.setAngelId(2l);
		enrolStub2.setInvoiceLineId(2l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();		
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV4();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub2 = paymentInV4();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV4();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invoiceStub);
		stubs.add(invLineStub2);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV4WithManyInvoicesSecondPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		invoiceStub.setAmountOwing(invoiceStub.getAmountOwing().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		invoiceStub.setTotalExGst(invoiceStub.getTotalExGst().add(invLineStub.getPriceEachExTax()));
		invoiceStub.setTotalGst(invoiceStub.getTotalGst().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();
		
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub3 = invoiceV4();
		invoiceStub3.setAngelId(3l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub3 = invoiceLineV4();
		invLineStub3.setAngelId(3L);
		invLineStub3.setInvoiceId(3l);
		invLineStub3.setEnrolmentId(null);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub3 = paymentInLineV4();
		pLineStub3.setAngelId(3l);
		pLineStub3.setInvoiceId(3l);
		
		paymentInStub.setAmount(paymentInStub.getAmount().add(pLineStub3.getAmount()));
		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		
		stubs.add(invoiceStub3);
		stubs.add(invLineStub3);
		stubs.add(pLineStub3);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV5WithManyInvoicesFirstPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV5();
		enrolStub2.setAngelId(2l);
		enrolStub2.setInvoiceLineId(2l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();		
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV5();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub2 = paymentInV5();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV5();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invoiceStub);
		stubs.add(invLineStub2);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV5WithManyInvoicesSecondPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub = enrolmentV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		invoiceStub.setAmountOwing(invoiceStub.getAmountOwing().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		invoiceStub.setTotalExGst(invoiceStub.getTotalExGst().add(invLineStub.getPriceEachExTax()));
		invoiceStub.setTotalGst(invoiceStub.getTotalGst().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = paymentInV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV5();
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub = contactV5();
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub = studentV5();
		
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub3 = invoiceV5();
		invoiceStub3.setAngelId(3l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub3 = invoiceLineV5();
		invLineStub3.setAngelId(3L);
		invLineStub3.setInvoiceId(3l);
		invLineStub3.setEnrolmentId(null);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub3 = paymentInLineV5();
		pLineStub3.setAngelId(3l);
		pLineStub3.setInvoiceId(3l);
		
		paymentInStub.setAmount(paymentInStub.getAmount().add(pLineStub3.getAmount()));
		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		
		stubs.add(invoiceStub3);
		stubs.add(invLineStub3);
		stubs.add(pLineStub3);
		return group;
	}
	
	private void conflictPaymentProcessingWithManyInvoices(GenericTransactionGroup respGroup) {
		ICayenneService service = getService(ICayenneService.class);
		ObjectContext context = service.newContext();
		assertNotNull("Check Response Group is not null", respGroup);
		GenericEnrolmentStub respEnrolStub = null;
		GenericPaymentInStub respPaymentInStub = null;
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (GenericEnrolmentStub) stub;
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
		}
		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check enrolment status.", "IN_TRANSACTION", respEnrolStub.getStatus());
		
		@SuppressWarnings("unchecked")
		List<PaymentIn> payments = context.performQuery(new SelectQuery(PaymentIn.class, ExpressionFactory.inDbExp(PaymentIn.ID_PK_COLUMN, 1l,2l)));
		for (PaymentIn paymentIn : payments) {
			if (paymentIn.getAngelId().equals(1l)) {
				assertEquals("Check payment status. ", PaymentStatus.FAILED, paymentIn.getStatus());
				assertEquals("2 paymentin lines for failed payment should exist", 2, paymentIn.getPaymentInLines().size());
				for (PaymentInLine payLine : paymentIn.getPaymentInLines()) {
					Invoice invoice = payLine.getInvoice();
					for (PaymentInLine paymentInLine : invoice.getPaymentInLines()) {
						//check that there are no reverse invoices linked
						assertFalse("Invoice should not be linked with any success payment", 
							PaymentStatus.SUCCESS.equals(paymentInLine.getPaymentIn().getStatus()));
					}
					if (invoice.getAngelId().equals(1l)) {
						assertEquals("2 invoicelines should be linked with this invoice", 2, invoice.getInvoiceLines().size());
						for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
							assertTrue("InvoiceLine price is positive", invoiceLine.getPriceEachIncTax().isGreaterThan(Money.ZERO));
							Enrolment enrolment = invoiceLine.getEnrolment();
							assertNotNull("This positive invoice have enrolment", enrolment);
							assertEquals("Enrolment should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
						}
					} else if (invoice.getAngelId().equals(3l)) {
						assertEquals("1 invoiceline should be linked with this invoice", 1, invoice.getInvoiceLines().size());
						InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
						assertTrue("InvoiceLine price is positive", invoiceLine.getPriceEachIncTax().isGreaterThan(Money.ZERO));
						assertNull("This positive invoice have no enrolment", invoiceLine.getEnrolment());
					} else {
						assertFalse("Unexpected invoice id=" + invoice.getAngelId(), true);
					}
				}
			} else if (paymentIn.getAngelId().equals(2l)) {
				assertEquals("Check payment status. ", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			} else {
				assertFalse("Unexpected payment id=" + paymentIn.getAngelId(), true);
			}
		}
	}
	
	@Test
	public void conflictPaymentProcessingV4WithManyInvoices() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		//process first payment attempt
		port.processPayment(conflictPaymentProcessingV4WithManyInvoicesFirstPartData());
		//process concurent payment attempt
		GenericTransactionGroup respGroup = port.processPayment(conflictPaymentProcessingV4WithManyInvoicesSecondPartData());
		conflictPaymentProcessingWithManyInvoices(respGroup);
	}
	
	@Test
	public void conflictPaymentProcessingV5WithManyInvoices() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		//process first payment attempt
		port.processPayment(conflictPaymentProcessingV5WithManyInvoicesFirstPartData());
		//process concurent payment attempt
		GenericTransactionGroup respGroup = port.processPayment(conflictPaymentProcessingV5WithManyInvoicesSecondPartData());
		conflictPaymentProcessingWithManyInvoices(respGroup);
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV4WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentFirstPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV4();
		enrolStub2.setAngelId(2l);
		enrolStub2.setInvoiceLineId(2L);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub3 = enrolmentV4();
		enrolStub3.setAngelId(3l);
		enrolStub3.setInvoiceLineId(3l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub3 = invoiceV4();
		invoiceStub3.setAngelId(3l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub3 = invoiceLineV4();
		invLineStub3.setAngelId(3L);
		invLineStub3.setInvoiceId(3l);
		invLineStub3.setEnrolmentId(3l);
		
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV4();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
				
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub2 = paymentInV4();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV4();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(enrolStub3);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invoiceStub3);
		stubs.add(invLineStub2);
		stubs.add(invLineStub3);
		stubs.add(invoiceStub);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV4WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentSecondPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub3 = paymentInLineV4();
		pLineStub3.setAngelId(3l);
		pLineStub3.setInvoiceId(3l);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		//adjust concurent invoice owing
		invoiceStub.setAmountOwing(invoiceStub.getAmountOwing().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		invoiceStub.setTotalExGst(invoiceStub.getTotalExGst().add(invLineStub.getPriceEachExTax()));
		invoiceStub.setTotalGst(invoiceStub.getTotalGst().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();
		
		paymentInStub.setAmount(paymentInStub.getAmount().add(pLineStub3.getAmount()));

		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(pLineStub3);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV5WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentFirstPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV5();
		enrolStub2.setAngelId(2l);
		enrolStub2.setInvoiceLineId(2L);
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub3 = enrolmentV5();
		enrolStub3.setAngelId(3l);
		enrolStub3.setInvoiceLineId(3l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub3 = invoiceV5();
		invoiceStub3.setAngelId(3l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub3 = invoiceLineV5();
		invLineStub3.setAngelId(3L);
		invLineStub3.setInvoiceId(3l);
		invLineStub3.setEnrolmentId(3l);
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV5();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
		
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub2 = paymentInV5();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV5();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(enrolStub3);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invoiceStub3);
		stubs.add(invLineStub2);
		stubs.add(invLineStub3);
		stubs.add(invoiceStub);
		return group;
	}
	
	private GenericTransactionGroup conflictPaymentProcessingV5WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentSecondPartData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub3 = paymentInLineV5();
		pLineStub3.setAngelId(3l);
		pLineStub3.setInvoiceId(3l);
		
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub = enrolmentV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		//adjust concurent invoice owing
		invoiceStub.setAmountOwing(invoiceStub.getAmountOwing().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		invoiceStub.setTotalExGst(invoiceStub.getTotalExGst().add(invLineStub.getPriceEachExTax()));
		invoiceStub.setTotalGst(invoiceStub.getTotalGst().add(invLineStub.getPriceEachExTax()).add(invLineStub.getTaxEach()));
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = paymentInV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV5();
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub = contactV5();
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub = studentV5();
		
		paymentInStub.setAmount(paymentInStub.getAmount().add(pLineStub3.getAmount()));

		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(pLineStub3);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		//stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private void conflictPaymentProcessingWithManyInvoicesWhenFailedPaymentAlsoFailEnrollment(GenericTransactionGroup respGroup) {
		ICayenneService service = getService(ICayenneService.class);
		ObjectContext context = service.newContext();
		assertNotNull("Check Response Group is not null", respGroup);
		GenericEnrolmentStub respEnrolStub = null;
		GenericPaymentInStub respPaymentInStub = null;
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (GenericEnrolmentStub) stub;
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
		}
		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		
		@SuppressWarnings("unchecked")
		List<PaymentIn> payments = context.performQuery(new SelectQuery(PaymentIn.class, ExpressionFactory.inDbExp(PaymentIn.ID_PK_COLUMN, 1l,2l)));
		for (PaymentIn paymentIn : payments) {
			if (paymentIn.getAngelId().equals(1l)) {
				assertEquals("Check payment status. ", PaymentStatus.FAILED, paymentIn.getStatus());
				assertEquals("2 paymentin lines for failed payment should exist", 2, paymentIn.getPaymentInLines().size());
				for (PaymentInLine payLine : paymentIn.getPaymentInLines()) {
					Invoice invoice = payLine.getInvoice();
					for (PaymentInLine paymentInLine : invoice.getPaymentInLines()) {
						//check that there are no reverse invoices linked
						PaymentIn pay = paymentInLine.getPaymentIn();
						if (pay.getId() != null && pay.getAngelId() != null) {
							assertFalse("Payments passed to system for processing should not be success", 
								PaymentStatus.SUCCESS.equals(pay.getStatus()));
						} else {
							assertTrue("Payment without angelid is a reverse payment",PaymentStatus.SUCCESS.equals(pay.getStatus()));
							assertEquals("Two invoices should be linked with this payment", 2, pay.getPaymentInLines().size());pay.getPaymentInLines().size();
							for (PaymentInLine line : pay.getPaymentInLines()) {
								Invoice inv = line.getInvoice();
								if (inv.getAngelId() != null && inv.getAngelId().equals(3l)) {
									assertTrue("Incorrect amount for paymentInline with angelid="+ line.getAngelId(), line.getAmount().compareTo(Money.ZERO) > 0);
								} else if (inv.getAngelId() == null) {
									assertTrue("Incorrect amount for paymentInline with angelid="+ line.getAngelId(), line.getAmount().compareTo(Money.ZERO) < 0);
									assertEquals("1 invoiceline should be linked with reverse invoice", 1, inv.getInvoiceLines().size());
									assertNull("No enrolment should be linked to reverse invoice", inv.getInvoiceLines().get(0).getEnrolment());
								} else {
									assertFalse("Unexpected invoice with id=" + inv.getId(), true);
								}
							}
						}
					}
					if (invoice.getAngelId().equals(1l)) {
						assertEquals("Invoice should be linked with both payments", 2, invoice.getPaymentInLines().size());
						assertEquals("2 invoicelines should be linked with this invoice", 2, invoice.getInvoiceLines().size());
						for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
							Enrolment enrol = invoiceLine.getEnrolment();
							if (invoiceLine.getAngelId().equals(2l)) {
								assertNotNull("Enrol for invoiceline with angelid=2 should not be empty", enrol);
								assertEquals("Enrol for invoiceline with angelid=2 should have angelid=2", 2l, enrol.getAngelId().longValue());
							} else if(invoiceLine.getAngelId().equals(1l)) {
								assertNotNull("Enrol for invoiceline with angelid=1 should not be empty", enrol);
								assertEquals("Enrol for invoiceline with angelid=1 should have angelid=1", 1l, enrol.getAngelId().longValue());
							} else {
								assertFalse("Unexpected invoiceline with angelid="+ invoiceLine.getAngelId(), true);
							}
							assertEquals("Enrolment for this invoice should have in transaction status", EnrolmentStatus.IN_TRANSACTION, enrol.getStatus());
						}
					} else if(invoice.getAngelId().equals(3l)) {
						assertEquals("Invoice should be linked with both payments", 2, invoice.getPaymentInLines().size());
						assertEquals("1 invoiceline should be linked with this invoice", 1, invoice.getInvoiceLines().size());
						InvoiceLine invoiceLine = invoice.getInvoiceLines().get(0);
						assertEquals("Invoice line should have angelid=3", 3l, invoiceLine.getAngelId().longValue());
						Enrolment enrol = invoiceLine.getEnrolment();
						assertNotNull("Enrolment linked with this invoiceline shoul be not null", enrol);
						assertEquals("Enrolment should have angelid=3", 3l, enrol.getAngelId().longValue());
						assertEquals("This enrollment should be failed", EnrolmentStatus.FAILED, enrol.getStatus());
					} else {
						assertFalse("Unexpected invoice with angelid="+ invoice.getAngelId(), true);
					}
				}
			} else if (paymentIn.getAngelId().equals(2l)) {
				assertEquals("Check payment status. ", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
			} else {
				assertFalse("Unexpected payment with angelid="+ paymentIn.getAngelId(), true);
			}
		}
	}
	
	@Test
	public void conflictPaymentProcessingV4WithManyInvoicesWhenFailedPaymentAlsoFailEnrollment() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		//process first payment attempt
		port.processPayment(conflictPaymentProcessingV4WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentFirstPartData());
		//process concurent payment attempt
		GenericTransactionGroup respGroup = port.processPayment(conflictPaymentProcessingV4WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentSecondPartData());
		conflictPaymentProcessingWithManyInvoicesWhenFailedPaymentAlsoFailEnrollment(respGroup);
	}
	
	@Test
	public void conflictPaymentProcessingV5WithManyInvoicesWhenFailedPaymentAlsoFailEnrollment() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		//process first payment attempt
		port.processPayment(conflictPaymentProcessingV5WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentFirstPartData());
		//process concurent payment attempt
		GenericTransactionGroup respGroup = port.processPayment(conflictPaymentProcessingV5WithManyInvoicesWhenFailedPaymentAlsoFailEnrollmentSecondPartData());
		conflictPaymentProcessingWithManyInvoicesWhenFailedPaymentAlsoFailEnrollment(respGroup);
	}
	
	@Test
	public void testV4ZeroPaymentForFreeInvoice() throws Exception {
		notCreditCardOrZeroPaymentV4(true, true, false);
	}
	
	@Test
	public void testV4ZeroPaymentForFreeInvoiceWithoutPlaces() throws Exception {
		notCreditCardOrZeroPaymentV4(true, true, true);
	}
	
	@Test
	public void testV5ZeroPaymentForFreeInvoice() throws Exception {
		notCreditCardOrZeroPaymentV5(true, true, false);
	}
	
	@Test
	public void testV5ZeroPaymentForFreeInvoiceWithoutPlaces() throws Exception {
		notCreditCardOrZeroPaymentV5(true, true, true);
	}
	
	private GenericTransactionGroup notCreditCardOrZeroPaymentV5Data(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub = enrolmentV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = invoiceV5();
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = paymentInV5();
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV5();
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub = contactV5();
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub = studentV5();

		if (!isZeroPayment) {
			// setting payment type to CASH
			paymentInStub.setType(0);
		} else {
			enrolStub.setCourseClassId(202l);
			paymentInStub.setAmount(BigDecimal.ZERO);
			invoiceStub.setAmountOwing(BigDecimal.ZERO);
			pLineStub.setAmount(BigDecimal.ZERO);
		}
		if (isFreeInvoice) {
			invLineStub.setPriceEachExTax(BigDecimal.ZERO);
			invLineStub.setDiscountEachExTax(BigDecimal.ZERO);
			invLineStub.setTaxEach(BigDecimal.ZERO);
		}

		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();

		if (withoutPlaces) {
			ish.oncourse.webservices.v5.stubs.replication.CourseClassStub courseClassStub = courseClassV5();
			enrolStub.setCourseClassId(courseClassStub.getAngelId());
			stubs.add(courseClassStub);
		}
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private GenericTransactionGroup notCreditCardOrZeroPaymentV4Data(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();

		if (!isZeroPayment) {
			// setting payment type to CASH
			paymentInStub.setType(0);
		} else {
			enrolStub.setCourseClassId(202l);
			paymentInStub.setAmount(BigDecimal.ZERO);
			invoiceStub.setAmountOwing(BigDecimal.ZERO);
			pLineStub.setAmount(BigDecimal.ZERO);
		}
		if (isFreeInvoice) {
			invLineStub.setPriceEachExTax(BigDecimal.ZERO);
			invLineStub.setDiscountEachExTax(BigDecimal.ZERO);
			invLineStub.setTaxEach(BigDecimal.ZERO);
		}
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		if (withoutPlaces) {
			ish.oncourse.webservices.v4.stubs.replication.CourseClassStub courseClassStub = courseClassV4();
			enrolStub.setCourseClassId(courseClassStub.getAngelId());
			stubs.add(courseClassStub);
		}

		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		return group;
	}
	
	private void notCreditCardOrZeroPayment(GenericTransactionGroup respGroup, boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) {
		assertNotNull("Check Response Group is not null", respGroup);

		GenericEnrolmentStub respEnrolStub = null;
		GenericPaymentInStub respPaymentInStub = null;

		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (GenericEnrolmentStub) stub;
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				if (isZeroPayment && isFreeInvoice && withoutPlaces) {
					if (((GenericPaymentInStub) stub).getType().equals(2)) {
						respPaymentInStub = (GenericPaymentInStub) stub;
					}
				} else {
					respPaymentInStub = (GenericPaymentInStub) stub;
				}
			}
		}
		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		String expectedEnrolStatus = (isZeroPayment && isFreeInvoice && withoutPlaces) ? "FAILED": "SUCCESS";
		assertEquals("Check enrolment status.", expectedEnrolStatus, respEnrolStub.getStatus());
		Integer expectedPaymentStatus = (isZeroPayment && isFreeInvoice && withoutPlaces) ? Integer.valueOf(7): Integer.valueOf(3);
		assertEquals("Check payment status. Expecting " + expectedPaymentStatus, expectedPaymentStatus, respPaymentInStub.getStatus());
		assertNull("Check that sessionId wasn't set.", respPaymentInStub.getSessionId());
	}

	private void notCreditCardOrZeroPaymentV4(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(notCreditCardOrZeroPaymentV4Data(isZeroPayment, isFreeInvoice, withoutPlaces));
		notCreditCardOrZeroPayment(respGroup, isZeroPayment, isFreeInvoice, withoutPlaces);
	}
	
	private void notCreditCardOrZeroPaymentV5(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(notCreditCardOrZeroPaymentV5Data(isZeroPayment, isFreeInvoice, withoutPlaces));
		notCreditCardOrZeroPayment(respGroup, isZeroPayment, isFreeInvoice, withoutPlaces);
	}
	
	@Test
	public void testV4PaymentStatus() throws Exception {
		testPaymentStatus(SupportedVersions.V4);
	}
	
	private void testPaymentStatus(SupportedVersions version) throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		//sessionId for in transaction payment, we expect an empty group as response
		String sessionId = "AAVV#$%%%#$3333";
		GenericTransactionGroup respGroup = port.getPaymentStatus(sessionId, version);
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group is empty.", respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//sessionId for payment in success status, we expect not empty group with paymentIn record inside.
		sessionId = "jfjf790aaajjj9900";
		respGroup = port.getPaymentStatus(sessionId, version);
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group isn't empty.", !respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		GenericPaymentInStub respPaymentInStub = null;
		GenericEnrolmentStub respEnrolmentStub = null;
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if (PaymentIn.class.getSimpleName().equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
			if (Enrolment.class.getSimpleName().equals(stub.getEntityIdentifier())) {
				respEnrolmentStub = (GenericEnrolmentStub) stub;
			}
		}
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
		assertNotNull("Check enrolment presents in response.", respEnrolmentStub);
		assertEquals("Check enrolment status. Expecting SUCESS.", EnrolmentStatus.SUCCESS.name(), respEnrolmentStub.getStatus());
		// now update the data via JDBC to emulate another instance data change
		Connection connection = onDataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("Update PaymentIn set status=3 where sessionId='AAVV#$%%%#$3333'");
		statement.execute("Update Enrolment set status=3 where id=2000");
		connection.commit();
		sessionId = "AAVV#$%%%#$3333";
		respGroup = port.getPaymentStatus(sessionId, version);
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group is empty.", !respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		respPaymentInStub = null;
		respEnrolmentStub = null;
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if (PaymentIn.class.getSimpleName().equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
			if (Enrolment.class.getSimpleName().equals(stub.getEntityIdentifier())) {
				respEnrolmentStub = (GenericEnrolmentStub) stub;
			}
		}
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
		assertNotNull("Check enrolment presents in response.", respEnrolmentStub);
		assertEquals("Check enrolment status. Expecting SUCESS.", EnrolmentStatus.SUCCESS.name(), respEnrolmentStub.getStatus());
	}
	
	@Test
	public void testV5PaymentStatus() throws Exception {
		testPaymentStatus(SupportedVersions.V5);
	}
	
	private GenericTransactionGroup v4ProcessRefundDataSuccess() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V4);
		ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub();
		paymentOut.setAmount(new BigDecimal(100));
		paymentOut.setAngelId(1l);
		paymentOut.setContactId(3l);
		paymentOut.setCreated(today);
		paymentOut.setEntityIdentifier("PaymentOut");
		paymentOut.setModified(today);
		paymentOut.setPaymentInTxnReference("tx123");
		paymentOut.setSource("W");
		paymentOut.setStatus(2);
		paymentOut.setType(2);

		List<GenericReplicationStub> stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);
		return reqGroup;
	}
	
	private GenericTransactionGroup v5ProcessRefundDataSuccess() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v5.stubs.replication.PaymentOutStub();
		paymentOut.setAmount(new BigDecimal(100));
		paymentOut.setAngelId(1l);
		paymentOut.setContactId(3l);
		paymentOut.setCreated(today);
		paymentOut.setEntityIdentifier("PaymentOut");
		paymentOut.setModified(today);
		paymentOut.setPaymentInTxnReference("tx123");
		paymentOut.setSource("W");
		paymentOut.setStatus(2);
		paymentOut.setType(2);

		List<GenericReplicationStub> stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);
		return reqGroup;
	}
	
	private GenericTransactionGroup v4ProcessRefundDataFail() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V4);

		ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub();
		paymentOut.setContactId(3l);
		paymentOut.setCreated(today);
		paymentOut.setEntityIdentifier("PaymentOut");
		paymentOut.setModified(today);
		paymentOut.setPaymentInTxnReference("tx123");
		paymentOut.setSource("W");
		paymentOut.setStatus(2);
		paymentOut.setType(2);
		
		paymentOut.setAngelId(2l);
		paymentOut.setAmount(new BigDecimal(2100));
		
		List<GenericReplicationStub> stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);
		return reqGroup;
	}
	
	private GenericTransactionGroup v5ProcessRefundDataFail() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V5);
		ish.oncourse.webservices.v5.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v5.stubs.replication.PaymentOutStub();
		paymentOut.setContactId(3l);
		paymentOut.setCreated(today);
		paymentOut.setEntityIdentifier("PaymentOut");
		paymentOut.setModified(today);
		paymentOut.setPaymentInTxnReference("tx123");
		paymentOut.setSource("W");
		paymentOut.setStatus(2);
		paymentOut.setType(2);		
		paymentOut.setAngelId(2l);
		paymentOut.setAmount(new BigDecimal(2100));
		
		List<GenericReplicationStub> stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);
		return reqGroup;
	}
	
	private void processRefundSuccess(GenericTransactionGroup respGroup) {
		assertNotNull(respGroup);
		GenericPaymentOutStub pResp = null;
		Long willowId = null;

		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (GenericPaymentOutStub) stub;
				willowId = stub.getWillowId();
			}
		}

		assertNotNull("Check paymentOut is present in response.", pResp);
		assertEquals("Check paymentOut status. Expecting SUCCESS.", Integer.valueOf(3), pResp.getStatus());
		assertNotNull("Check that dateBanked is set.", pResp.getDateBanked());
		assertNotNull("Check that datePaid is set.", pResp.getDatePaid());

		PaymentOut paymentOut = Cayenne.objectForPK(cayenneService.newNonReplicatingContext(), PaymentOut.class, willowId);
		paymentOut.setPersistenceState(PersistenceState.HOLLOW);

		assertNotNull("Check paymentOut is present in database.", paymentOut);
		assertEquals("Check paymentOut status. Expecting SUCCESS.", PaymentStatus.SUCCESS, paymentOut.getStatus());
		assertNotNull("Check that dateBanked is set.", paymentOut.getDateBanked());
		assertNotNull("Check that datePaid is set.", paymentOut.getDatePaid());
	}
	
	private void processRefundFailed(GenericTransactionGroup respGroup) {
		assertNotNull(respGroup);
		GenericPaymentOutStub pResp = null;
		Long willowId = null;

		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (GenericPaymentOutStub) stub;
				willowId = stub.getWillowId();
			}
		}
		assertNotNull("Check paymentOut is present in response.", pResp);
		assertEquals("Check paymentOut status. Expecting FAILED.", Integer.valueOf(4), pResp.getStatus());

		PaymentOut paymentOut = Cayenne.objectForPK(cayenneService.newNonReplicatingContext(), PaymentOut.class, willowId);
		paymentOut.setPersistenceState(PersistenceState.HOLLOW);

		assertNotNull("Check paymentOut is present in db.", paymentOut);
		assertEquals("Check paymentOut status. Expecting FAILED.", PaymentStatus.FAILED, paymentOut.getStatus());
	}

	@Test
	public void testV4ProcessRefund() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processRefund(v4ProcessRefundDataSuccess());
		processRefundSuccess(respGroup);
		respGroup = port.processRefund(v4ProcessRefundDataFail());
		processRefundFailed(respGroup);
	}
	
	@Test
	public void testV5ProcessRefund() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processRefund(v5ProcessRefundDataSuccess());
		processRefundSuccess(respGroup);
		respGroup = port.processRefund(v5ProcessRefundDataFail());
		processRefundFailed(respGroup);
	}
	
	@Test
	public void testAbandonPayment() throws Exception {
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext context = cayenneService.newContext();

		PaymentIn payment = Cayenne.objectForPK(context, PaymentIn.class, 2000);
		assertNotNull(payment);
		
		Enrolment enrolment = Cayenne.objectForPK(context, Enrolment.class, 2000);
		assertNotNull(enrolment);
		
		assertTrue(enrolment.getOutcomes().size() != 0);
		
		int attendanceCount = 0;
		for (Session session : enrolment.getCourseClass().getSessions()) {
			attendanceCount += session.getAttendances().size();
		}
		
		assertTrue(attendanceCount != 0);
		
		PaymentIn inversePayment = payment.abandonPayment();
		context.commitChanges();
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", inversePayment.getSessionId(), payment.getSessionId());
		assertEquals("Expecting that attendances remain in the database. Will be deleted later from Angel.", attendanceCount, dbUnitConnection.getRowCount("Attendance"));
		assertEquals("Expecting that outcomes remain in the database until deleted from angel.", 1, dbUnitConnection.getRowCount("Outcome"));
	}
	
	private ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invoiceLineV4() {
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = new ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub();
		invLineStub.setEntityIdentifier("InvoiceLine");
		invLineStub.setAngelId(1l);
		invLineStub.setCreated(today);
		invLineStub.setDescription("Invoice line item  for accounting course");
		invLineStub.setDiscountEachExTax(new BigDecimal(0));
		invLineStub.setEnrolmentId(1l);
		invLineStub.setModified(today);
		invLineStub.setPriceEachExTax(new BigDecimal(110));
		invLineStub.setQuantity(new BigDecimal(1));
		invLineStub.setTaxEach(BigDecimal.ZERO);
		//invLineStub.setTaxEach(new BigDecimal(10));
		invLineStub.setTitle("Accouting course item");
		invLineStub.setUnit("unit");
		invLineStub.setInvoiceId(1l);
		return invLineStub;
	}

	private ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceV4() {
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = new ish.oncourse.webservices.v4.stubs.replication.InvoiceStub();
		invoiceStub.setAngelId(1l);
		invoiceStub.setAmountOwing(new BigDecimal(110l));
		invoiceStub.setBillToAddress("Test billing address");
		invoiceStub.setContactId(2l);
		invoiceStub.setCreated(today);
		invoiceStub.setCustomerPO("PO");
		invoiceStub.setCustomerReference("ref123");
		invoiceStub.setDateDue(dueDate);
		invoiceStub.setDescription("Invoice for accounting course");
		invoiceStub.setEntityIdentifier("Invoice");
		invoiceStub.setInvoiceDate(today);
		invoiceStub.setInvoiceNumber(123l);
		invoiceStub.setModified(today);
		invoiceStub.setTotalExGst(new BigDecimal(110));
		invoiceStub.setTotalGst(new BigDecimal(110));
		invoiceStub.setAmountOwing(new BigDecimal(110));
		invoiceStub.setSource("W");
		invoiceStub.setStatus("Pending");
		return invoiceStub;
	}

	private ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolmentV4() {
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = new ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub();
		enrolStub.setAngelId(1l);
		enrolStub.setCourseClassId(200l);
		enrolStub.setCreated(today);
		enrolStub.setEntityIdentifier("Enrolment");
		enrolStub.setInvoiceLineId(1l);
		enrolStub.setModified(today);
		enrolStub.setReasonForStudy(1);
		enrolStub.setSource("W");
		enrolStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolStub.setStudentId(201l);
		return enrolStub;
	}
	
	private ish.oncourse.webservices.v4.stubs.replication.CourseClassStub courseClassV4() {
		ish.oncourse.webservices.v4.stubs.replication.CourseClassStub courseClassStub = new ish.oncourse.webservices.v4.stubs.replication.CourseClassStub();
		courseClassStub.setWillowId(1186958L);
		courseClassStub.setCode("1");
		courseClassStub.setEntityIdentifier("CourseClass");
		courseClassStub.setCourseId(200L);
		courseClassStub.setRoomId(200L);
		courseClassStub.setAngelId(200L);
		courseClassStub.setStartDate(today);
		courseClassStub.setEndDate(dueDate);
		courseClassStub.setWebVisible(false);
		courseClassStub.setCancelled(false);
		courseClassStub.setCreated(today);
		courseClassStub.setModified(today);
		//courseClassStub.setMaximumPlaces(30);
		//courseClassStub.setMinimumPlaces(30);
		courseClassStub.setMaximumPlaces(0);
		courseClassStub.setMinimumPlaces(0);
		courseClassStub.setCountOfSessions(3);
		courseClassStub.setDeliveryMode(1);
		courseClassStub.setFeeGst(new BigDecimal("45.45"));
		courseClassStub.setFeeExGst(new BigDecimal("454.55"));
		return courseClassStub;
	}
	
	private ish.oncourse.webservices.v5.stubs.replication.CourseClassStub courseClassV5() {
		ish.oncourse.webservices.v5.stubs.replication.CourseClassStub courseClassStub = new ish.oncourse.webservices.v5.stubs.replication.CourseClassStub();
		courseClassStub.setWillowId(1186958L);
		courseClassStub.setCode("1");
		courseClassStub.setEntityIdentifier("CourseClass");
		courseClassStub.setCourseId(200L);
		courseClassStub.setRoomId(200L);
		courseClassStub.setAngelId(200L);
		courseClassStub.setStartDate(today);
		courseClassStub.setEndDate(dueDate);
		courseClassStub.setWebVisible(false);
		courseClassStub.setCancelled(false);
		courseClassStub.setCreated(today);
		courseClassStub.setModified(today);
		//courseClassStub.setMaximumPlaces(30);
		//courseClassStub.setMinimumPlaces(30);
		courseClassStub.setMaximumPlaces(0);
		courseClassStub.setMinimumPlaces(0);
		courseClassStub.setCountOfSessions(3);
		courseClassStub.setDeliveryMode(1);
		courseClassStub.setDistantLearningCourse(false);
		courseClassStub.setFeeGst(new BigDecimal("45.45"));
		courseClassStub.setFeeExGst(new BigDecimal("454.55"));
		return courseClassStub;
	}

	private ish.oncourse.webservices.v4.stubs.replication.StudentStub studentV4() {
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = new ish.oncourse.webservices.v4.stubs.replication.StudentStub();
		studentStub.setAngelId(1l);
		studentStub.setContactId(1l);
		studentStub.setCountryOfBirthId(86l);
		studentStub.setCreated(today);
		studentStub.setModified(today);
		studentStub.setEntityIdentifier("Student");
		return studentStub;
	}

	private ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInV4() {
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInStub();
		paymentInStub.setAmount(new BigDecimal(110));
		paymentInStub.setAngelId(1l);
		paymentInStub.setContactId(4l);
		paymentInStub.setCreated(today);
		paymentInStub.setEntityIdentifier("PaymentIn");
		paymentInStub.setModified(today);
		paymentInStub.setSource("W");
		paymentInStub.setStatus(2);
		paymentInStub.setType(2);
		return paymentInStub;
	}

	private ish.oncourse.webservices.v4.stubs.replication.ContactStub contactV4() {
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = new ish.oncourse.webservices.v4.stubs.replication.ContactStub();
		contactStub.setAngelId(1l);
		contactStub.setBusinessPhoneNumber("3241322");
		contactStub.setCompany(false);
		contactStub.setCountryId(86l);
		contactStub.setDateOfBirth(today);
		contactStub.setEmailAddress("test@test.com");
		contactStub.setEntityIdentifier("Contact");
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		return contactStub;
	}

	private ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentInLineV4() {
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		pLineStub.setAngelId(1l);
		pLineStub.setCreated(today);
		pLineStub.setEntityIdentifier("PaymentInLine");
		pLineStub.setInvoiceId(1l);
		pLineStub.setPaymentInId(1l);
		pLineStub.setAmount(new BigDecimal(110l));
		return pLineStub;
	}
	
	private ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invoiceLineV5() {
		ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub invLineStub = new ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub();
		invLineStub.setEntityIdentifier("InvoiceLine");
		invLineStub.setAngelId(1l);
		invLineStub.setCreated(today);
		invLineStub.setDescription("Invoice line item  for accounting course");
		invLineStub.setDiscountEachExTax(new BigDecimal(0));
		invLineStub.setEnrolmentId(1l);
		invLineStub.setModified(today);
		invLineStub.setPriceEachExTax(new BigDecimal(110));
		invLineStub.setQuantity(new BigDecimal(1));
		//invLineStub.setTaxEach(new BigDecimal(10));
		invLineStub.setTaxEach(BigDecimal.ZERO);
		invLineStub.setTitle("Accouting course item");
		invLineStub.setUnit("unit");
		invLineStub.setInvoiceId(1l);
		return invLineStub;
	}

	private ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceV5() {
		ish.oncourse.webservices.v5.stubs.replication.InvoiceStub invoiceStub = new ish.oncourse.webservices.v5.stubs.replication.InvoiceStub();
		invoiceStub.setAngelId(1l);
		invoiceStub.setAmountOwing(new BigDecimal(110l));
		invoiceStub.setBillToAddress("Test billing address");
		invoiceStub.setContactId(2l);
		invoiceStub.setCreated(today);
		invoiceStub.setCustomerPO("PO");
		invoiceStub.setCustomerReference("ref123");
		invoiceStub.setDateDue(dueDate);
		invoiceStub.setDescription("Invoice for accounting course");
		invoiceStub.setEntityIdentifier("Invoice");
		invoiceStub.setInvoiceDate(today);
		invoiceStub.setInvoiceNumber(123l);
		invoiceStub.setModified(today);
		invoiceStub.setTotalExGst(new BigDecimal(110));
		invoiceStub.setTotalGst(new BigDecimal(110));
		invoiceStub.setAmountOwing(new BigDecimal(110));
		invoiceStub.setSource("W");
		invoiceStub.setStatus("Pending");
		return invoiceStub;
	}

	private ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolmentV5() {
		ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub enrolStub = new ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub();
		enrolStub.setAngelId(1l);
		enrolStub.setCourseClassId(200l);
		enrolStub.setCreated(today);
		enrolStub.setEntityIdentifier("Enrolment");
		enrolStub.setInvoiceLineId(1l);
		enrolStub.setModified(today);
		enrolStub.setReasonForStudy(1);
		enrolStub.setSource("W");
		enrolStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolStub.setStudentId(201l);
		return enrolStub;
	}

	private ish.oncourse.webservices.v5.stubs.replication.StudentStub studentV5() {
		ish.oncourse.webservices.v5.stubs.replication.StudentStub studentStub = new ish.oncourse.webservices.v5.stubs.replication.StudentStub();
		studentStub.setAngelId(1l);
		studentStub.setContactId(1l);
		studentStub.setCountryOfBirthId(86l);
		studentStub.setCreated(today);
		studentStub.setModified(today);
		studentStub.setEntityIdentifier("Student");
		return studentStub;
	}

	private ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInV5() {
		ish.oncourse.webservices.v5.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v5.stubs.replication.PaymentInStub();
		paymentInStub.setAmount(new BigDecimal(110));
		paymentInStub.setAngelId(1l);
		paymentInStub.setContactId(4l);
		paymentInStub.setCreated(today);
		paymentInStub.setEntityIdentifier("PaymentIn");
		paymentInStub.setModified(today);
		paymentInStub.setSource("W");
		paymentInStub.setStatus(2);
		paymentInStub.setType(2);
		return paymentInStub;
	}

	private ish.oncourse.webservices.v5.stubs.replication.ContactStub contactV5() {
		ish.oncourse.webservices.v5.stubs.replication.ContactStub contactStub = new ish.oncourse.webservices.v5.stubs.replication.ContactStub();
		contactStub.setAngelId(1l);
		contactStub.setBusinessPhoneNumber("3241322");
		contactStub.setCompany(false);
		contactStub.setCountryId(86l);
		contactStub.setDateOfBirth(today);
		contactStub.setEmailAddress("test@test.com");
		contactStub.setEntityIdentifier("Contact");
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		return contactStub;
	}

	private ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub paymentInLineV5() {
		ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub pLineStub = new ish.oncourse.webservices.v5.stubs.replication.PaymentInLineStub();
		pLineStub.setAngelId(1l);
		pLineStub.setCreated(today);
		pLineStub.setEntityIdentifier("PaymentInLine");
		pLineStub.setInvoiceId(1l);
		pLineStub.setPaymentInId(1l);
		pLineStub.setAmount(new BigDecimal(110l));
		return pLineStub;
	}
}
