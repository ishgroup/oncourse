package ish.oncourse.website.pages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.environment.IEnvironmentService;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.test.ServiceTest;

import java.io.IOException;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.test.PageTester;
import org.junit.Before;
import org.junit.Test;

public class Error500Test extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.website", "App", Error500Module.class);
	}

	@Test
	public void testWrongDomain() throws Exception {
		PageTester tester = getPageTester();
		Document doc = tester.renderPage("Page");
		Element expElement = doc.getElementById("container");
		String body = expElement.getChildMarkup();
		boolean result = body.contains("Site Not Found");
		assertTrue("Check exception message.", result);
		assertNotNull("Check body not empty.", body);
	}

	public static class Error500Module {

		public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
			return new RequestFilter() {
				public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
					return handler.service(request, response);
				}
			};
		}

		public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration, @Local RequestFilter logFilter) {
			configuration.override("LogFilter", logFilter);
		}

		public ILookupService buildLookupServiceOverride() {
			ILookupService mockService = mock(ILookupService.class);
			return mockService;
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
				@Local ILookupService lookupServiceOverride) {
			configuration.add(ILookupService.class, lookupServiceOverride);
		}

		public IPropertyService buildPropertyServiceOverride() {
			IPropertyService mockService = mock(IPropertyService.class);

			when(mockService.string(eq(Property.CustomComponentsPath))).thenReturn("src/test/resources");

			return mockService;
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
				@Local IPropertyService propServiceOverride) {
			configuration.add(IPropertyService.class, propServiceOverride);
		}
		
		public IEnvironmentService buildEnvironmentServiceOverride() {
			IEnvironmentService mockService = mock(IEnvironmentService.class);
			
			when(mockService.getApplicationName()).thenReturn("onCourseApp");
			when(mockService.getBuildServerID()).thenReturn("onCourseServer");
			when(mockService.getCiVersion()).thenReturn("100ci");
			when(mockService.getScmVersion()).thenReturn("100scm");
			return mockService;
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
				@Local IEnvironmentService environmentServiceOverride) {
			configuration.add(IEnvironmentService.class, environmentServiceOverride);
		}
		
		public IWebSiteVersionService buildWebSiteVersionServiceOverride() {
			IWebSiteVersionService mockService = mock(IWebSiteVersionService.class);	
			when(mockService.getCurrentVersion(any(WebSite.class))).thenReturn(null);
			return mockService;
		}
		
		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
											  @Local IWebSiteVersionService webSiteVersionServiceOverride) {
			configuration.add(IWebSiteVersionService.class, webSiteVersionServiceOverride);
		}
	}
}
