package ish.oncourse.admin.pages.college;

import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
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
		
		WebHostName domain = ObjectSelect.query(WebHostName.class).
				where(WebHostName.NAME.eq(domainName)).
				selectOne(context);
		
		WebSite selectedSite = ObjectSelect.query(WebSite.class).
				where(WebSite.SITE_KEY.eq(siteKey)).
				selectOne(context);
		
		if (domain != null && selectedSite != null) {
			domain.setWebSite(selectedSite);
			context.commitChanges();
		}
		
		return new TextStreamResponse("text/plain", "OK");
	}

}
