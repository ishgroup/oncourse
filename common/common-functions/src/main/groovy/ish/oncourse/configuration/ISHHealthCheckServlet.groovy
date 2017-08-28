package ish.oncourse.configuration

import groovy.servlet.GroovyServlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ISHHealthCheckServlet extends GroovyServlet {

    static final String VERSION_DEVELOPMENT = 'DEVELOPMENT'
    static final String VERSION_FILE_NAME = 'VERSION'
    public static final String ISH_HEALTH_CHECK_PATTERN = "/ISHHealthCheck"
    public static final Set<String> urlPatterns =  Collections.singleton(ISH_HEALTH_CHECK_PATTERN)
    public static final String SERVLET_NAME = "ISHHealthCheck"
    
    @Override
    void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userDir = System.getProperties().get('user.dir') as String
        File versionFile = new File(userDir +'/'+ VERSION_FILE_NAME)
        String version
        
        if (versionFile.exists()) {
            version = versionFile.newReader().readLine()
        } else {
            version = VERSION_DEVELOPMENT
        }
        response.writer.print(version)
    }
       
}
