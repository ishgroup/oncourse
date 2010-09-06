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
		ObjectContext sharedContext = cayenneService.sharedContext();
		// TODO get root tag not by harcoded name
		Expression qualifier = getSiteQualifier().andExp(
				ExpressionFactory.matchExp(Tag.NAME_PROPERTY, "Subjects"));
		SelectQuery query = new SelectQuery(Tag.class, qualifier);
		@SuppressWarnings("unchecked")
		List<Tag> listResult = sharedContext.performQuery(query);
		return !listResult.isEmpty() ? listResult.get(0) : null;
	}

	private Expression getSiteQualifier() {
		College currentCollege = webSiteService.getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY,
				currentCollege);
		return qualifier;
	}
}
