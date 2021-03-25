package ish.oncourse.test.tapestry;
import ish.oncourse.cayenne.cache.ICacheEnabledService;
import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.BindWebSiteServices;
import ish.oncourse.services.ModuleBinder;
import ish.oncourse.services.site.WebSiteServiceOverride;
import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.test.context.DataContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.ServiceBinder;

import javax.sql.DataSource;

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.EDITOR;

public class TestModule {
    public static ThreadLocal<DataSource> dataSource = new ThreadLocal<>();
    public static ThreadLocal<ServerRuntime> serverRuntime = new ThreadLocal<>();

    public static void bind(ServiceBinder binder) {
        new ModuleBinder()
                .bindWebSiteServices(new BindWebSiteServices().webSiteService(res -> new WebSiteServiceOverride() {
                    @Override
                    public College getCurrentCollege() {
                        ICollegeService collegeService = res.getService(ICollegeService.class);
                        return collegeService.findBySecurityCode(DataContext.DEFAULT_SERVICES_SECURITY_CODE);
                    }

                    @Override
                    public WebSite getCurrentWebSite() {
                        return getCurrentCollege().getWebSites().get(0);
                    }
                }))
                .dataSource(resources -> TestModule.dataSource.get())
                .serverRuntime(resources -> TestModule.serverRuntime.get())
                .cacheEnabledService(res -> new ICacheEnabledService() {

                    @Override
                    public boolean isCacheEnabled() {
                        return false;
                    }

                    @Override
                    public CacheDisableReason getDisableReason() {
                        return EDITOR;
                    }

                    @Override
                    public void setCacheEnabled(Boolean enabled) {}

                    @Override
                    public void setCacheEnabled(CacheDisableReason reason, Boolean enabled) {}

                }).bind(binder);
    }
}