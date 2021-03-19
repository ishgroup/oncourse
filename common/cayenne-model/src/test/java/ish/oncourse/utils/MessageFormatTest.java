package ish.oncourse.utils;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 * Created by akoiro on 14/04/2016.
 */
public class MessageFormatTest {

	@Test
	public void testMessageWithoutParameters() {
		Queueable queueable = getQueueable();
		MessageFormat messageFormat = MessageFormat.valueOf(queueable, "Test");
		String result = messageFormat.format();
		Assert.assertEquals(queueable.getClass().getSimpleName() + ": willowId: 2001, angelId: 3001, collegeId: 1001, Test", result);
	}

	@Test
	public void testMessageWithParameters() {
		Queueable queueable = getQueueable();
		MessageFormat messageFormat = MessageFormat.valueOf(queueable, "Test param1: %s param: %d", "param1", 1000);
		String result = messageFormat.format();
		Assert.assertEquals(queueable.getClass().getSimpleName() + ": willowId: 2001, angelId: 3001, collegeId: 1001, Test param1: param1 param: 1000", result);
	}

	private Queueable getQueueable() {
		College college = Mockito.mock(College.class);
		when(college.getId()).thenReturn(1001L);

		Queueable queueable = Mockito.mock(Queueable.class);
		when(queueable.getId()).thenReturn(2001L);
		when(queueable.getAngelId()).thenReturn(3001L);
		when(queueable.getCollege()).thenReturn(college);
		return queueable;
	}
}
