package ish.oncourse.services.node;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.BaseService;

import java.util.List;

import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebNodeTypeService extends BaseService<WebNodeType> implements
		IWebNodeTypeService {
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	public WebNodeType getDefaultWebNodeType() {
		IWebSiteService webSiteService = getWebSiteService();
		
		Expression expr = ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_VERSION_PROPERTY, 
				webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite()));

		expr = expr.andExp(ExpressionFactory
				.matchExp(WebNodeType.LAYOUT_KEY_PROPERTY,
						WebNodeType.DEFAULT_LAYOUT_KEY));
		
		List<WebNodeType> webNodeTypes = findByQualifier(expr);

		return (!webNodeTypes.isEmpty()) ? webNodeTypes.get(0) : null;
	}

	public List<WebNodeType> getWebNodeTypes() {
		IWebSiteService webSiteService = getWebSiteService();
		
		return findByQualifier(ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_VERSION_PROPERTY,
				webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite())));
	}
}
