package ish.oncourse.webservices.soap.v4;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Session;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
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
	public void testV6ProcessCreditCardPayment() throws Exception {
		testV6CreditCardPaymentProcessing();
	}
	
	@Test
	public void testV7ProcessCreditCardPayment() throws Exception {
		testV7CreditCardPaymentProcessing();
	}
	
	private GenericTransactionGroup prepareV6CreditCardPaymentData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V6);
		ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolStub = enrolmentV6();
		ish.oncourse.webservices.v6.stubs.replication.InvoiceStub invoiceStub = invoiceV6();
		ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInStub paymentInStub = paymentInV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV6();
		ish.oncourse.webservices.v6.stubs.replication.ContactStub contactStub = contactV6();
		ish.oncourse.webservices.v6.stubs.replication.StudentStub studentStub = studentV6();
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
	
	private GenericTransactionGroup prepareV7CreditCardPaymentData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V7);
		ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub enrolStub = enrolmentV7();
		ish.oncourse.webservices.v7.stubs.replication.InvoiceStub invoiceStub = invoiceV7();
		ish.oncourse.webservices.v7.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV7();
		ish.oncourse.webservices.v7.stubs.replication.PaymentInStub paymentInStub = paymentInV7();
		ish.oncourse.webservices.v7.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV7();
		ish.oncourse.webservices.v7.stubs.replication.ContactStub contactStub = contactV7();
		ish.oncourse.webservices.v7.stubs.replication.StudentStub studentStub = studentV7();
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
	
	private void testV6CreditCardPaymentProcessing() throws InternalReplicationFault {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV6CreditCardPaymentData());
		testCreditCardPaymentProcessing(respGroup);
	}
	
	private void testCreditCardPaymentProcessing(GenericTransactionGroup respGroup) {
		assertNotNull("Check Response Group is not null", respGroup);
		GenericEnrolmentStub respEnrolStub = null;
		GenericPaymentInStub respPaymentInStub = null;
		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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
	
	private void testV7CreditCardPaymentProcessing() throws InternalReplicationFault {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV7CreditCardPaymentData());
		testCreditCardPaymentProcessing(respGroup);
	}

	@Test
	public void testV6ProcessPaymentNoPlaces() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV6ProcessPaymentNoPlacesData());
		testProcessPaymentNoPlaces(respGroup);
	}
	
	private GenericTransactionGroup prepareV6ProcessPaymentNoPlacesData() {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V6);
		ish.oncourse.webservices.v6.stubs.replication.ContactStub contactStub1 = contactV6();
		ish.oncourse.webservices.v6.stubs.replication.StudentStub studentStub1 = studentV6();
		ish.oncourse.webservices.v6.stubs.replication.ContactStub contactStub2 = contactV6();
		contactStub2.setAngelId(2l);
		contactStub2.setStudentId(2l);
		ish.oncourse.webservices.v6.stubs.replication.StudentStub studentStub2 = studentV6();
		studentStub2.setAngelId(2l);
		studentStub2.setContactId(2l);
		ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolStub1 = enrolmentV6();
		enrolStub1.setCourseClassId(201l);
		enrolStub1.setStudentId(1l);
		enrolStub1.setInvoiceLineId(4l);
		ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV6();
		enrolStub1.setAngelId(2l);
		enrolStub2.setCourseClassId(201l);
		enrolStub2.setStudentId(2l);
		enrolStub2.setInvoiceLineId(3l);
		ish.oncourse.webservices.v6.stubs.replication.InvoiceStub invoiceStub = invoiceV6();
		//NOTE: that invoicelines with collegeid 1 + angelid 1 and 2 already used by replication test and may not be cleanup.
		ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invLineStub1 = invoiceLineV6();
		invLineStub1.setAngelId(4l);
		ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV6();
		invLineStub2.setAngelId(3l);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v6.stubs.replication.PaymentInStub paymentInStub = paymentInV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV6();
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
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V7);
		ish.oncourse.webservices.v7.stubs.replication.ContactStub contactStub1 = contactV7();
		ish.oncourse.webservices.v7.stubs.replication.StudentStub studentStub1 = studentV7();
		ish.oncourse.webservices.v7.stubs.replication.ContactStub contactStub2 = contactV7();
		contactStub2.setAngelId(2l);
		contactStub2.setStudentId(2l);
		ish.oncourse.webservices.v7.stubs.replication.StudentStub studentStub2 = studentV7();
		studentStub2.setAngelId(2l);
		studentStub2.setContactId(2l);
		ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub enrolStub1 = enrolmentV7();
		enrolStub1.setCourseClassId(201l);
		enrolStub1.setStudentId(1l);
		enrolStub1.setInvoiceLineId(4l);
		ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub enrolStub2 = enrolmentV7();
		enrolStub1.setAngelId(2l);
		enrolStub2.setCourseClassId(201l);
		enrolStub2.setStudentId(2l);
		enrolStub2.setInvoiceLineId(3l);
		ish.oncourse.webservices.v7.stubs.replication.InvoiceStub invoiceStub = invoiceV7();
		//NOTE: that invoicelines with collegeid 1 + angelid 1 and 2 already used by replication test and may not be cleanup.
		ish.oncourse.webservices.v7.stubs.replication.InvoiceLineStub invLineStub1 = invoiceLineV7();
		invLineStub1.setAngelId(4l);
		ish.oncourse.webservices.v7.stubs.replication.InvoiceLineStub invLineStub2 = invoiceLineV7();
		invLineStub2.setAngelId(3l);
		invLineStub2.setEnrolmentId(2l);
		ish.oncourse.webservices.v7.stubs.replication.PaymentInStub paymentInStub = paymentInV7();
		ish.oncourse.webservices.v7.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV7();
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
		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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
	public void testV7ProcessPaymentNoPlaces() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(prepareV6ProcessPaymentNoPlacesData());
		testProcessPaymentNoPlaces(respGroup);
	}

	@Test
	public void testV6ProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPaymentV6(true, false, false);
	}

	@Test
	public void testV6ProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPaymentV6(false, false, false);
	}
	
	@Test
	public void testV7ProcessPaymentZero() throws Exception {
		notCreditCardOrZeroPaymentV7(true, false, false);
	}

	@Test
	public void testV7ProcessNotCreditCardPayment() throws Exception {
		notCreditCardOrZeroPaymentV7(false, false, false);
	}

	@Test
	public void testV6ZeroPaymentForFreeInvoice() throws Exception {
		notCreditCardOrZeroPaymentV6(true, true, false);
	}
	
	@Test
	public void testV6ZeroPaymentForFreeInvoiceWithoutPlaces() throws Exception {
		notCreditCardOrZeroPaymentV6(true, true, true);
	}
	
	@Test
	public void testV7ZeroPaymentForFreeInvoice() throws Exception {
		notCreditCardOrZeroPaymentV7(true, true, false);
	}
	
	@Test
	public void testV7ZeroPaymentForFreeInvoiceWithoutPlaces() throws Exception {
		notCreditCardOrZeroPaymentV7(true, true, true);
	}

	private GenericTransactionGroup notCreditCardOrZeroPaymentV7Data(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V6);
		ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolStub = enrolmentV6();
		ish.oncourse.webservices.v6.stubs.replication.InvoiceStub invoiceStub = invoiceV6();
		ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInStub paymentInStub = paymentInV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV6();
		ish.oncourse.webservices.v6.stubs.replication.ContactStub contactStub = contactV6();
		ish.oncourse.webservices.v6.stubs.replication.StudentStub studentStub = studentV6();

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
			ish.oncourse.webservices.v6.stubs.replication.CourseClassStub courseClassStub = courseClassV6();
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

	private GenericTransactionGroup notCreditCardOrZeroPaymentV6Data(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) {
		GenericTransactionGroup group = PortHelper.createTransactionGroup(SupportedVersions.V6);
		ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolStub = enrolmentV6();
		ish.oncourse.webservices.v6.stubs.replication.InvoiceStub invoiceStub = invoiceV6();
		ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invLineStub = invoiceLineV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInStub paymentInStub = paymentInV6();
		ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub pLineStub = paymentInLineV6();
		ish.oncourse.webservices.v6.stubs.replication.ContactStub contactStub = contactV6();
		ish.oncourse.webservices.v6.stubs.replication.StudentStub studentStub = studentV6();

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
			ish.oncourse.webservices.v6.stubs.replication.CourseClassStub courseClassStub = courseClassV6();
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

		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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

	private void notCreditCardOrZeroPaymentV6(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(notCreditCardOrZeroPaymentV6Data(isZeroPayment, isFreeInvoice, withoutPlaces));
		notCreditCardOrZeroPayment(respGroup, isZeroPayment, isFreeInvoice, withoutPlaces);
	}

	private void notCreditCardOrZeroPaymentV7(boolean isZeroPayment, boolean isFreeInvoice, boolean withoutPlaces) throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processPayment(notCreditCardOrZeroPaymentV7Data(isZeroPayment, isFreeInvoice, withoutPlaces));
		notCreditCardOrZeroPayment(respGroup, isZeroPayment, isFreeInvoice, withoutPlaces);
	}
	
	@Test
	public void testV6PaymentStatus() throws Exception {
		testPaymentStatus(SupportedVersions.V6);
	}
	
	private void testPaymentStatus(SupportedVersions version) throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		//sessionId for in transaction payment, we expect an empty group as response
		String sessionId = "AAVV#$%%%#$3333";
		GenericTransactionGroup respGroup = port.getPaymentStatus(sessionId, version);
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group is empty.", respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//sessionId for payment in success status, we expect not empty group with paymentIn record inside.
		sessionId = "jfjf790aaajjj9900";
		respGroup = port.getPaymentStatus(sessionId, version);
		assertNotNull("Check transaction group is not null.", respGroup);
		assertTrue("Check that group isn't empty.", !respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		GenericPaymentInStub respPaymentInStub = null;
		GenericEnrolmentStub respEnrolmentStub = null;
		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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
		assertTrue("Check that group is empty.", !respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		
		respPaymentInStub = null;
		respEnrolmentStub = null;
		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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
	public void testV7PaymentStatus() throws Exception {
		testPaymentStatus(SupportedVersions.V7);
	}
	
	private GenericTransactionGroup v6ProcessRefundDataSuccess() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V6);
		ish.oncourse.webservices.v6.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v6.stubs.replication.PaymentOutStub();
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
	
	private GenericTransactionGroup v7ProcessRefundDataSuccess() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V7);
		ish.oncourse.webservices.v7.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v7.stubs.replication.PaymentOutStub();
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
	
	private GenericTransactionGroup v6ProcessRefundDataFail() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V6);

		ish.oncourse.webservices.v6.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v6.stubs.replication.PaymentOutStub();
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
	
	private GenericTransactionGroup v7ProcessRefundDataFail() {
		GenericTransactionGroup reqGroup = PortHelper.createTransactionGroup(SupportedVersions.V7);
		ish.oncourse.webservices.v7.stubs.replication.PaymentOutStub paymentOut = new ish.oncourse.webservices.v7.stubs.replication.PaymentOutStub();
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

		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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

		for (GenericReplicationStub stub : respGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
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
	public void testV6ProcessRefund() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processRefund(v6ProcessRefundDataSuccess());
		processRefundSuccess(respGroup);
		respGroup = port.processRefund(v6ProcessRefundDataFail());
		processRefundFailed(respGroup);
	}
	
	@Test
	public void testV7ProcessRefund() throws Exception {
		InternalPaymentService port = getService(InternalPaymentService.class);
		GenericTransactionGroup respGroup = port.processRefund(v7ProcessRefundDataSuccess());
		processRefundSuccess(respGroup);
		respGroup = port.processRefund(v7ProcessRefundDataFail());
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
		
		PaymentIn inversePayment = payment.abandonPayment().iterator().next();
		context.commitChanges();
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", inversePayment.getSessionId(), payment.getSessionId());
		assertEquals("Expecting that attendances remain in the database. Will be deleted later from Angel.", attendanceCount, dbUnitConnection.getRowCount("Attendance"));
		assertEquals("Expecting that outcomes remain in the database until deleted from angel.", 1, dbUnitConnection.getRowCount("Outcome"));
	}
	
	private ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invoiceLineV6() {
		ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub invLineStub = new ish.oncourse.webservices.v6.stubs.replication.InvoiceLineStub();
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

	private ish.oncourse.webservices.v6.stubs.replication.InvoiceStub invoiceV6() {
		ish.oncourse.webservices.v6.stubs.replication.InvoiceStub invoiceStub = new ish.oncourse.webservices.v6.stubs.replication.InvoiceStub();
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

	private ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolmentV6() {
		ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub enrolStub = new ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub();
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
	
	private ish.oncourse.webservices.v6.stubs.replication.CourseClassStub courseClassV6() {
		ish.oncourse.webservices.v6.stubs.replication.CourseClassStub courseClassStub = new ish.oncourse.webservices.v6.stubs.replication.CourseClassStub();
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
		courseClassStub.setDistantLearningCourse(false);
		return courseClassStub;
	}
	
	private ish.oncourse.webservices.v7.stubs.replication.CourseClassStub courseClassV7() {
		ish.oncourse.webservices.v7.stubs.replication.CourseClassStub courseClassStub = new ish.oncourse.webservices.v7.stubs.replication.CourseClassStub();
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
		courseClassStub.setDistantLearningCourse(false);
		return courseClassStub;
	}

	private ish.oncourse.webservices.v6.stubs.replication.StudentStub studentV6() {
		ish.oncourse.webservices.v6.stubs.replication.StudentStub studentStub = new ish.oncourse.webservices.v6.stubs.replication.StudentStub();
		studentStub.setAngelId(1l);
		studentStub.setContactId(1l);
		studentStub.setCountryOfBirthId(86l);
		studentStub.setCreated(today);
		studentStub.setModified(today);
		studentStub.setEntityIdentifier("Student");
		return studentStub;
	}

	private ish.oncourse.webservices.v6.stubs.replication.PaymentInStub paymentInV6() {
		ish.oncourse.webservices.v6.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v6.stubs.replication.PaymentInStub();
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

	private ish.oncourse.webservices.v6.stubs.replication.ContactStub contactV6() {
		ish.oncourse.webservices.v6.stubs.replication.ContactStub contactStub = new ish.oncourse.webservices.v6.stubs.replication.ContactStub();
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

	private ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub paymentInLineV6() {
		ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub pLineStub = new ish.oncourse.webservices.v6.stubs.replication.PaymentInLineStub();
		pLineStub.setAngelId(1l);
		pLineStub.setCreated(today);
		pLineStub.setModified(today);
		pLineStub.setEntityIdentifier("PaymentInLine");
		pLineStub.setInvoiceId(1l);
		pLineStub.setPaymentInId(1l);
		pLineStub.setAmount(new BigDecimal(110l));
		return pLineStub;
	}
	
	private ish.oncourse.webservices.v7.stubs.replication.InvoiceLineStub invoiceLineV7() {
		ish.oncourse.webservices.v7.stubs.replication.InvoiceLineStub invLineStub = new ish.oncourse.webservices.v7.stubs.replication.InvoiceLineStub();
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

	private ish.oncourse.webservices.v7.stubs.replication.InvoiceStub invoiceV7() {
		ish.oncourse.webservices.v7.stubs.replication.InvoiceStub invoiceStub = new ish.oncourse.webservices.v7.stubs.replication.InvoiceStub();
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

	private ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub enrolmentV7() {
		ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub enrolStub = new ish.oncourse.webservices.v7.stubs.replication.EnrolmentStub();
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

	private ish.oncourse.webservices.v7.stubs.replication.StudentStub studentV7() {
		ish.oncourse.webservices.v7.stubs.replication.StudentStub studentStub = new ish.oncourse.webservices.v7.stubs.replication.StudentStub();
		studentStub.setAngelId(1l);
		studentStub.setContactId(1l);
		studentStub.setCountryOfBirthId(86l);
		studentStub.setCreated(today);
		studentStub.setModified(today);
		studentStub.setEntityIdentifier("Student");
		return studentStub;
	}

	private ish.oncourse.webservices.v7.stubs.replication.PaymentInStub paymentInV7() {
		ish.oncourse.webservices.v7.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v7.stubs.replication.PaymentInStub();
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

	private ish.oncourse.webservices.v7.stubs.replication.ContactStub contactV7() {
		ish.oncourse.webservices.v7.stubs.replication.ContactStub contactStub = new ish.oncourse.webservices.v7.stubs.replication.ContactStub();
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

	private ish.oncourse.webservices.v7.stubs.replication.PaymentInLineStub paymentInLineV7() {
		ish.oncourse.webservices.v7.stubs.replication.PaymentInLineStub pLineStub = new ish.oncourse.webservices.v7.stubs.replication.PaymentInLineStub();
		pLineStub.setAngelId(1l);
		pLineStub.setCreated(today);
		pLineStub.setModified(today);
		pLineStub.setEntityIdentifier("PaymentInLine");
		pLineStub.setInvoiceId(1l);
		pLineStub.setPaymentInId(1l);
		pLineStub.setAmount(new BigDecimal(110l));
		return pLineStub;
	}
}
