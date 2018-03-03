/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template.resource;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.ui.services.pageload.TestDataFactory;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class GetCustomTemplateResourceTest {
	private IWebNodeService webNodeService;
	private IResourceService resourceService;
	private ComponentModel model;
	private GetCustomTemplateResource getResource;

	private Resource dbResource = Mockito.mock(Resource.class);
	private Resource cpResource = TestDataFactory.cpResource();

	@Before
	public void before() {
		webNodeService = TestDataFactory.webNodeService();
		resourceService = TestDataFactory.resourceService();
		model = TestDataFactory.componentModel();
		getResource = new GetCustomTemplateResource(webNodeService,
				resourceService);
		Mockito.when(model.getBaseResource()).thenReturn(cpResource);

	}

	@Test
	public void test_selector_without_axis() {
		ComponentResourceSelector selector = TestDataFactory.selector_without_axis();
		Assert.assertNull(getResource.get(model, selector));
		Mockito.verifyZeroInteractions(resourceService);
	}

	@Test
	public void test_selector_with_axis_but_without_definition() {
		ComponentResourceSelector selector = TestDataFactory.selector_with_axis_but_without_definition();
		Assert.assertNull(getResource.get(model, selector));
		Mockito.verifyZeroInteractions(resourceService);
	}

	@Test
	public void test_selector_with_axis_with_definition_but_for_other_component() {
		ComponentResourceSelector selector = TestDataFactory.selector_with_axis_with_definition_but_for_other_component();
		Assert.assertNull(getResource.get(model, selector));
	}

	@Test
	public void test_get_db_resource_when_selector_with_axis_and_with_definition_for_this_component() {
		Mockito.when(resourceService.getDbTemplateResource(webNodeService.getLayout(),
				TestDataFactory.custom_TextileTag_definition().getTemplateFileName())).thenReturn(dbResource);

		ComponentResourceSelector selector = TestDataFactory.selector_with_axis_and_with_definition_for_this_component();
		Resource resource = getResource.get(model, selector);
		Assert.assertEquals(dbResource, resource);
	}

	@Test
	public void test_get_cp_resource_when_selector_with_axis_and_with_definition_for_this_component() {
		ComponentResourceSelector selector = TestDataFactory.selector_with_axis_and_with_definition_for_this_component();
		Resource resource = getResource.get(model, selector);
		Assert.assertEquals(new ClasspathResource("ish/oncourse/textile/pages/CustomTextileTags.tml"), resource);
	}

}
