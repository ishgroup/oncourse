package ish.oncourse.portal.pages;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.tag.ITagService;

import org.apache.tapestry5.annotations.OnEvent;
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

	public Map<String,String> getMailingMapForSubscribe() {
	   return mailingMapForSubscribe;
	}

	public boolean getCurrentValue() {
	     return this.selectedMailingLists.contains(this.currentMailingList);
	}

	public void setCurrentValue(final boolean currentValue) {
	    final String mapValue = this.getMapValue();

	    if (currentValue) {
	        this.selectedMailingLists.add(mapValue);
	    } else {
	        this.selectedMailingLists.remove(mapValue);
	    }
	}


	public String getMapValue() {
	    return this.getMailingMapForSubscribe().get(this.currentMailingList);
	}
	
	@SetupRender
	void beforeRender() {
		this.currentUser = authService.getUser();
		
		selectedMailingLists = new HashSet<String>();
		mailingMapForSubscribe = new HashMap<String, String>();

//		// temporary show only tags for this user in future will use 
//		// tagService.getMailingLists();
		for (Tag tag : tagService.getTagsForEntity(Contact.class.getSimpleName(), currentUser.getId())) {
			mailingMapForSubscribe.put(tag.getId().toString(), tag.getName());
		}
		
	}


	public String getCollegeName() {
		return  authService.getUser().getCollege().getName();
	}
	
	public boolean getHaveMailingLists() {
		return mailingMapForSubscribe.size() > 0;
	}
	
	@OnEvent(component = "mailingListCheckbox")
	void onCheckedMailingListCheckbox() {
		System.out.println(getMapValue());
	}

	Object onSuccess() throws IOException {
		System.out.println(getMapValue());
		return this;
	}
}
