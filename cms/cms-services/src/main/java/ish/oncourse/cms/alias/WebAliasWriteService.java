package ish.oncourse.cms.alias;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.cms.services.state.ISessionStoreService;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;

public class WebAliasWriteService implements IWebAliasWriteService {

	@Inject
	private ISessionStoreService sessionStoreService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private IWebNodeService webNodeService;

	public void removeAlias(final WebUrlAlias alias) {
		ObjectContext ctx = sessionStoreService.sessionContext();
		
		WebUrlAlias obj = (WebUrlAlias) ctx.localObject(alias.getObjectId(),
				null);
		
		ctx.deleteObject(obj);
		ctx.commitChanges();
	}

	public WebUrlAlias create(Long nodeId, String urlPath) {
		ObjectContext ctx = sessionStoreService.sessionContext();
		
		WebUrlAlias alias = ctx.newObject(WebUrlAlias.class);
		
		alias.setUrlPath(urlPath);
		alias.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
		alias.setWebNode((WebNode) ctx.localObject(webNodeService.getNodeById(nodeId).getObjectId(), null));
		
		ctx.commitChanges();
		
		return alias;
	}
	
	
}
