package ish.oncourse.services.tag;

import ish.oncourse.function.IGet;
import ish.oncourse.model.*;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class TagService extends BaseService<Tag> implements ITagService {

	@Inject
	private Request request;

	private static final Logger logger = LogManager.getLogger();
	
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
		List<Tag> tags = findByQualifier(getSiteQualifier().andExp(Tag.NAME.eq(Tag.SUBJECTS_TAG_NAME)));
		return (tags.size() > 0) ? tags.get(0) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getTagGroupByName(java.lang.String)
	 */
	@Override
	public Tag getTagGroupByName(String name) {
		final List<Tag> tags = findByQualifier(getSiteQualifier().andExp(Tag.NAME.eq(name)).andExp(
				Tag.IS_TAG_GROUP.eq(Boolean.TRUE)));
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
		Expression qualifier = Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_IDENTIFIER).eq(entityName)
				.andExp(Tag.TAGGABLE_TAGS.dot(TaggableTag.TAGGABLE).dot(Taggable.ENTITY_WILLOW_ID).eq(entityId));
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
	 */
	private Expression getSiteQualifier() {
		College currentCollege = getWebSiteService().getCurrentCollege();
		return Tag.COLLEGE.eq(currentCollege).andExp(Tag.IS_WEB_VISIBLE.eq(true));
	}

	@Override
	public Tag getBrowseTag() {
		Long browseTagId = (Long) request.getAttribute(Tag.BROWSE_TAG_PARAM);
		return browseTagId == null ? null : findById(browseTagId);
	}

	@Override
	public List<Tag> getMailingLists() {
		return GetMailingLists.valueOf(getCayenneService().sharedContext(),
					null,
					getWebSiteService().getCurrentCollege())
				.get();
	}

	@Override
	public Set<Tag> getMailingListsContactSubscribed(Contact contact) {

		College currentCollege = getWebSiteService().getCurrentCollege();

		List<Taggable> taggableList = ObjectSelect.query(Taggable.class)
				.where(Taggable.ENTITY_IDENTIFIER.eq(Contact.class.getSimpleName()))
				.and(Taggable.ENTITY_WILLOW_ID.eq(contact.getId()))
				.and(Taggable.COLLEGE.eq(currentCollege))
				.prefetch(Taggable.TAGGABLE_TAGS.disjoint())
				.select(getCayenneService().sharedContext());


		Set<Tag> allMailingLists = new HashSet<>(getMailingLists());
		Set<Tag> tags = new HashSet<>();

		for (final Taggable t : taggableList) {
			for (final TaggableTag tg : t.getTaggableTags()) {
				Tag tag = tg.getTag();
				if (allMailingLists.stream().anyMatch(m -> m.getId().equals(tag.getId()))) {
					if(!tags.contains(tag)) {
						tags.add(tag);
					} else {
						logger.error("Contact willowId:{} has more than one relation to MailingList (Tag) willowId:{}", contact.getId(), tag.getId());
					}
				}
			}
		}

		return tags;
	}
	
	public void subscribeContactToMailingList(Contact contact, Tag mailingList) {
		if (isContactSubscribedToMailingList(contact, mailingList))
			return;
		ObjectContext context = getCayenneService().sharedContext();
		SubscribeToMailingList.valueOf(context, contact, mailingList).subscribe();
		context.commitChanges();
	}
	
	public void unsubscribeContactFromMailingList(Contact contact, Tag mailingList) {
		ObjectContext context = getCayenneService().sharedContext();

		TaggablesSupporter supporter = new TaggablesSupporter(context);
		List<Taggable> taggables = supporter.load(contact, mailingList);
		supporter.delete(taggables);
	}

	@Override
	public boolean isContactSubscribedToMailingList(Contact contact, Tag mailingList) {
		ObjectContext context = getCayenneService().sharedContext();
		TaggablesSupporter supporter = new TaggablesSupporter(context);
		List<Taggable> taggables = supporter.load(contact, mailingList);
		return taggables.size() > 0;
	}

	public <E extends Queueable> boolean hasTag(final E entity, final String tagPath) {
		IGet<Tag> getTag = new IGet<Tag>() {
			@Override
			public Tag get() {
				return TagService.this.getTagByFullPath(tagPath);
			}
		};

		IGet<E> getEntity = new IGet<E>() {
			@Override
			public E get() {
				return entity;
			}
		};

		EntityHasTag<E> hasTag = new EntityHasTag<>(getEntity, getTag);
		return hasTag.get();
	}

	private class TaggablesSupporter
	{
		private ObjectContext context;

		private TaggablesSupporter(ObjectContext context) {
			this.context = context;
		}

		public List<Taggable> load(Contact contact, Tag mailingList) {
			contact = context.localObject(contact);
			mailingList = context.localObject(mailingList);
			return ObjectSelect.query(Taggable.class).where(Taggable.ENTITY_IDENTIFIER.eq(Contact.class.getSimpleName()))
					.and(Taggable.ENTITY_WILLOW_ID.eq(contact.getId()))
					.and(Taggable.COLLEGE.eq(contact.getCollege()))
					.and(Taggable.TAGGABLE_TAGS.dot(TaggableTag.TAG).eq(mailingList)).select(context);
		}

		public void delete(List<Taggable> taggables)
		{
			for (Taggable t : new ArrayList<>(taggables)) {
				for (final TaggableTag tt : new ArrayList<>(t.getTaggableTags())) {
					context.deleteObject(tt);
				}
				context.deleteObjects(t);
			}
			context.commitChanges();
		}
	}
}
