package ish.oncourse.services.node;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class WebNodeService implements IWebNodeService {

	private static final Logger LOGGER = Logger.getLogger(WebNodeService.class);

	@Inject
	private IWebUrlAliasService webUrlAliasService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private Request request;

	@SuppressWarnings("unchecked")
	public List<WebNode> getNodes() {
		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		return cayenneService.sharedContext().performQuery(query);
	}

	public WebNode getHomePage() {
		return getNodeForNodePath("/");
	}

	public WebNode getNodeForNodeNumber(Integer nodeNumber) {
		SelectQuery query = new SelectQuery(WebNode.class);

		query.andQualifier(siteQualifier());
		query.andQualifier(ExpressionFactory.matchExp(
				WebNode.WEB_NODE_TYPE_PROPERTY + "."
						+ WebNodeType.NAME_PROPERTY, WEB_NODE_PAGE_TYPE_KEY));

		query.andQualifier(ExpressionFactory.matchExp(
				WebNode.NODE_NUMBER_PROPERTY, nodeNumber));

		@SuppressWarnings("unchecked")
		List<WebNode> nodes = cayenneService.sharedContext()
				.performQuery(query);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found " + nodes.size() + " nodes for query : "
					+ query);
		}

		if (nodes.size() > 1) {
			LOGGER.error("Expected one WebNode record, found " + nodes.size()
					+ " for query : " + query);
		}

		return (nodes.size() == 1) ? nodes.get(0) : null;
	}

	public WebNode getNodeForNodePath(String nodePath) {
		WebUrlAlias alias = webUrlAliasService.getAliasByPath(nodePath);
		if (alias == null) {
			return null;
		}
		return alias.getWebNode();
	}

	public WebNode getCurrentNode() {
		WebNode node = null;

		if (request.getAttribute(IWebNodeService.NODE) != null) {
			node = (WebNode) request.getAttribute(IWebNodeService.NODE);
		} else if (request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			node = getNodeForNodeNumber(Integer.parseInt(request
					.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER)));
		} else if (request.getAttribute(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			node = getNodeForNodeNumber(Integer.parseInt((String) request
					.getAttribute(IWebNodeService.NODE_NUMBER_PARAMETER)));
		} else if (request.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER) != null) {
			String pagePath = (String) request
					.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER);
			node = getNodeForNodePath(pagePath);
		}

		return node;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? ExpressionFactory.matchExp(
				WebNode.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()) : ExpressionFactory
				.matchExp(WebNode.WEB_SITE_PROPERTY, site);
		return expression;
	}

	public WebNode getNode(String searchProperty, Object value) {
		SelectQuery query = new SelectQuery(WebNode.class);
		query.andQualifier(siteQualifier());
		if (searchProperty != null) {
			query.andQualifier(ExpressionFactory.matchDbExp(searchProperty,
					value));
		}
		@SuppressWarnings("unchecked")
		List<WebNode> nodes = cayenneService.sharedContext()
				.performQuery(query);
		return !nodes.isEmpty() ? nodes.get(new Random().nextInt(nodes.size()))
				: null;
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery(
								"select max(wn.modified) from WebNode wn where "
										+ siteQualifier().toEJBQL("wn")))
				.get(0);
	}

	public boolean isNodeExist(String path) {
		WebUrlAlias alias = webUrlAliasService.getAliasByPath(path);
		if (alias == null) {
			return false;
		}
		return true;
	}

	private Integer getNextNodeNumber() {
		Expression siteExpr = ExpressionFactory.matchExp(
				WebNode.WEB_SITE_PROPERTY, webSiteService.getCurrentWebSite());

		Integer number = (Integer) cayenneService
				.sharedContext()
				.performQuery(
						new EJBQLQuery(
								"select max(wn.nodeNumber) from WebNode wn where "
										+ siteExpr.toEJBQL("wn"))).get(0);

		return ++number;
	}

	public List<WebNode> loadByIds(Object... ids) {
		if (ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(WebNode.class);
		q.andQualifier(ExpressionFactory.inDbExp("id", ids));
		
		return cayenneService.sharedContext().performQuery(q);
	}

	public WebNode newWebNode() {
		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);

		newPageNode.setName("New Page");

		WebSite webSite = (WebSite) ctx.localObject(webSiteService
				.getCurrentWebSite().getObjectId(), null);

		newPageNode.setWebSite(webSite);
		Integer nodeNumber = getNextNodeNumber();
		
		newPageNode.setNodeNumber(nodeNumber);

		WebNodeType webNodeType = (WebNodeType) ctx.localObject(
				webNodeTypeService.getDefaultWebNodeType().getObjectId(), null);

		newPageNode.setWebNodeType(webNodeType);

		WebContentVisibility contentVisibility = ctx
				.newObject(WebContentVisibility.class);

		contentVisibility.setRegionKey(RegionKey.CONTENT);
		contentVisibility.setWebNode(newPageNode);

		WebContent webContent = ctx.newObject(WebContent.class);
		webContent.setWebSite(webSite);
		webContent.setContent("Sample content text.");
		contentVisibility.setWebContent(webContent);

		ctx.commitChanges();
		
		return newPageNode;
	}
}
