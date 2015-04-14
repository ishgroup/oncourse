package ish.oncourse.admin.pages.college;

import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class ChangeDomainSite {
	
	@Inject
	private Request request;
	
	@Inject
	private ICayenneService cayenneService;
	
	public StreamResponse onActivate() {
		
		String domainName = request.getParameter("domain");
		String siteKey = request.getParameter("site");
		
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Expression exp = ExpressionFactory.matchExp(WebHostName.NAME_PROPERTY, domainName);
		SelectQuery query = new SelectQuery(WebHostName.class, exp);
		WebHostName domain = (WebHostName) Cayenne.objectForQuery(context, query);
		
		exp = ExpressionFactory.matchExp(WebSite.SITE_KEY_PROPERTY, siteKey);
		query = new SelectQuery(WebSite.class, exp);
		WebSite selectedSite = (WebSite) Cayenne.objectForQuery(context, query);
		
		if (domain != null && selectedSite != null) {
			domain.setWebSite(selectedSite);
			context.commitChanges();
		}
		
		return new TextStreamResponse("text/plain", "OK");
	}

}
