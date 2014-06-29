package ish.oncourse.cms.components;

import ish.oncourse.cms.services.Constants;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.model.SystemUser;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WillowUser;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class CmsNavigation {

    private static final String FULL_NAME_TEMPLATE = "%s %s";
    @InjectPage
    private Page page;

    @Inject
    private Request request;

    @Inject
    private Response response;

    @Inject
    @Property
    private IAuthenticationService authenticationService;

    @Property
    @Inject
    private IWebMenuService webMenuService;

    @Property
    @Parameter
    private WebNode node;

    @InjectComponent
    @Property
    private PageInfo pageInfo;

    @InjectComponent
    private Pages pagesComponent;

    @SetupRender
    public void beforeRender() {

    }

    public void onActionFromLogout() throws Exception {
        authenticationService.logout();
        response.sendRedirect(Constants.HOME_PAGE);
    }

    public Object onActionFromPages() {
        if (request.getSession(false) == null) {
            return page.getReloadPageBlock();
        }
        return pagesComponent.getPagesListZone().getBody();
    }

    public String getUserName() {
        WillowUser willowUser = authenticationService.getUser();

        if (willowUser != null)
            return String.format(FULL_NAME_TEMPLATE, willowUser.getFirstName(), willowUser.getLastName());
        SystemUser systemUser = authenticationService.getSystemUser();
        if (systemUser != null)
            return String.format(FULL_NAME_TEMPLATE, systemUser.getFirstName(), systemUser.getSurname());
        throw new IllegalStateException("authenticated user is nod defined");
    }

}
