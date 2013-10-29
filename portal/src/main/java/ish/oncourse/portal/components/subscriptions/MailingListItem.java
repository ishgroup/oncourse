package ish.oncourse.portal.components.subscriptions;

import ish.oncourse.model.Tag;
import org.apache.tapestry5.annotations.*;


public class MailingListItem {

	@Parameter
	@Property
	private Tag mailingList;

	@Parameter
	private boolean tagSelected;


	private boolean selected;


    @InjectContainer
	private MailingLists mailingLists;


	@SetupRender
	void beforeRender() {
		selected = this.tagSelected;
	}


	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected)
			mailingLists.addSelectedTag(mailingList);
		else
			mailingLists.removeSelectedTag(mailingList);
	}

}
