package ish.oncourse.model;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._Tag;

public class Tag extends _Tag {

	public List<Tag> getWebVisibleTags() {
		return ExpressionFactory.matchExp(IS_WEB_VISIBLE_PROPERTY, true)
				.filterObjects(getTags());
	}

	public boolean hasChildWithName(String name) {
		for (Tag tag : getWebVisibleTags()) {
			if (tag.getShortName().toUpperCase().equals(name.toUpperCase())
					|| tag.getName().toUpperCase().equals(name.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	public String getLink(String entityType) {
		String link = "";
		Tag subTag = this;
		while (subTag.getParent() != null) {
			String shortName = subTag.getShortName();
			String name = shortName != null ? shortName : subTag.getName();
			link = "/" + name.replaceAll(" ", "+").replaceAll("/", "|") + link;
			subTag = subTag.getParent();
		}
		if (entityType != null) {
			// TODO add the calculation of plural entity name for all the
			// taggable entities
			if (entityType.equals(Course.class.getSimpleName())) {
				entityType = "courses";
			}
		}
		if (entityType == null) {
			// Course is default entity type
			entityType = "courses";
		}
		link = "/" + entityType + link;
		return link;
	}
	
	public String getLink(){
		return getLink(Course.class.getSimpleName());
	}
}
