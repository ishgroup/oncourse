package ish.oncourse.model;

import ish.common.types.CourseEnrolmentType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PaymentInLineTest {
	
	private ObjectContext context;
	private Calendar calendar;
	private College college;
	private Course course;	
	private CourseClass courseClass;
	private WebSite webSite;
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
		college.setRequiresAvetmiss(false);
		context.commitChanges();
		
		course = context.newObject(Course.class);
		course.setCollege(college);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);
		
		courseClass = context.newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(college);
		courseClass.setMaximumPlaces(3);
		courseClass.setIsDistantLearningCourse(false);

		webSite = context.newObject(WebSite.class);
		webSite.setCollege(college);
		webSite.setName("Sydney Community College");
		webSite.setSiteKey("scc");
		webSite.setCreated(new Date());
		webSite.setModified(new Date());
		context.commitChanges();
	}
	
	@Test
	public void testCorrectEnrol() throws Exception {
		final PaymentIn paymentIn = prepareBaseData(new Money("175"));
		context.commitChanges();
		assertTrue(!paymentIn.getPaymentInLines().isEmpty());
		assertNotNull("PaymentInline id should not be null", paymentIn.getPaymentInLines().get(0).getId());
	}
	
	private PaymentIn prepareBaseData(final Money firstPaymentAmount) {
		
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
		invoice.setAmountOwing(new Money("75"));
		invoice.setInvoiceNumber(100L);
		invoice.setCollege(college);
		invoice.setInvoiceDate(calendar.getTime());
		invoice.setTotalExGst(new Money("75"));
		invoice.setTotalGst(new Money("75"));
		invoice.setDateDue(calendar.getTime());
		invoice.setContact(contact);
		invoice.setWebSite(webSite);
		
		final InvoiceLine invoiceLine = context.newObject(InvoiceLine.class);
		invoiceLine.setTitle("Test_invLine");
		invoiceLine.setCollege(college);
		invoiceLine.setPriceEachExTax(new Money(new BigDecimal(75)));
		invoiceLine.setTaxEach(Money.ZERO);
		invoiceLine.setQuantity(new BigDecimal(1));
		invoiceLine.setDiscountEachExTax(Money.ZERO);
		invoice.addToInvoiceLines(invoiceLine);
		
		final PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
		paymentInLine.setAmount(new Money("75"));
		paymentInLine.setCollege(college);
		paymentInLine.setInvoice(invoice);
		paymentIn.addToPaymentInLines(paymentInLine);
		
		//second invoice
		Invoice invoice2 = context.newObject(Invoice.class);
		invoice2.setAngelId(100L);
		invoice2.setAmountOwing(new Money("100"));
		invoice2.setInvoiceNumber(101L);
		invoice2.setCollege(college);
		invoice2.setInvoiceDate(calendar.getTime());
		invoice2.setTotalExGst(new Money("100"));
		invoice2.setTotalGst(new Money("100"));
		invoice2.setDateDue(calendar.getTime());
		invoice2.setContact(contact);
		invoice.setWebSite(webSite);


		final InvoiceLine invoiceLine2 = context.newObject(InvoiceLine.class);
		invoiceLine2.setTitle("Test_invLine");
		invoiceLine2.setCollege(college);
		invoiceLine2.setPriceEachExTax(new Money(new BigDecimal(100)));
		invoiceLine2.setTaxEach(Money.ZERO);
		invoiceLine2.setQuantity(new BigDecimal(1));
		invoiceLine2.setDiscountEachExTax(Money.ZERO);
		invoice2.addToInvoiceLines(invoiceLine2);
		
		final PaymentInLine paymentInLine2 = context.newObject(PaymentInLine.class);
		paymentInLine2.setAmount(new Money("100"));
		paymentInLine2.setCollege(college);
		paymentInLine2.setInvoice(invoice2);
		paymentIn.addToPaymentInLines(paymentInLine2);
		
		return paymentIn;
	}
	
	@Test
	public void testInCorrectEnrolByAmount() throws Exception {
		prepareBaseData(new Money("145"));
		
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
		final PaymentIn paymentIn = prepareBaseData(new Money("175"));
		final PaymentInLine paymentInLine = context.newObject(PaymentInLine.class);
		
		paymentInLine.setAmount(new Money("5"));
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
