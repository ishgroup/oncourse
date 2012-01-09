package ish.oncourse.enrol.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Mail {

	@Persist
	@Property
	private List<Tag> mailingLists;

	@Property
	private Tag currentMailingList;
	
	@Property
	private int listIndex;
	
	@Persist
	private Contact contact;
	
	@Property
	@Persist
	private boolean submissionSucceded;
	
	@InjectComponent
	@Property
	private Zone addStudentBlock;
	
	@InjectComponent
	@Property
	private Form detailsForm;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ITagService tagService;
	
	private boolean reset;
	
	@Persist
	@Property
	private List<Tag> selectedMailingLists;
	
	@SetupRender
	void setupRender() {
		this.mailingLists = tagService.getMailingLists();
		this.selectedMailingLists = new ArrayList<Tag>();
		
		if (contact != null) {
			for (Tag mailingList : tagService.getMailingListsContactSubscribed(contact)) {
				this.mailingLists.remove(mailingList);
			}
		}

		detailsForm.clearErrors();
	}
	
	@AfterRender
	void afterRender() {
		submissionSucceded = false;
	}
	
	public boolean getListChecked() {
		if (this.contact != null) {
			return tagService.getMailingListsContactSubscribed(contact).contains(currentMailingList);
		}
		return false;
	}
	
	public void setListChecked(boolean checked) {
		if (checked) {
			this.selectedMailingLists.add(mailingLists.get(listIndex));
		}
	}
	
	@OnEvent(component = "addStudentAction", value = "selected")
	void onSelectedFromAddStudentAction() {
		reset = false;
	}

	@OnEvent(component = "resetAll", value = "selected")
	void onSelectedFromReset() {
		reset = true;
	}

	@OnEvent(component = "detailsForm", value = "failure")
	Block refreshContactEntry() {
		this.selectedMailingLists.clear();
		return addStudentBlock.getBody();
	}
	
	@OnEvent(component="detailsForm", value="success")
	Object submit() {
		if (reset) {
			this.contact = null;
			this.selectedMailingLists.clear();
		}
		else {
			if (!selectedMailingLists.isEmpty()) {
				
				for (Tag list : selectedMailingLists) {
					tagService.subscribeContactToMailingList(contact, list);
				}
				this.submissionSucceded = true;
			}
		}
		return addStudentBlock.getBody();
	}
	
	public boolean isNewStudent() {
		return contact.getPersistenceState() == PersistenceState.NEW;
	}
	
	public Tag getCurrentList() {
		return mailingLists.get(listIndex);
	}
	
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	public boolean getHasContact() {
		return this.contact != null;
	}
	
	public boolean getHasMailingLists() {
		return !this.mailingLists.isEmpty();
	}
	
}
