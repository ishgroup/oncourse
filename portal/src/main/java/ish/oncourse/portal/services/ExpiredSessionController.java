package ish.oncourse.portal.services;

import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The class provide the generic solution of expired sssion handling for ajax requests.
 */
public class ExpiredSessionController implements Dispatcher {
    private static final String JSON_ERROR = "error";
    private static final String ERROR_sessionExpired = "sessionExpired";

    @Override
    public boolean dispatch(Request request, Response response) throws IOException {

        Session session = request.getSession(false);
        if (session == null && request.isXHR()) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            OutputStream os = response.getOutputStream("application/json;charset=UTF-8");
            os.write(String.format("{\"%s\":\"%s\"}", JSON_ERROR, ERROR_sessionExpired).getBytes());
            os.flush();
            return true;
        }

        return false;
    }
}
