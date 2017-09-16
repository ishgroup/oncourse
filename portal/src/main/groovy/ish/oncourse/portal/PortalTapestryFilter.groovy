/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal

import com.google.inject.Injector
import io.bootique.tapestry.di.InjectorModuleDef
import org.apache.tapestry5.internal.spring.SpringModuleDef
import org.apache.tapestry5.ioc.def.ModuleDef
import org.apache.tapestry5.spring.TapestrySpringFilter

import javax.servlet.*

import static org.apache.tapestry5.SymbolConstants.PRODUCTION_MODE
import static org.apache.tapestry5.internal.InternalConstants.TAPESTRY_APP_PACKAGE_PARAM
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM

/**
 * User: akoiro
 * Date: 26/8/17
 */
class PortalTapestryFilter implements Filter {

    public static final String SERVICES_APP_PACKAGE = "ish.oncourse.portal"

    private Injector injector

    PortalTapestryFilter(Injector injector) {
        this.injector = injector
    }

    private TapestrySpringFilter filter = new TapestrySpringFilter() {
        @Override
        protected ModuleDef[] provideExtraModuleDefs(ServletContext context) {
            return [
                    new SpringModuleDef(context),
                    new InjectorModuleDef(injector)
            ]
        }
    };

    @Override
    void init(FilterConfig filterConfig) throws ServletException {
        filterConfig.getServletContext().setInitParameter(TAPESTRY_APP_PACKAGE_PARAM, SERVICES_APP_PACKAGE)
        filterConfig.getServletContext().setInitParameter(CONFIG_LOCATION_PARAM, "classpath:application-context.xml")
        filterConfig.getServletContext().setInitParameter(PRODUCTION_MODE, Boolean.FALSE.toString())
        filter.init(filterConfig)
    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.getServletContext().getContextPath()
        filter.doFilter(servletRequest, servletResponse, filterChain)
    }


    @Override
    void destroy() {
        filter.destroy()
    }
}
