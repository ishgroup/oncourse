package ish.oncourse.portal.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.portal.PortalModule;
import ish.oncourse.portal.services.AppModule;
import ish.oncourse.portal.services.site.PortalSiteService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import net.sf.ehcache.CacheManager;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ImportModule({AppModule.class})
public class TestModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(ServerRuntime.class, resources -> ServerRuntime.builder()
				.dataSource(getDataSource())
				.addConfig("cayenne-oncourse.xml")
				.addModule(new PortalModule.PortalCayenneModule())
				.build());
	}

	public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return new RequestFilter() {
			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
				return handler.service(request, response);
			}
		};
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
