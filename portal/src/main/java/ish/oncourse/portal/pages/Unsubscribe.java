package ish.oncourse.portal.pages;

import java.util.List;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Unsubscribe {
	
	private static final String PARAM_DELIMETER = "-";
	
	@Property
	@Persist
	private Tag mailingList;
	
	@Property
	@Persist
	private Contact contact;
	
	@Property
	private boolean isSubscribed;
	
	@Property
	@Persist
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
			Long mailingListId = Long.parseLong(param.substring(0, param.indexOf(PARAM_DELIMETER)));

			this.mailingList = portalService.getMailingList(mailingListId);
			this.contact = portalService.getContact();
		
			return (this.mailingList == null || this.contact == null) ? pageNotFound : null;
		} catch (Exception e) {
			return pageNotFound;
		}
	}
	
	@SetupRender
	Object setupRender() {
		if (this.mailingList != null && this.contact != null) {
			if (isContactSubscribed(contact, mailingList)) {
				this.isSubscribed = true;
			}
			else {
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
	
	void onSelectedFromUnsubscribeAction() {
		unsubscribe = true;
	}
	
	void onSelectedFromRemainAction() {
		unsubscribe = false;
	}
	
	@OnEvent(component="unsubscribeForm", value="success")
	Object submitted() {
		if (unsubscribe) {
			tagService.unsubscribeContactFromMailingList(contact, mailingList);
			postUnsubscribe = true;
		}
		return null;
	}

	private boolean isContactSubscribed(Contact contact, Tag mailingList) {
		boolean result = false;
		
		for (TaggableTag tt : mailingList.getTaggableTags()) {
			Taggable tg = tt.getTaggable();
			if (Contact.class.getSimpleName().equals(tg.getEntityIdentifier()) && contact.getId().equals(tg.getEntityWillowId())) {
				result = true;
				break;
			}
		}
		return result;
	}
}
