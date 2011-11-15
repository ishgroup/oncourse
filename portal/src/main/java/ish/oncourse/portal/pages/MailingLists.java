package ish.oncourse.portal.pages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.tag.ITagService;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class MailingLists {

	@Inject
	private IAuthenticationService authService;
	
	@Inject
	private ITagService tagService;
	
	@Property
	private Contact currentUser;
	
	@Property
	private Object currentMailingList;

	@Persist
	private Set<String> selectedMailingLists;
	
	@Persist
	private  Map<String,String> mailingMapForSubscribe;
	
	public boolean getCurrentValue() {
	     return getSelectedMailingLists().contains(this.currentMailingList);
	}

	public void setCurrentValue(boolean currentValue) {
	    if (!getCurrentValue()) {
	    	getSelectedMailingLists().add((String) this.currentMailingList);
	    } else {
	    	getSelectedMailingLists().remove((String)this.currentMailingList);
	    }
	}

	public String getMapValue() {
	    return getMailingMapForSubscribe().get(this.currentMailingList);
	}
	
	@SetupRender
	void beforeRender() {
		this.currentUser = authService.getUser();
	}

	public Map<String,String> getMailingMapForSubscribe() {
		if (mailingMapForSubscribe == null) {
			mailingMapForSubscribe = new HashMap<String, String>();
			
			// get all mailing lists for this College
			for (Tag tag : tagService.getMailingLists()) {
				mailingMapForSubscribe.put(tag.getId().toString(), tag.getName());
			}
		}
		return mailingMapForSubscribe;
	}

	public Set<String> getSelectedMailingLists() {
		if (selectedMailingLists == null) {
			selectedMailingLists = new HashSet<String>();
			
			// get all mailing lists for this College
			for (Tag tag : tagService.getMailingListsContactSubscribed(currentUser)) {
				selectedMailingLists.add(tag.getId().toString());
			}
		}
		return selectedMailingLists;
	}

	public String getCollegeName() {
		return  authService.getUser().getCollege().getName();
	}
	
	public boolean getHaveMailingLists() {
		return getMailingMapForSubscribe().size() > 0;
	}
}
