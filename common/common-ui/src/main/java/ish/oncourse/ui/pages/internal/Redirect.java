package ish.oncourse.ui.pages.internal;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.ui.pages.PageNotFound;
import ish.oncourse.util.URLUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;

import java.io.IOException;
import java.net.URL;

import static ish.oncourse.util.URLUtils.buildURL;

/**
 * the class handels redirect functionality.
 */
public class Redirect {
    private static final Logger LOGGER = Logger.getLogger(Redirect.class);

    @Inject
    private Request request;


    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    public Object onActivate() throws IOException {
        String redirectTo = (String) request.getAttribute(PageLinkTransformer.REQUEST_ATTR_redirectTo);
        if (redirectTo == null)
            pageRenderLinkSource.createPageRenderLink(PageNotFound.class);

        //url to another site
        if (URLUtils.HTTP_URL_VALIDATOR.isValid(redirectTo))
            return new URL(redirectTo);

        //path to another page for the site
        if (URLUtils.HTTP_URL_VALIDATOR.isValidOnlyPath(redirectTo)) {
            return buildURL(request, redirectTo, false);
        }

        LOGGER.warn(String.format("Wrong redirect path: %s",redirectTo));
        return pageRenderLinkSource.createPageRenderLink(PageNotFound.class);
    }
}
