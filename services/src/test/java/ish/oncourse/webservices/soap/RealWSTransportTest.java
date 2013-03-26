package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.test.PageTester;

public abstract class RealWSTransportTest extends AbstractTransportTest {
	protected static final String V4_PAYMENT_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v4/payment";
	protected static final String V4_REPLICATION_ENDPOINT_PATH = TestServer.DEFAULT_CONTEXT_PATH + "/v4/replication";
	protected static final String V4_REPLICATION_WSDL = "wsdl/v4_replication.wsdl";
	protected static final String CARD_HOLDER_NAME = "john smith";
	protected static final String VALID_CARD_NUMBER = "5431111111111111";
	protected static final String DECLINED_CARD_NUMBER = "9999990000000378";
	protected static final String CREDIT_CARD_CVV = "1111";
	protected static final String VALID_EXPIRITY_MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1 + StringUtils.EMPTY;
	protected static final String VALID_EXPIRITY_YEAR = Calendar.getInstance().get(Calendar.YEAR) + StringUtils.EMPTY;
	
	protected static final String ID_ATTRIBUTE = "id";
	protected static final String ENROLMENT_IDENTIFIER = Enrolment.class.getSimpleName();
	protected static final String INVOICE_LINE_IDENTIFIER = InvoiceLine.class.getSimpleName();
	protected static final String PAYMENT_LINE_IDENTIFIER = PaymentInLine.class.getSimpleName();
	protected static final String INVOICE_IDENTIFIER = Invoice.class.getSimpleName();
	protected static final String PAYMENT_IDENTIFIER = PaymentIn.class.getSimpleName();
	
	protected ServiceTest serviceTest;
	protected PageTester tester;
	protected ICayenneService cayenneService;
		
	protected void fillV4PaymentStubs(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();
		ish.oncourse.webservices.v4.stubs.replication.PaymentInStub paymentInStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.toBigDecimal());
		paymentInStub.setContactId(1l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceStub invoiceStub = new ish.oncourse.webservices.v4.stubs.replication.InvoiceStub();
		invoiceStub.setContactId(1l);
		invoiceStub.setAmountOwing(hundredDollars.toBigDecimal());
		invoiceStub.setAngelId(1l);
		invoiceStub.setCreated(current);
		invoiceStub.setDateDue(current);
		invoiceStub.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub.setInvoiceDate(current);
		invoiceStub.setInvoiceNumber(123l);
		invoiceStub.setModified(current);
		invoiceStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		stubs.add(invoiceStub);
		ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub paymentLineStub = new ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(paymentInStub.getAmount());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);
		ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub invoiceLineStub = new ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(invoiceStub.getAmountOwing());
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);
		ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub enrolmentStub = new ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(invoiceLineStub.getAngelId());
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(1l);
		//link the invoiceLine with enrolment
		invoiceLineStub.setEnrolmentId(enrolmentStub.getAngelId());
		stubs.add(enrolmentStub);
		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
	
	@Override
	protected Long getCommunicationKey() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<College> colleges = context.performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		assertFalse("colleges should not be empty", colleges.isEmpty());
		assertTrue("Only 1 college should have id=1", colleges.size() == 1);
		College college = colleges.get(0);
		assertNotNull("Communication key should be not NULL", college.getCommunicationKey());
		return college.getCommunicationKey();
	}
	
	@Override
	protected String getSecurityCode() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		@SuppressWarnings("unchecked")
		List<College> colleges = context.performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		assertFalse("colleges should not be empty", colleges.isEmpty());
		assertTrue("Only 1 college should have id=1", colleges.size() == 1);
		College college = colleges.get(0);
		assertNotNull("Security code should be not NULL", college.getWebServicesSecurityCode());
		return college.getWebServicesSecurityCode();
	}

	protected ReplicationPortType getReplicationPortType() throws JAXBException {
		return getReplicationPortType(V4_REPLICATION_WSDL, V4_REPLICATION_ENDPOINT_PATH);
	}
	
	protected PaymentPortType getPaymentPortType() throws JAXBException {
		return getPaymentPortType(V4_REPLICATION_WSDL, V4_PAYMENT_ENDPOINT_PATH);
	}

}
