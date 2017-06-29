package ish.oncourse.portal.components.subscriptions;

import ish.oncourse.model.*;
import ish.oncourse.portal.pages.Login;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.MailingListHelper;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.*;

public class MailingLists {

	private static final Logger logger = LogManager.getLogger();



    @Property
    @Persist
    private boolean chkEmail;

    @Property
    @Persist
    private boolean chkSMS;

    @Property
    @Persist
    private boolean chkPost;

	@Property
	@Persist
	private Contact currentUser;

	@Property
	@Persist
	private List<Long> selectedMailingLists;

	@Property
	@Persist
	private List<Tag> mailingLists;

	@Property
	private int index;


	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

    @Inject
    private IPortalService portalService;

    @Inject
	private ITagService tagService;

	@Property
	@Persist
	private boolean isSaved;

	@Inject
	private Request request;

	@InjectPage
	private Login loginPage;

	@SetupRender
	void beforeRender() {
        ObjectContext objectContext = cayenneService.newContext();
		this.currentUser = objectContext.localObject(portalService.getContact());

		this.mailingLists = tagService.getMailingLists();
		this.selectedMailingLists = new ArrayList<>();

        this.chkPost = this.currentUser.getIsMarketingViaPostAllowed();
        this.chkSMS = this.currentUser.getIsMarketingViaSMSAllowed();
        this.chkEmail = this.currentUser.getIsMarketingViaEmailAllowed();

		initSelectedLists();
	}
	
	@AfterRender
	void afterRender() {
		if (isSaved) {
			this.isSaved = false;
		}
	}

	public Tag getMailingList()
	{
		return mailingLists.get(index);
	}

	public boolean isSelected()
	{
		Tag tag =  mailingLists.get(index);
		return selectedMailingLists.contains(tag.getId());
	}


	private void initSelectedLists() {
		Set<Tag> listOfUser = tagService.getMailingListsContactSubscribed(currentUser);
		for (Tag t : listOfUser) {
			selectedMailingLists.add(t.getId());
		}
	}

	public void addSelectedTag(Tag tag)
	{
		if (!selectedMailingLists.contains(tag.getId()))
			selectedMailingLists.add(tag.getId());
	}

	public void removeSelectedTag(Tag tag)
	{
		selectedMailingLists.remove(tag.getId());
	}


	public void onSubmitFromMailingListForm() {

        ObjectContext objectContext = currentUser.getObjectContext();

		this.isSaved = true;

		

        currentUser.setIsMarketingViaEmailAllowed(chkEmail);

        currentUser.setIsMarketingViaSMSAllowed(chkSMS);

        currentUser.setIsMarketingViaPostAllowed(chkPost);

		Set<Tag> listOfUser = new HashSet<>(tagService.getMailingListsContactSubscribed(currentUser));
		
		MailingListHelper.valueOf(tagService, selectedMailingLists, listOfUser, objectContext, currentUser, webSiteService.getCurrentCollege()).saveSubscriptions();

		objectContext.commitChanges();
	}

	public String getCollegeName() {
		return currentUser.getCollege().getName();
	}

	public boolean getHaveMailingLists() {
		return !mailingLists.isEmpty();
	}


	/**
	 * The method has been introduced to redirect users to login page when session expired
	 */
	public Object onException(Throwable cause) throws Throwable{
		if (mailingLists == null) {
			logger.warn("Persist properties have been cleared.", cause);
		} else {
			throw cause;
		}
		return loginPage;
	}

}
