package ish.oncourse.portal.pages;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;


public class Redirect {

    public static final String REDIRECT_TO = "redirectTo";

    @Inject
    private Request request;

    public Object onActivate() throws IOException {
        return new URL(request.getParameter(REDIRECT_TO));
    }

    static class HttpStatusCode implements Serializable {

        private  int statusCode;
        private  String location;

        public HttpStatusCode(int statusCode) {
            this.statusCode = statusCode;
            this.location = "";
        }

        public HttpStatusCode(int statusCode, String location) {
            this.statusCode = statusCode;
            this.location = location;
        }

        public HttpStatusCode(int statusCode, Link link) {
            this(statusCode, link.toRedirectURI());
        }

        public HttpStatusCode(int statusCode, URL url) {
            this(statusCode, url.toExternalForm());
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getLocation() {
            return location;
        }
    }
}
