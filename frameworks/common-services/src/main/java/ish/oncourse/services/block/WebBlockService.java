package ish.oncourse.services.block;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

public class WebBlockService implements IWebBlockService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	public WebBlock getWebBlock(String searchProperty, Object value) {
		WebSite currentSite = webSiteService.getCurrentWebSite();
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = ExpressionFactory.matchExp(
				WebBlock.WEB_SITE_PROPERTY, currentSite);
		if (WebBlock.NAME_PROPERTY.equals(searchProperty)) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					WebBlock.NAME_PROPERTY, value));
		}
		SelectQuery query = new SelectQuery(WebBlock.class, qualifier);
		@SuppressWarnings("unchecked")
		List<WebBlock> listResult = sharedContext.performQuery(query);
		return !listResult.isEmpty() ? listResult.get(0) : null;
	}

}
