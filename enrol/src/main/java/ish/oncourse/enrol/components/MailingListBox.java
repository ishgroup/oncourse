package ish.oncourse.enrol.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class MailingListBox {
	
	@Inject
	private ITagService tagService;
	
	@Property
	@Persist
	private List<Tag> mailingLists;
	
	@Property
	private int listIndex;
	
	@Persist
	private List<Tag> selectedMailingLists;
	
	@SetupRender
	void setupRender() {
		this.mailingLists = tagService.getMailingLists();
		this.selectedMailingLists = new ArrayList<Tag>();
	}
	
	public void setListChecked(boolean checked) {
		if (checked) {
			this.selectedMailingLists.add(mailingLists.get(listIndex));
		}
	}
	
	public boolean getListChecked() {
		return false;
	}
	
	public Tag getCurrentList() {
		return this.mailingLists.get(listIndex);
	}
	
	public List<Tag> getSelectedMailingLists() {
		return Collections.unmodifiableList(selectedMailingLists);
	}

}
