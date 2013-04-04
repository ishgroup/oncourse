package ish.oncourse.model;

import ish.common.types.EnrolmentStatus;
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
	
	
}
