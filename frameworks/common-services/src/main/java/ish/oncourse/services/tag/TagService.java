package ish.oncourse.services.tag;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

public class TagService implements ITagService {
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	public Tag getRootTag() {
		// TODO get root tag not by harcoded name
		return getTag(Tag.NAME_PROPERTY, "Subjects");
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
}
