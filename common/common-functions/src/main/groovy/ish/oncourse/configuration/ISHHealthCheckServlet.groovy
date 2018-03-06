package ish.oncourse.configuration

import groovy.servlet.GroovyServlet
import org.apache.commons.lang3.StringUtils

import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ISHHealthCheckServlet extends GroovyServlet {

    
    public static final String ISH_HEALTH_CHECK_PATTERN = "/ISHHealthCheck"
    public static final Set<String> urlPatterns =  Collections.singleton(ISH_HEALTH_CHECK_PATTERN)
    public static final String SERVLET_NAME = "ISHHealthCheck"

    private String appVersion = StringUtils.EMPTY

    @Override
    void init(ServletConfig config) throws ServletException {
        appVersion = ApplicationUtils.getAppVersion()
        super.init(config)
    }

    @Override
    void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.writer.print(appVersion)
    }
       
}
