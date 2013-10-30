package ish.oncourse.services.tag;

import ish.common.types.NodeSpecialType;
import ish.oncourse.model.*;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class TagService extends BaseService<Tag> implements ITagService {

	@Inject
	private Request request;

	public TagService(ICayenneService cayenneService, IWebSiteService webSiteService) {
		super(cayenneService, webSiteService);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getSubjectsTag()
	 */
	@Override
	public Tag getSubjectsTag() {
		List<Tag> tags = findByQualifier(getSiteQualifier().andExp(ExpressionFactory.matchExp(Tag.NAME_PROPERTY, Tag.SUBJECTS_TAG_NAME)));
		return (tags.size() > 0) ? tags.get(0) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getTagGroupByName(java.lang.String)
	 */
	@Override
	public Tag getTagGroupByName(String name) {
		final List<Tag> tags = findByQualifier(getSiteQualifier().andExp(ExpressionFactory.matchExp(Tag.NAME_PROPERTY, name)).andExp(
			ExpressionFactory.matchExp(Tag.IS_TAG_GROUP_PROPERTY, true)));
		return (tags.size() > 0) ? tags.get(0) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#loadByIds(java.lang.Object[])
	 */
	@Override
	public List<Tag> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		return findByQualifier(ExpressionFactory.inDbExp("id", ids));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getTagsForEntity(java.lang.String, java.lang.Long)
	 */
	@Override
	public List<Tag> getTagsForEntity(String entityName, Long entityId) {

		String pathSpec = Tag.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAGGABLE_PROPERTY;

		Expression qualifier = ExpressionFactory.matchExp(pathSpec + "." + Taggable.ENTITY_IDENTIFIER_PROPERTY, entityName).andExp(
				ExpressionFactory.matchExp(pathSpec + "." + Taggable.ENTITY_WILLOW_ID_PROPERTY, entityId));

		return findByQualifier(getSiteQualifier().andExp(qualifier));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getTagByFullPath(java.lang.String)
	 */
	@Override
	public Tag getTagByFullPath(String path) {
		if (path == null) {
			return null;
		}
		if (path.startsWith("/")) {
			path = path.replaceFirst("/", "");
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.equals("")) {
			return null;
		}
		String tagNames[] = path.split("/");
		/*
		 * if (path.contains("+") || path.contains("|")) { for (int j = 0; j < tagNames.length; j++) { // rewrite url // FIXME setup web
		 * container and httpd correctly to prevent them // from decoding URI in a way of changing "%2B" to "+", not to // " " tagNames[j] =
		 * tagNames[j].replaceAll("[_][+]", " ").replaceAll("[|]", "/"); } }
		 */
		for (int j = 0; j < tagNames.length; j++) {
			try {
				tagNames[j] = URLDecoder.decode(tagNames[j], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Tag rootTag = null;
		Tag subjectsTag = getSubjectsTag();
		int i = 0;
		if (tagNames[0].equalsIgnoreCase(Tag.SUBJECTS_TAG_NAME)) {
			rootTag = subjectsTag;
			// don't process subjects tag in loop
			i = 1;
		}
		if (rootTag == null) {
			if (subjectsTag != null && subjectsTag.hasChildWithName(tagNames[0])) {
				rootTag = subjectsTag;
			} else {
				rootTag = getTagGroupByName(tagNames[0]);
				//don't need to process tag group if we have it
				i = 1;
			}
		}

		if (rootTag == null) {
			return null;
		}
		for (; i < tagNames.length; i++) {
			Tag tag = rootTag.getChildWithName(tagNames[i]);
			if (tag == null) {
				return null;
			} else {
				rootTag = tag;
			}

		}

		return rootTag;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getEntityIdsByTagPath(java.lang.String, java.lang.String)
	 */
	public List<Long> getEntityIdsByTagPath(String tagPath, String entityName) {
		List<Long> ids = new ArrayList<>();

		Tag tag = getTagByFullPath(tagPath);

		if (tag == null) {
			return ids;
		}
		for (TaggableTag tt : tag.getTaggableTags()) {
			Taggable taggable = tt.getTaggable();
			if (entityName.equals(taggable.getEntityIdentifier())) {
				ids.add(taggable.getEntityWillowId());
			}
		}
		return ids;
	}

	/**
	 * Qualifier which restricts the tags to belong the current site and be web visible.
	 * 
	 * @return
	 */
	private Expression getSiteQualifier() {
		College currentCollege = getWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY, currentCollege).andExp(
				ExpressionFactory.matchExp(Tag.IS_WEB_VISIBLE_PROPERTY, true));
		return qualifier;
	}

	@Override
	public Tag getBrowseTag() {
		Long browseTagId = (Long) request.getAttribute(Tag.BROWSE_TAG_PARAM);
		return browseTagId == null ? null : findById(browseTagId);
	}

	@SuppressWarnings("unchecked")
	public List<Tag> getMailingLists() {

		List<Tag> tags = Collections.emptyList();

		// MAILING_LISTS(3, "Mailing lists") - see NodeSpecialType
		Expression qual = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY, getWebSiteService().getCurrentCollege()).andExp(
				ExpressionFactory.matchExp(Tag.SPECIAL_TYPE_PROPERTY, NodeSpecialType.MAILING_LISTS));
		qual = qual.andExp(ExpressionFactory.matchExp(Tag.PARENT_PROPERTY, null));
		SelectQuery q = new SelectQuery(Tag.class, qual);

		Tag parent = (Tag) Cayenne.objectForQuery(getCayenneService().sharedContext(), q);
		if (parent != null) {
			Expression childQual = getSiteQualifier().andExp(ExpressionFactory.matchExp(Tag.PARENT_PROPERTY, parent));
			q = new SelectQuery(Tag.class, childQual);
			tags = getCayenneService().sharedContext().performQuery(q);
		}

		return tags;
	}

	public List<Tag> getMailingListsContactSubscribed(Contact contact) {

		College currentCollege = getWebSiteService().getCurrentCollege();

		Expression qual = ExpressionFactory.matchExp(Taggable.ENTITY_IDENTIFIER_PROPERTY, Contact.class.getSimpleName())
				.andExp(ExpressionFactory.matchExp(Taggable.ENTITY_WILLOW_ID_PROPERTY, contact.getId()))
				.andExp(ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, currentCollege));

		SelectQuery q = new SelectQuery(Taggable.class, qual);
		q.addPrefetch(Taggable.TAGGABLE_TAGS_PROPERTY);
		@SuppressWarnings("unchecked")
		List<Taggable> taggableList = getCayenneService().sharedContext().performQuery(q);

		Set<Tag> allMailingLists = new HashSet<>(getMailingLists());
		List<Tag> tags = new ArrayList<>();

		for (final Taggable t : taggableList) {
			for (final TaggableTag tg : t.getTaggableTags()) {
				Tag tag = tg.getTag();
				if (allMailingLists.contains(tag)) {
					tags.add(tag);
				}
			}
		}

		return tags;
	}
	
	public void subscribeContactToMailingList(Contact contact, Tag mailingList) {
		ObjectContext context = getCayenneService().newContext();
		
		Date date = new Date();
		
		College college = context.localObject(contact.getCollege());
		
		Tag list = context.localObject(mailingList);
		
		Taggable taggable = context.newObject(Taggable.class);
		taggable.setCollege(college);
		taggable.setCreated(date);
		taggable.setModified(date);
		taggable.setEntityIdentifier(Contact.class.getSimpleName());
		taggable.setEntityWillowId(contact.getId());
		taggable.setEntityAngelId(contact.getAngelId());
		
		TaggableTag taggableTag = context.newObject(TaggableTag.class);
		taggableTag.setCollege(college);
		taggableTag.setTag(list);
		taggable.addToTaggableTags(taggableTag);
		
		context.commitChanges();
	}
	
	public void unsubscribeContactFromMailingList(Contact contact, Tag mailingList) {
		ObjectContext context = getCayenneService().newContext();
		College college = (College) context.localObject(contact.getCollege().getObjectId(), null);
		
		Expression qual = ExpressionFactory.matchExp(Taggable.ENTITY_IDENTIFIER_PROPERTY, Contact.class.getSimpleName())
				.andExp(ExpressionFactory.matchExp(Taggable.ENTITY_WILLOW_ID_PROPERTY, contact.getId()))
				.andExp(ExpressionFactory.matchExp(Taggable.COLLEGE_PROPERTY, college))
				.andExp(ExpressionFactory.matchExp(Taggable.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAG_PROPERTY, mailingList));
		SelectQuery query = new SelectQuery(Taggable.class, qual);
		@SuppressWarnings("unchecked")
		List<Taggable> taggables = context.performQuery(query);
		
		for (Taggable t : new ArrayList<>(taggables)) {
			for (final TaggableTag tt : new ArrayList<>(t.getTaggableTags())) {
				context.deleteObject(tt);
				context.deleteObject(t);
			}
		}
		
		context.commitChanges();
	}
}
