package ish.oncourse.model;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;

public class PaymentInLineTest {
	
	private ObjectContext context;
	private Calendar calendar;
	private College college;
	private Course course;	
	private CourseClass courseClass;
	private Invoice invoice;

	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		context = ContextUtils.createObjectContext();
		calendar = Calendar.getInstance();		
		college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		context.commitChanges();
		
		course = context.newObject(Course.class);
		course.setCollege(college);
		
		courseClass = context.newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(college);
		courseClass.setMaximumPlaces(3);
		
		context.commitChanges();
	}
	
	@Test
	public void testCorrectEnrol() throws Exception {
		final PaymentIn paymentIn = prepareBaseData(new BigDecimal(175));
		context.commitChanges();
		assertTrue(!paymentIn.getPaymentInLines().isEmpty());
		assertNotNull("PaymentInline id should not be null", paymentIn.getPaymentInLines().get(0).getId());
	}
	
	private PaymentIn prepareBaseData(final BigDecimal firstPaymentAmount) {
		
		final PaymentIn paymentIn = context.newObject(PaymentIn.class);
		paymentIn.setAngelId(100L);
		paymentIn.setCollege(college);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setAmount(firstPaymentAmount);
		paymentIn.setSource(PaymentSource.SOURCE_WEB);		
		calendar.add(Calendar.DAY_OF_MONTH, 5);
		
		final Contact contact = context.newObject(Contact.class);
		contact.setGivenName("testuser");
		contact.setFamilyName("testuser");
		contact.setCollege(college);
		
		//First invoice
		invoice = context.newObject(Invoice.class);
		invoice.setAngelId(100L);
		invoice.setAmountOwing(new BigDecimal(75));
		invoice.setInvoiceNumber(100L);
		invoice.setCollege(college);
		invoice.setInvoiceDate(calendar.getTime());
		invoice.setTotalExGst(new BigDecimal(75));
		invoice.setTotalGst(new BigDecimal(75));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setDateDue(calendar.getTime());
		invoice.setContact(contact);
		
		final InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setTitle("Test_invLine");
		invoiceLine.setCollege(college);
		invoiceLine.setPriceEachExTax(new Money(new BigDecimal(75)));
		invoiceLine.setTaxEach(new Money(new BigDecimal(0)));
		invoiceLine.setQuantity(new BigDecimal(1));
		invoiceLine.setDiscountEachExTax(new Money(new BigDecimal(0)));
		invoice.addToInvoiceLines(invoiceLine);
		
		final PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
		paymentInLine.setAmount(new BigDecimal(75));
		paymentInLine.setCollege(college);
		paymentInLine.setInvoice(invoice);
		paymentIn.addToPaymentInLines(paymentInLine);
		
		//second invoice
		Invoice invoice2 = context.newObject(Invoice.class);
		invoice2.setAngelId(100L);
		invoice2.setAmountOwing(new BigDecimal(100));
		invoice2.setInvoiceNumber(101L);
		invoice2.setCollege(college);
		invoice2.setInvoiceDate(calendar.getTime());
		invoice2.setTotalExGst(new BigDecimal(100));
		invoice2.setTotalGst(new BigDecimal(100));
		invoice2.setStatus(InvoiceStatus.PENDING);
		invoice2.setDateDue(calendar.getTime());
		invoice2.setContact(contact);
		
		final InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setTitle("Test_invLine");
		invoiceLine2.setCollege(college);
		invoiceLine2.setPriceEachExTax(new Money(new BigDecimal(100)));
		invoiceLine2.setTaxEach(new Money(new BigDecimal(0)));
		invoiceLine2.setQuantity(new BigDecimal(1));
		invoiceLine2.setDiscountEachExTax(new Money(new BigDecimal(0)));
		invoice2.addToInvoiceLines(invoiceLine2);
		
		final PaymentInLine paymentInLine2 = context.newObject(PaymentInLine.class);
		paymentInLine2.setAmount(new BigDecimal(100));
		paymentInLine2.setCollege(college);
		paymentInLine2.setInvoice(invoice2);
		paymentIn.addToPaymentInLines(paymentInLine2);
		
		return paymentIn;
	}
	
	@Test
	public void testInCorrectEnrolByAmount() throws Exception {
		prepareBaseData(new BigDecimal(145));
		
		boolean invalid = false;
		try {
			context.commitChanges();
		} catch (ValidationException e) {
			invalid = true;
		}
		assertTrue("PaymentInLine not the same as the paymentIn ammount!", invalid);
	}
	
	@Test
	public void testInCorrectEnrolByDuplicatePaymentInLine() throws Exception {
		final PaymentIn paymentIn = prepareBaseData(new BigDecimal(175));
		final PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
		
		paymentInLine.setAmount(new BigDecimal(5));
		paymentInLine.setCollege(college);
		paymentInLine.setInvoice(invoice);
		paymentIn.addToPaymentInLines(paymentInLine);
		
		boolean invalid = false;
		try {
			context.commitChanges();
		} catch (ValidationException e) {
			invalid = true;
		}
		assertTrue("PaymentInLine's invoice and paymentin not unique!", invalid);
	}
}
