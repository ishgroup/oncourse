package ish.oncourse.services.sites;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.Site;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

public class SitesService implements ISitesService {
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@SuppressWarnings("unchecked")
	public Site getSite(String searchProperty, Object value) {
		SelectQuery q = new SelectQuery(Site.class);
		q.andQualifier(getSiteQualifier().andExp(getAvailabilityQualifier()));
		if (searchProperty != null) {
			q.andQualifier(ExpressionFactory.matchExp(searchProperty, value));
		}
		List<Site> result = cayenneService.sharedContext().performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Site.COLLEGE_PROPERTY, webSiteService
				.getCurrentCollege());
	}

	/**
	 * @return
	 */
	private Expression getAvailabilityQualifier() {
		return ExpressionFactory.matchExp(Site.IS_WEB_VISIBLE_PROPERTY, true);
	}
}
