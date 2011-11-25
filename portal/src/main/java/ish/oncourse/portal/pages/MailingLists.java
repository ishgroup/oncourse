package ish.oncourse.portal.pages;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
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
	@Persist
	private Contact currentUser;
	
	@Property
	private Object currentMailingList;

	@Persist
	private Set<String> selectedMailingLists;
	
	@Persist
	private  Map<String,String> mailingMapForSubscribe;
	
	@Inject
	private ICayenneService cayenneService;
	@Inject
	private IWebSiteService webSiteService;
	
	public boolean getCurrentValue() {
	    return getSelectedMailingLists().contains(this.currentMailingList);
	}

	public void setCurrentValue(boolean currentValue) {

		// set to true and it was changed
		if (currentValue && !getCurrentValue()) {
	    	
	    	Tag tag = tagService.loadByIds(new Long((String) this.currentMailingList)).get(0);
	    	
	    	Taggable taggable = tag.getObjectContext().newObject(Taggable.class);
	    	taggable.setCollege(tag.getCollege());
	    	Date date = new Date();
	    	taggable.setCreated(date);
	    	taggable.setModified(date);
	    	taggable.setEntityIdentifier(Contact.class.getSimpleName());
	    	taggable.setEntityWillowId(currentUser.getId());
	    	
			TaggableTag taggableTag = tag.getObjectContext().newObject(TaggableTag.class);
			taggableTag.setTag(tag);
			taggableTag.setCollege(tag.getCollege());
			taggable.addToTaggableTags(taggableTag);
			
			tag.getObjectContext().commitChanges();
	    } else if (!currentValue && getCurrentValue()) { // set to false and it was changed
	    	
	    	Taggable taggableForRemove = null;
	    	TaggableTag taggableTagForRemove = null;
	    	Tag tag = tagService.loadByIds(new Long((String) this.currentMailingList)).get(0);
	    	for (TaggableTag tt: tag.getTaggableTags()) {
	    		if(tt != null && tt.getTaggable() != null && tag.getCollege().equals(tt.getTaggable().getCollege()) 
	    				&& Contact.class.getSimpleName().equals(tt.getTaggable().getEntityIdentifier()) 
	    				&& currentUser.getId().equals(tt.getTaggable().getEntityWillowId())){
	    			taggableTagForRemove = tt;
	    			taggableForRemove = tt.getTaggable();
	    			break;
	    		}
	    	}
	    	
	    	tag.getObjectContext().deleteObject(taggableTagForRemove);
	    	tag.getObjectContext().deleteObject(taggableForRemove);
	    	
	    	tag.getObjectContext().commitChanges();
	    }
	}

	public String getMapValue() {
	    return getMailingMapForSubscribe().get(this.currentMailingList);
	}
	
	@SetupRender
	void beforeRender() {
		this.currentUser = authService.getUser();
		this.mailingMapForSubscribe = new HashMap<String, String>();
		
		// get all mailing lists for this College
		for (Tag tag : tagService.getMailingLists()) {
			mailingMapForSubscribe.put(tag.getId().toString(), tag.getName());
		}
		
		this.selectedMailingLists =  new HashSet<String>();
		// get all mailing lists for this College
		for (Tag tag : tagService.getMailingListsContactSubscribed(currentUser)) {
			selectedMailingLists.add(tag.getId().toString());
		}
	}

	public Map<String,String> getMailingMapForSubscribe() {
		return mailingMapForSubscribe;
	}

	public Set<String> getSelectedMailingLists() {
		return selectedMailingLists;
	}

	public String getCollegeName() {
		return  currentUser.getCollege().getName();
	}
	
	public boolean getHaveMailingLists() {
		return getMailingMapForSubscribe().size() > 0;
	}
}
