package ish.oncourse.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.PaymentStatus;
import ish.oncourse.test.ContextUtils;
import java.util.Date;

import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

public class PaymentOutTest {
	private ObjectContext context;
	private College college;
	private Course course;
	private CourseClass courseClass;

	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		this.context = ContextUtils.createObjectContext();

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
	public void testNullStatusChange() throws Exception {
		PaymentOut paymentOut = context.newObject(PaymentOut.class);
		assertTrue("Paymentout status should be null before test", paymentOut.getStatus() == null);
		paymentOut.setStatus(null);
		assertNull("Paymentout should be able to set the null status for null status", paymentOut.getStatus());
		for (PaymentStatus status : PaymentStatus.values()) {
			paymentOut = context.newObject(PaymentOut.class);
			assertTrue("Paymentout status should be null before test", paymentOut.getStatus() == null);
			paymentOut.setStatus(status);
			assertTrue(String.format("Paymentout should be able to set the %s status after null status", status), status.equals(paymentOut.getStatus()));
		}
	}
	
	private void testNullSet(PaymentStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		PaymentOut paymentOut = context.newObject(PaymentOut.class);
		paymentOut.setStatus(testedStatus);
		assertTrue(firstMessage, testedStatus.equals(paymentOut.getStatus()));
		try {
			paymentOut.setStatus(null);
		} catch (Exception e) {
			assertTrue(secondMessage, testedStatus.equals(paymentOut.getStatus()));
		}
		assertNotNull(thirdMessage, paymentOut.getStatus());
	}
	
	@Test
	public void testNEW_StatusChange() throws Exception {
		testNullSet(PaymentStatus.NEW, "Paymentout status should be new before test", "Paymentout status should be new for this test", 
			"Payment out should not be able to set null status after new");
		for (PaymentStatus status : PaymentStatus.values()) {
			PaymentOut paymentOut = context.newObject(PaymentOut.class);
			paymentOut.setStatus(PaymentStatus.NEW);
			assertTrue("Paymentout status should be new before test", PaymentStatus.NEW.equals(paymentOut.getStatus()));
			paymentOut.setStatus(status);
			assertTrue(String.format("Paymentout should be able to set the %s status after new status", status), status.equals(paymentOut.getStatus()));
		}
	}
	
	@Test
	public void testQUEUED_StatusChange() throws Exception {
		testNullSet(PaymentStatus.QUEUED, "Paymentout status should be queued before test", "Paymentout status should be queued for this test", 
			"Payment out should not be able to set null status after queued");
		for (PaymentStatus status : PaymentStatus.values()) {
			PaymentOut paymentOut = context.newObject(PaymentOut.class);
			paymentOut.setStatus(PaymentStatus.QUEUED);
			assertTrue("Paymentout status should be queued before test", PaymentStatus.QUEUED.equals(paymentOut.getStatus()));
			if (!PaymentStatus.NEW.equals(status)) {
				paymentOut.setStatus(status);
				assertTrue(String.format("Paymentout should be able to set the %s status after queued status", status), status.equals(paymentOut.getStatus()));
			} else {
				try {
					paymentOut.setStatus(status);
				} catch (Exception e) {
					assertTrue("Paymentout status should be queued for this test", PaymentStatus.QUEUED.equals(paymentOut.getStatus()));
				}
				assertTrue("Paymentout status should be queued for this test", PaymentStatus.QUEUED.equals(paymentOut.getStatus()));
			}
		}
	}
	
	private void testIn_TransactionStatusLoop(PaymentStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		for (PaymentStatus status : PaymentStatus.values()) {
			PaymentOut paymentOut = context.newObject(PaymentOut.class);
			paymentOut.setStatus(testedStatus);
			assertTrue(firstMessage, testedStatus.equals(paymentOut.getStatus()));
			if (!PaymentStatus.NEW.equals(status) && !PaymentStatus.QUEUED.equals(status)) {
				paymentOut.setStatus(status);
				assertTrue(String.format(secondMessage, status), status.equals(paymentOut.getStatus()));
			} else {
				try {
					paymentOut.setStatus(status);
				} catch (Exception e) {
					assertTrue(thirdMessage, testedStatus.equals(paymentOut.getStatus()));
				}
				assertTrue(thirdMessage, testedStatus.equals(paymentOut.getStatus()));
			}
		}
	}
	
	@Test
	public void testIN_TRANSACTION__CARD_DETAILS_REQUIRED_StatusChange() throws Exception {
		//in transaction part
		testNullSet(PaymentStatus.IN_TRANSACTION, "Paymentout status should be in transaction before test", 
			"Paymentout status should be in transaction for this test", "Payment out should not be able to set null status after in transaction");
		testIn_TransactionStatusLoop(PaymentStatus.IN_TRANSACTION, "Paymentout status should be in transaction before test", 
			"Paymentout should be able to set the %s status after in transaction status", "Paymentout status should be in transaction for this test");
		
		//card details required part
		testNullSet(PaymentStatus.CARD_DETAILS_REQUIRED, "Paymentout status should be card details required before test", 
			"Paymentout status should be card details required for this test", "Payment out should not be able to set null status after card details required");
		testIn_TransactionStatusLoop(PaymentStatus.CARD_DETAILS_REQUIRED, "Paymentout status should be card details required before test", 
			"Paymentout should be able to set the %s status after card details required status", 
				"Paymentout status should be card details required for this test");
	}
	
	@Test
	public void testSUCCESS_StatusChange() throws Exception {
		testNullSet(PaymentStatus.SUCCESS, "Paymentout status should be success before test", "Paymentout status should be success for this test", 
			"Payment out should not be able to set null status after success");
		for (PaymentStatus status : PaymentStatus.values()) {
			PaymentOut paymentOut = context.newObject(PaymentOut.class);
			paymentOut.setStatus(PaymentStatus.SUCCESS);
			assertTrue("Paymentout status should be success before test", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
			if (PaymentStatus.SUCCESS.equals(status)) {
				paymentOut.setStatus(status);
				assertTrue(String.format("Paymentout should be able to set the %s status after success status", status), 
					status.equals(paymentOut.getStatus()));
			} else {
				try {
					paymentOut.setStatus(status);
				} catch (Exception e) {
					assertTrue("Paymentout status should be success for this test", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
				}
				assertTrue("Paymentout status should be success for this test", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
			}
		}
	}
	
	private void testFailedStatusLoop(PaymentStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		for (PaymentStatus status : PaymentStatus.values()) {
			PaymentOut paymentOut = context.newObject(PaymentOut.class);
			paymentOut.setStatus(testedStatus);
			assertTrue(firstMessage, testedStatus.equals(paymentOut.getStatus()));
			if (testedStatus.equals(status)) {
				paymentOut.setStatus(status);
				assertTrue(String.format(secondMessage, status), status.equals(paymentOut.getStatus()));
			} else {
				try {
					paymentOut.setStatus(status);
				} catch (Exception e) {
					assertTrue(thirdMessage, testedStatus.equals(paymentOut.getStatus()));
				}
				assertTrue(thirdMessage, testedStatus.equals(paymentOut.getStatus()));
			}
		}
	}
	
	@Test
	public void testFAILED__FAILED_CARD_DECLINED__FAILED_NO_PLACES__STATUS_CANCELLED__STATUS_REFUNDED_StatusChange() throws Exception {
		//failed part
		testNullSet(PaymentStatus.FAILED, "Paymentout status should be failed before test", "Paymentout status should be failed for this test", 
			"Payment out should not be able to set null status after failed");
		testFailedStatusLoop(PaymentStatus.FAILED, "Paymentout status should be failed before test", 
			"Paymentout should be able to set the %s status after failed status", "Paymentout status should be failed for this test");
		//card declined part
		testNullSet(PaymentStatus.FAILED_CARD_DECLINED, "Paymentout status should be failed card declined before test", 
			"Paymentout status should be failed card declined for this test", "Payment out should not be able to set null status after failed card declined");
		testFailedStatusLoop(PaymentStatus.FAILED_CARD_DECLINED, "Paymentout status should be failed card declined before test", 
			"Paymentout should be able to set the %s status after failed card declined status", 
				"Paymentout status should be failed card declined for this test");
		//no places part
		testNullSet(PaymentStatus.FAILED_NO_PLACES, "Paymentout status should be failed no places before test", 
				"Paymentout status should be failed no places for this test", "Payment out should not be able to set null status after failed no places");
		testFailedStatusLoop(PaymentStatus.FAILED_NO_PLACES, "Paymentout status should be failed no places before test", 
			"Paymentout should be able to set the %s status after failed no places status", "Paymentout status should be failed no places for this test");
	}
	
}
