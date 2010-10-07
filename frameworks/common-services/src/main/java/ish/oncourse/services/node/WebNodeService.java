package ish.oncourse.services.node;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
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

	public WebNode getNodeForNodeName(String nodeName) {
		WebNode result = null;

		if (!("".equals(nodeName))) {
			SelectQuery query = new SelectQuery(WebNode.class);
			query.andQualifier(siteQualifier());
			query.andQualifier(ExpressionFactory
					.matchExp(WebNode.WEB_NODE_TYPE_PROPERTY + "."
							+ WebNodeType.NAME_PROPERTY, WEB_NODE_PAGE_TYPE_KEY));

			String[] names = nodeName.split("/");
			int length = names.length;
			for (int i = 0; i < length; i++) {

				String path = "";
				for (int j = 0; j < length - 1 - i; j++) {
					path += WebNode.PARENT_NODE_PROPERTY + ".";
				}

				String shortNamePath = path + WebNode.SHORT_NAME_PROPERTY;
				String namePath = path + WebNode.NAME_PROPERTY;
				String value = ("%" + names[i] + "%").replaceAll("[+]", " ")
						.replaceAll("[|]", "/");
				query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(
						shortNamePath, value).orExp(
						ExpressionFactory.matchExp(shortNamePath, null).andExp(
								ExpressionFactory.likeIgnoreCaseExp(namePath,
										value))));
			}

			@SuppressWarnings("unchecked")
			List<WebNode> nodes = cayenneService.sharedContext().performQuery(
					query);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Found " + nodes.size() + " nodes for query : "
						+ query);
			}

			if (nodes.size() > 1) {
				LOGGER.error("Expected one WebNode record, found "
						+ nodes.size() + " for query : " + query);
			}

			result = (nodes.size() == 1) ? nodes.get(0) : null;
		}

		return result;
	}

	public WebNode getCurrentNode() {
		WebNode node = null;

		if (request.getAttribute(IWebNodeService.NODE) != null) {
			node = (WebNode) request.getAttribute(IWebNodeService.NODE);
		} else if (request.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER) != null) {
			node = getNodeForNodeNumber(Integer.parseInt(request
					.getParameter(IWebNodeService.NODE_NUMBER_PARAMETER)));
		} else if (request.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER) != null) {
			String pagePath = (String) request
					.getAttribute(IWebNodeService.PAGE_PATH_PARAMETER);
			node = getNodeForNodeName(pagePath);
		}

		return (node == null) ? getHomePage() : node;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? ExpressionFactory.matchExp(
				WebNode.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()) : ExpressionFactory
				.matchExp(WebNode.WEB_SITE_PROPERTY, site);

		expression = expression
				.andExp(ExpressionFactory.matchExp(
						WebNode.IS_PUBLISHED_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(
						WebNode.IS_WEB_NAVIGABLE_PROPERTY, true))
				.andExp(ExpressionFactory.matchExp(
						WebNode.IS_WEB_VISIBLE_PROPERTY, true));

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
		return !nodes.isEmpty() ? nodes.get(0) : null;
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
		String[] nodes = path.split("/");
		WebNode node = getNodeForNodeName(nodes[nodes.length - 1]);
		if (node == null) {
			return false;
		}
		for (int i = nodes.length - 2; i >= 0; i--) {
			WebNode parentNode = node.getParentNode();
			if (!(nodes[i].equals(parentNode.getShortName()) || nodes[i]
					.equals(parentNode.getName()))) {
				return false;
			}
		}
		return true;
	}
}
