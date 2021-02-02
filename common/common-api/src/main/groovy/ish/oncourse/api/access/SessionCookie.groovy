package ish.oncourse.api.access

import org.apache.commons.lang.StringUtils
import org.eclipse.jetty.server.Request

import javax.servlet.http.Cookie

class SessionCookie {

    private String userType
    private Long userId
    private String sessionNode
    private boolean exist = true

    public static final String SESSION_ID = 'ESESSIONID'

    private SessionCookie(){}

    static SessionCookie valueOf(Request request) {
        SessionCookie sessionCookie = new SessionCookie()

        Cookie cookie = request.cookies?.find { it.name == SESSION_ID }
        if (cookie && cookie.value && StringUtils.trimToNull(cookie.value)) {
            String value = cookie.value
            sessionCookie.sessionNode = "/${value.replace('&', '/')}"
            sessionCookie.userId = Long.valueOf(value.split('&')[0].split('-')[1])
            sessionCookie.userType = value.split('&')[0].split('-')[0]
        } else {
            sessionCookie.exist = false
        }
        return sessionCookie
    }


    String getUserType() {
        return userType
    }

    void setUserType(String useType) {
        this.userType = useType
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long useId) {
        this.userId = useId
    }

    String getSessionNode() {
        return sessionNode
    }

    void setSessionNode(String sessionNode) {
        this.sessionNode = sessionNode
    }

    boolean getExist() {
        return exist
    }

    void setExist(boolean exist) {
        this.exist = exist
    }
}
