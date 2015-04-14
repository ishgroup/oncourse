package ish.oncourse.portal.service;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.services.AppModule;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.*;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	
	public ICourseClassService buildCourseClassServiceOverride() {
		ICourseClassService mock = mock(ICourseClassService.class);
		return mock;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
			@Local ICourseClassService courseClassServiceOverride) {
		configuration.add(ICourseClassService.class, courseClassServiceOverride);
	}
	
	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
	     configuration.override(SymbolConstants.SECURE_ENABLED, "false");
	} 
}
