package ish.oncourse.portal.components;

import ish.oncourse.model.Tag;
import ish.oncourse.portal.pages.MailingLists;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class MailingListItem {

	@Parameter
	@Property
	private Tag mailingList;

	@Parameter
	private boolean tagSelected;


	private boolean selected;

	@InjectPage
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
