package ish.oncourse.enrol.waitinglist;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;

import java.util.List;

public class MailingListController extends AContactController {

    private List<Tag> selectedMailingLists;

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

    public void setSelectedMailingLists(List<Tag> selectedMailingLists) {
        this.selectedMailingLists = selectedMailingLists;
    }

    public void setTagService(ITagService tagService) {
        this.tagService = tagService;
    }
}
