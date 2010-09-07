package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;


public class WebNodeService implements IWebNodeService {

	private static final Logger LOGGER = Logger.getLogger(
			WebNodeService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	static final String NODE_NUMBER_PARAMETER = "n";
	static final String PAGE_PATH_PARAMETER = "p";
	static final String WEB_NODE_PAGE_TYPE_KEY = "Page";


	@SuppressWarnings("unchecked")
	public List<WebNode> getNodes() {
		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		query.addOrdering(WebNode.WEIGHTING_PROPERTY, SortOrder.ASCENDING);
		return cayenneService.sharedContext().performQuery(query);
	}

	public WebNode getHomePage() {
		return webSiteService.getCurrentWebSite().getHomePage();
	}

	public WebNode getCurrentPage() {

		WebNode result = null;

		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		query.andQualifier(ExpressionFactory.matchExp(
				WebNode.WEB_NODE_TYPE_PROPERTY + "." + WebNodeType.NAME_PROPERTY,
				WEB_NODE_PAGE_TYPE_KEY));

		if(request.getAttribute(NODE)!=null){
			return (WebNode) request.getAttribute(NODE);
		}else if (request.getParameter(NODE_NUMBER_PARAMETER) != null) {
			try {
				Integer nodeNumber = Integer.parseInt(request.getParameter(NODE_NUMBER_PARAMETER));
				query.andQualifier(ExpressionFactory.matchExp(
						WebNode.NODE_NUMBER_PROPERTY, nodeNumber));
			} catch(Exception e) {
				query = null;
			}
		} else if (request.getParameter(PAGE_PATH_PARAMETER) != null) {
			String pagePath = request.getParameter(PAGE_PATH_PARAMETER);
			if ( !("".equals(pagePath))) {
				String[]nodes=pagePath.split("/");
				int length = nodes.length;
				for(int i=0;i<length;i++){
					String path = "";
					for(int j=0;j<length-1-i;j++){
						path+=WebNode.PARENT_NODE_PROPERTY+".";
					}
				
					String shortNamePath = path+WebNode.SHORT_NAME_PROPERTY;
					String namePath = path+WebNode.NAME_PROPERTY;
					String value = ("%"+nodes[i]+"%").replaceAll("[+]", " ").replaceAll("[|]","/");
					query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(shortNamePath, value)
						.orExp(ExpressionFactory.matchExp(shortNamePath, null)
							.andExp(ExpressionFactory.likeIgnoreCaseExp(namePath, value)
						)));
				}
			}else{
				query = null;
			}
		}
		if (query != null) {
			@SuppressWarnings("unchecked")
			List<WebNode> nodes = cayenneService.sharedContext()
					.performQuery(query);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Found " + nodes.size() + " nodes for query : " + query);
			}

			if (nodes.size() > 1) {
				LOGGER.error("Expected one WebNode record, found " + nodes.size()
						+ " for query : " + query);
			}

			result = (nodes.size() == 1) ? nodes.get(0) : null;
		}

		return result;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ?
			ExpressionFactory.matchExp(
					WebNode.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
					webSiteService.getCurrentCollege())
			: ExpressionFactory.matchExp(WebNode.WEB_SITE_PROPERTY, site);

		expression = expression
				.andExp(ExpressionFactory.matchExp(WebNode.IS_PUBLISHED_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(WebNode.IS_WEB_NAVIGABLE_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(WebNode.IS_WEB_VISIBLE_PROPERTY, true));
		
		return expression;
	}

	public WebNode getNode(String searchProperty, Object value) {
		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		if(searchProperty!=null){
			query.andQualifier(ExpressionFactory.matchDbExp(searchProperty, value));
		}
		List<WebNode> nodes = cayenneService.sharedContext().performQuery(query);
		return !nodes.isEmpty()?nodes.get(0):null;
	}
}
