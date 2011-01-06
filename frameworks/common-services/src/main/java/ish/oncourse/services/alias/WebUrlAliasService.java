package ish.oncourse.services.alias;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebUrlAliasService extends BaseService<WebUrlAlias> implements
		IWebUrlAliasService {

	private static final Logger LOGGER = Logger.getLogger(WebUrlAliasService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public WebUrlAlias getAliasByPath(String path) {
		Expression pathExp = ExpressionFactory.matchExp(
				WebUrlAlias.URL_PATH_PROPERTY, path);
		Expression qualifier = siteQualifier().andExp(pathExp);
		SelectQuery query = new SelectQuery(WebUrlAlias.class, qualifier);
		List<WebUrlAlias> aliases = cayenneService.sharedContext()
				.performQuery(query);

		int size = aliases.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found " + size + " aliases for query : " + query);
		}

		if (size > 1) {
			LOGGER.error("Expected one WebUrlAlias record, found " + size
					+ " for query : " + query);
		}

		return (size == 1) ? aliases.get(0) : null;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? ExpressionFactory.matchExp(
				WebUrlAlias.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()) : ExpressionFactory
				.matchExp(WebUrlAlias.WEB_SITE_PROPERTY, site);
		return expression;
	}
}
