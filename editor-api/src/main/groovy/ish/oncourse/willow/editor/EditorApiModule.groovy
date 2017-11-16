package ish.oncourse.willow.editor

import com.google.inject.*
import io.bootique.ConfigModule
import io.bootique.cayenne.CayenneModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.milton.servlet.MiltonFilter
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cayenne.WillowApiCayenneModule
import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.log.LogAppInfo
import ish.oncourse.willow.editor.services.RequestFilter
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.services.access.AuthenticationService

import javax.servlet.FilterConfig
import javax.servlet.ServletException

class EditorApiModule extends ConfigModule {
    
    private static final String ROOT_URL_PATTERN = '/*'
    private static final String WEBDAV_URL_PATTERN = '/webdav/*'
    private static final String MILTON_CONFIGURATOR_PARAM = 'milton.configurator'
    private static final String MILTON_CONFIGURATOR_CLASS = 'ish.oncourse.willow.editor.webdav.Configurator'
    public static final String INJECTOR_PROPERTY = 'injector'

    private static final TypeLiteral<MappedServlet<ISHHealthCheckServlet>> ISH_HEALTH_CHECK_SERVLET =
            new TypeLiteral<MappedServlet<ISHHealthCheckServlet>>() {
            }
    
    private static final TypeLiteral<MappedFilter<MiltonFilter>> MILTON_FILTER =
            new TypeLiteral<MappedFilter<MiltonFilter>>() {
            }
    private static final TypeLiteral<MappedFilter<RequestFilter>> REQUEST_FILTER =
            new TypeLiteral<MappedFilter<RequestFilter>>() {
            }


    void configure(Binder binder) {
        CayenneModule.extend(binder).addModule(WillowApiCayenneModule)
        CayenneModule.extend(binder).addModule(JCacheModule)
        JettyModule.extend(binder)
                .addMappedFilter(REQUEST_FILTER)
                .addMappedServlet(ISH_HEALTH_CHECK_SERVLET)
                .addMappedFilter(MILTON_FILTER)
        
        binder.bind(RequestService)
        binder.bind(ICayenneService).to(CayenneService)
        binder.bind(AuthenticationService)
    }

    @Singleton
    @Provides
    MappedServlet<ISHHealthCheckServlet> createHealthCheckServlet(Injector injector) {
        LogAppInfo info = new LogAppInfo(injector.getInstance(DataSourceFactory.class).forName(LogAppInfo.DATA_SOURSE_NAME))
        info.log()
        new MappedServlet<>(new ISHHealthCheckServlet(), ISHHealthCheckServlet.urlPatterns, ISHHealthCheckServlet.SERVLET_NAME)
    }
    
    @Singleton
    @Provides
    MappedFilter<MiltonFilter> createMiltonFilter(Injector injector) {
        return new MappedFilter<MiltonFilter>(new MiltonFilter() {
            @Override 
            void init(FilterConfig config) throws ServletException {
                config.servletContext.setAttribute(INJECTOR_PROPERTY, injector)
                super.init(config)
            }
        }, Collections.singleton(WEBDAV_URL_PATTERN), MiltonFilter.simpleName, [(MILTON_CONFIGURATOR_PARAM) : MILTON_CONFIGURATOR_CLASS], 0)
    }
    
    @Singleton
    @Provides
    MappedFilter<RequestFilter> createRequestFilter() {
         new MappedFilter<RequestFilter>(new RequestFilter(),
                Collections.singleton(ROOT_URL_PATTERN), RequestFilter.simpleName, 0)
    }
}
