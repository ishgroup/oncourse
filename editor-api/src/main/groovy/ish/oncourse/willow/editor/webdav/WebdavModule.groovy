package ish.oncourse.willow.editor.webdav

import com.google.inject.*
import io.bootique.ConfigModule
import io.bootique.jetty.JettyModule
import io.bootique.jetty.MappedFilter
import io.milton.servlet.MiltonFilter

import javax.servlet.FilterConfig
import javax.servlet.ServletException 

class WebdavModule extends ConfigModule {
    
    private static final String MILTON_CONFIGURATOR_PARAM = 'milton.configurator'
    private static final String MILTON_CONFIGURATOR_CLASS = 'ish.oncourse.willow.editor.webdav.Configurator'
    public static final String INJECTOR_PROPERTY = 'injector'

    private static final String WEBDAV_URL_PATTERN = '/webdav/*'


    private static final TypeLiteral<MappedFilter<MiltonFilter>> MILTON_FILTER =
            new TypeLiteral<MappedFilter<MiltonFilter>>() {
            }

    @Override
    void configure(Binder binder) {
        JettyModule.extend(binder).addMappedFilter(MILTON_FILTER)
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
}
