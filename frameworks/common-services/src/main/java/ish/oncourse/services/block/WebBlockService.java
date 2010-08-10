package ish.oncourse.services.block;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebTagged;
import ish.oncourse.services.persistence.ICayenneService;
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
				WebBlock.SITE_PROPERTY, currentSite);
		if (WebBlock.NAME_PROPERTY.equals(searchProperty)) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					WebBlock.NAME_PROPERTY, value));
		} else if (WebTagged.TAG_PROPERTY.equals(searchProperty)) {
			EJBQLQuery taggedIdsQuery = new EJBQLQuery("Select wt."
					+ WebTagged.ENTITY_RECORD_ID_PROPERTY
					+ " from WebTagged wt where wt."
					+ WebTagged.ENTITY_IDENTIFIER_PROPERTY + "= :ent and wt."
					+ WebTagged.TAG_PROPERTY+"."+Tag.NAME_PROPERTY + "= :tag");
			taggedIdsQuery.setParameter("ent", WebBlock.class.getSimpleName());
			taggedIdsQuery.setParameter("tag", value);
			List taggedIds = sharedContext.performQuery(taggedIdsQuery);
			qualifier = qualifier.andExp(ExpressionFactory.inExp(
					WebBlock.ID_PROPERTY, taggedIds));
		}
		SelectQuery query = new SelectQuery(WebBlock.class, qualifier);
		List<WebBlock> listResult = sharedContext.performQuery(query);
		return !listResult.isEmpty() ? listResult.get(0) : null;
	}

}
