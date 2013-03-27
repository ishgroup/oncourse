package ish.oncourse.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.MessageStatus;
import ish.oncourse.test.ContextUtils;

import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

public class MessagePersonTest {
	private ObjectContext context;
	
	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		context = ContextUtils.createObjectContext();
	}
	
	@Test
	public void testNullStatusChange() throws Exception {
		MessagePerson messagePerson = context.newObject(MessagePerson.class);
		assertTrue("MessagePerson status should be null before test", messagePerson.getStatus() == null);
		messagePerson.setStatus(null);
		assertNull("MessagePerson should be able to set the null status for null status", messagePerson.getStatus());
		for (MessageStatus status : MessageStatus.values()) {
			messagePerson = context.newObject(MessagePerson.class);
			assertTrue("MessagePerson status should be null before test", messagePerson.getStatus() == null);
			messagePerson.setStatus(status);
			assertTrue(String.format("MessagePerson should be able to set the %s status after null status", status), status.equals(messagePerson.getStatus()));
		}
	}
	
	private void testNullSet(MessageStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		MessagePerson messagePerson = context.newObject(MessagePerson.class);
		messagePerson.setStatus(testedStatus);
		assertTrue(firstMessage, testedStatus.equals(messagePerson.getStatus()));
		try {
			messagePerson.setStatus(null);
		} catch (Exception e) {
			assertTrue(secondMessage, testedStatus.equals(messagePerson.getStatus()));
		}
		assertNotNull(thirdMessage, messagePerson.getStatus());
	}
	
	@Test
	public void testQUEUED_StatusChange() throws Exception {
		testNullSet(MessageStatus.QUEUED, "MessagePerson status should be queued before test", "MessagePerson status should be queued for this test", 
			"MessagePerson should not be able to set null status after queued");
		for (MessageStatus status : MessageStatus.values()) {
			MessagePerson messagePerson = context.newObject(MessagePerson.class);
			messagePerson.setStatus(MessageStatus.QUEUED);
			assertTrue("MessagePerson status should be queued before test", MessageStatus.QUEUED.equals(messagePerson.getStatus()));
			messagePerson.setStatus(status);
			assertTrue(String.format("MessagePerson should be able to set the %s status after queued status", status), status.equals(messagePerson.getStatus()));
		}
	}
	
	private void testTheSameStatusLoop(MessageStatus testedStatus, String firstMessage, String secondMessage, String thirdMessage) {
		for (MessageStatus status : MessageStatus.values()) {
			MessagePerson messagePerson = context.newObject(MessagePerson.class);
			messagePerson.setStatus(testedStatus);
			assertTrue(firstMessage, testedStatus.equals(messagePerson.getStatus()));
			if (testedStatus.equals(status)) {
				messagePerson.setStatus(status);
				assertTrue(String.format(secondMessage, status), status.equals(messagePerson.getStatus()));
			} else {
				try {
					messagePerson.setStatus(status);
				} catch (Exception e) {
					assertTrue(thirdMessage, testedStatus.equals(messagePerson.getStatus()));
				}
				assertTrue(thirdMessage, testedStatus.equals(messagePerson.getStatus()));
			}
		}
	}
	
	@Test
	public void testSENT__FAILED_StatusChange() throws Exception {
		//sent part
		testNullSet(MessageStatus.SENT, "MessagePerson status should be sent before test", "MessagePerson status should be sent for this test", 
			"MessagePerson should not be able to set null status after sent");
		testTheSameStatusLoop(MessageStatus.SENT, "MessagePerson status should be sent before test", 
			"MessagePerson should be able to set the %s status after sent status", "MessagePerson status should be sent for this test");
		//failed part
		testNullSet(MessageStatus.FAILED, "MessagePerson status should be failed before test", "MessagePerson status should be failed for this test", 
			"MessagePerson should not be able to set null status after failed");
		testTheSameStatusLoop(MessageStatus.FAILED, "MessagePerson status should be failed before test", 
			"MessagePerson should be able to set the %s status after failed status", "MessagePerson status should be failed for this test");
	}
}
