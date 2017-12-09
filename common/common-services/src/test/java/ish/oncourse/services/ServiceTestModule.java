package ish.oncourse.services;

import ish.oncourse.cayenne.cache.ICacheEnabledService;
import ish.oncourse.model.College;
import ish.oncourse.services.html.NoCacheMetaProvider;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.PageRenderer;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Local;

import javax.sql.DataSource;

public class ServiceTestModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(ServerRuntime.class, resources -> ServiceTest.serverRuntime.get());

		binder.bind(DataSource.class, resources -> ServiceTest.dataSource.get());


		BinderFunctions.bindEntityServices(binder);
		BinderFunctions.bindEnvServices(binder, "services", true);
		BinderFunctions.bindPaymentGatewayServices(binder);
		BinderFunctions.bindTapestryServices(binder, NoCacheMetaProvider.class, PageRenderer.class);
		BinderFunctions.bindReferenceServices(binder);
		new BindWebSiteServices().binder(binder).webSiteService(res -> new WebSiteServiceOverride() {
			@Override
			public College getCurrentCollege() {
				ICollegeService collegeService = res.getService(ICollegeService.class);
				return collegeService.findBySecurityCode("345ttn44$%9");
			}
		}).bind();

		binder.bind(ICacheEnabledService.class, res -> new ICacheEnabledService() {
			@Override
			public boolean isCacheEnabled() {
				return false;
			}

			@Override
			public void setCacheEnabled(Boolean enabled) {

			}
		});
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local IWebSiteService webSiteService) {
		configuration.add(IWebSiteService.class, webSiteService);
	}

}
