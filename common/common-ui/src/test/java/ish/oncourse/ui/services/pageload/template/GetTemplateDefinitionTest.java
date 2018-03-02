/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import org.junit.Assert;
import org.junit.Test;

import static ish.oncourse.ui.services.pageload.template.TestDataFactory.*;


/**
 * User: akoiro
 * Date: 3/3/18
 */
public class GetTemplateDefinitionTest {

	@Test
	public void test_selector_without_axis() {
		GetTemplateDefinition getDefinition = new GetTemplateDefinition(selector_without_axis());
		Assert.assertNull(getDefinition.get("TextileTags"));
	}

	@Test
	public void test_selector_with_axis_but_without_definition() {
		GetTemplateDefinition getDefinition =
				new GetTemplateDefinition(selector_with_axis_but_without_definition());
		Assert.assertNull(getDefinition.get("TextileTags"));
	}


	@Test
	public void test_selector_with_axis_with_definition_for_other_component() {
		GetTemplateDefinition getDefinition =
				new GetTemplateDefinition(selector_with_axis_with_definition_but_for_other_component());
		Assert.assertNull(getDefinition.get("TextileTags"));
	}

	@Test
	public void test_selector_with_axis_with_definition_for_this_component() {
		GetTemplateDefinition getDefinition = new GetTemplateDefinition(selector_with_axis_and_with_definition_for_this_component());
		Assert.assertEquals(custom_TextileTag_definition(), getDefinition.get("TextileTags"));
	}

}
