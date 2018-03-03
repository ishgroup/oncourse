/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import org.apache.tapestry5.internal.util.MultiKey;
import org.junit.Assert;
import org.junit.Test;

import static ish.oncourse.ui.services.pageload.TestDataFactory.*;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class GetTemplateKeyTest {

	@Test
	public void test_key_selector_without_axis_and_with_layout() {
		GetTemplateKey getTemplateKey = new GetTemplateKey(webNodeService(),
				request());

		MultiKey multiKey = getTemplateKey.get("TagsTextile", selector_without_axis());

		MultiKey expected = new MultiKey("TagsTextile", selector_without_axis(), "cce.cc", 1L);
		Assert.assertEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile1", selector_without_axis(), "cce.cc", 1L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_with_axis_but_without_definition(), "cce.cc", 1L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_without_axis(), "cce.cc1", 1L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_without_axis(), "cce.cc1", 2L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_without_axis(), "cce.cc", null);
		Assert.assertNotEquals(expected, multiKey);
	}

	@Test
	public void test_key_selector_with_axis_and_with_layout() {
		GetTemplateKey getTemplateKey = new GetTemplateKey(webNodeService(),
				request());

		MultiKey multiKey = getTemplateKey.get("TagsTextile", selector_with_axis_and_with_definition_for_this_component());

		MultiKey expected = new MultiKey("TagsTextile", selector_with_axis_and_with_definition_for_this_component(), "cce.cc", 1L);
		Assert.assertEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile1", selector_with_axis_and_with_definition_for_this_component(), "cce.cc", 1L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_with_axis_but_without_definition(), "cce.cc", 1L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_with_axis_and_with_definition_for_this_component(), "cce.cc1", 1L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_with_axis_and_with_definition_for_this_component(), "cce.cc", 2L);
		Assert.assertNotEquals(expected, multiKey);

		expected = new MultiKey("TagsTextile", selector_with_axis_and_with_definition_for_this_component(), "cce.cc", null);
		Assert.assertNotEquals(expected, multiKey);
	}

}
