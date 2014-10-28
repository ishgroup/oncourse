package ish.oncourse.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import ish.common.types.CourseEnrolmentType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnrolmentTest {
	private ObjectContext context;
	
	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		context = ContextUtils.createObjectContext();
	}
	
	@Test
	public void testNullStatusChange() throws Exception {
		Enrolment enrolment = context.newObject(Enrolment.class);
		assertEquals("Enrolment status should be in transaction before test", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		boolean allowToSetNull = true;
		try {
			enrolment.setStatus(null);
		} catch (Exception e) {
			allowToSetNull = false;
		}
		assertFalse("Exception should thrown on attempt to change in transaction to Null statuses", allowToSetNull);
		assertNotNull("Enrolment should not be able to set the null status for in transaction status", enrolment.getStatus());
	}
	
	private void testNullSet(EnrolmentStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		Enrolment enrolment = new Enrolment();
		enrolment.setStatus(testedStatus);
		context.registerNewObject(enrolment);
		assertTrue(firstMessage, testedStatus.equals(enrolment.getStatus()));
		try {
			enrolment.setStatus(null);
		} catch (Exception e) {
			assertEquals(secondMessage, testedStatus,enrolment.getStatus());
		}
		assertNotNull(thirdMessage, enrolment.getStatus());
	}
	
	@Test
	public void testNEW_StatusChange() throws Exception {
		testNullSet(EnrolmentStatus.NEW, "Enrolment status should be new before test", "Enrolment status should be new for this test", 
			"Enrolment should not be able to set null status after new");
		for (EnrolmentStatus status : EnrolmentStatus.values()) {
			Enrolment enrolment = new Enrolment();
			enrolment.setStatus(EnrolmentStatus.NEW);
			context.registerNewObject(enrolment);
			assertEquals("Enrolment status should be new before test", EnrolmentStatus.NEW,enrolment.getStatus());
			enrolment.setStatus(status);
			assertEquals(String.format("Enrolment should be able to set the %s status after new status", status), status,enrolment.getStatus());
		}
	}
	
	@Test
	public void testQUEUED_StatusChange() throws Exception {
		testNullSet(EnrolmentStatus.QUEUED, "Enrolment status should be queued before test", "Enrolment status should be queued for this test", 
			"Enrolment should not be able to set null status after queued");
		for (EnrolmentStatus status : EnrolmentStatus.values()) {
			Enrolment enrolment = new Enrolment();
			enrolment.setStatus(EnrolmentStatus.QUEUED);
			context.registerNewObject(enrolment);
			assertEquals("Enrolment status should be queued before test", EnrolmentStatus.QUEUED,enrolment.getStatus());
			if (!EnrolmentStatus.NEW.equals(status)) {
				enrolment.setStatus(status);
				assertTrue(String.format("Enrolment should be able to set the %s status after queued status", status), status.equals(enrolment.getStatus()));
			} else {
				try {
					enrolment.setStatus(status);
				} catch (Exception e) {
					assertEquals("Enrolment status should be queued for this test", EnrolmentStatus.QUEUED,enrolment.getStatus());
				}
				assertEquals("Enrolment status should be queued for this test", EnrolmentStatus.QUEUED,enrolment.getStatus());
			}
		}
	}
	
	@Test
	public void testIN_TRANSACTION_StatusChange() throws Exception {
		testNullSet(EnrolmentStatus.IN_TRANSACTION, "Enrolment status should be in transaction before test", 
			"Enrolment status should be in transaction for this test",  "Enrolment should not be able to set null status after in transaction");
		for (EnrolmentStatus status : EnrolmentStatus.values()) {
			Enrolment enrolment = new Enrolment();
			enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
			context.registerNewObject(enrolment);
			assertEquals("Enrolment status should be in transaction before test", EnrolmentStatus.IN_TRANSACTION,enrolment.getStatus());
			// TODO: status NEW should be included in this test after task 17341 will be done
			if (/*!EnrolmentStatus.NEW.equals(status) &&*/ !EnrolmentStatus.QUEUED.equals(status)) {
				enrolment.setStatus(status);
				assertEquals(String.format("Enrolment should be able to set the %s status after in transaction status", status), 
					status, enrolment.getStatus());
			} else {
				try {
					enrolment.setStatus(status);
				} catch (Exception e) {
					assertEquals("Enrolment status should be in transaction for this test", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
				}
				assertEquals("Enrolment status should be in transaction for this test", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
			}
		}
	}
	
	@Test
	public void testSUCCESS_StatusChange() throws Exception {
		testNullSet(EnrolmentStatus.SUCCESS, "Enrolment status should be success before test", "Enrolment status should be success for this test",  
			"Enrolment should not be able to set null status after success");
		for (EnrolmentStatus status : EnrolmentStatus.values()) {
			Enrolment enrolment = new Enrolment();
			enrolment.setStatus(EnrolmentStatus.SUCCESS);
			context.registerNewObject(enrolment);
			assertEquals("Enrolment status should be success before test", EnrolmentStatus.SUCCESS,enrolment.getStatus());
			if (EnrolmentStatus.SUCCESS.equals(status) || EnrolmentStatus.CANCELLED.equals(status) || EnrolmentStatus.REFUNDED.equals(status)) {
				enrolment.setStatus(status);
				assertEquals(String.format("Enrolment should be able to set the %s status after success status", status), status,enrolment.getStatus());
			} else {
				try {
					enrolment.setStatus(status);
				} catch (Exception e) {
					assertEquals("Enrolment status should be success for this test", EnrolmentStatus.SUCCESS,enrolment.getStatus());
				}
				assertEquals("Enrolment status should be success for this test", EnrolmentStatus.SUCCESS,enrolment.getStatus());
			}
		}
	}
	
	private void testTheSameStatusLoop(EnrolmentStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		for (EnrolmentStatus status : EnrolmentStatus.values()) {
			Enrolment enrolment = new Enrolment();
			enrolment.setStatus(testedStatus);
			context.registerNewObject(enrolment);
			assertEquals(firstMessage, testedStatus,enrolment.getStatus());
			if (testedStatus.equals(status)) {
				enrolment.setStatus(status);
				assertEquals(String.format(secondMessage, status), status,enrolment.getStatus());
			} else {
				try {
					enrolment.setStatus(status);
				} catch (Exception e) {
					assertEquals(thirdMessage, testedStatus, enrolment.getStatus());
				}
				assertEquals(thirdMessage, testedStatus, enrolment.getStatus());
			}
		}
	}
	
	@Test
	public void testFAILED__FAILED_CARD_DECLINED__FAILED_NO_PLACES__CANCELLED__REFUNDED__CORRUPTED_StatusChange() throws Exception {
		//failed part
		testNullSet(EnrolmentStatus.FAILED, "Enrolment status should be failed before test", "Enrolment status should be failed for this test",  
			"Enrolment should not be able to set null status after failed");
		testTheSameStatusLoop(EnrolmentStatus.FAILED, "Enrolment status should be failed before test", 
			"Enrolment should be able to set the %s status after failed status", "Enrolment status should be failed for this test");
		//card declined part
		testNullSet(EnrolmentStatus.FAILED_CARD_DECLINED, "Enrolment status should be card declined before test", 
			"Enrolment status should be card declined for this test", "Enrolment should not be able to set null status after card declined");
		testTheSameStatusLoop(EnrolmentStatus.FAILED_CARD_DECLINED, "Enrolment status should be card declined before test", 
			"Enrolment should be able to set the %s status after card declined status", "Enrolment status should be card declined for this test");
		//no places part
		testNullSet(EnrolmentStatus.FAILED_NO_PLACES, "Enrolment status should be no places before test", "Enrolment status should be no places for this test",  
			"Enrolment should not be able to set null status after no places");
		testTheSameStatusLoop(EnrolmentStatus.FAILED_NO_PLACES, "Enrolment status should be no places before test", 
			"Enrolment should be able to set the %s status after no places status", "Enrolment status should be no places for this test");
		//canceled part
		testNullSet(EnrolmentStatus.CANCELLED, "Enrolment status should be canceled before test", "Enrolment status should be canceled for this test",  
			"Enrolment should not be able to set null status after canceled");
		testTheSameStatusLoop(EnrolmentStatus.CANCELLED, "Enrolment status should be canceled before test", 
				"Enrolment should be able to set the %s status after canceled status", "Enrolment status should be canceled for this test");
		//refunded part
		testNullSet(EnrolmentStatus.REFUNDED, "Enrolment status should be refunded before test", "Enrolment status should be refunded for this test",  
			"Enrolment should not be able to set null status after refunded");
		testTheSameStatusLoop(EnrolmentStatus.REFUNDED, "Enrolment status should be refunded before test", 
				"Enrolment should be able to set the %s status after refunded status", "Enrolment status should be refunded for this test");
		//corrupted part
		testNullSet(EnrolmentStatus.CORRUPTED, "Enrolment status should be corrupted before test", "Enrolment status should be corrupted for this test",  
			"Enrolment should not be able to set null status after corrupted");
		testTheSameStatusLoop(EnrolmentStatus.CORRUPTED, "Enrolment status should be corrupted before test", 
				"Enrolment should be able to set the %s status after corrupted status", "Enrolment status should be corrupted for this test");
	}
	
	@Test
	public void testGetOriginalInvoiceLine() {
		Date currentTime = new Date();
		College college = context.newObject(College.class);
		college.setName("name");
		college.setTimeZone("Australia/Sydney");
		college.setFirstRemoteAuthentication(new Date());
		college.setRequiresAvetmiss(false);
		Course course = context.newObject(Course.class);
		course.setCollege(college);
		course.setEnrolmentType(CourseEnrolmentType.OPEN_FOR_ENROLMENT);
		CourseClass courseClass = context.newObject(CourseClass.class);
		courseClass.setCourse(course);
		courseClass.setCollege(college);
		courseClass.setMaximumPlaces(3);
		courseClass.setIsDistantLearningCourse(false);
		context.commitChanges();
		
		Calendar calendar =  Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 5);

		Contact contact = (Contact) context.newObject(Contact.class);
		contact.setGivenName("Test_Payer");
		contact.setFamilyName("Test_Payer");
		contact.setCollege(college);
		
		Invoice invoice1 = context.newObject(Invoice.class);
		invoice1.setAngelId(100l);
		invoice1.setAmountOwing(new Money("225"));
		invoice1.setInvoiceNumber(100l);
		invoice1.setCollege(college);
		invoice1.setInvoiceDate(calendar.getTime());
		invoice1.setTotalExGst(new Money("225"));
		invoice1.setTotalGst(new Money("225"));
		invoice1.setDateDue(calendar.getTime());
		invoice1.setContact(contact);
		invoice1.setSource(PaymentSource.SOURCE_ONCOURSE);

		InvoiceLine invLine1 = context.newObject(InvoiceLine.class);
		invLine1.setTitle("Test_invLine1");
		invLine1.setCollege(college);
		invLine1.setPriceEachExTax(Money.ZERO);//positive
		invLine1.setTaxEach(Money.ZERO);
		invLine1.setQuantity(new BigDecimal(1));
		invLine1.setDiscountEachExTax(Money.ZERO);
		invLine1.setCreated(currentTime);
		invLine1.setEnrolment(newEnrolment(college, courseClass));

		InvoiceLine invLine2 = context.newObject(InvoiceLine.class);
		invLine2.setTitle("Test_invLine2");
		invLine2.setCollege(college);
		invLine2.setEnrolment(invLine1.getEnrolment());
		invLine2.setPriceEachExTax(Money.ZERO.subtract(new Money("220")));//negative
		invLine2.setTaxEach(Money.ZERO);
		invLine2.setQuantity(new BigDecimal(1));
		invLine2.setCreated(currentTime);
		invLine2.setDiscountEachExTax(Money.ZERO);

		invoice1.addToInvoiceLines(invLine1);
		invoice1.addToInvoiceLines(invLine2);
		Enrolment enrolment = invLine1.getEnrolment();
		context.commitChanges();
		
		//check the case with 1 positive and 1 negative invoiceline
		assertEquals("Enrolment should have 2 linked invoiceLines", 2, enrolment.getInvoiceLines().size());
		assertNotNull("Original invoice should be linked", enrolment.getOriginalInvoiceLine());
		assertEquals("Original invoiceLine should have not negative FinalPriceToPayIncTax for this case", 
			invLine1, enrolment.getOriginalInvoiceLine());
		
		invLine1.setPriceEachExTax(Money.ONE);
		invLine2.setPriceEachExTax(Money.ZERO);
		invLine2.setCreated(currentTime);
		invLine1.setCreated(new Date());
		context.commitChanges();
		
		//check when to both invoicelines have positive FinalPriceToPayIncTax but different create date
		assertEquals("Enrolment should have 2 linked invoiceLines", 2, enrolment.getInvoiceLines().size());
		assertNotNull("Original invoice should be linked", enrolment.getOriginalInvoiceLine());
		assertEquals("Original invoiceLine should be firstly created invoiceline with not negative FinalPriceToPayIncTax", 
			invLine2, enrolment.getOriginalInvoiceLine());
	}
	
	private Enrolment newEnrolment(College college, CourseClass courseClass) {

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
	
	
}
