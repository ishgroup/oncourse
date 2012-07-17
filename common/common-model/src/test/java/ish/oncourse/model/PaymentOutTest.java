package ish.oncourse.model;

import static org.junit.Assert.assertFalse;
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
	public void testAvailableSuccessStatusChanges() throws Exception {
		PaymentOut paymentOut = context.newObject(PaymentOut.class);
		assertFalse("Payment out status should not be new when added", PaymentStatus.NEW.equals(paymentOut.getStatus()));
		paymentOut.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment out status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentOut.getStatus()));
		paymentOut.setStatus(PaymentStatus.SUCCESS);
		assertTrue("Payment out status should be able to change from in transaction to success", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
		try {
			paymentOut.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from success to failed", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
		}
		assertFalse("Payment out status should be in success status", PaymentStatus.FAILED.equals(paymentOut.getStatus()));
		try {
			paymentOut.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from success to failed card declined", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
		}
		assertFalse("Payment out status should be in success status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentOut.getStatus()));
		
		try {
			paymentOut.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from success to failed no places", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
		}
		assertFalse("Payment out status should be in success status", PaymentStatus.FAILED_NO_PLACES.equals(paymentOut.getStatus()));
		try {
			paymentOut.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from success to corrupted", PaymentStatus.SUCCESS.equals(paymentOut.getStatus()));
		}
		assertFalse("Payment out status should be in success status", PaymentStatus.CORRUPTED.equals(paymentOut.getStatus()));
	}
	
	@Test
	public void testAvailableCorruptedStatusChanges() throws Exception {
		PaymentOut paymentIn = context.newObject(PaymentOut.class);
		assertFalse("Payment out status should not be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment out status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.CORRUPTED);
		assertTrue("Payment out status should be able to change from in transaction to corrupted", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from corrupted to failed", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in corrupted status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from corrupted to failed card declined", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in corrupted status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from corrupted to failed no places", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in corrupted status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change to success from corrupted", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in corrupted status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableFailedStatusChanges() throws Exception {
		PaymentOut paymentIn = context.newObject(PaymentOut.class);
		assertFalse("Payment out status should not be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment out status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.FAILED);
		assertTrue("Payment out status should be able to change from in transaction to failed", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change to success from failed", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed to failed card declined", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed to failed no places", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed to corrupted", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableFailedNoPlacesStatusChanges() throws Exception {
		PaymentOut paymentIn = context.newObject(PaymentOut.class);
		assertFalse("Payment out status should not be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment out status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		assertTrue("Payment out status should be able to change from in transaction to failed no places", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change to success from failed no places", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed no places status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed no places to failed card declined", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed no places status", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change to failed from failed no places", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed no places status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed no places to corrupted", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed no places status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}
	
	@Test
	public void testAvailableFailedCardDeclinedStatusChanges() throws Exception {
		PaymentOut paymentIn = context.newObject(PaymentOut.class);
		assertFalse("Payment out status should not be new when added", PaymentStatus.NEW.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		assertTrue("Payment out status should be able to change to in transaction from new", PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus()));
		paymentIn.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
		assertTrue("Payment out status should be able to change from in transaction to failed card declined", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change to success from failed card declined", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.SUCCESS.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.FAILED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change to failed from failed card declined", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed card declined status", PaymentStatus.FAILED.equals(paymentIn.getStatus()));
		
		try {
			paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed card declined to failed no places", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.FAILED_NO_PLACES.equals(paymentIn.getStatus()));
		try {
			paymentIn.setStatus(PaymentStatus.CORRUPTED);
		} catch (Exception e) {
			assertTrue("Payment out status should not be able to change from failed card declined to corrupted", PaymentStatus.FAILED_CARD_DECLINED.equals(paymentIn.getStatus()));
		}
		assertFalse("Payment out status should be in failed status", PaymentStatus.CORRUPTED.equals(paymentIn.getStatus()));
	}
}
