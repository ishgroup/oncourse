package ish.oncourse.services.sites;

import ish.oncourse.model.Site;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

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
		q.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		q.setCacheGroup(Site.class.getSimpleName());
		List<Site> result = cayenneService.newContext().performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Site.COLLEGE_PROPERTY, webSiteService.getCurrentCollege());
	}

	/**
	 * @return
	 */
	private Expression getAvailabilityQualifier() {
		return ExpressionFactory.matchExp(Site.IS_WEB_VISIBLE_PROPERTY, true);
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService
				.newContext()
				.performQuery(
						new EJBQLQuery("select max(s.modified) from Site s where "
								+ getSiteQualifier().andExp(getAvailabilityQualifier()).toEJBQL("s"))).get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Site> loadByIds(List<Long> ids) {
		SelectQuery q = new SelectQuery(Site.class, ExpressionFactory.inDbExp(Site.ID_PK_COLUMN, ids));
		q.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		q.setCacheGroup(Site.class.getSimpleName());
		q.andQualifier(getSiteQualifier().andExp(getAvailabilityQualifier()));
		return cayenneService.newContext().performQuery(q);
	}
}
