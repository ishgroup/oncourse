/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import com.google.inject.Injector;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.tapestry.di.InjectorModuleDef;
import ish.oncourse.tapestry.WillowModuleDef;
import ish.oncourse.util.log.LogAppInfo;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.internal.spring.SpringModuleDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.spring.TapestrySpringFilter;

import javax.servlet.*;
import javax.sql.DataSource;
import java.io.IOException;

import static org.apache.tapestry5.SymbolConstants.PRODUCTION_MODE;
import static org.apache.tapestry5.internal.InternalConstants.TAPESTRY_APP_PACKAGE_PARAM;
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;

/**
 * User: akoiro
 * Date: 26/8/17
 */
public class ServicesTapestryFilter implements Filter {

	public static final String SERVICES_APP_PACKAGE = "ish.oncourse.webservices";

	private Injector injector;

	ServicesTapestryFilter(Injector injector) {
		this.injector = injector;
	}

	private TapestrySpringFilter filter = new TapestrySpringFilter() {
		@Override
		protected ModuleDef[] provideExtraModuleDefs(ServletContext context) {
			return new ModuleDef[]{
					new SpringModuleDef(context),
					new InjectorModuleDef(injector),
					new WillowModuleDef(injector.getInstance(DataSourceFactory.class).forName(LogAppInfo.DATA_SOURSE_NAME),
							injector.getInstance(ServerRuntime.class))
			};
		}
	};

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		filterConfig.getServletContext().setInitParameter(TAPESTRY_APP_PACKAGE_PARAM, SERVICES_APP_PACKAGE);
		filterConfig.getServletContext().setInitParameter(CONFIG_LOCATION_PARAM, "classpath:application-context.xml");
		filterConfig.getServletContext().setInitParameter(PRODUCTION_MODE, Boolean.TRUE.toString());
		filter.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		filter.doFilter(servletRequest, servletResponse, filterChain);
	}


	@Override
	public void destroy() {
		filter.destroy();
	}
}
