package ish.oncourse.ui.services;

import org.apache.tapestry5.Link;

import java.io.Serializable;
import java.net.URL;

/**
 * The class was introduced to implement '301 redirect' for our application.
 * @see ish.oncourse.ui.pages.internal.Redirect301
 * @see ish.oncourse.ui.services.UIModule#contributeComponentEventResultProcessor(org.apache.tapestry5.ioc.MappedConfiguration, org.apache.tapestry5.services.Response)
 */
public class HttpStatusCode implements Serializable {

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
