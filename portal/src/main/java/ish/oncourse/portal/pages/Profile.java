package ish.oncourse.portal.pages;


import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 10:33 AM
 */
public class Profile {

    @Inject
    private Messages messages;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IPortalService portalService;

    @Persist
    @Property
    private Contact contact;

    @Property
    @Persist
    private String activeTabId;

  @SetupRender
  void setupRender()
    {
        ObjectContext context = cayenneService.newContext();

        contact = context.localObject(portalService.getContact());

        if (activeTabId == null)
            activeTabId = "tab_profile";
    }

    @OnEvent(value = "setActiveTab")
    public void setActiveTab(String activeTabId)
    {
        this.activeTabId = activeTabId;
    }

    public String getActiveClass(String tabId)
    {
        return tabId.equals(activeTabId) ? messages.get("class.active") : StringUtils.EMPTY;
    }

    public boolean showCensusTab() {
        return contact.getStudent() != null &&
                contact.getCollege().getRequiresAvetmiss();
    }
}
