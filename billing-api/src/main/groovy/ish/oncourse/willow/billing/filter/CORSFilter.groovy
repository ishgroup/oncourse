package ish.oncourse.willow.billing.filter

import ish.oncourse.api.request.RequestService
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class CORSFilter implements Filter {

    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        (servletResponse as Response).addHeader('Access-Control-Allow-Origin', '*')
        (servletResponse as Response).addHeader('Access-Control-Allow-Credentials', 'false')
        (servletResponse as Response).addHeader('Access-Control-Allow-Methods', 'PUT,GET,OPTIONS,POST,DELETE')
        (servletResponse as Response).addHeader('Access-Control-Allow-Headers', 'Content-Type,Authorization')
        filterChain.doFilter(servletRequest, servletResponse)
    }

    @Override
    void destroy() {

    }
}
