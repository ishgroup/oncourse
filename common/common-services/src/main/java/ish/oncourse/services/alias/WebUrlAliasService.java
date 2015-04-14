package ish.oncourse.services.alias;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class WebUrlAliasService extends BaseService<WebUrlAlias> implements
		IWebUrlAliasService {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	public WebUrlAlias getAliasByPath(String path) {
		Expression pathExp = ExpressionFactory.matchExp(
				WebUrlAlias.URL_PATH_PROPERTY, path);
		Expression qualifier = siteQualifier().andExp(pathExp);
		SelectQuery query = new SelectQuery(WebUrlAlias.class, qualifier);
		@SuppressWarnings("unchecked")
		List<WebUrlAlias> aliases = cayenneService.sharedContext()
				.performQuery(query);

		int size = aliases.size();
		logger.debug("Found {} aliases for query: {}", size, query);

		if (size > 1) {
			logger.error("Expected one WebUrlAlias record, found {} for query: {}", size, query);
		}

		return (aliases.isEmpty()) ? null : aliases.get(0);
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? ExpressionFactory.matchExp(
				WebUrlAlias.WEB_SITE_VERSION_PROPERTY + "." + WebSiteVersion.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()) : ExpressionFactory
					.matchExp(WebUrlAlias.WEB_SITE_VERSION_PROPERTY, webSiteVersionService.getCurrentVersion());
		return expression;
	}

    @Override
    public List<WebUrlAlias> getRedirects() {
        ObjectContext context = cayenneService.newContext();

        Expression expression = ExpressionFactory.matchExp(WebUrlAlias.WEB_SITE_VERSION_PROPERTY, webSiteVersionService.getCurrentVersion());
        expression = expression.andExp(ExpressionFactory.matchExp(WebUrlAlias.WEB_NODE_PROPERTY, null));

        SelectQuery query = new SelectQuery(WebUrlAlias.class, expression);
        query.addOrdering(new Ordering(WebUrlAlias.MODIFIED_PROPERTY, SortOrder.DESCENDING));
        return context.performQuery(query);
    }
}
