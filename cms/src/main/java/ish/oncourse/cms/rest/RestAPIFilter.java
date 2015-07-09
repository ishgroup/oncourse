package ish.oncourse.cms.rest;

import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.access.AuthenticationStatus;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class RestAPIFilter implements Filter {
    private static final Logger logger = LogManager.getLogger();
    private static final long PUBLISH_INTERVAL = 2 * 60 * 1000;

    private IAuthenticationService authenticationService;
    private IWebSiteService webSiteService;
    private IWebSiteVersionService webSiteVersionService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        Registry registry = (Registry) servletContext.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
        authenticationService = registry.getService(IAuthenticationService.class);
        webSiteService = registry.getService(IWebSiteService.class);
        webSiteVersionService = registry.getService(IWebSiteVersionService.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String authHeader = request.getHeader("Authorization");

        if (!((HttpServletRequest) servletRequest).getServletPath().equals("/api/publish")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().append("Page not found");
            return;
        }

        if (authHeader == null) {
            unauthorized(response, "Unauthorized");
            return;
        }

        if (authorized(authHeader, response, filterChain)) {
            publish(response);
        }
    }

    private void publish(HttpServletResponse response) throws IOException {
        WebSite webSite = webSiteService.getCurrentWebSite();
        WebSiteVersion webSiteVersion = webSiteVersionService.getDeployedVersion(webSite);
        if (System.currentTimeMillis() - webSiteVersion.getDeployedOn().getTime() > PUBLISH_INTERVAL) {
            webSiteVersionService.publish();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().append("Please wait up to 10 minutes for the changes to be visible on the live site.");
        } else {
            response.setStatus(429);//too many requests
            response.getWriter().append("Please wait 2 minutes between each publish request.");
        }
    }

    @Override
    public void destroy() {
    }


    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"oncourse\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    private boolean authorized(String authHeader, HttpServletResponse response, FilterChain filterChain) throws IOException {
        StringTokenizer st = new StringTokenizer(authHeader);
        if (st.hasMoreTokens()) {
            String basic = st.nextToken();

            if (basic.equalsIgnoreCase("Basic")) {
                try {
                    String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                    int p = credentials.indexOf(":");
                    if (p != -1) {
                        String _username = credentials.substring(0, p).trim();
                        String _password = credentials.substring(p + 1).trim();
                        AuthenticationStatus authenticationStatus = authenticationService.authenticate(_username, _password);
                        switch (authenticationStatus) {
                            case SUCCESS:
                                return true;
                            case INVALID_CREDENTIALS:
                            case NO_MATCHING_USER:
                            case MORE_THAN_ONE_USER:
                                logger.error("Invalid user or password");
                                unauthorized(response, "Invalid user or password");
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                    } else {
                        unauthorized(response, "Invalid authentication token");
                    }
                } catch (UnsupportedEncodingException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ExceptionUtils.getStackTrace(e));
                }
            }
        }
        return false;
    }
}
