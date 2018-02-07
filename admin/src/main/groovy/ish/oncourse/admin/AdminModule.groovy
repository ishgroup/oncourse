package ish.oncourse.admin

import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import groovy.transform.CompileStatic
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.bootique.tapestry.di.InjectorModuleDef
import ish.oncourse.admin.services.CacheEnabledService
import ish.oncourse.admin.services.access.AuthenticationFilter
import ish.oncourse.api.cxf.CXFModule
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.ICacheEnabledService
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.tapestry.WillowModuleDef
import ish.oncourse.tapestry.WillowTapestryFilter
import ish.oncourse.tapestry.WillowTapestryFilterBuilder
import ish.oncourse.util.log.LogAppInfo
import org.apache.cayenne.configuration.server.ServerRuntime
import ish.oncourse.admin.services.RequestFilter
import org.apache.cayenne.configuration.server.ServerRuntimeBuilder;

import javax.servlet.Filter

@CompileStatic
class AdminModule extends ConfigModule {
    private static final String URL_PATTERN = "/*"

    private static final String TAPESTRY_APP_NAME = "app"

    public static final String DATA_SOURCE_NAME = LogAppInfo.DATA_SOURSE_NAME;

    private static final TypeLiteral<MappedFilter<WillowTapestryFilter>> TAPESTRY_FILTER =
            new TypeLiteral<MappedFilter<WillowTapestryFilter>>() {
            }

    private static final TypeLiteral<MappedFilter<RequestFilter>> REQUEST_FILTER =
            new TypeLiteral<MappedFilter<RequestFilter>>() {
            }

    private static final TypeLiteral<MappedFilter<AuthenticationFilter>> AUTH_FILTER =
            new TypeLiteral<MappedFilter<AuthenticationFilter>>() {
            }


    @Singleton
    @Provides
    MappedFilter<WillowTapestryFilter> createTapestryFilter(Injector injector) {
        new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(DATA_SOURCE_NAME)).log()
        Filter filter = new WillowTapestryFilterBuilder()
                .moduleDef(new InjectorModuleDef(injector))
                .moduleDef(new WillowModuleDef(injector.getInstance(DataSourceFactory).forName(LogAppInfo.DATA_SOURSE_NAME),
                injector.getInstance(ServerRuntime), injector.getInstance(ICacheEnabledService)))
                .appPackage("ish.oncourse.admin").build()
        return new MappedFilter<>(filter,
                Collections.singleton(URL_PATTERN),
                TAPESTRY_APP_NAME, 1
        )
    }

    @Singleton
    @Provides
    MappedFilter<RequestFilter> createRequestFilter(ICacheEnabledService service) {
        new MappedFilter<RequestFilter>(new RequestFilter(service),
                Collections.singleton(URL_PATTERN), RequestFilter.simpleName, 0)
    }

    @Provides
    ICacheEnabledService createCacheEnabledModule() {
        new CacheEnabledService()
    }

    @Singleton
    @Provides
    CacheEnabledModule createCacheEnabledModule(ICacheEnabledService service) {
        new CacheEnabledModule(service)
    }

    @Override
    void configure(Binder binder) {
        CayenneModule.extend(binder)
                .addModule(new WillowCayenneModuleBuilder().build())
                .addModule(new JCacheModule())
                .addModule(CacheEnabledModule)
        JettyModule.extend(binder)
                .addMappedFilter(REQUEST_FILTER)
                .addMappedFilter(AUTH_FILTER)
                .addMappedFilter(TAPESTRY_FILTER)
                .addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME))
                .addStaticServlet("resources", URL_PATTERN)
    }


    static class CacheEnabledModule implements org.apache.cayenne.di.Module {

        private ICacheEnabledService service

        CacheEnabledModule(ICacheEnabledService service) {
            this.service = service
        }

        @Override
        void configure(org.apache.cayenne.di.Binder binder) {
            binder.bind(ICacheEnabledService).toInstance(service)
        }
    }
}
