package ish.oncourse.services.node;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.cayenne.Cayenne;
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
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	@Inject
	private IWebNodeTypeService webNodeTypeService;

	@Override
	public WebNode findById(Long willowId) {

		Expression qualifier = ExpressionFactory.matchDbExp(WebNode.ID_PK_COLUMN, willowId);

		SelectQuery q = new SelectQuery(WebNode.class, siteQualifier().andExp(qualifier));

		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY);
		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY + "." + WebContentVisibility.WEB_CONTENT_PROPERTY);

		appyCacheSettings(q);

		return (WebNode) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	@SuppressWarnings("unchecked")
	public List<WebNode> getNodes() {

		SelectQuery q = new SelectQuery(WebNode.class, siteQualifier());
		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY);
		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY + "." + WebContentVisibility.WEB_CONTENT_PROPERTY);

		appyCacheSettings(q);

		return cayenneService.sharedContext().performQuery(q);
	}

	public WebNode getHomePage() {
		return getNodeForNodePath("/");
	}

	public WebNode getNodeForNodeNumber(Integer nodeNumber) {
		Expression expr = siteQualifier();
		expr = expr.andExp(ExpressionFactory.matchExp(WebNode.NODE_NUMBER_PROPERTY, nodeNumber));

		SelectQuery q = new SelectQuery(WebNode.class, expr);
		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY);
		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY + "." + WebContentVisibility.WEB_CONTENT_PROPERTY);

		appyCacheSettings(q);

		@SuppressWarnings("unchecked")
		List<WebNode> nodes = cayenneService.sharedContext().performQuery(q);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found " + nodes.size() + " nodes for expr : " + expr);
		}

		if (nodes.size() > 1) {
			LOGGER.error("Expected one WebNode record, found " + nodes.size() + " for query : " + expr);
		}

		return (nodes.size() == 1) ? nodes.get(0) : null;
	}

	public WebNode getNodeForNodePath(String nodePath) {

		Expression expr = siteQualifier();
		expr = expr.andExp(ExpressionFactory.matchExp(WebNode.WEB_URL_ALIASES_PROPERTY + "." + WebUrlAlias.URL_PATH_PROPERTY, nodePath));

		SelectQuery q = new SelectQuery(WebNode.class, expr);

		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY);
		q.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY + "." + WebContentVisibility.WEB_CONTENT_PROPERTY);

		appyCacheSettings(q);

		return (WebNode) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	public WebNode getCurrentNode() {

		WebNode node = null;

		if (request.getAttribute(IWebNodeService.NODE) != null) {
			
			node = (WebNode) request.getAttribute(IWebNodeService.NODE);
			
		} else if (request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {

			Integer nodeNumber = null;

			try {
				nodeNumber = Integer.parseInt(request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER));
			} catch (NumberFormatException e) {
				LOGGER.debug("Can not parse nodeNumber.", e);
			}

			if (nodeNumber != null) {
				node = getNodeForNodeNumber(nodeNumber);
			}

		} else if (request.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER) != null) {
			
			String pagePath = (String) request.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER);
			node = getNodeForNodePath(pagePath);
		}

		return node;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? ExpressionFactory.matchExp(WebNode.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()) : ExpressionFactory.matchExp(WebNode.WEB_SITE_PROPERTY, site);
		return expression;
	}

	public WebNode getRandomNode() {

		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = siteQualifier();
		EJBQLQuery q = new EJBQLQuery("select count(i) from WebNode i where " + qualifier.toEJBQL("i"));
		Long count = (Long) sharedContext.performQuery(q).get(0);
		WebNode randomResult = null;
		int attempt = 0;

		while (randomResult == null && attempt++ < 5) {
			int random = new Random().nextInt(count.intValue());

			SelectQuery query = new SelectQuery(WebNode.class, qualifier);
			query.setFetchOffset(random);
			query.setFetchLimit(1);

			query.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY);
			query.addPrefetch(WebNode.WEB_CONTENT_VISIBILITY_PROPERTY + "." + WebContentVisibility.WEB_CONTENT_PROPERTY);

			appyCacheSettings(query);

			randomResult = (WebNode) Cayenne.objectForQuery(sharedContext, query);
		}

		return randomResult;
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService.sharedContext()
				.performQuery(new EJBQLQuery("select max(wn.modified) from WebNode wn where " + siteQualifier().toEJBQL("wn"))).get(0);
	}

	public synchronized Integer getNextNodeNumber() {
		Expression siteExpr = ExpressionFactory.matchExp(WebNode.WEB_SITE_PROPERTY, webSiteService.getCurrentWebSite());

		Integer number = (Integer) cayenneService.sharedContext()
				.performQuery(new EJBQLQuery("select max(wn.nodeNumber) from WebNode wn where " + siteExpr.toEJBQL("wn"))).get(0);
		return ++number;
	}

	@Override
	public synchronized WebNode createNewNode() {
		ObjectContext ctx = cayenneService.newContext();

		WebNode newPageNode = ctx.newObject(WebNode.class);
		newPageNode.setName("New Page");
		WebSite webSite = (WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null);
		newPageNode.setWebSite(webSite);
		newPageNode.setNodeNumber(getNextNodeNumber());

		newPageNode.setWebNodeType((WebNodeType) ctx.localObject(webNodeTypeService.getDefaultWebNodeType().getObjectId(), null));

		WebContent webContent = ctx.newObject(WebContent.class);
		webContent.setWebSite(webSite);
		webContent.setContent("Sample content");

		WebContentVisibility webContentVisibility = ctx.newObject(WebContentVisibility.class);
		webContentVisibility.setWebNode(newPageNode);
		webContentVisibility.setRegionKey(RegionKey.content);
		webContentVisibility.setWebContent(webContent);

		return newPageNode;
	}

	private void appyCacheSettings(SelectQuery query) {
		// TODO: uncomment after we've properly configure JGroups or JMS, and
		// fix https://issues.apache.org/jira/browse/CAY-1585

		/*
		 * query.setCacheGroups(CacheGroup.PAGES.name());
		 * query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		 */
	}
}
