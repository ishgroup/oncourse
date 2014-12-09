package ish.oncourse.enrol.waitinglist;

import ish.oncourse.model.CustomField;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;

import java.util.ArrayList;
import java.util.List;

public class MailingListController extends AContactController {

    private List<Tag> selectedMailingLists = new ArrayList<>();

	private List<Tag> mailingLists;

    private ITagService tagService;

    public void addMailingList() {
        if (!getErrors().isEmpty())
            return;
        switch (getState())
        {
            case ADD_CONTACT:
                addContact();
                break;
            case EDIT_CONTACT:
                saveContact();
                break;
            case FINISHED:
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void saveContact() {

        if (selectedMailingLists == null || selectedMailingLists.isEmpty())
        {
            addError("message-mailingListNotSelected", getMessages().format("message-mailingListNotSelected"));
            return;
        }
        super.saveContact();
        List<Tag> subscribedLists = tagService.getMailingListsContactSubscribed(getContact());
        for (Tag list : selectedMailingLists) {
            if (!containsMailingList(subscribedLists,list)) {
                tagService.subscribeContactToMailingList(getContact(), list);
            }
            else
            {
                addWarning("message-alreadySubscribed", getMessages().format("message-alreadySubscribed",getContact().getFullName(), list.getName()));
            }
        }
    }

    private boolean containsMailingList(List<Tag> subscribedLists, Tag tag)
    {
        for (Tag  tag1: subscribedLists) {
            if (tag1.getId().equals(tag.getId()))
                return true;
        }
        return false;
    }

    public List<Tag> getSelectedMailingLists() {
        return selectedMailingLists;
    }

	public List<Tag> getMailingLists()
	{
		if (mailingLists == null)
		{
			mailingLists =  tagService.getMailingLists();
		}
		return mailingLists;
	}

    public void setSelectedMailingLists(List<Tag> selectedMailingLists) {
        this.selectedMailingLists = selectedMailingLists;
    }

	public void selectedMailList(int index)
	{
		if (!this.selectedMailingLists.contains(mailingLists.get(index)))
			this.selectedMailingLists.add(mailingLists.get(index));
	}

	public void deselectedMailList(int index)
	{
		this.selectedMailingLists.remove(mailingLists.get(index));
	}

	public boolean isSelectedMailList(int index)
	{
		return this.selectedMailingLists.contains(mailingLists.get(index));
	}


    public void setTagService(ITagService tagService) {
        this.tagService = tagService;
    }

	@Override
	public List<CustomField> getCustomFields() {
		return getContact().getCustomFields();
	}

    @Override
    public String getHeaderTitle() {
        return null;
    }

    @Override
    public String getHeaderMessage() {
        return null;
    }

	@Override
	public boolean isCompanyPayer() {
		return false;
	}
}
