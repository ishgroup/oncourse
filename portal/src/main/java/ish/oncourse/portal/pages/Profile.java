package ish.oncourse.portal.pages;


import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import javax.servlet.http.HttpServletRequest;

/**
 * User: artem
 * Date: 10/29/13
 * Time: 10:33 AM
 */
public class Profile {

    @Inject
    private Messages messages;

    @Inject
    private HttpServletRequest httpRequest;

    @Inject
    private Request request;
    
    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IPortalService portalService;

    @Property
    private Contact contact;

    @Persist(value = PersistenceConstants.CLIENT)
    private String tab;


    @SetupRender
    void setupRender()
    {
        if (tab == null) {
            tab = "tab_profile";
        }
        ObjectContext context = cayenneService.newContext();
        contact = context.localObject(portalService.getContact());
    }
    
    public boolean showCensusTab() {
        return contact.getStudent() != null;
    }
    
    @OnEvent(component = "profileform", value = EventConstants.SELECTED)
    void saveProfile() {
        tab = "tab_profile";
    }

    @OnEvent(component = "passwordform", value = EventConstants.SELECTED)
    void savePassword() {
        tab = "tab_password";
    }

    @OnEvent(component = "censusform", value = EventConstants.SELECTED)
    void saveCensus() {
        tab = "tab_census";
    }

    public String getActiveClass(String tabName) {
        return tab.equals(tabName) ? "active" : StringUtils.EMPTY;
    }
}
