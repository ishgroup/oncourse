package ish.oncourse.services.resource;

import ish.oncourse.services.resource.ResourceService;
import ish.oncourse.services.resource.Resource;
import ish.oncourse.services.resource.PrivateResource;
import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.MockWebSiteService;

public class ResourceServiceTest extends Assert {

	@Test
	public void getWebResource() throws Exception {
		String packagePath = getClass().getPackage().getName()
				.replace('.', '/');

		URL placeholderUrl = getClass().getClassLoader().getResource(
				packagePath + "/placeholder.txt");

		final File root = new File(placeholderUrl.toURI()).getParentFile();

		IPropertyService propertyService = new IPropertyService() {

			public String string(Property property) {
				switch (property) {
				case CustomComponentsPath:
					return root.getAbsolutePath();
				default:
					throw new IllegalArgumentException(String.valueOf(property));
				}
			}
		};

		MockWebSiteService webSiteService1 = new MockWebSiteService();

		ResourceService resourceService = new ResourceService(propertyService,
				webSiteService1);

		Resource r1 = resourceService.getWebResource(null, "x/some.css");
		assertNotNull(r1);
		assertEquals("/s/x/some.css", r1.getPublicUrl());

		Resource r2 = resourceService.getWebResource("app", "x/some.css");
		assertNotNull(r2);
		assertEquals("/s/x/some.css", r2.getPublicUrl());

		Resource r3 = resourceService.getWebResource("Ajax", "x/some.css");
		assertNotNull(r3);
		assertEquals(
				"/s/Frameworks/Ajax.framework/x/some.css",
				r3.getPublicUrl());
	}

	@Test
	public void getConfigResource() throws Exception {

		String packagePath = getClass().getPackage().getName()
				.replace('.', '/');

		URL placeholderUrl = getClass().getClassLoader().getResource(
				packagePath + "/placeholder.txt");

		final File root = new File(placeholderUrl.toURI()).getParentFile();

		IPropertyService propertyService = new IPropertyService() {

			public String string(Property property) {
				switch (property) {
				case CustomComponentsPath:
					return root.getAbsolutePath();
				default:
					throw new IllegalArgumentException(String.valueOf(property));
				}
			}
		};

		MockWebSiteService webSiteService1 = new MockWebSiteService();
		webSiteService1.setSiteCode("-non-existent-");

		ResourceService resourceService = new ResourceService(propertyService,
				webSiteService1);

		PrivateResource dummyConf = resourceService
				.getConfigResource("dummy.conf");
		assertNotNull(dummyConf);
		assertEquals(root.toURI().toURL().toExternalForm()
				+ "default/config/dummy.conf", dummyConf.getPrivateUrl()
				.toExternalForm());

		MockWebSiteService webSiteService2 = new MockWebSiteService();
		webSiteService2.setSiteCode("testcollege");

		resourceService = new ResourceService(propertyService, webSiteService2);

		PrivateResource dummyConf2 = resourceService
				.getConfigResource("dummy.conf");
		assertNotNull(dummyConf2);
		assertEquals(root.toURI().toURL().toExternalForm()
				+ "testcollege/config/dummy.conf", dummyConf2.getPrivateUrl()
				.toExternalForm());
	}
}
