package ish.oncourse.services.authentication

import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.http.HttpServletRequest

class CheckBasicAuth {

    static final String HTTP_HEADER_AUTH = 'Authorization'
    static final String AUTH_TYPE_BASIC = 'Basic'

    private static final Logger logger = LogManager.logger

    private IAuthenticationService service
    private HttpServletRequest request

    static CheckBasicAuth valueOf(IAuthenticationService service, HttpServletRequest request) {
        CheckBasicAuth checker = new CheckBasicAuth()
        checker.service = service
        checker.request = request
        return checker
    }

    AuthenticationResult check() {
        String authHeader = request.getHeader(HTTP_HEADER_AUTH)
        if (StringUtils.trimToNull(authHeader)) {
            StringTokenizer st = new StringTokenizer(authHeader)
            if (st.hasMoreTokens()) {
                String basic = st.nextToken()
                if (basic.equalsIgnoreCase(AUTH_TYPE_BASIC)) {
                    try {
                        String credentials = new String(Base64.decodeBase64(st.nextToken()), 'UTF-8')
                        int p = credentials.indexOf(':')
                        if (p != -1) {
                            String _username = credentials.substring(0, p).trim()
                            String _password = credentials.substring(p + 1).trim()
                            return service.authenticate(_username, _password, false)
                        }
                    } catch (UnsupportedEncodingException e) {
                        logger.catching(e)
                    }
                }
            }
        }
        return AuthenticationResult.valueOf(AuthenticationStatus.INVALID_CREDENTIALS)
    }
}
