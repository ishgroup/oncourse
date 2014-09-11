package ish.oncourse.ui.pages.internal;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.ui.pages.PageNotFound;
import ish.oncourse.ui.services.HttpStatusCode;
import ish.oncourse.util.URLUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

import static ish.oncourse.util.URLUtils.buildURL;

/**
 * the class handels 'redirect 301' functionality.
 */
public class Redirect301 {
    private static final Logger LOGGER = Logger.getLogger(Redirect301.class);

    @Inject
    private Request request;

    @Inject
    private Response response;


    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    public Object onActivate() throws IOException {
        String redirectTo = (String) request.getAttribute(PageLinkTransformer.REQUEST_ATTR_redirectTo);
        if (redirectTo == null) {
            LOGGER.debug(String.format("Redirect path is null"));
            return pageRenderLinkSource.createPageRenderLink(PageNotFound.class);
        }

        //url to another site
        if (URLUtils.HTTP_URL_VALIDATOR.isValid(redirectTo)) {
            return new HttpStatusCode(HttpServletResponse.SC_MOVED_PERMANENTLY, new URL(redirectTo));
        }

        //path to another page for the site
        if (URLUtils.HTTP_URL_VALIDATOR.isValidOnlyPath(redirectTo)) {
            return new HttpStatusCode(HttpServletResponse.SC_MOVED_PERMANENTLY, buildURL(request, redirectTo, false));
        }

        LOGGER.warn(String.format("Wrong redirect path: %s",redirectTo));
        return pageRenderLinkSource.createPageRenderLink(PageNotFound.class);
    }
}
