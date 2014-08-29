package ish.oncourse.portal.components.subscriptions;

import ish.oncourse.model.*;
import ish.oncourse.portal.pages.Login;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.*;

public class MailingLists {

	private static final Logger LOGGER = Logger.getLogger(MailingLists.class);



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
		List<Tag> listOfUser = tagService.getMailingListsContactSubscribed(currentUser);
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

		Set<Tag> listOfUser = new HashSet<>(tagService.getMailingListsContactSubscribed(currentUser));

        currentUser.setIsMarketingViaEmailAllowed(chkEmail);

        currentUser.setIsMarketingViaSMSAllowed(chkSMS);

        currentUser.setIsMarketingViaPostAllowed(chkPost);

		for (Long id : selectedMailingLists) {
			List<Tag> tagList = tagService.loadByIds(id);
			if (!tagList.isEmpty()) {
				Tag tag = tagList.get(0);
				if (!listOfUser.contains(tag)) {
					Tag local = objectContext.localObject(tag);
					Taggable taggable = objectContext.newObject(Taggable.class);
					taggable.setCollege(local.getCollege());
					Date date = new Date();
					taggable.setCreated(date);
					taggable.setModified(date);
					taggable.setEntityIdentifier(Contact.class.getSimpleName());
					taggable.setEntityWillowId(currentUser.getId());
					taggable.setEntityAngelId(currentUser.getAngelId());

					TaggableTag taggableTag = objectContext.newObject(TaggableTag.class);
					taggableTag.setTag(local);
					taggableTag.setCollege(local.getCollege());
					taggable.addToTaggableTags(taggableTag);
				} else {
					listOfUser.remove(tag);
				}
			}
		}

		if (!listOfUser.isEmpty()) {
			for (Tag tag : new ArrayList<>(listOfUser)) {
				College currentCollege = webSiteService.getCurrentCollege();
				Expression qual = ExpressionFactory.matchExp(Taggable.ENTITY_IDENTIFIER_PROPERTY, Contact.class.getSimpleName())
						.andExp(ExpressionFactory.matchExp(Taggable.ENTITY_WILLOW_ID_PROPERTY, currentUser.getId()))
						.andExp(ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, currentCollege));
				qual = qual.andExp(ExpressionFactory.matchExp(Taggable.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAG_PROPERTY, tag));

				SelectQuery q = new SelectQuery(Taggable.class, qual);
				List<Taggable> taggableList = objectContext.performQuery(q);

				for (Taggable t : new ArrayList<>(taggableList)) {
					for (final TaggableTag tg : new ArrayList<>(t.getTaggableTags())) {
						objectContext.deleteObjects(tg);
						objectContext.deleteObjects(t);
					}
				}
			}
		}

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
			LOGGER.warn("Persist properties have been cleared.", cause);
		} else {
			throw cause;
		}
		return loginPage;
	}

}
