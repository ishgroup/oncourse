package ish.oncourse.willow.billing.filter

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import ish.oncourse.api.access.GuestFilter
import ish.oncourse.api.request.RequestService
import ish.util.SecurityUtil
import org.apache.zookeeper.CreateMode

@GuestFilter
@CompileDynamic
class GuestSessionFilter extends BillingSessionFilter {
    
    private static final String RE_CAPTCHA_TOKEN = 'xGRecaptcha'
    private static final String RE_CAPTCHA_HOST = 'https://www.google.com'
    private static final String RE_CAPTCHA_PATH = '/recaptcha/api/siteverify'
    private static final String RE_CAPTCHA_SECRET = '6LenRkYaAAAAAKhZKrYLzuEcy5W6Qspj_NgZJ0yy'
    private static final String GUEST_ID = "Guest-1"
    
    @Inject
    GuestSessionFilter(RequestService requestService, ZKSessionManager sessionManager) {
        super(requestService, sessionManager)
    }

    @Override
    protected String authentificate(String token) {
        HTTPBuilder builder =  new HTTPBuilder()
        builder.handler['failure'] = { response, body ->
            return 'Request rejected, reCaptcha check failed'
        }
        builder.handler['success'] = { response, body ->
            if (body.success) {
                return null
            } else {
                return 'Request rejected, reCaptcha check failed'
            }
        }
        builder.post(
                [uri: RE_CAPTCHA_HOST,
                 path: RE_CAPTCHA_PATH,
                 contentType: ContentType.JSON,
                 requestContentType: ContentType.JSON,
                 query: [
                         secret: RE_CAPTCHA_SECRET,
                         response: token
                 ]
                ])
    }

    @Override
    protected String getAuthToken() {
        return requestService.request.getHeader(RE_CAPTCHA_TOKEN)
    }
    
    @Override
    void createSession() {
        String sessionId = SecurityUtil.generateRandomPassword(20)
        sessionManager.persistSession(GUEST_ID, sessionId, CreateMode.EPHEMERAL)
        String sessionToken = "$GUEST_ID&$sessionId".toString()
        requestService.setSessionToken(sessionToken, SESSION_MAX_AGE)
    }
}
