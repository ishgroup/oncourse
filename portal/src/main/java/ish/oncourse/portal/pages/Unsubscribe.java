package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.GetIsTagAssignedTo;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.UnlinkTagFromQueuable;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Unsubscribe {
	
	private static final String PARAM_DELIMETER = "-";
	
	@Property
	private Tag contactTag;

	@Property
	@Persist(value = PersistenceConstants.CLIENT)
	private Long contactTagId;
	
	@Property
	private Contact contact;

	@Property
	@Persist(value = PersistenceConstants.CLIENT)
	private String contactUniqueCode;
	
	@Property
	private boolean isSubscribed;
	
	@Property
	@Persist(value = PersistenceConstants.CLIENT)
	private boolean postUnsubscribe;

    @Inject ITagService tagService;

	@Inject
	private IPortalService portalService;
	
	@Inject
	private IContactService contactService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	private boolean unsubscribe;

	Object onActivate(String param) {
        try {
			parseContactAndMailingList(param);
			initData();
            if (this.contactTag == null || this.contact == null) {
                return pageNotFound;
            }
            return null;
        } catch (Exception e) {
            return pageNotFound;
        }
	}
	
	public Object onActivate() {
		if (contactTagId == null || contactUniqueCode == null ) {
			return pageNotFound;
		} else {
			initData();
			return null;
		}
	}

	@SetupRender
	Object setupRender() {
		if (this.contactTag != null && this.contact != null) {
			if (GetIsTagAssignedTo.valueOf(cayenneService.sharedContext(), contactTag, contact).get()) {
				this.isSubscribed = true;
			} else {
				this.isSubscribed = false;
			}
			return null;
		}
		return pageNotFound;
	}
	
	@AfterRender
	void afterRender() {
		postUnsubscribe = false;
	}

	@OnEvent(component="unsubscribeForm", value="success")
	Object submitted() {
		if (unsubscribe) {
			UnlinkTagFromQueuable.valueOf(cayenneService.sharedContext(), contact, contactTag).apply();
			postUnsubscribe = true;
		}
		return null;
	}

	private void parseContactAndMailingList(String param) {
		contactTagId = Long.parseLong(param.substring(0, param.indexOf(PARAM_DELIMETER)));
		contactUniqueCode = param.substring(param.indexOf(PARAM_DELIMETER) + 1);
	}
	
	private void initData() {
		contact =  contactService.findByUniqueCode(contactUniqueCode);
		List<Tag> tagList = tagService.loadByIds(contactTagId);
		if (!tagList.isEmpty()) {
			this.contactTag = tagList.get(0);
		}
	}
	
	void onSelectedFromUnsubscribeAction() {
		unsubscribe = true;
	}
	
	void onSelectedFromRemainAction() {
		unsubscribe = false;
	}
}
