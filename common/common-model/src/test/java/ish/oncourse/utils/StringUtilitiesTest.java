package ish.oncourse.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilitiesTest {

	@Test
	public void test_cutToNull()
	{
		String template = "%cpart1%cpart2%c";
		for (char i = 0; i < 32; i++) {
			String value = String.format(template,i,i,i);
			assertEquals(value, "part1part2", StringUtilities.cutToNull(value));
		}
		String value = String.format(template,' ',' ',' ');
		assertEquals(value,"part1 part2",StringUtilities.cutToNull(value));


		assertEquals("part1 part2","part1 part2",StringUtilities.cutToNull("part1 part2"));


		StringBuffer buffer = new StringBuffer();
		for (char i = 0; i < 33; i++) {
			buffer.append(i);
		}
		assertNull(StringUtilities.cutToNull(buffer.toString()));
	}

	@Test
	public void test_cutToEmpty()
	{
		String template = "%cpart1%cpart2%c";
		for (char i = 0; i < 32; i++) {
			String value = String.format(template,i,i,i);
			assertEquals(value, "part1part2", StringUtilities.cutToEmpty(value));
		}
		String value = String.format(template,' ',' ',' ');
		assertEquals(value,"part1 part2",StringUtilities.cutToEmpty(value));

		assertEquals("part1 part2","part1 part2",StringUtilities.cutToEmpty("part1 part2"));


		StringBuffer buffer = new StringBuffer();
		for (char i = 0; i < 33; i++) {
			buffer.append(i);
		}
		assertEquals(StringUtils.EMPTY, StringUtilities.cutToEmpty(buffer.toString()));
	}

}
