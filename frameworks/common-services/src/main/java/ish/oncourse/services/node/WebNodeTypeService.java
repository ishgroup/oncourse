package ish.oncourse.services.node;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.BaseService;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

public class WebNodeTypeService extends BaseService<WebNodeType> implements
		IWebNodeTypeService {

	public WebNodeType getDefaultWebNodeType() {

		Expression expr = ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_PROPERTY, getWebSiteService()
						.getCurrentWebSite());

		expr = expr.andExp(ExpressionFactory
				.matchExp(WebNodeType.LAYOUT_KEY_PROPERTY,
						WebNodeType.DEFAULT_LAYOUT_KEY));

		return (WebNodeType) findByQualifier(expr).get(0);
	}

	public List<WebNodeType> getWebNodeTypes() {
		return findByQualifier(ExpressionFactory.matchExp(
				WebNodeType.WEB_SITE_PROPERTY, getWebSiteService()
						.getCurrentWebSite()));
	}
}
