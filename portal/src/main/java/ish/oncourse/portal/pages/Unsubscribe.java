package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Unsubscribe {
	
	private static final String PARAM_DELIMETER = "-";
	
	@Property
	private Tag mailingList;

	@Property
	@Persist(value = PersistenceConstants.CLIENT)
	private Long mailingListId;
	
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
            if (this.mailingList == null || this.contact == null) {
                return pageNotFound;
            }
            return null;
        } catch (Exception e) {
            return pageNotFound;
        }
	}
	
	public Object onActivate() {
		if (mailingListId == null || contactUniqueCode == null ) {
			return pageNotFound;
		} else {
			initData();
			return null;
		}
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

	@OnEvent(component="unsubscribeForm", value="success")
	Object submitted() {
		if (unsubscribe) {
			tagService.unsubscribeContactFromMailingList(contact, mailingList);
			postUnsubscribe = true;
		}
		return null;
	}

	private void parseContactAndMailingList(String param) {
		mailingListId = Long.parseLong(param.substring(0, param.indexOf(PARAM_DELIMETER)));
		contactUniqueCode = param.substring(param.indexOf(PARAM_DELIMETER) + 1);
	}
	
	private void initData() {
		contact =  contactService.findByUniqueCode(contactUniqueCode);
		List<Tag> tagList = tagService.loadByIds(mailingListId);
		if (!tagList.isEmpty()) {
			this.mailingList = tagList.get(0);
		}
	}
	
	void onSelectedFromUnsubscribeAction() {
		unsubscribe = true;
	}
	
	void onSelectedFromRemainAction() {
		unsubscribe = false;
	}
}
