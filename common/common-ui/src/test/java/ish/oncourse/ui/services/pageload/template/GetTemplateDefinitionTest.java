/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class GetTemplateDefinitionTest {

	@Test
	public void test_selector_without_axis() {
		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		GetTemplateDefinition getDefinition = new GetTemplateDefinition(selector);
		Assert.assertNull(getDefinition.get("TextileTags"));
	}

	@Test
	public void test_selector_with_axis_not_with_definition() {
		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		params.put("param3", "param3");
		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		selector = selector.withAxis(Map.class, params);

		GetTemplateDefinition getDefinition = new GetTemplateDefinition(selector);
		Assert.assertNull(getDefinition.get("TextileTags"));
	}


	@Test
	public void test_selector_with_axis_with_definition_for_other_component() {
		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		CustomTemplateDefinition definition = new CustomTemplateDefinition();
		definition.setTemplateFileName("CustomTagItem.tml");
		definition.setTemplateClassName("TagItem");
		params.put(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, definition);

		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		selector = selector.withAxis(Map.class, params);

		GetTemplateDefinition getDefinition = new GetTemplateDefinition(selector);

		Assert.assertNull(getDefinition.get("TextileTags"));
	}

	@Test
	public void test_selector_with_axis_with_definition_for_this_component() {
		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		CustomTemplateDefinition definition = new CustomTemplateDefinition();
		definition.setTemplateFileName("CustomTextileTags.tml");
		definition.setTemplateClassName("TextileTags");
		params.put(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, definition);

		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		selector = selector.withAxis(Map.class, params);

		GetTemplateDefinition getDefinition = new GetTemplateDefinition(selector);
		Assert.assertEquals(definition, getDefinition.get("TextileTags"));
	}

}
