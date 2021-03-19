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
import org.apache.tapestry5.services.pageload.ComponentResourceLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class GetSiteTemplateResourceTest {
	private IWebNodeService webNodeService;
	private IResourceService resourceService;
	private ComponentResourceLocator componentResourceLocator;
	private ComponentModel componentModel;
	private GetSiteTemplateResource getResource;

	@Before
	public void before() {
		componentModel = TestDataFactory.componentModel();
		Mockito.when(componentModel.getComponentClassName()).thenReturn("ish.oncourse.ui.pages.Courses");
		Mockito.when(componentModel.getBaseResource()).thenReturn(new ClasspathResource("ish/oncourse/ui/pages/Courses"));

		webNodeService = TestDataFactory.webNodeService();
		resourceService = TestDataFactory.resourceService();
		componentResourceLocator = TestDataFactory.componentResourceLocator();

		getResource = new GetSiteTemplateResource(webNodeService, resourceService, componentResourceLocator);
	}

	@Test
	public void core_tapestry_component() {
		Mockito.when(componentModel.getComponentClassName()).thenReturn("org.apache.tapestry5.corelib.components.If");
		Mockito.when(componentModel.getBaseResource()).thenReturn(new ClasspathResource("org/apache/tapestry5/corelib/components/If"));
		Resource resource = getResource.get(componentModel, TestDataFactory.selector_without_axis());
		Assert.assertNotNull(resource);
		Assert.assertEquals("org/apache/tapestry5/corelib/components/If.tml", resource.getPath());
		Mockito.verify(componentResourceLocator,  Mockito.times(1)).locateTemplate(componentModel, TestDataFactory.selector_without_axis());
		Mockito.verifyZeroInteractions(resourceService);
	}

	@Test
	public void willow_component___layout_is_null() {
		Mockito.when(webNodeService.getLayout()).thenReturn(null);
		Resource resource = getResource.get(componentModel, TestDataFactory.selector_without_axis());
		Assert.assertNotNull(resource);
		Assert.assertEquals("ish/oncourse/ui/pages/Courses.tml", resource.getPath());
		Mockito.verify(componentResourceLocator, Mockito.times(1)).locateTemplate(componentModel, TestDataFactory.selector_without_axis());
		Mockito.verifyZeroInteractions(resourceService);
	}

	@Test
	public void willow_component___layout_is_notnull___not_in_db() {
		Mockito.when(resourceService.getDbTemplateResource(webNodeService.getLayout(), "Courses.tml")).thenReturn(null);
		Resource resource = getResource.get(componentModel, TestDataFactory.selector_without_axis());
		Assert.assertNotNull(resource);
		Assert.assertEquals("ish/oncourse/ui/pages/Courses.tml", resource.getPath());
		Mockito.verify(resourceService, Mockito.times(1)).getDbTemplateResource(webNodeService.getLayout(), "Courses.tml");
		Mockito.verify(componentResourceLocator,  Mockito.times(1)).locateTemplate(componentModel, TestDataFactory.selector_without_axis());
	}


	@Test
	public void willow_component___layout_is_notnull___in_db() {
		Resource dbResource = Mockito.mock(Resource.class);
		Mockito.when(resourceService.getDbTemplateResource(webNodeService.getLayout(), "Courses.tml")).thenReturn(dbResource);

		Resource resource = getResource.get(componentModel, TestDataFactory.selector_without_axis());
		Assert.assertNotNull(resource);
		Assert.assertEquals(dbResource, resource);
		Mockito.verify(resourceService, Mockito.times(1)).getDbTemplateResource(webNodeService.getLayout(), "Courses.tml");
		Mockito.verifyZeroInteractions(componentResourceLocator);
	}

	@Test
	public void willow_component___layout_is_not_null___in_db___has_custom_template_name___for_other_component() {
		Resource dbResource = Mockito.mock(Resource.class);
		Mockito.when(resourceService.getDbTemplateResource(webNodeService.getLayout(), "Courses.tml")).thenReturn(dbResource);
		Resource resource = getResource.get(componentModel, TestDataFactory.selector_with_axis_with_definition_but_for_other_component());
		Assert.assertNotNull(resource);
		Assert.assertEquals(dbResource, resource);
		Mockito.verify(resourceService, Mockito.times(1)).getDbTemplateResource(webNodeService.getLayout(), "Courses.tml");
		Mockito.verifyZeroInteractions(componentResourceLocator);
	}

	@Test
	public void willow_component___layout_is_notnull___not_in_db___has_custom_template_name() {
		componentModel = TestDataFactory.componentModel();
		Mockito.when(resourceService.getDbTemplateResource(webNodeService.getLayout(), "CustomTextileTags.tml")).thenReturn(null);
		Resource resource = getResource.get(componentModel, TestDataFactory.selector_with_axis_and_with_definition_for_this_component());
		Assert.assertNotNull(resource);
		Assert.assertEquals(new ClasspathResource("ish/oncourse/textile/pages/CustomTextileTags.tml"), resource);
		Mockito.verify(resourceService, Mockito.times(1)).getDbTemplateResource(webNodeService.getLayout(), "CustomTextileTags.tml");
		Mockito.verifyZeroInteractions(componentResourceLocator);
	}

	@Test
	public void willow_component___layout_is_notnull___in_db___has_custom_template_name() {
		componentModel = TestDataFactory.componentModel();
		Resource dbResource = Mockito.mock(Resource.class);
		Mockito.when(resourceService.getDbTemplateResource(webNodeService.getLayout(), "CustomTextileTags.tml")).thenReturn(dbResource);

		Resource resource = getResource.get(componentModel, TestDataFactory.selector_with_axis_and_with_definition_for_this_component());

		Assert.assertNotNull(resource);
		Assert.assertEquals(dbResource, resource);
		Mockito.verify(resourceService, Mockito.times(1)).getDbTemplateResource(webNodeService.getLayout(), "CustomTextileTags.tml");
		Mockito.verifyZeroInteractions(componentResourceLocator);
	}



}
