package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.v4.stubs.replication.ContactStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.StudentStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

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

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/webservices/soap/v4/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/paymentDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 3);

		this.dueDate = cal.getTime();
		this.today = new Date();
	}

	@Test
	public void testProcessCreditCardPayment() throws Exception {

		TransactionGroup group = new TransactionGroup();

		EnrolmentStub enrolStub = enrolment();
		InvoiceStub invoiceStub = invoice();
		InvoiceLineStub invLineStub = invoiceLine();
		PaymentInStub paymentInStub = paymentIn();
		PaymentInLineStub pLineStub = paymentInLine();
		ContactStub contactStub = contact();
		StudentStub studentStub = student();

		List<ReplicationStub> stubs = group.getAttendanceOrBinaryDataOrBinaryInfo();

		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);

		PaymentPortType port = getService(PaymentPortType.class);
		TransactionGroup respGroup = port.processPayment(group);

		assertNotNull("Check Response Group is not null", respGroup);

		EnrolmentStub respEnrolStub = null;
		PaymentInStub respPaymentInStub = null;

		for (ReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (EnrolmentStub) stub;
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (PaymentInStub) stub;
			}
		}

		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);

		assertEquals("Check enrolment status.", "IN_TRANSACTION", respEnrolStub.getStatus());
		assertEquals("Check payment status.", Integer.valueOf(2), respPaymentInStub.getStatus());

		assertNotNull("Check if sessionId is set.", respPaymentInStub.getSessionId());
	}

	@Test
	public void testProcessPaymentNoPlaces() throws Exception {

		TransactionGroup group = new TransactionGroup();

		ContactStub contactStub1 = contact();
		StudentStub studentStub1 = student();

		ContactStub contactStub2 = contact();
		contactStub2.setAngelId(2l);
		contactStub2.setStudentId(2l);

		StudentStub studentStub2 = student();
		studentStub2.setAngelId(2l);
		studentStub2.setContactId(2l);

		EnrolmentStub enrolStub1 = enrolment();
		enrolStub1.setCourseClassId(201l);
		enrolStub1.setStudentId(1l);
		enrolStub1.setInvoiceLineId(1l);

		EnrolmentStub enrolStub2 = enrolment();
		enrolStub1.setAngelId(2l);
		enrolStub2.setCourseClassId(201l);
		enrolStub2.setStudentId(2l);
		enrolStub2.setInvoiceLineId(2l);

		InvoiceStub invoiceStub = invoice();

		InvoiceLineStub invLineStub1 = invoiceLine();
		InvoiceLineStub invLineStub2 = invoiceLine();
		invLineStub2.setAngelId(2l);
		invLineStub2.setEnrolmentId(2l);

		PaymentInStub paymentInStub = paymentIn();
		PaymentInLineStub pLineStub = paymentInLine();

		List<ReplicationStub> stubs = group.getAttendanceOrBinaryDataOrBinaryInfo();

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

		PaymentPortType port = getService(PaymentPortType.class);
		TransactionGroup respGroup = port.processPayment(group);

		assertNotNull("Check Response Group is not null", respGroup);

		List<EnrolmentStub> enrolStubs = new ArrayList<EnrolmentStub>(2);
		List<PaymentInStub> paymentStubs = new ArrayList<PaymentInStub>(2);
		List<InvoiceStub> invoiceStubs = new ArrayList<InvoiceStub>();

		for (ReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				EnrolmentStub enrol = (EnrolmentStub) stub;
				assertEquals("Check enrolment status.", "FAILED", enrol.getStatus());
				enrolStubs.add(enrol);
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				PaymentInStub p = (PaymentInStub) stub;
				assertNull("Check that sessionId wasn't set.", p.getSessionId());
				assertTrue("Check payment status. Expecting SUCESS or FAILED_NO_PLACES.", p.getStatus().equals(Integer.valueOf(3))
						|| p.getStatus().equals(Integer.valueOf(7)));
				paymentStubs.add(p);
			} else if ("Invoice".equals(stub.getEntityIdentifier())) {
				InvoiceStub inv = (InvoiceStub) stub;
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
	public void testProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPayment(true);
	}

	@Test
	public void testProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPayment(false);
	}

	private void notCreditCardOrZeroPayment(boolean isZeroPayment) throws Exception {

		TransactionGroup group = new TransactionGroup();

		EnrolmentStub enrolStub = enrolment();

		InvoiceStub invoiceStub = invoice();
		InvoiceLineStub invLineStub = invoiceLine();
		PaymentInStub paymentInStub = paymentIn();
		PaymentInLineStub pLineStub = paymentInLine();
		ContactStub contactStub = contact();
		StudentStub studentStub = student();

		if (!isZeroPayment) {
			// setting payment type to CASH
			paymentInStub.setType(0);
		} else {
			enrolStub.setCourseClassId(202l);
			paymentInStub.setAmount(BigDecimal.ZERO);
			invoiceStub.setAmountOwing(BigDecimal.ZERO);
			pLineStub.setAmount(BigDecimal.ZERO);
		}

		List<ReplicationStub> stubs = group.getAttendanceOrBinaryDataOrBinaryInfo();

		stubs.add(enrolStub);
		stubs.add(paymentInStub);
		stubs.add(pLineStub);
		stubs.add(invoiceStub);
		stubs.add(invLineStub);
		stubs.add(contactStub);
		stubs.add(studentStub);

		PaymentPortType port = getService(PaymentPortType.class);
		TransactionGroup respGroup = port.processPayment(group);

		assertNotNull("Check Response Group is not null", respGroup);

		EnrolmentStub respEnrolStub = null;
		PaymentInStub respPaymentInStub = null;

		for (ReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("Enrolment".equals(stub.getEntityIdentifier())) {
				respEnrolStub = (EnrolmentStub) stub;
			} else if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (PaymentInStub) stub;
			}
		}

		assertNotNull("Check enrolment presents in response.", respEnrolStub);
		assertNotNull("Check payment presents in response.", respPaymentInStub);

		assertEquals("Check enrolment status.", "SUCCESS", respEnrolStub.getStatus());
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());

		assertNull("Check that sessionId wasn't set.", respPaymentInStub.getSessionId());
	}

	@Test
	public void testPaymentStatus() throws Exception {
		PaymentPortType port = getService(PaymentPortType.class);
		
		//sessionId for in transaction payment, we expect an empty group as response
		String sessionId = "AAVV#$%%%#$3333";
		TransactionGroup respGroup = port.getPaymentStatus(sessionId);
		
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group is empty.", respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		//sessionId for payment in success status, we expect not empty group with paymentIn record inside.
		sessionId = "jfjf790aaajjj9900";
		respGroup = port.getPaymentStatus(sessionId);
		
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group isn't empty.", !respGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		PaymentInStub respPaymentInStub = null;
		
		for (ReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentIn".equals(stub.getEntityIdentifier())) {
				respPaymentInStub = (PaymentInStub) stub;
			}
		}
		
		assertNotNull("Check payment presents in response.", respPaymentInStub);
		assertEquals("Check payment status. Expecting SUCESS.", Integer.valueOf(3), respPaymentInStub.getStatus());
	}

	@Test
	public void testProcessRefund() throws Exception {

		TransactionGroup reqGroup = new TransactionGroup();

		PaymentOutStub paymentOut = new PaymentOutStub();
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

		PaymentPortType port = getService(PaymentPortType.class);
		List<ReplicationStub> stubs = reqGroup.getAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);

		TransactionGroup respGroup = port.processRefund(reqGroup);

		assertNotNull(respGroup);

		PaymentOutStub pResp = null;

		for (ReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (PaymentOutStub) stub;
			}
		}

		assertNotNull("Check paymentOut presents in response.", pResp);
		assertEquals("Check paymentOut status. Expecting SUCESS.", Integer.valueOf(3), pResp.getStatus());
		assertNotNull("Check that dateBanked is set.", pResp.getDateBanked());
		assertNotNull("Check that datePaid is set.", pResp.getDatePaid());

		reqGroup = new TransactionGroup();

		paymentOut.setAngelId(2l);
		paymentOut.setAmount(new BigDecimal(2100));
		stubs = reqGroup.getAttendanceOrBinaryDataOrBinaryInfo();
		stubs.add(paymentOut);

		respGroup = port.processRefund(reqGroup);

		assertNotNull(respGroup);
		
		pResp = null;
		
		for (ReplicationStub stub : respGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
			if ("PaymentOut".equals(stub.getEntityIdentifier())) {
				pResp = (PaymentOutStub) stub;
			}
		}
		
		assertNotNull("Check paymentOut presents in response.", pResp);
		assertEquals("Check paymentOut status. Expecting FAILED.", Integer.valueOf(4), pResp.getStatus());

	}

	private InvoiceLineStub invoiceLine() {
		InvoiceLineStub invLineStub = new InvoiceLineStub();
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

	private InvoiceStub invoice() {
		InvoiceStub invoiceStub = new InvoiceStub();
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
		return invoiceStub;
	}

	private EnrolmentStub enrolment() {
		EnrolmentStub enrolStub = new EnrolmentStub();
		enrolStub.setAngelId(1l);
		enrolStub.setCourseClassId(200l);
		enrolStub.setCreated(today);
		enrolStub.setEntityIdentifier("Enrolment");
		enrolStub.setInvoiceLineId(1l);
		enrolStub.setModified(today);
		enrolStub.setReasonForStudy(1);
		enrolStub.setSource("W");
		enrolStub.setStatus("IN_TRANSACTION");
		enrolStub.setStudentId(201l);
		return enrolStub;
	}

	private StudentStub student() {
		StudentStub studentStub = new StudentStub();
		studentStub.setAngelId(1l);
		studentStub.setContactId(1l);
		studentStub.setCountryOfBirthId(86l);
		studentStub.setCreated(today);
		studentStub.setModified(today);
		studentStub.setEntityIdentifier("Student");
		return studentStub;
	}

	private PaymentInStub paymentIn() {
		PaymentInStub paymentInStub = new PaymentInStub();
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

	private ContactStub contact() {
		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(1l);
		contactStub.setBusinessPhoneNumber("3241322");
		contactStub.setCompany(false);
		contactStub.setCountryId(86l);
		contactStub.setDateOfBirth(today);
		contactStub.setEmailAddress("test@test.com");
		contactStub.setEntityIdentifier("Contact");
		return contactStub;
	}

	private PaymentInLineStub paymentInLine() {
		PaymentInLineStub pLineStub = new PaymentInLineStub();
		pLineStub.setAngelId(1l);
		pLineStub.setCreated(today);
		pLineStub.setEntityIdentifier("PaymentInLine");
		pLineStub.setInvoiceId(1l);
		pLineStub.setPaymentInId(1l);
		pLineStub.setAmount(new BigDecimal(110l));
		return pLineStub;
	}
}
