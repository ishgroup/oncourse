package ish.oncourse.portal.pages;

import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

public class MailingLists {

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
	private IAuthenticationService authService;

	@Inject
	private ITagService tagService;

	@Property
	@Persist
	private boolean isSaved;

	@SetupRender
	void beforeRender() {
		this.currentUser = authService.getUser();
		this.mailingLists = tagService.getMailingLists();
		this.selectedMailingLists = new ArrayList<Long>();
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
		this.isSaved = true;
		
		Set<Tag> listOfUser = new HashSet<Tag>(tagService.getMailingListsContactSubscribed(currentUser));
		ObjectContext objectContext = cayenneService.newContext();

		for (Long id : selectedMailingLists) {
			List<Tag> tagList = tagService.loadByIds(id);
			if (!tagList.isEmpty()) {
				Tag tag = tagList.get(0);
				if (!listOfUser.contains(tag)) {
					Tag local = (Tag) objectContext.localObject(tag.getObjectId(), null);
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
			for (Tag tag : new ArrayList<Tag>(listOfUser)) {
				College currentCollege = webSiteService.getCurrentCollege();
				Expression qual = ExpressionFactory.matchExp(Taggable.ENTITY_IDENTIFIER_PROPERTY, Contact.class.getSimpleName())
						.andExp(ExpressionFactory.matchExp(Taggable.ENTITY_WILLOW_ID_PROPERTY, currentUser.getId()))
						.andExp(ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, currentCollege));
				qual = qual.andExp(ExpressionFactory.matchExp(Taggable.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAG_PROPERTY, tag));

				SelectQuery q = new SelectQuery(Taggable.class, qual);
				List<Taggable> taggableList = objectContext.performQuery(q);

				for (Taggable t : new ArrayList<Taggable>(taggableList)) {
					for (final TaggableTag tg : new ArrayList<TaggableTag>(t.getTaggableTags())) {
						objectContext.deleteObject(tg);
						objectContext.deleteObject(t);
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
}
