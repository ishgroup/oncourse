package ish.oncourse.model;

import ish.oncourse.model.auto._Tag;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.TagsTextileEntityTypes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;

public class Tag extends _Tag implements Queueable {
	private static final long serialVersionUID = -1118193158649089145L;
	public static final String SUBJECTS_TAG_NAME = "Subjects";
	public static final String BROWSE_TAG_PARAM = "browseTag";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@SuppressWarnings("unchecked")
	public List<Tag> getWebVisibleTags() {
		List<Tag> visibleTags;
		
		if (getObjectId().isTemporary()) {
			visibleTags = ExpressionFactory.matchExp(IS_WEB_VISIBLE_PROPERTY, true).filterObjects(getTags());
		}
		else {
			SelectQuery  q = new SelectQuery(Tag.class);
			q.andQualifier(ExpressionFactory.matchExp(Tag.PARENT_PROPERTY, this));
			q.andQualifier(ExpressionFactory.matchExp(IS_WEB_VISIBLE_PROPERTY, true));
			q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			visibleTags = getObjectContext().performQuery(q);
		}

		Collections.sort(visibleTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				int result = tag1.getWeighting() - tag2.getWeighting();
				if (result != 0) {
					return result;
				}
				return tag1.getName().compareToIgnoreCase(tag2.getName());
			}
		});

		return visibleTags;
	}

	public boolean hasChildWithName(String name) {
		return getChildWithName(name) != null;
	}

	public Tag getChildWithName(String name) {
		for (Tag tag : getWebVisibleTags()) {
			if (((tag.getShortName() != null) && tag.getShortName().equalsIgnoreCase(name))
					|| ((tag.getName() != null) && tag.getName().equalsIgnoreCase(name))) {
				return tag;
			}
		}

		return null;
	}

	public String getLink(String entityType) {
		if (entityType != null) {
			TagsTextileEntityTypes type = TagsTextileEntityTypes.valueOf(entityType);
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
		while (subTag != null) {
			String shortName = subTag.getShortName();
			String name = shortName != null ? shortName : subTag.getName();
			// rewrite for url
			try {
				// FIXME setup web container and httpd correctly to prevent them
				// from decoding URI in a way of changing "%2B" to "+", not to
				// " "
				path = "/" + URLEncoder.encode(name, "UTF-8") + path;
			} catch (UnsupportedEncodingException e) {
				path = "/";
			}
			Tag parent = subTag.getParent();
			// hide "Subjects" from url
			if (parent == null || parent.getName().equalsIgnoreCase(SUBJECTS_TAG_NAME)) {
				subTag = null;
			} else {
				subTag = parent;
			}
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

	public Tag getRoot() {
		Tag result = this;

		while (result.getParent() != null) {
			result = result.getParent();
		}
		return result;
	}

	public List<Tag> getAllWebVisibleChildren() {
		List<Tag> children = new ArrayList<Tag>();
		addAllWebVisibleChildren(children, this);
		return children;
	}

	private void addAllWebVisibleChildren(List<Tag> children, Tag tag) {
		for (Tag t : tag.getWebVisibleTags()) {
			children.add(t);
			addAllWebVisibleChildren(children, t);
		}
	}

	public boolean isParentOf(Tag tag) {
		return isParentOf(this, tag);
	}

	private static boolean isParentOf(Tag parent, Tag tag) {

		boolean isParent = false;

		for (Tag child : parent.getTags()) {
			if (child.getId().equals(tag.getId())) {
				isParent = true;
			} else {
				isParent = isParentOf(child, tag);
			}

			if (isParent) {
				break;
			}
		}

		return isParent;
	}

	public boolean isHasDetails() {
		String detail = getDetail();
		return detail != null && !"".equals(detail);
	}
}
