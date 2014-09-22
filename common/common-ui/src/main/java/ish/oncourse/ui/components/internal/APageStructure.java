package ish.oncourse.ui.components.internal;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.ui.components.GoogleTagmanager;
import ish.oncourse.util.RequestUtil;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;
import static ish.oncourse.util.HTMLUtils.USER_AGENT_HEADER;


/**
 * IMPORTANT NOTE:
 * AFTER SOME CHANGES ARE ADDED TO APageStructure WE NEED TO REVIEW cms:PageStructure.java, cms:PageStructure.tml, common-ui:PageStructure.tml
 */
public abstract class APageStructure {

    @Inject
    private IWebNodeTypeService webNodeTypeService;

    @Inject
    private Request request;

    @Inject
    private ComponentResources resources;

    @Inject
    private ICacheMetaProvider cacheMetaProvider;

    @InjectComponent
    private GoogleTagmanager googleTagmanager;

    @Property
    @Parameter
    private String bodyId;

    @Parameter
    private String bodyClass;

    @Property
    @Parameter
    private WebNodeType webNodeType;

    @Property
    @Parameter
    @Deprecated//we need to use pageName property
    private String title;

    @Property
    @Parameter
    private String pageName;

    @Property
    @Parameter
    private WebNode webNode;


    @Property
    @Parameter
    private String canonicalLinkPath;

    @Property
    @Parameter
    private String metaDescription;

    /**
     * Google tag mananger event name.
     */
    @Property
    @Parameter
    private String eventName;

    //we should use for the property set and get method instead of @Property and @Parameter
    //annotations to allow tapestry passes the parameter through a few componnets
    private Cart cart;

    @SetupRender
    public void beforeRender() {
        if (!resources.isBound("webNodeType")) {
            this.webNodeType = webNodeTypeService.getDefaultWebNodeType();
        }

        //TODO: the code can be removed after all custom templates will be adjusted to use Title component
        if (pageName != null && title == null)
            title = pageName;
        if (title != null && pageName == null)
            pageName = title;

        googleTagmanager.setCart(cart);
    }

    public String getAgentAwareBodyClass() {
        return bodyClass + RequestUtil.getAgentAwareClass(request.getHeader(USER_AGENT_HEADER));
    }

    public boolean isWrapped() {
        //value false for the parameter can be used if we want to get only content some page
        //without headers, footers, left and right blocks. See example: PaymentEditor.tml /enrol/ui/cvv?wrap=false
        String wrap = request.getParameter("wrap");
        return wrap == null || Boolean.parseBoolean(wrap);
    }

    public String getCacheMeta()
    {
        return cacheMetaProvider.getMetaContent();
    }

    public IWebNodeTypeService getWebNodeTypeService() {
        return webNodeTypeService;
    }

    public Request getRequest() {
        return request;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
