package ish.oncourse.services.node;

import ish.oncourse.model.*;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class WebNodeService extends BaseService<WebNode> implements IWebNodeService {
	
	private static final String SAMPLE_WEB_CONTENT = "Sample content";
	private static final String NEW_PAGE_WEB_NODE_NAME = "New Page";
	private static final String DOT_CHARACTER = ".";
	private static final String LEFT_SLASH_CHARACTER = "/";
	private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_PATH_TEMPLATE = "/page/%s";

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	@Inject
	private IWebNodeTypeService webNodeTypeService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	@Inject
	private ITextileConverter textileConverter;

    @Inject
    private IRequestCacheService requestCacheService;

	@Override
	public WebNode findById(Long willowId) {
		ObjectSelect<WebNode> q = ObjectSelect.query(WebNode.class)
				.and(ExpressionFactory.matchDbExp(WebNode.ID_PK_COLUMN, willowId));
		applyCommons(q);
		return q.selectFirst(cayenneService.newContext());
	}

	@SuppressWarnings("unchecked")
	public List<WebNode> getNodes() {
		ObjectSelect<WebNode> q = ObjectSelect.query(WebNode.class);
		applyCommons(q);
		return q.select(cayenneService.newContext());
	}

	public WebNode getHomePage() {
		return getNodeForNodePath(LEFT_SLASH_CHARACTER);
	}

	public WebNode getNodeForNodeNumber(Integer nodeNumber) {
		ObjectSelect<WebNode> q = ObjectSelect.query(WebNode.class).and(WebNode.NODE_NUMBER.eq(nodeNumber));
		applyCommons(q);
		return q.selectFirst(cayenneService.newContext());
	}

	public WebNode getNodeForNodePath(String nodePath) {
		ObjectSelect<WebNode> q = ObjectSelect.query(WebNode.class)
				.and(WebNode.WEB_URL_ALIASES.dot(WebUrlAlias.URL_PATH).eq(nodePath));
		applyCommons(q);
		return q.selectFirst(cayenneService.newContext());
	}

	public WebNode getCurrentNode() {
		WebNode node = null;
        if (request.getParameter(NODE_NUMBER_PARAMETER) != null
				|| request.getAttribute(NODE_NUMBER_PARAMETER) != null) {
			String nodeNumberString = request.getParameter(NODE_NUMBER_PARAMETER) != null ? request
					.getParameter(NODE_NUMBER_PARAMETER) : (String) request
					.getAttribute(NODE_NUMBER_PARAMETER);
			try {
                Integer nodeNumber = Integer.parseInt(nodeNumberString);
                node = getNodeForNodeNumber(nodeNumber);
			} catch (NumberFormatException e) {
				logger.debug("Can not parse nodeNumber.", e);
			}
		}

		if (node == null && request.getAttribute(PAGE_PATH_PARAMETER) != null) {
			String pagePath = (String) request.getAttribute(PAGE_PATH_PARAMETER);
			node = getNodeForNodePath(pagePath);
		}
		return node;
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		return (site == null) ? WebNode.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(webSiteService.getCurrentCollege()):
		WebNode.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion());
	}

	public WebNode getRandomNode() {

		ObjectContext sharedContext = cayenneService.newContext();
		Expression qualifier = siteQualifier();
		EJBQLQuery q = new EJBQLQuery("select count(i) from WebNode i where " + qualifier.toEJBQL("i"));
		Long count = (Long) sharedContext.performQuery(q).get(0);
		WebNode randomResult = null;
		int attempt = 0;

		while (randomResult == null && attempt++ < 5) {
			int random = new Random().nextInt(count.intValue());

			ObjectSelect<WebNode> query = ObjectSelect.query(WebNode.class).offset(random);
			applyCommons(query);

			randomResult = query.selectFirst(sharedContext);
		}

		return randomResult;
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService.newContext()
				.performQuery(new EJBQLQuery("select max(wn.modified) from WebNode wn where " + siteQualifier().toEJBQL("wn"))).get(0);
	}

	public synchronized Integer getNextNodeNumber() {
		Expression siteExpr = ExpressionFactory.matchExp(WebNode.WEB_SITE_VERSION.getName(),
				webSiteVersionService.getCurrentVersion());

		Integer number = (Integer) cayenneService.newContext()
				.performQuery(new EJBQLQuery("select max(wn.nodeNumber) from WebNode wn where " + siteExpr.toEJBQL("wn"))).get(0);
        if (number == null)
            number = 0;
		return ++number;
	}

	@Override
	public synchronized WebNode createNewNode() {
		ObjectContext ctx = cayenneService.newContext();

		WebSiteVersion webSiteVersion = ctx.localObject(webSiteVersionService.getCurrentVersion());
		WebNodeType webNodeType = ctx.localObject(webNodeTypeService.getDefaultWebNodeType());
		Integer nextNodeNumber = getNextNodeNumber();
		return createNewNodeBy(webSiteVersion, webNodeType, NEW_PAGE_WEB_NODE_NAME + " (" + nextNodeNumber + ")" , SAMPLE_WEB_CONTENT, nextNodeNumber);
	}

	@Override
    public synchronized WebNode createNewNodeBy(WebSiteVersion webSiteVersion,
                                                WebNodeType webNodeType,
                                                String nodeName,
                                                String content,
                                                Integer nodeNumber)
    {
        ObjectContext ctx = webSiteVersion.getObjectContext();
        WebNode newPageNode = ctx.newObject(WebNode.class);
        newPageNode.setName(nodeName);
        newPageNode.setWebSiteVersion(webSiteVersion);
        newPageNode.setNodeNumber(nodeNumber);

        newPageNode.setWebNodeType(webNodeType);

        WebContent webContent = ctx.newObject(WebContent.class);
        webContent.setWebSiteVersion(webSiteVersion);
		webContent.setContentTextile(content);
        webContent.setContent(textileConverter.convertCoreTextile(content));

        WebContentVisibility webContentVisibility = ctx.newObject(WebContentVisibility.class);
        webContentVisibility.setWebNode(newPageNode);
        webContentVisibility.setRegionKey(RegionKey.content);
        webContentVisibility.setWebContent(webContent);

        return newPageNode;
    }

	private void applyCommons(ObjectSelect query) {
		query.and(siteQualifier());
		query.prefetch(WebNode.WEB_NODE_TYPE.disjoint());
		query.prefetch(WebNode.WEB_NODE_TYPE.dot(WebNodeType.WEB_SITE_LAYOUT).disjoint());
		query.prefetch(WebNode.WEB_CONTENT_VISIBILITY.disjoint());
		query.prefetch(WebNode.WEB_CONTENT_VISIBILITY.dot(WebContentVisibility.WEB_CONTENT).disjoint());
		query.prefetch(WebNode.WEB_URL_ALIASES.disjoint());
		query.cacheStrategy(QueryCacheStrategy.SHARED_CACHE, WebNode.class.getSimpleName());
	}

	@Override
	public WebSiteLayout getLayout() {
		WebSiteLayout layout = null;

		//if the requested site is not exist - return null
		if (webSiteService.getCurrentWebSite() == null) {
			return null;
		}

		WebNode webNode = getCurrentNode();
		if (webNode != null) {
			layout = webNode.getWebNodeType().getWebSiteLayout();
		}
		else if (webNodeTypeService.getDefaultWebNodeType() != null) {
			layout = webNodeTypeService.getDefaultWebNodeType().getWebSiteLayout();
		}

		return layout;
	}


    public String getPath(WebNode webNode) {
        WebUrlAlias defaultAlias = getDefaultWebURLAlias(webNode);

        return defaultAlias == null ? String.format(PAGE_PATH_TEMPLATE,webNode.getNodeNumber()) :
                defaultAlias.getUrlPath();
    }

    public WebUrlAlias getDefaultWebURLAlias(WebNode webNode) {
	    List<WebUrlAlias> result = WebUrlAlias.DEFAULT.eq(true).filterObjects(webNode.getWebUrlAliases());
	    if (!result.isEmpty()) {
		    return result.get(0);
	    }

	    if (webNode.getObjectId().isTemporary()) {
		    return null;
	    }

	    ObjectContext context = cayenneService.newContext();
	    return ObjectSelect.query(WebUrlAlias.class)
				.cacheStrategy(QueryCacheStrategy.SHARED_CACHE, WebUrlAlias.class.getSimpleName())
			    .and(WebUrlAlias.WEB_NODE.eq(webNode))
			    .and(WebUrlAlias.DEFAULT.eq(true))
			    .selectOne(context);
    }

	@Override
	public WebNode getNodeForName(String nodeName) {
		return ObjectSelect.query(WebNode.class)
				.cacheStrategy(QueryCacheStrategy.SHARED_CACHE, WebNode.class.getSimpleName())
				.and(siteQualifier())
				.and(WebNode.NAME.eq(nodeName))
				.selectOne(cayenneService.newContext());
	}
}
