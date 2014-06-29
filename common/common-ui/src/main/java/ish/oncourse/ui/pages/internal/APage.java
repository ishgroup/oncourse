package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * IMPORTANT NOTE:
 * AFTER SOME CHANGES ARE ADDED TO APage WE NEED TO REVIEW cms:PageStructure.java, cms:PageStructure.tml, common-ui:PageStructure.tml
 */
public abstract class APage {

    private static final String INTERNAL_PAGE_BODY_CLASS = "internal-page";

    private static final String MAIN_PAGE_BODY_CLASS = "main-page";

    private static final String PAGE_BODY_ID_PREFIX = "page";

    private static final String MAIN_BODY_ID = "Main";


    private WebNode node;

    @Inject
    private Request request;

    @Inject
    private RequestGlobals requestGlobals;

    @Inject
    private IWebNodeService webNodeService;

    @Inject
    private IFacebookMetaProvider facebookMetaProvider;

    private static final Logger logger = Logger.getLogger(Page.class);

    @SetupRender
    public boolean beforeRender() throws IOException {
        node = webNodeService.getCurrentNode();
        if (node == null || !node.isPublished()) {
            logger.warn(String.format("CurrentNode \"%s\" is %s in %s/%s",
                    node == null ? "undefined" : webNodeService.getPath(node),
                    node == null ? "null" : "unpublished",
                    request.getServerName(),
                    request.getPath()));

            HttpServletResponse httpServletResponse = requestGlobals.getHTTPServletResponse();
            httpServletResponse.setContentType("text/html;charset=UTF-8");
            httpServletResponse.sendRedirect("http://" + request.getServerName() + "/PageNotFound");
        }
        return true;
    }


    public String getBodyId() {
        return (isHomePage() || this.node == null) ? MAIN_BODY_ID : (PAGE_BODY_ID_PREFIX + this.node.getNodeNumber());
    }

    public WebNodeType getWebNodeType() {
        return node != null ? node.getWebNodeType() : null;
    }

    public String getBodyClass() {
        return (isHomePage()) ? MAIN_PAGE_BODY_CLASS :INTERNAL_PAGE_BODY_CLASS;
    }

    private boolean isHomePage() {
        WebNode homePage = webNodeService.getHomePage();
        return homePage != null && node != null && node.getId() != null && node.getId().equals(homePage.getId());
    }


    public String getMetaDescription() {
        return facebookMetaProvider.getDescriptionContent(node);
    }


    public WebNode getNode() {
        return node;
    }

    public void setNode(WebNode node) {
        this.node = node;
    }

    public IWebNodeService getWebNodeService() {
        return webNodeService;
    }

    public Request getRequest() {
        return request;
    }
}
