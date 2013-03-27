package ish.oncourse.webservices.replication.services;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationPortTypeTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentServiceImplTest extends ServiceTest {
	private ICayenneService service;
	private College college;
	
	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

		InputStream st = ReplicationPortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/paymentDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
		service = getService(ICayenneService.class);
		@SuppressWarnings("unchecked")
		List<College> colleges = service.sharedContext().performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		service.sharedContext().performQuery(new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l)));
		college = colleges.isEmpty() ? null : colleges.get(0);
	}
	
	private College localizeCollege(ObjectContext context) {
		if (college == null) {
			return null;
		} else {
			return (College) context.localObject(college);
		}
	}
	
	@Test
	public void getLinesForConflictedInvoices() {
		ObjectContext context = service.newContext();
		Contact contact = context.newObject(Contact.class);
		PaymentIn p1 = context.newObject(PaymentIn.class);
		p1.setAmount(new Money("110"));
		p1.setCollege(localizeCollege(context));
		p1.setAngelId(1l);
		p1.setContact(contact);
		contact.setCollege(p1.getCollege());
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_WEB);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		PaymentInLine pil1 = context.newObject(PaymentInLine.class);
		pil1.setPaymentIn(p1);
		pil1.setAngelId(1l);
		pil1.setCreated(new Date());
		pil1.setAmount(new Money("110"));
		pil1.setCollege(p1.getCollege());
		Invoice i1 = context.newObject(Invoice.class);
		pil1.setInvoice(i1);
		i1.setAngelId(1l);
		i1.setAmountOwing(new Money("110"));
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
		i1.setTotalExGst(new Money("100"));
		i1.setTotalGst(new Money("110"));
		i1.setSource(PaymentSource.SOURCE_WEB);
		i1.setCollege(p1.getCollege());
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
		il1.setCollege(p1.getCollege());
		
		PaymentIn p2 = context.newObject(PaymentIn.class);
		p2.setAmount(new Money("110"));
		p2.setAngelId(1l);
		p2.setContact(contact);
		p2.setCreated(new Date());
		p2.setModified(new Date());
		p2.setSource(PaymentSource.SOURCE_WEB);
		p2.setStatus(PaymentStatus.IN_TRANSACTION);
		p2.setType(PaymentType.CREDIT_CARD);
		p2.setCollege(p1.getCollege());
		PaymentInLine pil2 = context.newObject(PaymentInLine.class);
		pil2.setPaymentIn(p2);
		pil2.setAngelId(1l);
		pil2.setCreated(new Date());
		pil2.setAmount(new Money("110"));
		pil2.setInvoice(i1);
		pil2.setCollege(p1.getCollege());
		context.commitChanges();
		
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
		p1.setAmount(new Money("110"));
		p1.setAngelId(1l);
		p1.setContact(contact);
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_WEB);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		p1.setCollege(localizeCollege(context));
		contact.setCollege(p1.getCollege());
		PaymentInLine pil1 = context.newObject(PaymentInLine.class);
		pil1.setPaymentIn(p1);
		pil1.setAngelId(1l);
		pil1.setCreated(new Date());
		pil1.setAmount(new Money("110"));
		pil1.setCollege(p1.getCollege());
		Invoice i1 = context.newObject(Invoice.class);
		pil1.setInvoice(i1);
		i1.setAngelId(1l);
		i1.setAmountOwing(new Money("110"));
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
		i1.setTotalExGst(new Money("100"));
		i1.setTotalGst(new Money("110"));
		i1.setSource(PaymentSource.SOURCE_WEB);
		i1.setCollege(p1.getCollege());
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
		il1.setCollege(p1.getCollege());
		
		PaymentIn p2 = context.newObject(PaymentIn.class);
		p2.setAmount(new Money("110"));
		p2.setAngelId(1l);
		p2.setContact(contact);
		p2.setCreated(new Date());
		p2.setModified(new Date());
		p2.setSource(PaymentSource.SOURCE_ONCOURSE);
		p2.setStatus(PaymentStatus.IN_TRANSACTION);
		p2.setType(PaymentType.CREDIT_CARD);
		p2.setCollege(p1.getCollege());
		PaymentInLine pil2 = context.newObject(PaymentInLine.class);
		pil2.setPaymentIn(p2);
		pil2.setAngelId(1l);
		pil2.setCreated(new Date());
		pil2.setAmount(new Money("110"));
		pil2.setInvoice(i1);
		pil2.setCollege(p1.getCollege());
		context.commitChanges();
		
		InternalPaymentService port = getService(InternalPaymentService.class);
		List<PaymentIn> updatedPayments = new ArrayList<PaymentIn>();
		boolean result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertTrue("Payment should have conflict invoices", result);
		assertTrue("One payment should be updated", !updatedPayments.isEmpty());
		assertEquals("One payment should be updated",1, updatedPayments.size());
		assertEquals("One payment should be updated",p1, updatedPayments.get(0));
		assertEquals("Payment inside the updatedPayments should be failed", PaymentStatus.FAILED, p1.getStatus());
		context.deleteObjects(pil2);
		context.deleteObjects(p2);
		updatedPayments.clear();
		result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertFalse("Payment should have no conflict invoices", result);
		assertTrue("No payments should be updated", updatedPayments.isEmpty());
	}
	
	@Test
	public void isHaveConflictedInInvoicesWithReverse() {
		ObjectContext context = service.newContext();
		Contact contact = context.newObject(Contact.class);
		
		PaymentIn p1 = context.newObject(PaymentIn.class);
		p1.setAmount(new Money("220"));
		p1.setAngelId(1l);
		p1.setContact(contact);
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_ONCOURSE);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		p1.setCollege(localizeCollege(context));
		contact.setCollege(p1.getCollege());
		
		PaymentInLine pil1 = context.newObject(PaymentInLine.class);
		pil1.setPaymentIn(p1);
		pil1.setAngelId(1l);
		pil1.setCreated(new Date());
		pil1.setAmount(new Money("110"));
		pil1.setCollege(p1.getCollege());
		
		Invoice i1 = context.newObject(Invoice.class);
		pil1.setInvoice(i1);
		i1.setAngelId(1l);
		i1.setAmountOwing(new Money("110"));
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
		i1.setTotalExGst(new Money("100"));
		i1.setTotalGst(new Money("110"));
		i1.setSource(PaymentSource.SOURCE_ONCOURSE);
		i1.setCollege(p1.getCollege());
		
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
		il1.setCollege(p1.getCollege());
		
		PaymentIn p2 = context.newObject(PaymentIn.class);
		p2.setAmount(new Money("110"));
		p2.setAngelId(1l);
		p2.setContact(contact);
		p2.setCreated(new Date());
		p2.setModified(new Date());
		p2.setSource(PaymentSource.SOURCE_WEB);
		p2.setStatus(PaymentStatus.IN_TRANSACTION);
		p2.setType(PaymentType.CREDIT_CARD);
		p2.setCollege(p1.getCollege());
		
		PaymentInLine pil2 = context.newObject(PaymentInLine.class);
		pil2.setPaymentIn(p2);
		pil2.setAngelId(1l);
		pil2.setCreated(new Date());
		pil2.setAmount(new Money("110"));
		pil2.setInvoice(i1);
		pil2.setCollege(p1.getCollege());
		
		Invoice i2 = context.newObject(Invoice.class);
		i2.setAngelId(1l);
		i2.setAmountOwing(new Money("110"));
		i2.setBillToAddress("Test billing address");
		i2.setContact(contact);
		i2.setCreated(new Date());
		i2.setCustomerPO("PO");
		i2.setCustomerReference("ref123");
		i2.setDateDue(new Date());
		i2.setDescription("Invoice for accounting course");
		i2.setInvoiceDate(new Date());
		i2.setInvoiceNumber(1234l);
		i2.setModified(new Date());
		i2.setTotalExGst(new Money("100"));
		i2.setTotalGst(new Money("110"));
		i2.setSource(PaymentSource.SOURCE_ONCOURSE);
		i2.setCollege(p1.getCollege());
		
		InvoiceLine il2 = context.newObject(InvoiceLine.class);
		il2.setInvoice(i2);
		il2.setAngelId(1l);
		il2.setCreated(new Date());
		il2.setDescription("Invoice line item  for accounting course");
		il2.setDiscountEachExTax(Money.ZERO);
		il2.setModified(new Date());
		il2.setPriceEachExTax(new Money("110.00"));
		il2.setQuantity(new BigDecimal(1));
		il2.setTaxEach(Money.ZERO);
		il2.setTitle("Accouting course item");
		il2.setUnit("unit");
		il2.setCollege(p1.getCollege());
		
		Enrolment e1 = context.newObject(Enrolment.class);
		e1.setAngelId(1l);
		@SuppressWarnings("unchecked")
		List<CourseClass> courseClasses = context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1186958L)));
		e1.setCourseClass((CourseClass) context.localObject(courseClasses.get(0)));
		e1.setCreated(new Date());
		e1.setInvoiceLine(il2);
		e1.setModified(new Date());
		e1.setReasonForStudy(1);
		e1.setSource(PaymentSource.SOURCE_WEB);
		e1.setStatus(EnrolmentStatus.IN_TRANSACTION);
		@SuppressWarnings("unchecked")
		List<Student> students = context.performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 1189147L)));
		e1.setStudent((Student) context.localObject(students.get(0)));
		e1.setCollege(p1.getCollege());
		
		PaymentInLine pil3 = context.newObject(PaymentInLine.class);
		pil3.setPaymentIn(p1);
		pil3.setAngelId(1l);
		pil3.setCreated(new Date());
		pil3.setAmount(new Money("110"));
		pil3.setInvoice(i2);
		pil3.setCollege(p1.getCollege());

		context.commitChanges();
		
		InternalPaymentService port = getService(InternalPaymentService.class);
		List<PaymentIn> updatedPayments = new ArrayList<PaymentIn>();
		boolean result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		i1.updateAmountOwing();
		i2.updateAmountOwing();
		assertTrue("Payment should have conflict invoices", result);
		assertTrue("One payment should be updated", !updatedPayments.isEmpty());
		assertEquals("Two payments should be updated", 2, updatedPayments.size());
		assertEquals("Two payments should be updated, second should be p1", p1, updatedPayments.get(1));
		assertEquals("Payment inside the updatedPayments should be failed", PaymentStatus.FAILED, p1.getStatus());
		assertEquals("Amount owing for i2 should be 0 because we revert it", Money.ZERO, i2.getAmountOwing());
		assertEquals("Inoice 1 amount owing should be positive and = 120$", new Money("120.00"), i1.getAmountOwing());
		context.deleteObjects(pil2);
		context.deleteObjects(p2);
		updatedPayments.clear();
		result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertFalse("Payment should have no conflict invoices", result);
		assertTrue("No payments should be updated", updatedPayments.isEmpty());
	}
	
	@Test
	public void isHaveConflictedInInvoicesWithoutReverseByEnrolmentStatus() {
		ObjectContext context = service.newContext();
		Contact contact = context.newObject(Contact.class);
		
		PaymentIn p1 = context.newObject(PaymentIn.class);
		p1.setAmount(new Money("220"));
		p1.setAngelId(1l);
		p1.setContact(contact);
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_ONCOURSE);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		p1.setCollege(localizeCollege(context));
		contact.setCollege(p1.getCollege());
		
		PaymentInLine pil1 = context.newObject(PaymentInLine.class);
		pil1.setPaymentIn(p1);
		pil1.setAngelId(1l);
		pil1.setCreated(new Date());
		pil1.setAmount(new Money("110"));
		pil1.setCollege(p1.getCollege());
		
		Invoice i1 = context.newObject(Invoice.class);
		pil1.setInvoice(i1);
		i1.setAngelId(1l);
		i1.setAmountOwing(new Money("110"));
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
		i1.setTotalExGst(new Money("100"));
		i1.setTotalGst(new Money("110"));
		i1.setSource(PaymentSource.SOURCE_ONCOURSE);
		i1.setCollege(p1.getCollege());
		
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
		il1.setCollege(p1.getCollege());
		
		PaymentIn p2 = context.newObject(PaymentIn.class);
		p2.setAmount(new Money("110"));
		p2.setAngelId(1l);
		p2.setContact(contact);
		p2.setCreated(new Date());
		p2.setModified(new Date());
		p2.setSource(PaymentSource.SOURCE_WEB);
		p2.setStatus(PaymentStatus.IN_TRANSACTION);
		p2.setType(PaymentType.CREDIT_CARD);
		p2.setCollege(p1.getCollege());
		
		PaymentInLine pil2 = context.newObject(PaymentInLine.class);
		pil2.setPaymentIn(p2);
		pil2.setAngelId(1l);
		pil2.setCreated(new Date());
		pil2.setAmount(new Money("110"));
		pil2.setInvoice(i1);
		pil2.setCollege(p1.getCollege());
		
		Invoice i2 = context.newObject(Invoice.class);
		i2.setAngelId(1l);
		i2.setAmountOwing(new Money("110"));
		i2.setBillToAddress("Test billing address");
		i2.setContact(contact);
		i2.setCreated(new Date());
		i2.setCustomerPO("PO");
		i2.setCustomerReference("ref123");
		i2.setDateDue(new Date());
		i2.setDescription("Invoice for accounting course");
		i2.setInvoiceDate(new Date());
		i2.setInvoiceNumber(1234l);
		i2.setModified(new Date());
		i2.setTotalExGst(new Money("100"));
		i2.setTotalGst(new Money("110"));
		i2.setSource(PaymentSource.SOURCE_ONCOURSE);
		i2.setCollege(p1.getCollege());
		
		InvoiceLine il2 = context.newObject(InvoiceLine.class);
		il2.setInvoice(i2);
		il2.setAngelId(1l);
		il2.setCreated(new Date());
		il2.setDescription("Invoice line item  for accounting course");
		il2.setDiscountEachExTax(Money.ZERO);
		il2.setModified(new Date());
		il2.setPriceEachExTax(new Money("110.00"));
		il2.setQuantity(new BigDecimal(1));
		il2.setTaxEach(Money.ZERO);
		il2.setTitle("Accouting course item");
		il2.setUnit("unit");
		il2.setCollege(p1.getCollege());
		
		Enrolment e1 = context.newObject(Enrolment.class);
		e1.setAngelId(1l);
		@SuppressWarnings("unchecked")
		List<CourseClass> courseClasses = context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1186958L)));
		e1.setCourseClass((CourseClass) context.localObject(courseClasses.get(0)));
		e1.setCreated(new Date());
		e1.setModified(new Date());
		e1.setReasonForStudy(1);
		e1.setSource(PaymentSource.SOURCE_WEB);
		e1.setStatus(EnrolmentStatus.SUCCESS);
		@SuppressWarnings("unchecked")
		List<Student> students = context.performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 1189147L)));
		e1.setStudent((Student) context.localObject(students.get(0)));
		e1.setInvoiceLine(il2);
		e1.setCollege(p1.getCollege());
		
		PaymentInLine pil3 = context.newObject(PaymentInLine.class);
		pil3.setPaymentIn(p1);
		pil3.setAngelId(1l);
		pil3.setCreated(new Date());
		pil3.setAmount(new Money("110"));
		pil3.setInvoice(i2);
		pil3.setCollege(p1.getCollege());
		
		context.commitChanges();
		
		InternalPaymentService port = getService(InternalPaymentService.class);
		List<PaymentIn> updatedPayments = new ArrayList<PaymentIn>();
		boolean result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertTrue("Payment should have conflict invoices", result);
		assertTrue("One payment should be updated", !updatedPayments.isEmpty());
		assertEquals("Two payments should be updated", 1, updatedPayments.size());
		assertEquals("Two payments should be updated, second should be p1", p1, updatedPayments.get(0));
		assertEquals("Payment inside the updatedPayments should be failed", PaymentStatus.FAILED, p1.getStatus());
		assertEquals("Amount owing for i2 should be 110 because we don't revert it", new Money("110.00"), i2.getAmountOwing());
		assertEquals("Inoice 1 amount owing should be positive and = 120$", new Money("120.00"), i1.getAmountOwing());
		context.deleteObjects(pil2);
		context.deleteObjects(p2);
		updatedPayments.clear();
		result = port.isHaveConflictedInInvoices(p1, updatedPayments);
		assertFalse("Payment should have no conflict invoices", result);
		assertTrue("No payments should be updated", updatedPayments.isEmpty());
	}
	
	@Test
	public void isEnrolmentsCorrect() {
		ObjectContext context = service.newContext();
		Contact contact = context.newObject(Contact.class);
		
		PaymentIn p1 = context.newObject(PaymentIn.class);
		p1.setAmount(Money.ZERO);
		p1.setAngelId(1l);
		p1.setContact(contact);
		p1.setCreated(new Date());
		p1.setModified(new Date());
		p1.setSource(PaymentSource.SOURCE_ONCOURSE);
		p1.setStatus(PaymentStatus.IN_TRANSACTION);
		p1.setType(PaymentType.CREDIT_CARD);
		p1.setCollege(localizeCollege(context));
		contact.setCollege(p1.getCollege());
		
		Invoice i1 = context.newObject(Invoice.class);
		i1.setAngelId(1l);
		i1.setAmountOwing(new Money("110"));
		i1.setBillToAddress("Test billing address");
		i1.setContact(contact);
		i1.setCreated(new Date());
		i1.setCustomerPO("PO");
		i1.setCustomerReference("ref123");
		i1.setDateDue(new Date());
		i1.setDescription("Invoice for accounting course");
		i1.setInvoiceDate(new Date());
		i1.setInvoiceNumber(1234l);
		i1.setModified(new Date());
		i1.setTotalExGst(Money.ZERO);
		i1.setTotalGst(Money.ZERO);
		i1.setSource(PaymentSource.SOURCE_ONCOURSE);
		i1.setCollege(p1.getCollege());
		
		InvoiceLine il1 = context.newObject(InvoiceLine.class);
		il1.setInvoice(i1);
		il1.setAngelId(1l);
		il1.setCreated(new Date());
		il1.setDescription("Invoice line item  for accounting course");
		il1.setDiscountEachExTax(Money.ZERO);
		il1.setModified(new Date());
		il1.setPriceEachExTax(Money.ZERO);
		il1.setQuantity(new BigDecimal(1));
		il1.setTaxEach(Money.ZERO);
		il1.setTitle("Accouting course item");
		il1.setUnit("unit");
		il1.setCollege(p1.getCollege());
		
		InvoiceLine il2 = context.newObject(InvoiceLine.class);
		il2.setInvoice(i1);
		il2.setAngelId(2l);
		il2.setCreated(new Date());
		il2.setDescription("Invoice line item  for accounting course");
		il2.setDiscountEachExTax(Money.ZERO);
		il2.setModified(new Date());
		il2.setPriceEachExTax(Money.ZERO);
		il2.setQuantity(new BigDecimal(1));
		il2.setTaxEach(Money.ZERO);
		il2.setTitle("Accouting course item");
		il2.setUnit("unit");
		il2.setCollege(p1.getCollege());
		
		Enrolment e1 = context.newObject(Enrolment.class);
		e1.setAngelId(1l);
		@SuppressWarnings("unchecked")
		List<CourseClass> courseClasses = context.performQuery(new SelectQuery(CourseClass.class, 
			ExpressionFactory.matchDbExp(CourseClass.ID_PK_COLUMN, 1186958L)));
		e1.setCourseClass((CourseClass) context.localObject(courseClasses.get(0)));
		e1.setCreated(new Date());
		e1.setInvoiceLine(il1);
		e1.setModified(new Date());
		e1.setReasonForStudy(1);
		e1.setSource(PaymentSource.SOURCE_WEB);
		e1.setStatus(EnrolmentStatus.FAILED);
		@SuppressWarnings("unchecked")
		List<Student> students = context.performQuery(new SelectQuery(Student.class, ExpressionFactory.matchDbExp(Student.ID_PK_COLUMN, 1189147L)));
		e1.setStudent((Student) context.localObject(students.get(0)));
		e1.setCollege(p1.getCollege());
		
		Enrolment e2 = context.newObject(Enrolment.class);
		e2.setAngelId(2l);
		e2.setCourseClass(e1.getCourseClass());
		e2.setCreated(new Date());
		e2.setInvoiceLine(il2);
		e2.setModified(new Date());
		e2.setReasonForStudy(1);
		e2.setSource(PaymentSource.SOURCE_WEB);
		e2.setStatus(EnrolmentStatus.SUCCESS);
		e2.setStudent(e1.getStudent());
		e2.setCollege(p1.getCollege());
		
		PaymentInLine pil3 = context.newObject(PaymentInLine.class);
		pil3.setPaymentIn(p1);
		pil3.setAngelId(1l);
		pil3.setCreated(new Date());
		pil3.setAmount(Money.ZERO);
		pil3.setInvoice(i1);
		pil3.setCollege(p1.getCollege());
		
		context.commitChanges();
		
		InternalPaymentService port = getService(InternalPaymentService.class);
		assertFalse("Failed enrolment for zero owing invoice isn't a  correct enrollment", port.isEnrolmentsCorrect(Arrays.asList(e1)));
				assertTrue("Success enrolment for zero owing invoice should return true ", port.isEnrolmentsCorrect(Arrays.asList(e2)));
	}

}
