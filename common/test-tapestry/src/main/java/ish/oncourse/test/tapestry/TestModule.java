package ish.oncourse.test.tapestry;

import ish.oncourse.cayenne.cache.ICacheEnabledService;
import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.BindWebSiteServices;
import ish.oncourse.services.ModuleBinder;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.system.ICollegeService;
import org.apache.tapestry5.ioc.ServiceBinder;

public class TestModule {
	public static void bind(ServiceBinder binder) {
		new ModuleBinder()
				.bindWebSiteServices(new BindWebSiteServices().webSiteService(res -> new WebSiteServiceOverride() {
					@Override
					public College getCurrentCollege() {
						ICollegeService collegeService = res.getService(ICollegeService.class);
						return collegeService.findBySecurityCode("345ttn44$%9");
					}

					@Override
					public WebSite getCurrentWebSite() {
						return getCurrentCollege().getWebSites().get(0);
					}
				}))
				.dataSource(resources -> ServiceTest.dataSource.get())
				.serverRuntime(resources -> ServiceTest.serverRuntime.get())
				.cacheEnabledService(res -> new ICacheEnabledService() {
					@Override
					public boolean isCacheEnabled() {
						return false;
					}

					@Override
					public void setCacheEnabled(Boolean enabled) {

					}
				}).bind(binder);
	}
}
