/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.textile;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class CustomTemplateDefinitionTest {

	@Test
	public void testEquals_HashCode() {
		CustomTemplateDefinition definition0 = new CustomTemplateDefinition();
		definition0.setTemplateClassName("TemplateClassName0");
		definition0.setTemplateFileName("TemplateFileName0");

		CustomTemplateDefinition definition1 = new CustomTemplateDefinition();
		definition1.setTemplateClassName("TemplateClassName0");
		definition1.setTemplateFileName("TemplateFileName0");
		Assert.assertEquals("Test TemplateClassName, TemplateFileName are the same", definition0, definition1);
		Assert.assertEquals("Test TemplateClassName, TemplateFileName are the same", definition0.hashCode(), definition1.hashCode());

		definition1 = new CustomTemplateDefinition();
		definition1.setTemplateClassName("TemplateClassName1");
		definition1.setTemplateFileName("TemplateFileName1");
		Assert.assertNotEquals("Test TemplateClassName, TemplateFileName are not the same", definition0, definition1);
		Assert.assertNotEquals("Test TemplateClassName, TemplateFileName are the same", definition0.hashCode(), definition1.hashCode());

		definition1 = new CustomTemplateDefinition();
		definition1.setTemplateClassName(null);
		definition1.setTemplateFileName(null);
		Assert.assertNotEquals("Test when the second TemplateClassName, TemplateFileName are null", definition0, definition1);
		Assert.assertNotEquals("Test when the second TemplateClassName, TemplateFileName are null", definition0.hashCode(), definition1.hashCode());
	}
}
