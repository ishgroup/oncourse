package ish.oncourse.enrol.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;

import java.util.List;

import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Mail {

	@Property
	private Tag currentMailingList;
	
	@Persist
	private Contact contact;
	
	@Property
	@Persist
	private boolean submissionSucceded;
	
	@InjectComponent
	@Property
	private Zone addStudentBlock;
	
	@Inject
	private ITagService tagService;
	
	private boolean reset;
	
	@Persist
	private List<Tag> selectedMailingLists;
	
	@SetupRender
	Object setupRender() {
		if (getHasContact() && !selectedMailingLists.isEmpty()) {
			return submit();
		}
		
		return null;
	}
	
	@AfterRender
	void afterRender() {
		if (submissionSucceded) {
			
			this.contact = null;
			this.submissionSucceded = false;
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
			if (getHasContact()) {
				if (!selectedMailingLists.isEmpty()) {
				
					List<Tag> subscribedLists = tagService.getMailingListsContactSubscribed(contact);
					
					for (Tag list : selectedMailingLists) {
						if (!subscribedLists.contains(list)) {
							tagService.subscribeContactToMailingList(contact, list);
						}
					}
					this.submissionSucceded = true;
				}
			}
		}
		return addStudentBlock.getBody();
	}
	
	public boolean isNewStudent() {
		return contact.getPersistenceState() == PersistenceState.NEW;
	}
	
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	public void setSelectedMailingLists(List<Tag> mailingLists) {
		this.selectedMailingLists = mailingLists;
	}
	
	public List<Tag> getSelectedMailingLists() {
		return this.selectedMailingLists;
	}
	
	public boolean getHasContact() {
		return this.contact != null;
	}
	
	public boolean getHasMailingLists() {
		return !tagService.getMailingLists().isEmpty();
	}
	
}
