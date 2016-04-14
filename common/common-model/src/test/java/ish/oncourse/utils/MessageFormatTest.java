package ish.oncourse.utils;

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
		Assert.assertEquals(queueable.getClass().getSimpleName() + ": willowId: 1234, angelId: 4321: Test", result);
	}

	@Test
	public void testMessageWithParameters() {
		Queueable queueable = getQueueable();
		MessageFormat messageFormat = MessageFormat.valueOf(queueable, "Test param1: %s param: %d", "param1", 1000);
		String result = messageFormat.format();
		Assert.assertEquals(queueable.getClass().getSimpleName() + ": willowId: 1234, angelId: 4321: Test param1: param1 param: 1000", result);
	}

	private Queueable getQueueable() {
		Queueable queueable = Mockito.mock(Queueable.class);
		when(queueable.getId()).thenReturn(1234L);
		when(queueable.getAngelId()).thenReturn(4321L);
		return queueable;
	}
}
