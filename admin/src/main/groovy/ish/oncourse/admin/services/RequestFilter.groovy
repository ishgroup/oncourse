package ish.oncourse.admin.services

import ish.oncourse.cayenne.cache.ICacheEnabledService
import ish.oncourse.services.site.WebSiteVersionService
import org.eclipse.jetty.server.Request

import javax.servlet.*

class RequestFilter implements Filter {
    private ICacheEnabledService service

    RequestFilter(ICacheEnabledService service) {
        this.service = service
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Boolean isEditor = (servletRequest as Request).cookies.find { it.name == WebSiteVersionService.EDITOR_PREFIX }.any()
        service.setCacheEnabled(!isEditor)
        filterChain.doFilter(servletRequest, servletResponse)
    }

    @Override
    void destroy() {

    }
}
