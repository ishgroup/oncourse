package ish.oncourse.webservices.replication.services;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationPortTypeTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;

public class PaymentServiceImplTest extends ServiceTest {
	private ICayenneService service;
	
	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/paymentDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
		service = getService(ICayenneService.class);
	}
	
	@Test
	public void getLinesForConflictedInvoices() {
		ObjectContext context = service.newContext();
		Contact contact = context.newObject(Contact.class);
		PaymentIn p1 = context.newObject(PaymentIn.class);
		p1.setAmount(new BigDecimal(110));
		p1.setAngelId(1l);
		p1.setContact(contact);
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_WEB);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		PaymentInLine pil1 = context.newObject(PaymentInLine.class);
		pil1.setPaymentIn(p1);
		pil1.setAngelId(1l);
		pil1.setCreated(new Date());
		pil1.setAmount(new BigDecimal(110l));
		Invoice i1 = context.newObject(Invoice.class);
		pil1.setInvoice(i1);
		i1.setAngelId(1l);
		i1.setAmountOwing(new BigDecimal(110l));
		i1.setBillToAddress("Test billing address");
		i1.setContact(contact);
		i1.setCreated(new Date());
		i1.setCustomerPO("PO");
		i1.setCustomerReference("ref123");
		i1.setDateDue(new Date());
		i1.setDescription("Invoice for accounting course");
		i1.setInvoiceDate(new Date());
		i1.setInvoiceNumber(123l);
		i1.setModified(new Date());
		i1.setTotalExGst(new BigDecimal(100));
		i1.setTotalGst(new BigDecimal(110));
		i1.setSource(PaymentSource.SOURCE_WEB);
		InvoiceLine il1 = context.newObject(InvoiceLine.class);
		il1.setInvoice(i1);
		il1.setAngelId(1l);
		il1.setCreated(new Date());
		il1.setDescription("Invoice line item  for accounting course");
		il1.setDiscountEachExTax(Money.ZERO);
		il1.setModified(new Date());
		il1.setPriceEachExTax(new Money("110.00"));
		il1.setQuantity(new BigDecimal(1));
		il1.setTaxEach(new Money("10.00"));
		il1.setTitle("Accouting course item");
		il1.setUnit("unit");
		
		PaymentIn p2 = context.newObject(PaymentIn.class);
		p2.setAmount(new BigDecimal(110));
		p2.setAngelId(1l);
		p2.setContact(contact);
		p2.setCreated(new Date());
		p2.setModified(new Date());
		p2.setSource(PaymentSource.SOURCE_WEB);
		p2.setStatus(PaymentStatus.IN_TRANSACTION);
		p2.setType(PaymentType.CREDIT_CARD);
		PaymentInLine pil2 = context.newObject(PaymentInLine.class);
		pil2.setPaymentIn(p2);
		pil2.setAngelId(1l);
		pil2.setCreated(new Date());
		pil2.setAmount(new BigDecimal(110l));
		pil2.setInvoice(i1);
		
		InternalPaymentService port = getService(InternalPaymentService.class);
		List<ObjectId> conflictInvoiceObjectIds = port.getLinesForConflictedInvoices(p1);
		assertEquals("One conflict invoice exist",1, conflictInvoiceObjectIds.size());
		assertEquals("One conflict invoice exist",i1.getObjectId(), conflictInvoiceObjectIds.get(0));
	}
	
	@Test
	public void isHaveConflictedInInvoices() {
		ObjectContext context = service.newContext();
		Contact contact = context.newObject(Contact.class);
		PaymentIn p1 = context.newObject(PaymentIn.class);
		p1.setAmount(new BigDecimal(110));
		p1.setAngelId(1l);
		p1.setContact(contact);
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_WEB);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		PaymentInLine pil1 = context.newObject(PaymentInLine.class);
		pil1.setPaymentIn(p1);
		pil1.setAngelId(1l);
		pil1.setCreated(new Date());
		pil1.setAmount(new BigDecimal(110l));
		Invoice i1 = context.newObject(Invoice.class);
		pil1.setInvoice(i1);
		i1.setAngelId(1l);
		i1.setAmountOwing(new BigDecimal(110l));
		i1.setBillToAddress("Test billing address");
		i1.setContact(contact);
		i1.setCreated(new Date());
		i1.setCustomerPO("PO");
		i1.setCustomerReference("ref123");
		i1.setDateDue(new Date());
		i1.setDescription("Invoice for accounting course");
		i1.setInvoiceDate(new Date());
		i1.setInvoiceNumber(123l);
		i1.setModified(new Date());
		i1.setTotalExGst(new BigDecimal(100));
		i1.setTotalGst(new BigDecimal(110));
		i1.setSource(PaymentSource.SOURCE_WEB);
		InvoiceLine il1 = context.newObject(InvoiceLine.class);
		il1.setInvoice(i1);
		il1.setAngelId(1l);
		il1.setCreated(new Date());
		il1.setDescription("Invoice line item  for accounting course");
		il1.setDiscountEachExTax(Money.ZERO);
		il1.setModified(new Date());
		il1.setPriceEachExTax(new Money("110.00"));
		il1.setQuantity(new BigDecimal(1));
		il1.setTaxEach(new Money("10.00"));
		il1.setTitle("Accouting course item");
		il1.setUnit("unit");
		
		PaymentIn p2 = context.newObject(PaymentIn.class);
		p2.setAmount(new BigDecimal(110));
		p2.setAngelId(1l);
		p2.setContact(contact);
		p2.setCreated(new Date());
		p2.setModified(new Date());
		p2.setSource(PaymentSource.SOURCE_WEB);
		p2.setStatus(PaymentStatus.IN_TRANSACTION);
		p2.setType(PaymentType.CREDIT_CARD);
		PaymentInLine pil2 = context.newObject(PaymentInLine.class);
		pil2.setPaymentIn(p2);
		pil2.setAngelId(1l);
		pil2.setCreated(new Date());
		pil2.setAmount(new BigDecimal(110l));
		pil2.setInvoice(i1);
		
		InternalPaymentService port = getService(InternalPaymentService.class);
		List<PaymentIn> updatedPayments = new ArrayList<PaymentIn>();
		boolean result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertTrue("Payment should have conflict invoices", result);
		context.deleteObject(pil2);
		context.deleteObject(p2);
		result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertFalse("Payment should have no conflict invoices", result);
	}

}
