package ish.oncourse.services.authentication

import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CheckBasicAuth {

    private static final Logger logger = LogManager.logger

    IAuthenticationService service
    String authHeader

    static CheckBasicAuth valueOf(IAuthenticationService service, String authHeader) {
        CheckBasicAuth checker = new CheckBasicAuth()
        checker.service = service
        checker.authHeader = authHeader
        return checker
    }

    AuthenticationStatus check() {
        if (StringUtils.trimToNull(authHeader)) {
            StringTokenizer st = new StringTokenizer(authHeader)
            if (st.hasMoreTokens()) {
                String basic = st.nextToken()
                if (basic.equalsIgnoreCase('Basic')) {
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
        return AuthenticationStatus.INVALID_CREDENTIALS
    }
}
