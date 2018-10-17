package ish.oncourse.log4j.lookup;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter which saves requests for their subsequent logging
 */
public class XSiteKeySaverFilter implements Filter {

    private static final String X_SITE_KEY_HEADER = "x-site-key";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            String header = ((HttpServletRequest) request).getHeader(X_SITE_KEY_HEADER);
            XSiteKeyStorage.put(header);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
