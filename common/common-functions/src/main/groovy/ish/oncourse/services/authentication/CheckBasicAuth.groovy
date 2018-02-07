package ish.oncourse.services.authentication

import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.http.HttpServletRequest

class CheckBasicAuth {

    private static final Logger logger = LogManager.logger

    private static final String HEADER_Authorization = 'Authorization'
    private static final String TOKEN_Basic = 'Basic'

    private IAuthenticationService service
    private HttpServletRequest request

    static CheckBasicAuth valueOf(IAuthenticationService service, HttpServletRequest request) {
        CheckBasicAuth checker = new CheckBasicAuth()
        checker.service = service
        checker.request = request
        return checker
    }

    AuthenticationResult check() {
        String authHeader = StringUtils.trimToNull(request.getHeader(HEADER_Authorization))
        if (authHeader) {
            StringTokenizer st = new StringTokenizer(authHeader)
            if (st.hasMoreTokens()) {
                String basic = st.nextToken()
                if (basic.equalsIgnoreCase(TOKEN_Basic)) {
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
                    return AuthenticationResult.valueOf(AuthenticationStatus.INVALID_CREDENTIALS)
                }
            }
        } else {
            return AuthenticationResult.valueOf(AuthenticationStatus.UNAUTHORIZED)
        }

    }
}
