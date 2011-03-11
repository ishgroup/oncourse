package ish.oncourse.services.tag;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.services.BaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public class TagService extends BaseService<Tag> implements ITagService {

	public Tag getRootTag() {
		List<Tag> tags = findByQualifier(getSiteQualifier().andExp(
				ExpressionFactory.matchExp(Tag.NAME_PROPERTY, "Subjects")));
		return (tags.size() > 0) ? tags.get(0) : null;
	}

	public List<Tag> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		return findByQualifier(ExpressionFactory.inDbExp("id", ids));
	}

	private Expression getSiteQualifier() {
		College currentCollege = getWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY, currentCollege)
				.andExp(ExpressionFactory.matchExp(Tag.IS_WEB_VISIBLE_PROPERTY, true));
		return qualifier;
	}

	public Tag getSubTagByName(String name) {
		Tag rootTag = getRootTag();

		List<Tag> tags = findByQualifier(getSiteQualifier().andExp(
				ExpressionFactory.matchExp(Tag.NAME_PROPERTY, name)));

		for (Tag tag : tags) {
			Tag parent = tag;
			if (parent.equals(rootTag)) {
				return tag;
			}
			while (parent.getParent() != null) {
				parent = parent.getParent();
				if (parent.equals(rootTag)) {
					return tag;
				}
			}
		}
		return null;
	}

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

	public List<Tag> getTagsForEntity(String entityName, Long entityId) {

		String pathSpec = Tag.TAGGABLE_TAGS_PROPERTY + "." + TaggableTag.TAGGABLE_PROPERTY;

		Expression qualifier = ExpressionFactory.matchExp(
				pathSpec + "." + Taggable.ENTITY_IDENTIFIER_PROPERTY, entityName).andExp(
				ExpressionFactory.matchExp(pathSpec + "." + Taggable.ENTITY_WILLOW_ID_PROPERTY,
						entityId));

		return findByQualifier(getSiteQualifier().andExp(qualifier));
	}

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
		Expression qualifier = getSiteQualifier();
		for (int i = 0; i < tagNames.length; i++) {
			StringBuffer prop = new StringBuffer();
			for (int j = tagNames.length - i - 1; j > 0; j--) {
				prop.append(Tag.PARENT_PROPERTY).append(".");
			}
			prop.append(Tag.NAME_PROPERTY);
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(prop.toString(), tagNames[i]));
		}

		SelectQuery query = new SelectQuery(Tag.class, qualifier);
		return (Tag) DataObjectUtils.objectForQuery(getCayenneService().sharedContext(), query);
	}

}
