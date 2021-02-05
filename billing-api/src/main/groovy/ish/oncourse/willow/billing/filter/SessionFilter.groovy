package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import ish.oncourse.api.access.AuthFilter
import ish.oncourse.api.access.SessionCookie
import ish.oncourse.api.request.RequestService
import ish.util.SecurityUtil

import javax.ws.rs.ClientErrorException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response

@AuthFilter
@CompileDynamic
class SessionFilter  implements ContainerRequestFilter {

    RequestService requestService
    ZKSessionManager sessionManager
    private static final String RE_CAPTCHA_TOKEN = 'x-g-recaptcha'
    private static final String RE_CAPTCHA_HOST = 'https://www.google.com'
    private static final String RE_CAPTCHA_PATH = '/recaptcha/api/siteverify'
    private static final String RE_CAPTCHA_SECRET = '6LenRkYaAAAAAKhZKrYLzuEcy5W6Qspj_NgZJ0yy'
    private static final String GUEST_ID = "Guest-1"
    private static final Integer SESSION_MAX_AGE = 3600
    @Inject
    AuthenticationFilter(RequestService requestService, ZKSessionManager sessionManager) {
        this.requestService = requestService
        this.sessionManager = sessionManager
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
        if (!sessionManager.sessionExist(sessionNode)) {
            requestService.setSessionToken(null,0)
            requestService.response.sendError(Response.Status.UNAUTHORIZED.statusCode,  "User session invalid")
        }
    }

    void createSession() {
        String sessionId = SecurityUtil.generateRandomPassword(20)
        sessionManager.persistSession(GUEST_ID, sessionId)
        String sessionToken = "$GUEST_ID&$sessionId".toString()
        requestService.setSessionToken(sessionToken, SESSION_MAX_AGE)
    }
}
