package ish.oncourse.portal.service;

import ish.oncourse.cayenne.WillowCayenneModuleBuilder;
import ish.oncourse.cayenne.cache.JCacheModule;
import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.services.AppModule;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ImportModule({AppModule.class})
public class TestModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(ServerRuntime.class, resources -> ServerRuntime.builder()
				.dataSource(getDataSource())
				.addConfig("cayenne-oncourse.xml")
				.addModule(new WillowCayenneModuleBuilder().build())
				.addModule(new JCacheModule())
				.build());
	}

	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return (request, response, handler) -> handler.service(request, response);
	}

	private static DataSource getDataSource() {
		try {
			return ServiceTest.getDataSource("jdbc/oncourse");
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}


	public IWebSiteService decorateWebSiteService(final IWebSiteService delegate) {
		WebSite webSite = mock(WebSite.class);

		when(webSite.getName()).thenReturn("Sydney Community College Test Site");
		when(webSite.getSiteKey()).thenReturn("scc");
		when(webSite.getResourceFolderName()).thenReturn("default");

		College college = mock(College.class);

		when(college.getWebSites()).thenReturn(Arrays.asList(webSite));
		when(webSite.getCollege()).thenReturn(college);

		WebHostName host = mock(WebHostName.class);
		when(host.getName()).thenReturn("tara.oncourse.cc/portal");
		when(host.getWebSite()).thenReturn(webSite);

		PortalSiteService mockService = mock(PortalSiteService.class);

		when(mockService.getCurrentCollege()).thenReturn(college);
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
