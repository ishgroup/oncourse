package ish.oncourse.portal.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.AbstractOptionModel;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.services.StringValueEncoder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.AbstractSelectModel;

public class MailingLists {

	@Inject
	private IAuthenticationService authService;

	@Inject
	private ITagService tagService;

	@Property
	@Persist
	private Contact currentUser;

	@Persist
	@Property
	private List<String> selectedMailingLists;

	@Persist
	private List<Tag> mailingLists;

	@Persist
	@Property
	private SelectModel mailingListModel;

	@Persist
	@Property
	private ValueEncoder<String> mailingListEncoder;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Property
	@Persist
	private boolean isSaved;

	@SetupRender
	void beforeRender() {
		this.currentUser = authService.getUser();
		this.mailingLists = tagService.getMailingLists();
		this.selectedMailingLists = new ArrayList<String>();
		this.mailingListModel = new MailingListSelectModel(this.mailingLists);
		this.mailingListEncoder = new StringValueEncoder();
		initSelectedLists();
	}
	
	@AfterRender
	void afterRender() {
		if (isSaved) {
			this.isSaved = false;
		}
	}

	private void initSelectedLists() {
		List<Tag> listOfUser = tagService.getMailingListsContactSubscribed(currentUser);
		for (Tag t : listOfUser) {
			selectedMailingLists.add(t.getId().toString());
		}
	}

	public void onSubmitFromMailingListForm() {
		this.isSaved = true;
		
		Set<Tag> listOfUser = new HashSet<Tag>(tagService.getMailingListsContactSubscribed(currentUser));
		ObjectContext objectContext = cayenneService.newContext();

		for (String id : selectedMailingLists) {
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

	/**
	 * Select model for mailing lists
	 * 
	 * @author anton
	 */
	private static class MailingListSelectModel extends AbstractSelectModel {

		private List<Tag> mailingLists;

		public MailingListSelectModel(List<Tag> mailingLists) {
			super();
			this.mailingLists = mailingLists;
		}

		@Override
		public List<OptionModel> getOptions() {
			List<OptionModel> options = new ArrayList<OptionModel>(mailingLists.size());

			for (Tag t : mailingLists) {
				final Tag mailingList = t;
				OptionModel option = new AbstractOptionModel() {
					@Override
					public String getLabel() {
						return mailingList.getName();
					}

					@Override
					public Object getValue() {
						return mailingList.getId().toString();
					}
				};
				options.add(option);
			}

			return options;
		}

		@Override
		public List<OptionGroupModel> getOptionGroups() {
			return null;
		}
	}

	public String getCollegeName() {
		return currentUser.getCollege().getName();
	}

	public boolean getHaveMailingLists() {
		return !mailingLists.isEmpty();
	}
}
