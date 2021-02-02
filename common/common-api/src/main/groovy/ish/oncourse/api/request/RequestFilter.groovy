package ish.oncourse.api.request

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse


class RequestFilter implements Filter {

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RequestService.ThreadLocalRequest.set(servletRequest as Request)
        RequestService.ThreadLocalResponse.set(servletResponse as Response)
        filterChain.doFilter(servletRequest, servletResponse)
    }

    @Override
    void destroy() {

    }
}
