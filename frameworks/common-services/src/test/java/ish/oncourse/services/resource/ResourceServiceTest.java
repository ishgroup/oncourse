package ish.oncourse.services.resource;

import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.MockWebSiteService;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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

		Resource r1 = resourceService.getWebResource("x/some.css");
		assertNotNull(r1);
		assertEquals("/s/x/some.css", r1.getPublicUrl());
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

		List<PrivateResource> dummyConf = resourceService
				.getConfigResources("dummy.conf");
		
		assertNotNull(dummyConf);
		assertEquals(root.toURI().toURL().toExternalForm()
				+ "default/conf/dummy.conf", dummyConf.get(0).getPrivateUrl()
				.toExternalForm());

		MockWebSiteService webSiteService2 = new MockWebSiteService();
		webSiteService2.setSiteCode("testcollege");

		resourceService = new ResourceService(propertyService, webSiteService2);

		List<PrivateResource> dummyConf2 = resourceService
				.getConfigResources("dummy.conf");
		
		assertNotNull(dummyConf2);
		//because this resource doesn't exist in real test path
		assertEquals(root.toURI().toURL().toExternalForm()
				+ "default/conf/dummy.conf", dummyConf2.get(0).getPrivateUrl()
				.toExternalForm());
	}
}
