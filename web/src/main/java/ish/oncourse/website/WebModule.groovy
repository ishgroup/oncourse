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
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.services.persistence.ISHObjectContextFactory
import ish.oncourse.util.log.LogAppInfo
import ish.oncourse.website.services.CacheEnabledService
import ish.oncourse.cayenne.cache.ICacheEnabledService
import org.apache.cayenne.configuration.Constants
import org.apache.cayenne.configuration.ObjectContextFactory

/**
 * User: akoiro
 * Date: 23/8/17
 */
@CompileStatic
class WebModule extends ConfigModule {
    private static final String URL_PATTERN = "/*"

    private static final String TAPESTRY_APP_NAME = "app"

    public static final String DATA_SOURCE_NAME = LogAppInfo.DATA_SOURSE_NAME;

    private static final TypeLiteral<MappedFilter<WebTapestryFilter>> TAPESTRY_FILTER =
            new TypeLiteral<MappedFilter<WebTapestryFilter>>() {
            }
    
    private static final TypeLiteral<MappedFilter<RequestFilter>> REQUEST_FILTER =
            new TypeLiteral<MappedFilter<RequestFilter>>() {
            }


    @Singleton
    @Provides
    MappedFilter<WebTapestryFilter> createTapestryFilter(Injector injector) {
        LogAppInfo info = new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(DATA_SOURCE_NAME))
        info.log()
        WebTapestryFilter filter = new WebTapestryFilter(injector)
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
                .addModule(new WebCayenneModule())
                .addModule(new JCacheModule())
                .addModule(CacheEnabledModule)
        JettyModule.extend(binder)
                .addMappedFilter(REQUEST_FILTER)
                .addMappedFilter(TAPESTRY_FILTER)
                .addMappedServlet(new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME))
                .addStaticServlet("resources", URL_PATTERN)

    }


    static class WebCayenneModule implements org.apache.cayenne.di.Module {
        @Override
        void configure(org.apache.cayenne.di.Binder binder) {
            binder.bindMap(Object.class, Constants.PROPERTIES_MAP).put(Constants.CI_PROPERTY, "true")
            binder.bind(ObjectContextFactory.class).toInstance(new ISHObjectContextFactory(false))
        }
    }

    static class CacheEnabledModule  implements org.apache.cayenne.di.Module {
        
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