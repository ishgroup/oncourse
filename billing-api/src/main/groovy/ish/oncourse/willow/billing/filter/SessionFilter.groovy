package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import ish.oncourse.api.access.AuthFilter
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.api.request.RequestService

import javax.ws.rs.ClientErrorException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response

@AuthFilter
class SessionFilter  implements ContainerRequestFilter {

    RequestService requestService
    
    private static final String RE_CAPTCHA_TOKEN = 'x-g-recaptcha'
    private static final String RE_CAPTCHA_HOST = 'https://www.google.com'
    private static final String RE_CAPTCHA_PATH = '/recaptcha/api/siteverify'
    private static final String RE_CAPTCHA_SECRET = '6LenRkYaAAAAAKhZKrYLzuEcy5W6Qspj_NgZJ0yy'
    
    @Inject
    AuthenticationFilter(RequestService requestService) {
        this.requestService = requestService
    }
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        SessionCookie sessionCookie = SessionCookie.valueOf(requestService.request)
        if (sessionCookie.exist) {
            verifySession(sessionCookie.sessionNode)
        } else  {
            String reCaptcha = requestService.request.getHeader(RE_CAPTCHA_TOKEN)
            if  (reCaptcha) {
                HTTPBuilder builder =  new HTTPBuilder()
                builder.post(
                        [uri: RE_CAPTCHA_HOST,
                         path: RE_CAPTCHA_PATH,
                         contentType: ContentType.JSON,
                         requestContentType: ContentType.JSON,
                         body: [
                                 secret: RE_CAPTCHA_SECRET,
                                 response: reCaptcha
                                ]
                        ])
                builder.handler['failure'] = { response, body ->
                    throw new ClientErrorException('Request rejected, reCaptcha check failed', Response.Status.NOT_ACCEPTABLE)
                }
                builder.handler['success'] = { response, body ->
                    if (body.success) {
                        createSession()
                    } else {
                        throw new ClientErrorException('Request rejected, reCaptcha check failed', Response.Status.NOT_ACCEPTABLE)
                    }
                }
            } else {
                throw new ClientErrorException('Request rejected, no reCaptcha token provided', Response.Status.NOT_ACCEPTABLE)
            }
            
        }

    }

    void verifySession(String sessionNode) {
        
    }

    void createSession() {
        
    }
}
