package ish.oncourse.model;

import ish.oncourse.model.auto._Tag;
import ish.oncourse.utils.TagsTextileEntityTypes;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;

public class Tag extends _Tag {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public List<Tag> getWebVisibleTags() {
		return ExpressionFactory.matchExp(IS_WEB_VISIBLE_PROPERTY, true)
				.filterObjects(getTags());
	}

	public boolean hasChildWithName(String name) {
		for (Tag tag : getWebVisibleTags()) {
			if (((tag.getShortName() != null) && tag.getShortName()
					.toUpperCase().equals(name.toUpperCase()))
					|| ((tag.getName() != null) && tag.getName().toUpperCase()
							.equals(name.toUpperCase()))) {
				return true;
			}
		}
		return false;
	}

	public String getLink(String entityType) {
		if (entityType != null) {
			TagsTextileEntityTypes type = TagsTextileEntityTypes
					.valueOf(entityType);
			// TODO add the calculation of plural entity name for all the
			// taggable entities
			switch (type) {
			case Course:
				entityType = "courses";
				break;
			}
		}
		if (entityType == null) {
			// Course is default entity type
			entityType = "courses";
		}
		String link = "/" + entityType + getDefaultPath();
		return link;
	}

	public String getLink() {
		return getLink(Course.class.getSimpleName());
	}

	public String getDefaultPath() {
		String path = "";
		Tag subTag = this;
		while (subTag.getParent() != null) {
			String shortName = subTag.getShortName();
			String name = shortName != null ? shortName : subTag.getName();
			path = "/" + name.replaceAll(" ", "+").replaceAll("/", "|") + path;
			subTag = subTag.getParent();
		}

		return path;
	}

	/**
	 * @return the ancestor tag that has no grandparent, that is the level 2 tag
	 *         for this tag. As an example, /subjects/sport/swimming would
	 *         return sport
	 */
	public Tag getLevel2Ancestor() {
		Tag aTag = this;
		if (hasParentTag() && !getParent().hasParentTag()) {
			return this; // level 2 tag should return itself
		}
		while (aTag.getParent() != null) {
			if (!aTag.getParent().hasParentTag()) {
				return aTag;
			}
			aTag = aTag.getParent();
		}
		return null;
	}

	public boolean hasParentTag() {
		return getParent() != null;
	}
}
