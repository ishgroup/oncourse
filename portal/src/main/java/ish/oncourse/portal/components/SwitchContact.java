package ish.oncourse.portal.components;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.pages.PageNotFound;
import ish.oncourse.portal.pages.Timetable;
import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class SwitchContact {
    @Inject
    private IPortalService portalService;

    @Property
    @Persist
    private List<Contact> contacts;

    @Property
    private Contact contact;

    @InjectPage
    private PageNotFound pageNotFound;

    @InjectPage
    private Timetable timetable;

    @Inject
    private ComponentResources componentResources;

    @SetupRender
    boolean setupRender() {
        contacts = portalService.getChildContacts();
        return true;
    }

    public boolean isActive(Contact contact) {
        return portalService.isSelectedContact(contact);
    }

    public boolean needToShow()
    {
        return contacts.size() > 1;
    }

    @OnEvent(value = "selectContact")
    public Object selectContact(Long contactId)
    {
        for (Contact contact : contacts) {
            if (contact.getId().equals(contactId)) {
                portalService.selectContact(contact);
                return componentResources.getPage();
            }
        }
        return pageNotFound;
    }
}
