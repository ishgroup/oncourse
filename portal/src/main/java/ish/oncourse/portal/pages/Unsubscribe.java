package ish.oncourse.portal.pages;

import java.util.ArrayList;
import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.tag.ITagService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Unsubscribe {
	
	private static final String PARAM_DELIMETER = "-";
	
	@Property
	@Persist
	private Tag mailingList;
	
	@Property
	@Persist
	private Contact contact;
	
	@Property
	private boolean isSubscribed;
	
	@Property
	@Persist
	private boolean postUnsubscribe;
	
	@Inject
	private ITagService tagService;
	
	@Inject
	private IContactService contactService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	Object onActivate(String param) {
		try {
			String mailingListId = param.substring(0, param.indexOf(PARAM_DELIMETER));
			String contactUniqueCode = param.substring(param.indexOf(PARAM_DELIMETER) + 1);
			
			ObjectContext context = cayenneService.sharedContext();
			
			this.mailingList = Cayenne.objectForPK(context, Tag.class, Long.parseLong(mailingListId));
			this.contact = contactService.findByUniqueCode(contactUniqueCode);
		
			return null;
		} catch (Exception e) {
			return pageNotFound;
		}
	}
	
	@SetupRender
	Object setupRender() {
		if (this.contact != null && this.mailingList != null) {
			this.isSubscribed = isContactSubscribed(contact, mailingList);
			return null;
		}
		return pageNotFound;
	}
	
	@AfterRender
	void afterRender() {
		postUnsubscribe = false;
	}
	
	@OnEvent(component="unsubscribeForm", value="success")
	Object submitted() {
		ObjectContext context = cayenneService.newContext();
		College college = (College) context.localObject(contact.getCollege().getObjectId(), null);
		
		Expression qual = ExpressionFactory.matchExp(Taggable.ENTITY_IDENTIFIER_PROPERTY, Contact.class.getSimpleName())
				.andExp(ExpressionFactory.matchExp(Taggable.ENTITY_WILLOW_ID_PROPERTY, contact.getId()))
				.andExp(ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, college))
				.andExp(ExpressionFactory.matchExp(Taggable.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAG_PROPERTY, mailingList));
		SelectQuery query = new SelectQuery(Taggable.class, qual);
		List<Taggable> taggables = context.performQuery(query);
		
		for (Taggable t : new ArrayList<Taggable>(taggables)) {
			for (final TaggableTag tt : new ArrayList<TaggableTag>(t.getTaggableTags())) {
				context.deleteObject(tt);
				context.deleteObject(t);
			}
		}
		
		context.commitChanges();
		postUnsubscribe = true;
		return null;
	}

	private boolean isContactSubscribed(Contact contact, Tag mailingList) {
		ObjectContext context = cayenneService.sharedContext();
		College currentCollege = contact.getCollege();

		Expression qual = ExpressionFactory.matchExp(Taggable.ENTITY_IDENTIFIER_PROPERTY, Contact.class.getSimpleName())
				.andExp(ExpressionFactory.matchExp(Taggable.ENTITY_WILLOW_ID_PROPERTY, contact.getId()))
				.andExp(ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, currentCollege))
				.andExp(ExpressionFactory.matchExp(Taggable.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAG_PROPERTY, mailingList));

		SelectQuery q = new SelectQuery(Taggable.class, qual);
		q.addPrefetch(Taggable.TAGGABLE_TAGS_PROPERTY);
		List<Taggable> taggableList = context.performQuery(q);

		return !taggableList.isEmpty();
	}
}
