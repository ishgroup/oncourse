package ish.oncourse.portal.components;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class SwitchContact {
    @Inject
    private IPortalService portalService;

    @Property
    private List<Contact> contacts;

    @Property
    private Contact contact;

    @Inject
    private ComponentResources componentResources;

    @SetupRender
    boolean setupRender() {
        contacts = portalService.getChildContacts();
        return true;
    }

    public Contact getSelectedContact()
    {
        return portalService.getContact();
    }

    public boolean isActive(Contact contact) {
        return portalService.isSelectedContact(contact);
    }

    public boolean hasRelatedContacts() {
        return contacts.size() > 1;
    }

    @OnEvent(value = "selectContact")
    public void selectContact(Long contactId)
    {
        for (Contact contact : portalService.getChildContacts()) {
			if (contact.getId().equals(contactId)) {
				portalService.selectContact(contact);
				break;
			}
		}
    }
}
