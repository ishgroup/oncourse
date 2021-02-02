package ish.oncourse.api.request

import com.google.inject.Singleton
import ish.oncourse.api.access.SessionCookie
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

import javax.servlet.http.Cookie

@Singleton
class RequestService {

    static final ThreadLocal<Request> ThreadLocalRequest = new ThreadLocal<Request>()
    static final ThreadLocal<Response> ThreadLocalResponse = new ThreadLocal<Response>()


    Request getRequest() {
        ThreadLocalRequest.get()
    }

    Response getResponse() {
        ThreadLocalResponse.get()
    }

    void setSessionToken(String value, int maxAge) {
        Cookie cookie = new Cookie(SessionCookie.SESSION_ID, value)
        cookie.domain = request.serverName
        cookie.path = request.contextPath
        cookie.maxAge = maxAge
        cookie.comment = 'Session identifier'
        cookie.httpOnly = false
        cookie.secure = false
        cookie.version = 0

        response.addCookie(cookie)
    }

}
