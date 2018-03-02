/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.textile.TextileUtil;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class TestDataFactory {

	public static ComponentResourceSelector selector_without_axis() {
		return new ComponentResourceSelector(Locale.US);
	}

	public static ComponentResourceSelector selector_with_axis_but_without_definition() {
		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		params.put("param3", "param3");
		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		return selector.withAxis(Map.class, params);
	}

	public static ComponentResourceSelector selector_with_axis_with_definition_but_for_other_component() {
		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		CustomTemplateDefinition definition = custom_TagItem_definition();
		params.put(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, definition);

		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		return selector.withAxis(Map.class, params);
	}

	public static CustomTemplateDefinition custom_TagItem_definition() {
		CustomTemplateDefinition definition = new CustomTemplateDefinition();
		definition.setTemplateFileName("CustomTagItem.tml");
		definition.setTemplateClassName("TagItem");
		return definition;
	}

	public static CustomTemplateDefinition custom_TextileTag_definition() {
		CustomTemplateDefinition definition = new CustomTemplateDefinition();
		definition.setTemplateFileName("CustomTextileTags.tml");
		definition.setTemplateClassName("TextileTags");
		return definition;
	}


	public static ComponentResourceSelector selector_with_axis_and_with_definition_for_this_component() {
		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		CustomTemplateDefinition definition = custom_TextileTag_definition();
		params.put(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, definition);

		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.US);
		return selector.withAxis(Map.class, params);
	}

	public static Request request() {
		Request request = Mockito.mock(Request.class);
		Mockito.when(request.getServerName()).thenReturn("cce.cc");
		return request;
	}

	public static IWebNodeService webNodeService() {
		WebSiteLayout layout = Mockito.mock(WebSiteLayout.class);
		Mockito.when(layout.getId()).thenReturn(1L);
		IWebNodeService service = Mockito.mock(IWebNodeService.class);
		Mockito.when(service.getLayout()).thenReturn(layout);
		return service;
	}

	public static IResourceService resourceService() {
		IResourceService service = Mockito.mock(IResourceService.class);
		return service;
	}

}
