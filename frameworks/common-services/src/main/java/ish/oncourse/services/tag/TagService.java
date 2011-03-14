package ish.oncourse.services.tag;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.services.BaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class TagService extends BaseService<Tag> implements ITagService {

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getSubjectsTag()
	 */
	@Override
	public Tag getSubjectsTag() {
		List<Tag> tags = findByQualifier(getSiteQualifier().andExp(
				ExpressionFactory.matchExp(Tag.NAME_PROPERTY, Tag.SUBJECTS_TAG_NAME)));
		return (tags.size() > 0) ? tags.get(0) : null;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getTagGroupByName(java.lang.String)
	 */
	@Override
	public Tag getTagGroupByName(String name) {
		List<Tag> tags = findByQualifier(getSiteQualifier().andExp(
				ExpressionFactory.matchExp(Tag.NAME_PROPERTY, name)).andExp(
				ExpressionFactory.matchExp(Tag.IS_TAG_GROUP_PROPERTY, true)));
		return (tags.size() > 0) ? tags.get(0) : null;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#loadByIds(java.lang.Object[])
	 */
	@Override
	public List<Tag> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		return findByQualifier(ExpressionFactory.inDbExp("id", ids).andExp(getSiteQualifier()));
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getTagsForEntity(java.lang.String,
	 *      java.lang.Long)
	 */
	@Override
	public List<Tag> getTagsForEntity(String entityName, Long entityId) {

		String pathSpec = Tag.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAGGABLE_PROPERTY;

		Expression qualifier = ExpressionFactory.matchExp(
				pathSpec + "." + Taggable.ENTITY_IDENTIFIER_PROPERTY, entityName).andExp(
				ExpressionFactory.matchExp(pathSpec + "." + Taggable.ENTITY_WILLOW_ID_PROPERTY,
						entityId));

		return findByQualifier(getSiteQualifier().andExp(qualifier));
	}

	/**
	 * 
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
		if (path.contains("+") || path.contains("|")) {
			for (int j = 0; j < tagNames.length; j++) {
				// rewrite url
				tagNames[j] = tagNames[j].replaceAll("[+]", " ").replaceAll("[|]", "/");
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
			if (subjectsTag.hasChildWithName(tagNames[0])) {
				rootTag = subjectsTag;
			} else {
				rootTag = getTagGroupByName(tagNames[0]);
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
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tag.ITagService#getEntityIdsByTagPath(java.lang.String,
	 *      java.lang.String)
	 */
	public List<Long> getEntityIdsByTagPath(String tagPath, String entityName) {
		List<Long> ids = new ArrayList<Long>();

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
	 * Qualifier which restricts the tags to belong the current site and be web
	 * visible.
	 * 
	 * @return
	 */
	private Expression getSiteQualifier() {
		College currentCollege = getWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY, currentCollege)
				.andExp(ExpressionFactory.matchExp(Tag.IS_WEB_VISIBLE_PROPERTY, true));
		return qualifier;
	}

}
