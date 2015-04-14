package ish.oncourse.services.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import org.apache.tapestry5.ioc.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LastEditedVisitorTest {

	private LastEditedVisitor visitor;

	@Before
	public void setup() {
		Messages messages = mock(Messages.class);
		
		when(messages.format(anyString(), any(), any())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return String.format("Last edited %s %s ago", Arrays.copyOfRange(args, 1, args.length));
			}
		});
		
		visitor = new LastEditedVisitor();
	}

	@After
	public void tearDown() {
		visitor = null;
	}
	
	@Test
	public void testGetLastEdited() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -5);
		
		WebContent block = mock(WebContent.class);
		when(block.getModified()).thenReturn(cal.getTime());
		String message = visitor.visitWebContent(block);
		assertNotNull(message);
		assertTrue(message.contains("minutes"));
		
		WebNode node = mock(WebNode.class);
		when(node.getModified()).thenReturn(cal.getTime());
		message = visitor.visitWebNode(node);
		assertNotNull(message);
		assertTrue(message.contains("minutes"));
		
		WebNodeType webNodeType = mock(WebNodeType.class);
		when(webNodeType.getModified()).thenReturn(cal.getTime());
		message = visitor.visitWebNodeType(webNodeType);
		assertNotNull(message);
		assertTrue(message.contains("minutes"));
	}

	@Test
	public void testGetLastEditedDays() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);

		Date lastModified = cal.getTime();

		String[] lastEdited = visitor.getLastEdited(lastModified);

		assertNotNull(lastEdited);
		assertTrue(lastEdited.length == 2);

		assertNotNull("Null returned", lastEdited);
		assertTrue("Incorrect array length", lastEdited.length == 2);

		assertTrue("Wrong time value","1".equals(lastEdited[0]));
		assertTrue("Wrong time unit", "day".equals(lastEdited[1]));

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -2);

		lastModified = cal.getTime();

		lastEdited = visitor.getLastEdited(lastModified);

		assertNotNull("Null returned", lastEdited);
		assertTrue("Incorrect array length", lastEdited.length == 2);

		assertTrue("Wrong time value","2".equals(lastEdited[0]));
		assertTrue("Wrong time unit", "days".equals(lastEdited[1]));


		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -567);

		lastModified = cal.getTime();

		lastEdited = visitor.getLastEdited(lastModified);
		
		assertNotNull("Null returned", lastEdited);
		assertTrue("Incorrect array length", lastEdited.length == 2);

		assertTrue("Wrong time value","567".equals(lastEdited[0]));
		assertTrue("Wrong time unit", "days".equals(lastEdited[1]));

	}
	
	@Test
	public void testGetLastEditedHours() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);

		Date lastModified = cal.getTime();

		String[] lastEdited = visitor.getLastEdited(lastModified);

		assertNotNull("Null returned", lastEdited);
		assertTrue("Incorrect array length", lastEdited.length == 2);

		assertTrue("Wrong time value","1".equals(lastEdited[0]));
		assertTrue("Wrong time unit", "hour".equals(lastEdited[1]));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -2);

		lastModified = cal.getTime();

		lastEdited = visitor.getLastEdited(lastModified);

		assertNotNull("Null returned", lastEdited);
		assertTrue("Incorrect array length", lastEdited.length == 2);

		assertTrue("Wrong time value","2".equals(lastEdited[0]));
		assertTrue("Wrong time unit", "hours".equals(lastEdited[1]));

		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -23);

		lastModified = cal.getTime();

		lastEdited = visitor.getLastEdited(lastModified);
		
		assertNotNull("Null returned", lastEdited);
		assertTrue("Incorrect array length", lastEdited.length == 2);

		assertTrue("Wrong time value","23".equals(lastEdited[0]));
		assertTrue("Wrong time unit", "hours".equals(lastEdited[1]));
	}
	
	@Test
	public void testGetLastEditedMinutes() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1);

		Date lastModified = cal.getTime();

		String[] lastEdited = visitor.getLastEdited(lastModified);

		assertNotNull(lastEdited);
		assertTrue(lastEdited.length == 2);

		assertTrue("1".equals(lastEdited[0]));
		assertTrue("minute".equals(lastEdited[1]));

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);

		lastModified = cal.getTime();

		lastEdited = visitor.getLastEdited(lastModified);

		assertTrue("2".equals(lastEdited[0]));
		assertTrue("minutes".equals(lastEdited[1]));

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -59);

		lastModified = cal.getTime();

		lastEdited = visitor.getLastEdited(lastModified);
		
		assertTrue("59".equals(lastEdited[0]));
		assertTrue("minutes".equals(lastEdited[1]));
	}
	
	@Test
	public void testGetLastEditedSeconds() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -15);

		Date lastModified = cal.getTime();

		String[] lastEdited = visitor.getLastEdited(lastModified);

		assertNotNull(lastEdited);
		assertTrue(lastEdited.length == 2);
		assertTrue("seconds".equals(lastEdited[1]));
	}
}
