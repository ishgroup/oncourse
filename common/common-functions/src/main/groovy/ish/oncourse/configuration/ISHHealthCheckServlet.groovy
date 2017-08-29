package ish.oncourse.configuration

import groovy.servlet.GroovyServlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ISHHealthCheckServlet extends GroovyServlet {

    
    public static final String ISH_HEALTH_CHECK_PATTERN = "/ISHHealthCheck"
    public static final Set<String> urlPatterns =  Collections.singleton(ISH_HEALTH_CHECK_PATTERN)
    public static final String SERVLET_NAME = "ISHHealthCheck"
    
    @Override
    void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.writer.print(ApplicationUtils.getAppVersion())
    }
       
}
