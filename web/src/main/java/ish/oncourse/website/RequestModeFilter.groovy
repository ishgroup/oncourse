package ish.oncourse.website

import ish.oncourse.cayenne.cache.ICacheEnabledService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.Cookie

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason
import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.EDITOR
import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.NONE
import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.PUBLISHER
import static ish.oncourse.services.site.WebSiteVersionService.EDITOR_PREFIX

/**
 * Three cache mode supported:
 *
 * <p>- cache enabled, this is default mode, disable reason: {@link ICacheEnabledService.CacheDisableReason#NONE NONE}</p>
 * <p>- cache disabled in EDITOR mode, disable reason: {@link ICacheEnabledService.CacheDisableReason#EDITOR EDITOR}. Switched on by setting request cookie "Cookie: editor=true".</p>
 * <p>- cache disabled in PUBLISHER mode, disable reason: {@link ICacheEnabledService.CacheDisableReason#PUBLISHER PUBLISER}. Switched on by setting request cookie "Cookie: publisher=true".</p>
 * EDITOR cache mode has higher priority when both cookies are set.
 *
 * @see ICacheEnabledService
 * {@see }
 */
class RequestModeFilter implements Filter {

    private static final Logger logger = LogManager.logger

    private ICacheEnabledService service

    RequestModeFilter(ICacheEnabledService service) {
        this.service = service
    }
    
    @Override
    void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Cookie[] cookies = (servletRequest as Request).cookies

        //regular site cache mode
        CacheDisableReason cacheDisableReason = NONE
        Boolean isCacheEnabled = true

        if (cookies.find { it.name == EDITOR_PREFIX }.any()) {
            // disable caching for editor mode
            isCacheEnabled = false
            cacheDisableReason = EDITOR
        }
        else if (cookies.find { it.name == PUBLISHER.toString().toLowerCase() }.any()) {
            // Run cache clean for site after editor has been published
            isCacheEnabled = false
            cacheDisableReason = PUBLISHER
        }

        logger.debug("Caching mode is {}", cacheDisableReason)

        service.setCacheEnabled(cacheDisableReason, isCacheEnabled)

        filterChain.doFilter(servletRequest, servletResponse)
    }

    @Override
    void destroy() {

    }
}