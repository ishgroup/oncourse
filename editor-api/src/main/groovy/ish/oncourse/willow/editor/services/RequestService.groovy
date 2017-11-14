package ish.oncourse.willow.editor.services

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response

class RequestService {

    static final ThreadLocal<Request> ThreadLocalRequest = new ThreadLocal<Request>()
    static final ThreadLocal<Response> ThreadLocalResponse = new ThreadLocal<Response>()


    Request getRequest() {
        ThreadLocalRequest.get()
    }

    Response getResponse() {
        ThreadLocalResponse.get()
    }

}
