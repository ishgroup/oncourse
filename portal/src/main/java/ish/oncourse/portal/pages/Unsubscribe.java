package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

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
			parseContactAndMailingList(param);

            if (this.mailingList == null || this.contact == null) {
                return pageNotFound;
            }

            return null;
        } catch (Exception e) {
            return pageNotFound;
        }
	}

	private void parseContactAndMailingList(String param) {
		Long mailingListId = Long.parseLong(param.substring(0, param.indexOf(PARAM_DELIMETER)));
		String contactUniqueCode = param.substring(param.indexOf(PARAM_DELIMETER) + 1);

		List<Tag> tagList = tagService.loadByIds(mailingListId);
		if (!tagList.isEmpty()) {
            this.mailingList = tagList.get(0);
        }
		this.contact = contactService.findByUniqueCode(contactUniqueCode);
	}

	public Object onActivate()
    {
        return this.mailingList == null || this.contact == null ?  pageNotFound :  null;
    }

	@SetupRender
	Object setupRender() {
		if (this.mailingList != null && this.contact != null) {
			if (tagService.isContactSubscribedToMailingList(this.contact, this.mailingList)) {
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
}
