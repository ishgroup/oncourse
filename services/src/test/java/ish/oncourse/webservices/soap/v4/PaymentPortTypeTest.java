package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.EnrolmentStatus;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Session;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericPaymentOutStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

public class PaymentPortTypeTest extends ServiceTest {

	private Date dueDate;
	private Date today;

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/paymentDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

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
	
	private void testV4CreditCardPaymentProcessing() throws InternalReplicationFault {
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
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
		assertEquals("Check payment status.", Integer.valueOf(2), respPaymentInStub.getStatus());
		assertNotNull("Check if sessionId is set.", respPaymentInStub.getSessionId());
	}
	
	private void testV5CreditCardPaymentProcessing() throws InternalReplicationFault {
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
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
		assertEquals("Check payment status.", Integer.valueOf(2), respPaymentInStub.getStatus());
		assertNotNull("Check if sessionId is set.", respPaymentInStub.getSessionId());
	}
	
	@Test
	public void testV5ProcessCreditCardPayment() throws Exception {
		testV5CreditCardPaymentProcessing();
	}

	@Test
	public void testV4ProcessPaymentNoPlaces() throws Exception {
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

		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
				ish.oncourse.webservices.v4.stubs.replication.InvoiceStub inv = (ish.oncourse.webservices.v4.stubs.replication.InvoiceStub) stub;
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

		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
				ish.oncourse.webservices.v5.stubs.replication.InvoiceStub inv = (ish.oncourse.webservices.v5.stubs.replication.InvoiceStub) stub;
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
	public void testV4ProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPaymentV4(true);
	}

	@Test
	public void testV4ProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPaymentV4(false);
	}
	
	@Test
	public void testV5ProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPaymentV5(true);
	}

	@Test
	public void testV5ProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPaymentV5(false);
	}
	
	@Test
	public void conflictPaymentProcessingV4WithTheSameInvoice() throws Exception {
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
		//ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();

		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		//stubs.add(invoiceStub);
		stubs.add(invLineStub2);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		
		ICayenneService service = getService(ICayenneService.class);
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
				assertEquals("Check payment status. ", Integer.valueOf(4), paymentIn.getStatus().getDatabaseValue());
				assertEquals("Only 1 paymentin line for failed payment should exist", 1, paymentIn.getPaymentInLines().size());
				Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
				invoice.updateAmountOwing();
				assertEquals("Amount owing should be default", new Money("240.00").toBigDecimal(),invoice.getAmountOwing());//240=(110+10 of tax)*2
			} else if (paymentIn.getAngelId().equals(2l)) {
				assertEquals("Check payment status. ", Integer.valueOf(2), paymentIn.getStatus().getDatabaseValue());
			}
		}		
	}
	
	@Test
	public void conflictPaymentProcessingV4WithManyInvoices() throws Exception {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV4();
		enrolStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub3 = invoiceV4();
		invoiceStub3.setAngelId(3l);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub3 = invoiceLineV4();
		invLineStub3.setAngelId(3L);
		invLineStub3.setInvoiceId(3l);
		invLineStub3.setEnrolmentId(null);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV4();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub2 = paymentInV4();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV4();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub3 = paymentInLineV4();
		pLineStub3.setAngelId(3l);
		pLineStub3.setInvoiceId(3l);
		//pLineStub3.setPaymentInId(2l);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		//ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();
		
		paymentInStub.setAmount(paymentInStub.getAmount().add(pLineStub3.getAmount()));

		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invoiceStub3);
		stubs.add(invLineStub2);
		stubs.add(invLineStub3);
		stubs.add(pLineStub3);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		
		ICayenneService service = getService(ICayenneService.class);
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
				assertEquals("Check payment status. ", Integer.valueOf(4), paymentIn.getStatus().getDatabaseValue());
				assertEquals("Only 1 paymentin line for failed payment should exist", 2, paymentIn.getPaymentInLines().size());
				Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
				invoice.updateAmountOwing();
				assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getInvoiceLines().size()).toBigDecimal(),invoice.getAmountOwing());//240=(110+10 of tax)*2
				invoice = paymentIn.getPaymentInLines().get(1).getInvoice();
				invoice.updateAmountOwing();
				assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getInvoiceLines().size()).toBigDecimal(),invoice.getAmountOwing());//(110+10 of tax)
			} else if (paymentIn.getAngelId().equals(2l)) {
				assertEquals("Check payment status. ", Integer.valueOf(2), paymentIn.getStatus().getDatabaseValue());
			}
		}		
	}
	
	@Test
	public void conflictPaymentProcessingV4WithManyInvoicesWhenFailedPaymentAlsoFailEnrollment() throws Exception {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V4);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV4();
		enrolStub2.setAngelId(2l);
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
		invLineStub3.setTaxEach(Money.ZERO.toBigDecimal());
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV4();
		invLineStub2.setAngelId(2L);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub2 = paymentInV4();
		paymentInStub2.setAngelId(2l);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub2 = paymentInLineV4();
		pLineStub2.setAngelId(2l);
		pLineStub2.setPaymentInId(2l);
		
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub3 = paymentInLineV4();
		pLineStub3.setAngelId(3l);
		pLineStub3.setInvoiceId(3l);
		//pLineStub3.setPaymentInId(2l);
		
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolStub = enrolmentV4();
		//ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = invoiceV4();
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = paymentInV4();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV4();
		ish.oncourse.webservices.v4.stubs.replication.ContactStub contactStub = contactV4();
		ish.oncourse.webservices.v4.stubs.replication.StudentStub studentStub = studentV4();
		
		paymentInStub.setAmount(paymentInStub.getAmount().add(pLineStub3.getAmount()));

		
		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(enrolStub2);
		stubs.add(enrolStub3);
		stubs.add(paymentInStub2);
		stubs.add(pLineStub2);
		stubs.add(invoiceStub3);
		stubs.add(invLineStub2);
		stubs.add(invLineStub3);
		stubs.add(pLineStub3);
		
		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);
		
		ICayenneService service = getService(ICayenneService.class);
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);
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
		//assertEquals("Check enrolment status.", "IN_TRANSACTION", respEnrolStub.getStatus());
		
		@SuppressWarnings("unchecked")
		List<PaymentIn> payments = context.performQuery(new SelectQuery(PaymentIn.class, ExpressionFactory.inDbExp(PaymentIn.ID_PK_COLUMN, 1l,2l)));
		for (PaymentIn paymentIn : payments) {
			if (paymentIn.getAngelId().equals(1l)) {
				assertEquals("Check payment status. ", Integer.valueOf(4), paymentIn.getStatus().getDatabaseValue());
				assertEquals("Only 1 paymentin line for failed payment should exist", 2, paymentIn.getPaymentInLines().size());
				Invoice invoice = paymentIn.getPaymentInLines().get(0).getInvoice();
				invoice.updateAmountOwing();
				if (invoice.getPaymentInLines().size() == 1) {
					assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getPaymentInLines().size()).toBigDecimal(),invoice.getAmountOwing());//(110+10 of tax)
				} else {
					switch (invoice.getInvoiceLines().size()) {
					case 1:
						Enrolment enrol = invoice.getInvoiceLines().get(0).getEnrolment();
						if (enrol != null) {
							assertEquals("Amount owing should be default", Money.ZERO.toBigDecimal(),invoice.getAmountOwing());
							assertEquals("This enrollment should be failed", EnrolmentStatus.FAILED, enrol.getStatus());
						} else {
							assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getInvoiceLines().size()).toBigDecimal(),invoice.getAmountOwing());
						}
						break;
					case 2:
						assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getInvoiceLines().size()).toBigDecimal(),invoice.getAmountOwing());
						enrol = invoice.getInvoiceLines().get(0).getEnrolment();
						if (enrol == null) {
							enrol = invoice.getInvoiceLines().get(1).getEnrolment();
						}
						assertEquals("This enrollment should be not processed", EnrolmentStatus.IN_TRANSACTION, enrol.getStatus());
						break;
					default:
						assertTrue("Incorrect invoice lines for invoice received in test", false);
						break;
					}
				
				}
				invoice = paymentIn.getPaymentInLines().get(1).getInvoice();
				invoice.updateAmountOwing();
				if (invoice.getPaymentInLines().size() == 1) {
					assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getPaymentInLines().size()).toBigDecimal(),invoice.getAmountOwing());//(110+10 of tax)
				} else {
					switch (invoice.getInvoiceLines().size()) {
					case 1:
						Enrolment enrol = invoice.getInvoiceLines().get(0).getEnrolment();
						if (enrol != null) {
							assertEquals("Amount owing should be default", Money.ZERO.toBigDecimal(),invoice.getAmountOwing());
							assertEquals("This enrollment should be failed", EnrolmentStatus.FAILED, enrol.getStatus());
						} else {
							assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getInvoiceLines().size()).toBigDecimal(),invoice.getAmountOwing());
						}
						break;
					case 2:
						assertEquals("Amount owing should be default", new Money("120.00").multiply(invoice.getInvoiceLines().size()).toBigDecimal(),invoice.getAmountOwing());
						enrol = invoice.getInvoiceLines().get(0).getEnrolment();
						if (enrol == null) {
							enrol = invoice.getInvoiceLines().get(1).getEnrolment();
						}
						assertEquals("This enrollment should be not processed", EnrolmentStatus.IN_TRANSACTION, enrol.getStatus());
						break;
					default:
						assertTrue("Incorrect invoice lines for invoice received in test", false);
						break;
					}
				}
			} else if (paymentIn.getAngelId().equals(2l)) {
				assertEquals("Check payment status. ", Integer.valueOf(2), paymentIn.getStatus().getDatabaseValue());
			}
		}		
	}

	private void notCreditCardOrZeroPaymentV4(boolean isZeroPayment) throws Exception {
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

		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();

		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);

		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);

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
		assertEquals("Check enrolment status.", "SUCCESS", respEnrolStub.getStatus());
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
		assertNull("Check that sessionId wasn't set.", respPaymentInStub.getSessionId());
	}
	
	private void notCreditCardOrZeroPaymentV5(boolean isZeroPayment) throws Exception {
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

		List<GenericReplicationStub> stubs = group.getGenericAttendanceOrBinaryDataOrBinaryInfo();

		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);

		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(group);

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
		assertEquals("Check enrolment status.", "SUCCESS", respEnrolStub.getStatus());
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
		assertNull("Check that sessionId wasn't set.", respPaymentInStub.getSessionId());
	}
	
	@Test
	public void testV4PaymentStatus() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		
		//sessionId for in transaction payment, we expect an empty group as response
		String sessionId = "AAVV#$%%%#$3333";
		GenericTransactionGroup respGroup = port.getPaymentStatus(sessionId, SupportedVersions.V4);
		
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group is empty.", respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		//sessionId for payment in success status, we expect not empty group with paymentIn record inside.
		sessionId = "jfjf790aaajjj9900";
		respGroup = port.getPaymentStatus(sessionId, SupportedVersions.V4);
		
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group isn't empty.", !respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		GenericPaymentInStub respPaymentInStub = null;
		
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
		}
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
	}
	
	@Test
	public void testV5PaymentStatus() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		
		//sessionId for in transaction payment, we expect an empty group as response
		String sessionId = "AAVV#$%%%#$3333";
		GenericTransactionGroup respGroup = port.getPaymentStatus(sessionId, SupportedVersions.V5);
		
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group is empty.", respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		//sessionId for payment in success status, we expect not empty group with paymentIn record inside.
		sessionId = "jfjf790aaajjj9900";
		respGroup = port.getPaymentStatus(sessionId, SupportedVersions.V5);
		
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group isn't empty.", !respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		GenericPaymentInStub respPaymentInStub = null;
		
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (GenericPaymentInStub) stub;
			}
		}
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
	}

	@Test
	public void testV4ProcessRefund() throws Exception {

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

		InternalPaymentService port = getService(InternalPaymentService.class);
		List<GenericReplicationStub> stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);

		GenericTransactionGroup respGroup = port.processRefund(reqGroup);

		assertNotNull(respGroup);

		GenericPaymentOutStub pResp = null;

		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (GenericPaymentOutStub) stub;
			}
		}

		assertNotNull("Check paymentOut presents in response.", pResp);
		assertEquals("Check paymentOut status. Expecting SUCESS.", Integer.valueOf(3), pResp.getStatus());
		assertNotNull("Check that dateBanked is set.", pResp.getDateBanked());
		assertNotNull("Check that datePaid is set.", pResp.getDatePaid());

		reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V4);

		paymentOut.setAngelId(2l);
		paymentOut.setAmount(new BigDecimal(2100));
		stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);

		respGroup = port.processRefund(reqGroup);

		assertNotNull(respGroup);
		
		pResp = null;
		
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (GenericPaymentOutStub) stub;
			}
		}
		
		assertNotNull("Check paymentOut presents in response.", pResp);
		assertEquals("Check paymentOut status. Expecting FAILED.", Integer.valueOf(4), pResp.getStatus());

	}
	
	@Test
	public void testV5ProcessRefund() throws Exception {

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

		InternalPaymentService port = getService(InternalPaymentService.class);
		List<GenericReplicationStub> stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);

		GenericTransactionGroup respGroup = port.processRefund(reqGroup);

		assertNotNull(respGroup);

		GenericPaymentOutStub pResp = null;

		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (GenericPaymentOutStub) stub;
			}
		}

		assertNotNull("Check paymentOut presents in response.", pResp);
		assertEquals("Check paymentOut status. Expecting SUCESS.", Integer.valueOf(3), pResp.getStatus());
		assertNotNull("Check that dateBanked is set.", pResp.getDateBanked());
		assertNotNull("Check that datePaid is set.", pResp.getDatePaid());

		reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V5);

		paymentOut.setAngelId(2l);
		paymentOut.setAmount(new BigDecimal(2100));
		stubs = reqGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);

		respGroup = port.processRefund(reqGroup);

		assertNotNull(respGroup);
		
		pResp = null;
		
		for (GenericReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (GenericPaymentOutStub) stub;
			}
		}
		
		assertNotNull("Check paymentOut presents in response.", pResp);
		assertEquals("Check paymentOut status. Expecting FAILED.", Integer.valueOf(4), pResp.getStatus());

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
		
		payment.abandonPayment();
		context.commitChanges();
		
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
		invLineStub.setTaxEach(new BigDecimal(10));
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
		invoiceStub.setTotalExGst(new BigDecimal(100));
		invoiceStub.setTotalGst(new BigDecimal(110));
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
		invLineStub.setTaxEach(new BigDecimal(10));
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
		invoiceStub.setTotalExGst(new BigDecimal(100));
		invoiceStub.setTotalGst(new BigDecimal(110));
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
