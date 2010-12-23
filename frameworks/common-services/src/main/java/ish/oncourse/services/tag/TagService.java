package ish.oncourse.services.tag;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TagService implements ITagService {
	
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	public Tag getRootTag() {
		// TODO get root tag not by harcoded name
		return getTag(Tag.NAME_PROPERTY, "Subjects");
	}
	
	public List<Tag> loadByIds(Object... ids) {
		
		if (ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(Tag.class);
		q.andQualifier(ExpressionFactory.inDbExp("id", ids));

		return cayenneService.sharedContext().performQuery(q);
	}
	
	private Expression getSiteQualifier() {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY,
				currentCollege).andExp(
				ExpressionFactory.matchExp(Tag.IS_WEB_VISIBLE_PROPERTY, true));
		return qualifier;
	}

	public Tag getTag(String searchProperty, Object value) {
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = getSiteQualifier();
		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					searchProperty, value));
		}
		SelectQuery query = new SelectQuery(Tag.class, qualifier);
		@SuppressWarnings("unchecked")
		List<Tag> listResult = sharedContext.performQuery(query);
		return !listResult.isEmpty() ? listResult.get(0) : null;
	}
	
	public List<Tag> getTags(String searchProperty, Object value) {
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = getSiteQualifier();
		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					searchProperty, value));
		}
		SelectQuery query = new SelectQuery(Tag.class, qualifier);
		return sharedContext.performQuery(query);
	}

	public Tag getSubTagByName(String name) {
		Tag rootTag = getRootTag();
		List<Tag> tags = getTags(Tag.NAME_PROPERTY, name);
		for(Tag tag:tags){
			Tag parent = tag;
			if(parent.equals(rootTag)){
				return tag;
			}
			while(parent.getParent()!=null){
				parent = parent.getParent();
				if(parent.equals(rootTag)){
					return tag;
				}
			}
		}
		return null;
	}

	public List<Long> getEntityIdsByTagName(String tagName, String entityName) {
		if (tagName == null || entityName == null) {
			return null;
		}

		List<Long>ids=new ArrayList<Long>();
		
		Tag tag = getSubTagByName(tagName);
		for(TaggableTag tt:tag.getTaggableTags()){
			Taggable taggable = tt.getTaggable();
			if(entityName.equals(taggable.getEntityIdentifier())){
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
		
		SelectQuery query = new SelectQuery(Tag.class, getSiteQualifier().andExp(qualifier));

		return cayenneService.sharedContext().performQuery(query);
	}

}
