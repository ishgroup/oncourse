package ish.oncourse.admin.services;

import ish.oncourse.cayenne.cache.ICacheEnabledService;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.apache.tapestry5.ioc.def.DecoratorDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.def.ServiceDef;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class WillowModuleDef implements ModuleDef {

    private static final Logger logger = LogManager.getLogger();

    private DataSourceServiceDef dataSourceServiceDef;
    private ServerRuntimeServiceDef serverRuntimeServiceDef;
    private CacheEnabledServiceDef cacheEnabledServiceDef;

    public WillowModuleDef(DataSource dataSource, ServerRuntime serverRuntime) {
        this.dataSourceServiceDef = new DataSourceServiceDef(dataSource);
        this.serverRuntimeServiceDef = new ServerRuntimeServiceDef(serverRuntime);
        this.cacheEnabledServiceDef = new CacheEnabledServiceDef(new ICacheEnabledService() {
            public boolean isCacheEnabled() {
                return false;
            }

            @Override
            public void setCacheEnabled(Boolean enabled) {}

            @Override
            public void setCacheEnabled(CacheDisableReason reason, Boolean enabled) {}
        });
    }

    public WillowModuleDef(DataSource dataSource, ServerRuntime serverRuntime, ICacheEnabledService enabledService) {
        this.dataSourceServiceDef = new DataSourceServiceDef(dataSource);
        this.serverRuntimeServiceDef = new ServerRuntimeServiceDef(serverRuntime);
        this.cacheEnabledServiceDef = new CacheEnabledServiceDef(enabledService);
    }

    @Override
    public Set<String> getServiceIds() {
        LinkedHashSet<String> r = new LinkedHashSet<>();
        r.add(DataSourceServiceDef.ID);
        r.add(ServerRuntimeServiceDef.ID);
        r.add(CacheEnabledServiceDef.ID);

        return r;
    }

    @Override
    public ServiceDef getServiceDef(String serviceId) {
        switch (serviceId) {
            case DataSourceServiceDef.ID:
                return this.dataSourceServiceDef;
            case ServerRuntimeServiceDef.ID:
                return this.serverRuntimeServiceDef;
            case CacheEnabledServiceDef.ID:
                return this.cacheEnabledServiceDef;
            default:
                throw new IllegalArgumentException(serviceId);
        }
    }

    @Override
    public Set<DecoratorDef> getDecoratorDefs() {
        return Collections.emptySet();
    }

    @Override
    public Set<ContributionDef> getContributionDefs() {
        return Collections.emptySet();
    }

    @Override
    public Class<?> getBuilderClass() {
        return null;
    }

    @Override
    public String getLoggerName() {
        return WillowModuleDef.class.getName();
    }
}
