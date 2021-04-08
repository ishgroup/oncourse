/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.website

import com.google.inject.*
import groovy.transform.CompileStatic
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.bootique.tapestry.di.InjectorModuleDef
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.cayenne.cache.ICacheEnabledService
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.tapestry.WillowModuleDef
import ish.oncourse.tapestry.WillowTapestryFilter
import ish.oncourse.tapestry.WillowTapestryFilterBuilder
import ish.oncourse.util.log.LogAppInfo
import ish.oncourse.website.cache.CacheInvalidationService
import ish.oncourse.cayenne.cache.ICacheInvalidationService
import ish.oncourse.website.services.CacheEnabledService
import org.apache.cayenne.configuration.server.ServerRuntime

import javax.servlet.Filter

/**
 * User: akoiro
 * Date: 23/8/17
 */
@CompileStatic
class WebModule extends ConfigModule {
    private static final String URL_PATTERN = "/*"

    private static final String TAPESTRY_APP_NAME = "app"

    public static final String DATA_SOURCE_NAME = LogAppInfo.DATA_SOURSE_NAME;

    private static final TypeLiteral<MappedFilter<WillowTapestryFilter>> TAPESTRY_FILTER =
            new TypeLiteral<MappedFilter<WillowTapestryFilter>>() {
            }

    private static final TypeLiteral<MappedFilter<RequestModeFilter>> REQUEST_FILTER =
            new TypeLiteral<MappedFilter<RequestModeFilter>>() {
            }


    @Singleton
    @Provides
    MappedFilter<WillowTapestryFilter> createTapestryFilter(Injector injector) {
        new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(DATA_SOURCE_NAME)).log()
        Filter filter = new WillowTapestryFilterBuilder()
                .moduleDef(new InjectorModuleDef(injector))
                .moduleDef(new WillowModuleDef(injector.getInstance(DataSourceFactory).forName(LogAppInfo.DATA_SOURSE_NAME),
                injector.getInstance(ServerRuntime), injector.getInstance(ICacheEnabledService)))
                .appPackage("ish.oncourse.website").build()
        return new MappedFilter<>(filter,
                Collections.singleton(URL_PATTERN),
                TAPESTRY_APP_NAME, 1
        )
    }

    @Singleton
    @Provides
    MappedFilter<RequestModeFilter> createRequestFilter(ICacheEnabledService service) {
        new MappedFilter<RequestModeFilter>(new RequestModeFilter(service),
                Collections.singleton(URL_PATTERN), RequestModeFilter.simpleName, 0)
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
            binder.bind(ICacheInvalidationService).to(CacheInvalidationService)
        }
    }


}