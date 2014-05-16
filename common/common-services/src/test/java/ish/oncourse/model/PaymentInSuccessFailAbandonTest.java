package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.EJBQLQuery;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.Assert.*;

public class PaymentInSuccessFailAbandonTest extends ServiceTest {
	
	private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/lifecycle/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream("ish/oncourse/model/paymentDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void testPaymentSuccess() throws Exception {
		PaymentIn paymentIn = Cayenne.objectForPK(cayenneService.newContext(), PaymentIn.class, 2000);
		paymentIn.succeed();
		paymentIn.getObjectContext().commitChanges();
		
		//check replication queue, 
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select transactionId from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		BigInteger transactionId = (BigInteger) actualData.getValue(0, "transactionId");
		assertNotNull("Transaction id not null", transactionId);
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertEquals("1 Enrolment in the queue.", 1, actualData.getRowCount());
		
		assertEquals("Payment status success.", PaymentStatus.SUCCESS, paymentIn.getStatus());
		
		Enrolment enrol = Cayenne.objectForPK(cayenneService.newContext(), Enrolment.class, 2000);
		assertEquals("Enrol status sucess.", EnrolmentStatus.SUCCESS, enrol.getStatus());
	}
	
	@Test
	public void testSuccessWebPayment() throws Exception {
		
		ObjectContext context = cayenneService.newContext();
		College college = Cayenne.objectForPK(context, College.class, 1);
		WebSite webSite = Cayenne.objectForPK(context, WebSite.class, 1);
		CourseClass courseClass = Cayenne.objectForPK(context, CourseClass.class, 1186958);
		CourseClass courseClass2 = Cayenne.objectForPK(context, CourseClass.class, 1186959);
		Calendar calendar = Calendar.getInstance();
		
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		paymentIn.setCollege(college);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setAmount(new Money("310"));
		paymentIn.setSource(PaymentSource.SOURCE_WEB);
		paymentIn.setType(PaymentType.CREDIT_CARD);

		calendar.add(Calendar.DAY_OF_MONTH, 5);

		Contact contact1 = (Contact) context.newObject(Contact.class);
		contact1.setGivenName("Test_Payer");
		contact1.setFamilyName("Test_Payer");
		contact1.setCollege(college);
		
		Student student1 = (Student) context.newObject(Student.class);
		student1.setCollege(college);
		student1.setContact(contact1);
		
		Contact contact2 = (Contact) context.newObject(Contact.class);
		contact2.setGivenName("Test_Payer2");
		contact2.setFamilyName("Test_Payer2");
		contact2.setCollege(college);
		
		Student student2 = (Student) context.newObject(Student.class);
		student2.setCollege(college);
		student2.setContact(contact1);
		
		Invoice invoice1 = context.newObject(Invoice.class);
		invoice1.setAngelId(100l);
		invoice1.setAmountOwing(new Money("150"));
		invoice1.setInvoiceNumber(100l);
		invoice1.setCollege(college);
		invoice1.setInvoiceDate(calendar.getTime());
		invoice1.setTotalExGst(new Money("150"));
		invoice1.setTotalGst(new Money("150"));
		invoice1.setDateDue(calendar.getTime());
		invoice1.setContact(contact1);
		invoice1.setSource(PaymentSource.SOURCE_WEB);
		invoice1.setWebSite(webSite);

		Enrolment enrol1 = new Enrolment();
		enrol1.setCourseClass(courseClass);
		enrol1.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrol1.setCollege(college);
		enrol1.setSource(PaymentSource.SOURCE_ONCOURSE);
		enrol1.setStudent(student1);

		InvoiceLine invLine1 = context.newObject(InvoiceLine.class);
		invLine1.setTitle("Test_invLine1");
		invLine1.setCollege(college);
		invLine1.setPriceEachExTax(new Money(new BigDecimal(150)));
		invLine1.setTaxEach(Money.ZERO);
		invLine1.setQuantity(new BigDecimal(1));
		invLine1.setDiscountEachExTax(Money.ZERO);
		invLine1.setEnrolment(enrol1);
		invLine1.setInvoice(invoice1);

		Enrolment enrol2 = new Enrolment();
		enrol2.setCourseClass(courseClass2);
		enrol2.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrol2.setCollege(college);
		enrol2.setSource(PaymentSource.SOURCE_ONCOURSE);
		enrol2.setStudent(student2);
		
		InvoiceLine invLine2 = context.newObject(InvoiceLine.class);
		invLine2.setTitle("Test_invLine2");
		invLine2.setCollege(college);
		invLine2.setEnrolment(enrol2);
		invLine2.setPriceEachExTax(new Money(new BigDecimal(160)));
		invLine2.setTaxEach(Money.ZERO);
		invLine2.setQuantity(new BigDecimal(1));
		invLine2.setDiscountEachExTax(Money.ZERO);
		invLine2.setInvoice(invoice1);

		PaymentInLine pLine1 = context.newObject(PaymentInLine.class);
		pLine1.setAmount(new Money("310"));
		pLine1.setCollege(college);
		pLine1.setInvoice(invoice1);
		paymentIn.addToPaymentInLines(pLine1);

		context.commitChanges();
		
		assertEquals("Check paymentIn saved.", false, paymentIn.getObjectId().isTemporary());
		assertEquals("Check invoice1 saved.", false, invoice1.getObjectId().isTemporary());

		//check replication queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn'"));
		assertEquals("zero PaymentIn in the queue.", 0, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine'"));
		assertEquals("zero PaymentInLine in the queue.", 0, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Invoice'"));
		// invoice ant its lines should not go to the queue until one of its payments is in final state (this is needed for 2.1.x email notifications to work properly)
		assertEquals("no Invoices should be in the queue.", 0, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine'"));
		assertEquals("no InvoiceLines in the queue.", 0, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment'"));
		assertEquals("zero Enrolment in the queue.", 0, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Contact'"));
		assertEquals("2 Contacts in the queue.", 2, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Student'"));
		assertEquals("2 Students in the queue.", 2, actualData.getRowCount());
		
		//clean up QueuedRecords
		context.performQuery(new EJBQLQuery("delete from QueuedRecord"));
		context.performQuery(new EJBQLQuery("delete from QueuedTransaction"));
		context.commitChanges();
		
		paymentIn.succeed();
		context.commitChanges();
		
		dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=%s", invoice1.getId()));
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select transactionId from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=%s", invLine1.getId()));
		assertEquals("1 InvoiceLine1 in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select transactionId from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=%s", invLine2.getId()));
		assertEquals("1 InvoiceLine2 in the queue.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select transactionId from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=%s", paymentIn.getId()));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select transactionId from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=%s", pLine1.getId()));
		assertEquals("1 PaymentInLine1 in the queue.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=%s", enrol1.getId()));
		assertEquals("1 Enrolment1 in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=%s", enrol2.getId()));
		assertEquals("1 Enrolment2 in the queue.", 1, actualData.getRowCount());
		
		assertEquals("Payment status success.", PaymentStatus.SUCCESS, paymentIn.getStatus());
		assertEquals("Enrol1 status sucess.", EnrolmentStatus.SUCCESS, enrol1.getStatus());
		assertEquals("Enrol2 status sucess.", EnrolmentStatus.SUCCESS, enrol2.getStatus());
	}
	
	@Test
	public void testPaymentFail() throws Exception {
		PaymentIn paymentIn = Cayenne.objectForPK(cayenneService.newContext(), PaymentIn.class, 2000);
		paymentIn.failPayment();
		paymentIn.getObjectContext().commitChanges();
		
		//check replication queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		BigInteger transactionId = (BigInteger) actualData.getValue(0, "transactionId");
		assertNotNull("Transaction id not null", transactionId);
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertEquals("1 Enrolment in the queue.", 1, actualData.getRowCount());
		
		assertEquals("Payment status success.", PaymentStatus.FAILED, paymentIn.getStatus());
		
		Enrolment enrol = Cayenne.objectForPK(cayenneService.newContext(), Enrolment.class, 2000);
		assertEquals("Enrol status sucess.", EnrolmentStatus.IN_TRANSACTION, enrol.getStatus());
	}
	
	@Test
	public void testPaymentAbandon() throws Exception {
		PaymentIn paymentIn = Cayenne.objectForPK(cayenneService.newContext(), PaymentIn.class, 2000);
        PaymentIn reversePayment = null;
        PaymentIn directPayment = null;
        Collection<PaymentIn> paymentIns = paymentIn.abandonPayment();
        assertEquals("we should get only 2 payments: one direct and one reverse", 2, paymentIns.size());
        for (PaymentIn next : paymentIns) {
            if (next.getType() == PaymentType.REVERSE) {
                assertNull("REVERSE payment should be only one", reversePayment);
                reversePayment = next;
            }
            else
                directPayment = next;
        }

		paymentIn.getObjectContext().commitChanges();
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", reversePayment.getSessionId(), paymentIn.getSessionId());
		
		assertEquals("Check type internal.", PaymentType.REVERSE, reversePayment.getType());
		assertEquals("Zero amount.", 0, reversePayment.getAmount().intValue());
		
		//check replication queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		BigInteger transactionId = (BigInteger) actualData.getValue(0, "transactionId");
		assertNotNull("Transaction id not null", transactionId);
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=%s", reversePayment.getId()));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 inverse PaymentIn in the queue.", 1, actualData.getRowCount());
		
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertEquals("1 Enrolment in the queue.", 1, actualData.getRowCount());
		
		assertEquals("Payment status success.", PaymentStatus.FAILED, paymentIn.getStatus());
		
		Enrolment enrol = Cayenne.objectForPK(cayenneService.newContext(), Enrolment.class, 2000);
		assertEquals("Enrol status sucess.", EnrolmentStatus.FAILED, enrol.getStatus());
	}
	
	@Test
	public void testPaymentAbandonKeepInvoice() throws Exception {
		PaymentIn paymentIn = Cayenne.objectForPK(cayenneService.newContext(), PaymentIn.class, 2000);
		paymentIn.abandonPaymentKeepInvoice();
		paymentIn.getObjectContext().commitChanges();
		
		//check replication queue
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		ITable actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Invoice' and entityWillowId=2000"));
		BigInteger transactionId = (BigInteger) actualData.getValue(0, "transactionId");
		assertNotNull("Transaction id not null", transactionId);
		assertEquals("1 Invoice in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='InvoiceLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 InvoiceLine in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentIn' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='PaymentInLine' and entityWillowId=2000"));
		assertEquals("The same transactionId", transactionId, actualData.getValue(0, "transactionId"));
		assertEquals("1 PaymentIn in the queue.", 1, actualData.getRowCount());
		actualData = dbUnitConnection.createQueryTable("QueuedRecord", String.format("select * from QueuedRecord where entityIdentifier='Enrolment' and entityWillowId=2000"));
		assertEquals("1 Enrolment in the queue.", 1, actualData.getRowCount());
		
		assertEquals("Payment status success.", PaymentStatus.FAILED, paymentIn.getStatus());
		
		Enrolment enrol = Cayenne.objectForPK(cayenneService.newContext(), Enrolment.class, 2000);
		assertEquals("Enrol status sucess.", EnrolmentStatus.SUCCESS, enrol.getStatus());
	}
}
