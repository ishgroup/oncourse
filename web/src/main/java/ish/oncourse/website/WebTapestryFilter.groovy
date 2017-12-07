/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.website

import com.google.inject.Injector
import io.bootique.jdbc.DataSourceFactory
import io.bootique.tapestry.di.InjectorModuleDef
import ish.oncourse.cayenne.cache.ICacheEnabledService
import ish.oncourse.tapestry.WillowModuleDef
import ish.oncourse.util.log.LogAppInfo
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.tapestry5.TapestryFilter
import org.apache.tapestry5.ioc.def.ModuleDef

import javax.servlet.*

import static org.apache.tapestry5.SymbolConstants.PRODUCTION_MODE
import static org.apache.tapestry5.internal.InternalConstants.TAPESTRY_APP_PACKAGE_PARAM

/**
 * User: akoiro
 * Date: 26/8/17
 */
class WebTapestryFilter implements Filter {

    public static final String SERVICES_APP_PACKAGE = "ish.oncourse.website"

    private Injector injector

    WebTapestryFilter(Injector injector) {
        this.injector = injector
    }

    private TapestryFilter filter = new TapestryFilter() {
        @Override
        protected ModuleDef[] provideExtraModuleDefs(ServletContext context) {
            return [
                    new InjectorModuleDef(injector),
                    new WillowModuleDef(injector.getInstance(DataSourceFactory).forName(LogAppInfo.DATA_SOURSE_NAME),
                            injector.getInstance(ServerRuntime), injector.getInstance(ICacheEnabledService)
                    )
            ]
        }
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {
        filterConfig.getServletContext().setInitParameter(TAPESTRY_APP_PACKAGE_PARAM, SERVICES_APP_PACKAGE)
        filterConfig.getServletContext().setInitParameter(PRODUCTION_MODE, Boolean.FALSE.toString())
        filter.init(filterConfig)
    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filter.doFilter(servletRequest, servletResponse, filterChain)
    }


    @Override
    void destroy() {
        filter.destroy()
    }
}
