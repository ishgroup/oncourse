package ish.oncourse.portal.components.subscriptions;

import ish.oncourse.model.*;
import ish.oncourse.portal.pages.Login;
import ish.oncourse.portal.pages.Subscriptions;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.MailingListHelper;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.*;
import java.util.stream.Collectors;

public class MailingLists {

	private static final Logger logger = LogManager.getLogger();

    @Property
    private boolean chkEmail;

    @Property
    private boolean chkSMS;

    @Property
    private boolean chkPost;

	@Property
	private Contact currentUser;

	private List<Long> selectedMailingLists;

	@Property
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

	@Inject
	private Request request;

	@InjectPage
	private Login loginPage;
	
	public static final String ITEM_ID_PATTERN = "mailingList_";
	
	@SetupRender
	void beforeRender() {
        ObjectContext objectContext = cayenneService.newContext();
		this.currentUser = objectContext.localObject(portalService.getContact());
		this.mailingLists = tagService.getMailingLists();
        this.chkPost = this.currentUser.getIsMarketingViaPostAllowed();
        this.chkSMS = this.currentUser.getIsMarketingViaSMSAllowed();
        this.chkEmail = this.currentUser.getIsMarketingViaEmailAllowed();
		initSelectedLists();
	}

	public boolean isSelected() {
		return selectedMailingLists.contains(mailingLists.get(index).getId());
	}

	public Long getId() {
		return mailingLists.get(index).getId();
	}

	public String getName() {
		return mailingLists.get(index).getName();
	}

	public String getDetail() {
		return mailingLists.get(index).getDetail();
	}

	private void initSelectedLists() {
		Set<Tag> listOfUser = tagService.getMailingListsContactSubscribed(currentUser);
		selectedMailingLists =  new ArrayList<>();
		for (Tag t : listOfUser) {
			selectedMailingLists.add(t.getId());
		}
	}
	
	@OnEvent(value = "saveMailingListForm")
	public void saveMailingListForm() {
		ObjectContext objectContext = cayenneService.newContext();
		this.currentUser = objectContext.localObject(portalService.getContact());
		
        currentUser.setIsMarketingViaEmailAllowed(request.getParameter("chkEmail") != null);
        currentUser.setIsMarketingViaSMSAllowed(request.getParameter("chkSMS") != null);
        currentUser.setIsMarketingViaPostAllowed(request.getParameter("chkPost") != null);
        
		
		Set<Tag> listOfUser = new HashSet<>(tagService.getMailingListsContactSubscribed(currentUser));

		selectedMailingLists = request.getParameterNames().stream()
				.filter(s -> s.startsWith(ITEM_ID_PATTERN))
				.map(s -> Long.valueOf(s.replace(ITEM_ID_PATTERN, StringUtils.EMPTY)))
				.collect(Collectors.toList());
		
		
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
