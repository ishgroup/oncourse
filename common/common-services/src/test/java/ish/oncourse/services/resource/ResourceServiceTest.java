package ish.oncourse.services.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
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

		IWebSiteService webSiteService1 = mockWebSiteService("scc");
		ILookupService lookupService = mock(ILookupService.class);
		
		ResourceService resourceService = new ResourceService(propertyService,
				webSiteService1, lookupService, null, null);

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

		IWebSiteService webSiteService1 = mockWebSiteService("-non-existent-");
		ILookupService lookupService = mock(ILookupService.class);
		
		ResourceService resourceService = new ResourceService(propertyService,
				webSiteService1, lookupService, null, null);

		List<PrivateResource> dummyConf = resourceService
				.getConfigResources("dummy.conf");
		
		assertNotNull(dummyConf);
		assertEquals(root.toURI().toURL().toExternalForm()
				+ "default/conf/dummy.conf", dummyConf.get(0).getPrivateUrl()
				.toExternalForm());

		IWebSiteService webSiteService2 = mockWebSiteService("testcollege");

		resourceService = new ResourceService(propertyService, webSiteService2, lookupService, null, null);

		List<PrivateResource> dummyConf2 = resourceService
				.getConfigResources("dummy.conf");
		
		assertNotNull(dummyConf2);
		//because this resource doesn't exist in real test path
		assertEquals(root.toURI().toURL().toExternalForm()
				+ "default/conf/dummy.conf", dummyConf2.get(0).getPrivateUrl()
				.toExternalForm());
	}
	
	private IWebSiteService mockWebSiteService(String siteCode) {
		WebSite webSite = mock(WebSite.class);
		
		when(webSite.getName()).thenReturn("Sydney Community College Test Site");
		when(webSite.getSiteKey()).thenReturn("scc");
		
		College college = mock(College.class);
		
		when(college.getWebSites()).thenReturn(Arrays.asList(webSite));
		when(webSite.getCollege()).thenReturn(college);

		WebHostName host = mock(WebHostName.class);
		when(host.getName()).thenReturn("scc.test1.oncourse.net.au");
		when(host.getWebSite()).thenReturn(webSite);

		IWebSiteService webSiteService = mock(IWebSiteService.class);

		when(webSiteService.getCurrentCollege()).thenReturn(college);
		when(webSiteService.getCurrentDomain()).thenReturn(host);
		when(webSiteService.getCurrentWebSite()).thenReturn(webSite);
		
		return webSiteService;
	}
}
