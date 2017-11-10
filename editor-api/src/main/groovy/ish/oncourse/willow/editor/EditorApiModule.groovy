package ish.oncourse.willow.editor

import com.google.inject.*
import io.bootique.ConfigModule
import io.bootique.jdbc.DataSourceFactory
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.bootique.jetty.MappedServlet
import io.milton.servlet.MiltonFilter
import ish.oncourse.configuration.ISHHealthCheckServlet
import ish.oncourse.util.log.LogAppInfo
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature

import javax.servlet.FilterConfig

class EditorApiModule extends ConfigModule {
    private static final String WEBDAV_URL_PATTERN = '/webdav/*'
    private static final String MILTON_CONFIGURATOR_PARAM = 'milton.configurator'
    private static final String MILTON_CONFIGURATOR_CLASS = 'ish.oncourse.willow.editor.webdav.Configurator'


    private static final TypeLiteral<MappedServlet<ISHHealthCheckServlet>> ISH_HEALTH_CHECK_SERVLET =
            new TypeLiteral<MappedServlet<ISHHealthCheckServlet>>() {
            }
    
    private static final TypeLiteral<MappedFilter<MiltonFilter>> MILTON_FILTER =
            new TypeLiteral<MappedFilter<MiltonFilter>>() {
            }


    void configure(Binder binder) {
        JettyModule.extend(binder)
                .addMappedServlet(ISH_HEALTH_CHECK_SERVLET)
                .addMappedFilter(MILTON_FILTER)
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
    MappedFilter<MiltonFilter> createMiltonFilter() {
        return new MappedFilter<MiltonFilter>(new MiltonFilter(),
                Collections.singleton(WEBDAV_URL_PATTERN), 'MiltonFilter', [(MILTON_CONFIGURATOR_PARAM) : MILTON_CONFIGURATOR_CLASS], 0)
    }
}
