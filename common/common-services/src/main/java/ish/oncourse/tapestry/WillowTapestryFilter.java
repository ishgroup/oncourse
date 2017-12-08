/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.tapestry;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.def.ModuleDef;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.tapestry5.SymbolConstants.PRODUCTION_MODE;

/**
 * User: akoiro
 * Date: 8/12/17
 */
public class WillowTapestryFilter implements Filter {
	private TapestryFilter tapestryFilter;
	private Map<String, String> parameters;

	WillowTapestryFilter(final List<ModuleDef> moduleDefs,
						 final List<Class<? extends ModuleDef>> moduleDefClasses,
						 Map<String, String> parameters) {
		this.tapestryFilter = new TapestryFilter() {
			@Override
			protected ModuleDef[] provideExtraModuleDefs(ServletContext context) {

				List<ModuleDef> result = new ArrayList<>();
				moduleDefClasses.forEach((Class<? extends ModuleDef> c) -> {
					try {
						result.add(c.getConstructor(ServletContext.class).newInstance(context));
					} catch (Exception e) {
						throw new IllegalArgumentException(e);
					}
				});
				result.addAll(moduleDefs);
				return result.toArray(new ModuleDef[]{});
			}
		};
		this.parameters = parameters;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		filterConfig.getServletContext().setInitParameter(PRODUCTION_MODE, Boolean.TRUE.toString());
		parameters.keySet().forEach((key) -> filterConfig.getServletContext().setInitParameter(key, parameters.get(key)));
		tapestryFilter.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		tapestryFilter.doFilter(servletRequest, servletResponse, filterChain);
	}

	@Override
	public void destroy() {
		tapestryFilter.destroy();
	}
}