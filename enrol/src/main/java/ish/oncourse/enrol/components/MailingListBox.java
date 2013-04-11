package ish.oncourse.enrol.components;

import ish.oncourse.enrol.waitinglist.MailingListController;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class MailingListBox {

	@Inject
	private ITagService tagService;

	@Property
	@Parameter(required = true)
	private MailingListController controller;

	@Property
	private int listIndex;


	@SetupRender
	void setupRender() {
	}

	public void setListChecked(boolean checked) {
		if (checked) {
			this.controller.selectedMailList(listIndex);
		} else {
			this.controller.selectedMailList(listIndex);
		}


	}

	public boolean getListChecked() {
		return this.controller.isSelectedMailList(listIndex);
	}

	public Tag getCurrentList() {
		return this.controller.getMailingLists().get(listIndex);
	}

}
