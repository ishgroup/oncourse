package ish.oncourse.model;

import static org.junit.Assert.*;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

public class PaymentInTest {

	private ObjectContext context;
	private Calendar calendar;
	private College college;
	private Course course;
	private CourseClass courseClass;

	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		this.context = ContextUtils.createObjectContext();
		this.calendar = Calendar.getInstance();

		this.college = context.newObject(College.class);

		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);

		context.commitChanges();

		this.course = context.newObject(Course.class);
		course.setCollege(college);

		this.courseClass = context.newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(college);
		courseClass.setMaximumPlaces(3);
		courseClass.setIsDistantLearningCourse(false);

		context.commitChanges();
	}
	
	@Test
	public void testIsZeroAmount() throws Exception {
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		Money amount = new Money("224.34");
		paymentIn.setAmount(amount);
		assertTrue("PaymentIn is not zero payment.", !paymentIn.isZeroPayment());
		amount = new Money("-300");
		paymentIn.setAmount(amount);
		assertTrue("PaymentIn is not zero payment.", !paymentIn.isZeroPayment());
		amount = new Money("0");
		paymentIn.setAmount(amount);
		assertTrue("PaymentIn is zero payment with integer argument.", paymentIn.isZeroPayment());
		amount = new Money("0.00");
		paymentIn.setAmount(amount);
		assertTrue("PaymentIn is zero payment with string argument.", paymentIn.isZeroPayment());
	}
	
	@Test
	public void testAbandonPayment() throws Exception {

		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		paymentIn.setCollege(college);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setAmount(new Money("150"));
		paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE);
		paymentIn.setType(PaymentType.CREDIT_CARD);
		paymentIn.setSessionId("1234567890");

		calendar.add(Calendar.DAY_OF_MONTH, 5);

		Contact contact = (Contact) context.newObject(Contact.class);
		contact.setGivenName("Test_Payer");
		contact.setFamilyName("Test_Payer");
		contact.setCollege(college);

		Invoice invoice1 = context.newObject(Invoice.class);
		invoice1.setAngelId(100l);
		invoice1.setAmountOwing(new Money("-75"));
		invoice1.setInvoiceNumber(100l);
		invoice1.setCollege(college);
		invoice1.setInvoiceDate(calendar.getTime());
		invoice1.setTotalExGst(new Money("-75"));
		invoice1.setTotalGst(new Money("-75"));
		invoice1.setDateDue(calendar.getTime());
		invoice1.setContact(contact);
		invoice1.setSource(PaymentSource.SOURCE_ONCOURSE);

		Invoice invoice2 = context.newObject(Invoice.class);
		invoice2.setAngelId(102l);
		invoice2.setAmountOwing(new Money("225"));
		invoice2.setCollege(college);
		invoice2.setInvoiceNumber(101l);
		invoice2.setInvoiceDate(calendar.getTime());
		invoice2.setTotalExGst(new Money("225"));
		invoice2.setTotalGst(new Money("225"));
		invoice2.setDateDue(calendar.getTime());
		invoice2.setContact(contact);
		invoice2.setSource(PaymentSource.SOURCE_ONCOURSE);

		InvoiceLine invLine1 = context.newObject(InvoiceLine.class);
		invLine1.setTitle("Test_invLine1");
		invLine1.setCollege(college);
		invLine1.setPriceEachExTax(new Money("-75"));
		invLine1.setTaxEach(Money.ZERO);
		invLine1.setQuantity(new BigDecimal(1));
		invLine1.setDiscountEachExTax(Money.ZERO);
		invLine1.setEnrolment(newEnrolment());

		InvoiceLine invLine2 = context.newObject(InvoiceLine.class);
		invLine2.setTitle("Test_invLine2");
		invLine2.setCollege(college);
		invLine2.setEnrolment(newEnrolment());
		invLine2.setPriceEachExTax(new Money("220"));
		invLine2.setTaxEach(Money.ZERO);
		invLine2.setQuantity(new BigDecimal(1));
		invLine2.setDiscountEachExTax(Money.ZERO);

		invoice2.addToInvoiceLines(invLine1);
		invoice2.addToInvoiceLines(invLine2);

		PaymentInLine pLine1 = context.newObject(PaymentInLine.class);
		pLine1.setAmount(new Money("-75"));
		pLine1.setCollege(college);
		pLine1.setInvoice(invoice1);

		PaymentInLine pLine2 = context.newObject(PaymentInLine.class);
		pLine2.setAmount(new Money("225"));
		pLine2.setCollege(college);
		pLine2.setInvoice(invoice2);

		paymentIn.addToPaymentInLines(pLine1);
		paymentIn.addToPaymentInLines(pLine2);

		context.commitChanges();

		PaymentIn inversePayment = paymentIn.abandonPayment();

		assertNotNull("Expecting not null inverse payment.", inversePayment);
		assertEquals("Reverse payment sessionid should be equal to payment sessionid", inversePayment.getSessionId(), paymentIn.getSessionId());
		context.commitChanges();

		assertTrue("Checking that id isn't temporary.", !inversePayment.getObjectId().isTemporary());
		assertTrue("Inverse payment amount is zero.", Money.ZERO.equals(inversePayment.getAmount()));

        // test for invoice source (#13908) -  if direct invoice was has source ONCOURSE so reverse invoice should have the same source
        List<PaymentInLine> lines = inversePayment.getPaymentInLines();
        for (PaymentInLine paymentInLine : lines) {
            Invoice invoice = paymentInLine.getInvoice();
            assertTrue("Invoice source is ONCOURSE", invoice.getSource() == PaymentSource.SOURCE_ONCOURSE);
        }

	}

	private Enrolment newEnrolment() {

		Enrolment enrol = new Enrolment();
		enrol.setCourseClass(courseClass);
		enrol.setStatus(EnrolmentStatus.IN_TRANSACTION);
		enrol.setCollege(college);
		enrol.setSource(PaymentSource.SOURCE_ONCOURSE);

		Student student = (Student) context.newObject(Student.class);
		student.setCollege(college);

		Contact contact = (Contact) context.newObject(Contact.class);
		contact.setCollege(college);
		contact.setGivenName("Test_CourseClass");
		contact.setFamilyName("Test_CourseClass");

		student.setContact(contact);

		enrol.setStudent(student);

		return enrol;
	}
	
	@Test
	public void testAvailableSuccessStatusChanges() throws Exception {
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		assertTrue("Payment in status should be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment in status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.SUCCESS);
		assertTrue("Payment in status should be able to change from in transaction to success", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from success to failed", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in success status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from success to failed card declined", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in success status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from success to failed no places", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in success status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from success to corrupted", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in success status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableCorruptedStatusChanges() throws Exception {
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		assertTrue("Payment in status should be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment in status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.CORRUPTED);
		assertTrue("Payment in status should be able to change from in transaction to corrupted", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from corrupted to failed", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in corrupted status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from corrupted to failed card declined", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in corrupted status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from corrupted to failed no places", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in corrupted status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change to success from corrupted", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in corrupted status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableFailedStatusChanges() throws Exception {
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		assertTrue("Payment in status should be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment in status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.FAILED);
		assertTrue("Payment in status should be able to change from in transaction to failed", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change to success from failed", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed to failed card declined", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed to failed no places", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed to corrupted", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableFailedNoPlacesStatusChanges() throws Exception {
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		assertTrue("Payment in status should be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment in status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		assertTrue("Payment in status should be able to change from in transaction to failed no places", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change to success from failed no places", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed no places status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed no places to failed card declined", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed no places status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change to failed from failed no places", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed no places status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed no places to corrupted", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed no places status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableFailedCardDeclinedStatusChanges() throws Exception {
		PaymentIn paymentIn = context.newObject(PaymentIn.class);
		assertTrue("Payment in status should be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment in status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		assertTrue("Payment in status should be able to change from in transaction to failed card declined", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change to success from failed card declined", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change to failed from failed card declined", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed card declined status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed card declined to failed no places", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment in status should not be able to change from failed card declined to corrupted", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment in status should be in failed status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}


    @Test
    public void testMakeShallowCopy()
    {
        try {
            PaymentIn paymentIn = context.newObject(PaymentIn.class);
            paymentIn.setSource(PaymentSource.SOURCE_ONCOURSE);

            PaymentIn copy = paymentIn.makeShallowCopy();
            assertEquals("copy source field  should equals original source field", paymentIn.getSource(), copy.getSource());

            paymentIn = context.newObject(PaymentIn.class);
            paymentIn.setSource(PaymentSource.SOURCE_WEB);
            copy = paymentIn.makeShallowCopy();
            assertEquals("copy source field  should equals original source field", paymentIn.getSource(), copy.getSource());
        } finally {
            context.rollbackChanges();
        }
    }
}
