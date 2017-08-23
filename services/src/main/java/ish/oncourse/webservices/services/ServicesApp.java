/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.services;

import com.google.inject.Binder;
import io.bootique.Bootique;
import io.bootique.ConfigModule;
import io.bootique.cayenne.CayenneModuleProvider;
import io.bootique.jdbc.JdbcModuleProvider;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.JettyModuleProvider;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.spring.TapestrySpringFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * User: akoiro
 * Date: 20/8/17
 */
public class ServicesApp {
	public static void main(String[] args) {
		Bootique bootique = Bootique.app(args).args("--server", "--config=classpath:application.yml");
		bootique.module(new JdbcModuleProvider());
		bootique.module(new CayenneModuleProvider());
		bootique.module(new JettyModuleProvider());
		bootique.module(ServicesModule.class);
		bootique.exec();
	}


	public static class ServicesModule extends ConfigModule {
		@Override
		public void configure(Binder binder) {
			JettyModule.extend(binder)
					.addFilter(new ServicesTapestryFilter(), "app", 0, "/services/*")
					.addServlet(new CXFServlet(), "WebServicesServlet", "/services/*");
		}

		@Override
		protected String defaultConfigPrefix() {
			return "ServicesModule";
		}
	}

	static class ServicesTapestryFilter implements Filter {
		private TapestrySpringFilter filter = new TapestrySpringFilter();

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			filterConfig.getServletContext().setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "ish.oncourse.webservices");
			filterConfig.getServletContext().setInitParameter("contextConfigLocation", "classpath:application-context.xml");
			filterConfig.getServletContext().setInitParameter(SymbolConstants.PRODUCTION_MODE, Boolean.FALSE.toString());
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


