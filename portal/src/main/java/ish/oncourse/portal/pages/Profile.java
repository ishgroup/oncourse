package ish.oncourse.portal.pages;


import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.PersistenceConstants;
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
    private IPortalService portalService;

    @Property
    private Contact contact;

    @SetupRender
    void setupRender()
    {
        ObjectContext context = cayenneService.newContext();
        contact = context.localObject(portalService.getContact());
    }

    public boolean showCensusTab() {
        return contact.getStudent() != null;
    }
}
