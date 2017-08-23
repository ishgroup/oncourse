/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import com.google.inject.*;
import io.bootique.ConfigModule;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.jetty.MappedServlet;
import io.bootique.tapestry.di.InjectorModuleDef;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.spring.SpringModuleDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.spring.TapestrySpringFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collections;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class ServicesModule extends ConfigModule {
	private static final String URL_PATTERN = "/*";
	private static final String TAPESTRY_APP_NAME = "app";

	private static final TypeLiteral<MappedFilter<ServicesTapestryFilter>> TAPESTRY_FILTER =
			new TypeLiteral<MappedFilter<ServicesTapestryFilter>>() {
			};

	private static final TypeLiteral<MappedServlet<CXFServlet>> CXF_SERVLET =
			new TypeLiteral<MappedServlet<CXFServlet>>() {
			};

	@Singleton
	@Provides
	MappedFilter<ServicesTapestryFilter> createTapestryFilter(Injector injector) {
		ServicesTapestryFilter filter = new ServicesTapestryFilter(injector);
		return new MappedFilter<>(filter, Collections.singleton(URL_PATTERN), TAPESTRY_APP_NAME, 0);
	}

	@Singleton
	@Provides
	MappedServlet<CXFServlet> createCXFServlet() {
		return new MappedServlet<>(new CXFServlet(), Collections.singleton(URL_PATTERN), "cxf");
	}


	@Override
	public void configure(Binder binder) {
		JettyModule.extend(binder)
				.addMappedFilter(TAPESTRY_FILTER)
				.addMappedServlet(CXF_SERVLET);
	}

	private static class ServicesTapestryFilter implements Filter {
		private Injector injector;

		private ServicesTapestryFilter(Injector injector) {
			this.injector = injector;
		}

		private TapestrySpringFilter filter = new TapestrySpringFilter() {
			@Override
			protected ModuleDef[] provideExtraModuleDefs(ServletContext context) {
				return new ModuleDef[]{
						new SpringModuleDef(context),
						new InjectorModuleDef(injector)
				};
			}
		};

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			filterConfig.getServletContext().setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "ish.oncourse.webservices");
			filterConfig.getServletContext().setInitParameter("contextConfigLocation", "classpath:application-context.xml");
			filterConfig.getServletContext().setInitParameter(SymbolConstants.PRODUCTION_MODE, Boolean.TRUE.toString());
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

}