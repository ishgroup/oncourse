package ish.oncourse.model;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._Tag;

public class Tag extends _Tag {

	public List<Tag> getWebVisibleTags() {
		return ExpressionFactory.matchExp(IS_WEB_VISIBLE_PROPERTY, true)
				.filterObjects(getTags());
	}
	
}
