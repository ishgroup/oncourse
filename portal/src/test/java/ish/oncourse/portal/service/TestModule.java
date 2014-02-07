package ish.oncourse.portal.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.AppModule;
import ish.oncourse.portal.services.pageload.IUserAgentDetector;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;

@SubModule(AppModule.class)
public class TestModule {
	
	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return new RequestFilter() {
			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
				return handler.service(request, response);
			}
		};
	}

	public static PortalSiteService buildPortalSiteServiceOverride() {

		WebSite webSite = mock(WebSite.class);

		when(webSite.getName()).thenReturn("Sydney Community College Test Site");
		when(webSite.getSiteKey()).thenReturn("scc");
		when(webSite.getResourceFolderName()).thenReturn("default");

		College college = mock(College.class);

		when(college.getWebSites()).thenReturn(Arrays.asList(webSite));
		when(webSite.getCollege()).thenReturn(college);

		WebHostName host = mock(WebHostName.class);
		when(host.getName()).thenReturn("tara.test1.oncourse.net.au/portal");
		when(host.getWebSite()).thenReturn(webSite);

		PortalSiteService mockService = mock(PortalSiteService.class);

		when(mockService.getCurrentCollege()).thenReturn(college);
		when(mockService.getCurrentDomain()).thenReturn(host);
		when(mockService.getCurrentWebSite()).thenReturn(webSite);
		
		return mockService;
	}
	
	public static IAuthenticationService buildAuthenticationServiceOverride() {

		IAuthenticationService mockService = mock(IAuthenticationService.class);

		Date today = new Date();

		Contact user = new Contact();
		user.setCreated(today);
		user.setFamilyName("familyName");
		user.setGivenName("givenName");
		user.setEmailAddress("test@test.com");
		user.setModified(today);
		user.setPassword("12345");

		when(mockService.getUser()).thenReturn(user);
		
		return mockService;
	}
	
	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IAuthenticationService authServiceOverride) {
		configuration.add(IAuthenticationService.class, authServiceOverride);
	}
	
	public ICourseClassService buildCourseClassServiceOverride() {
		ICourseClassService mock = mock(ICourseClassService.class);
		return mock;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local ICourseClassService courseClassServiceOverride) {
		configuration.add(ICourseClassService.class, courseClassServiceOverride);
	}
	
//	public IUserAgentDetector buildIUserAgentDetectorOverride() {
//		IUserAgentDetector mock = mock(IUserAgentDetector.class);
//		when(mock.getUserAgent()).thenReturn(UserAgent.DESKTOP);
//		return mock;
//	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local IUserAgentDetector iUserAgentDetector) {
		configuration.add(IUserAgentDetector.class, iUserAgentDetector);
	}
	
	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
	     configuration.override(SymbolConstants.SECURE_ENABLED, "false");
	} 
}
