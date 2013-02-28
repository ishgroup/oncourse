package ish.oncourse.admin.pages.college;

import ish.oncourse.model.*;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.util.SecurityUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DeleteDenyException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Web {

    public final static String DEFAULT_HOME_PAGE_NAME = "Home page";
	
	@Property
	@Persist
	private College college;
	
	@Property
	@Persist
	private Map<String, WebSite> sites;
	
	@SuppressWarnings("all")
	@Property
	private List<WebHostName> domains;
	
	@SuppressWarnings("all")
	@Property
	private List<WillowUser> cmsUsers;
	
	@Property
	private WebHostName currentDomain;
	
	@SuppressWarnings("all")
	@Property
	private WebSite currentSite;
	
	@SuppressWarnings("all")
	@Property
	private WillowUser currentUser;
	
	@Property
	private String newSiteNameValue;
	
	@Property
	private String newSiteKeyValue;
	
	@Property
	private String newSiteGoogleAnalyticsValue;
	
	@Property
	private String newDomainValue;
	
	@Property
	private String newDomainSite;
	
	@Property
	private String newUserEmailValue;
	
	@Property
	private String newUserPasswordValue;
	
	@Property
	private String newUserFirstNameValue;
	
	@Property
	private String newUserLastNameValue;
	
	@SuppressWarnings("all")
	@Property
	private String changeSiteUrl;
	
	@Property
	private String currentSiteKey;
	
	@SuppressWarnings("all")
	@Property
	@Persist
	private StringSelectModel siteSelectModel;
	
	@SuppressWarnings("all")
	@Property
	@Persist
	private boolean siteDeleteFailed;
	
	@SuppressWarnings("all")
	private String selectedSite;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;

    @Inject
    private IWebNodeService webNodeService;
	
	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

	@SuppressWarnings("unchecked")
	@SetupRender
	void setupRender() {
		this.changeSiteUrl = response.encodeURL(request.getContextPath() + "/college/changeDomainSite");
		this.sites = new HashMap<String, WebSite>();
		
		ObjectContext context = cayenneService.sharedContext();
		
		College college = (College) context.localObject(this.college.getObjectId(), null);
		
		Expression sitesExp = ExpressionFactory.matchExp(WebSite.COLLEGE_PROPERTY, college);
		Expression domainsExp = ExpressionFactory.matchExp(WebHostName.COLLEGE_PROPERTY, college);
		Expression cmsUsersExp = ExpressionFactory.matchExp(WillowUser.COLLEGE_PROPERTY, college);
		
		SelectQuery sitesQuery = new SelectQuery(WebSite.class, sitesExp);
		SelectQuery domainsQuery = new SelectQuery(WebHostName.class, domainsExp);
		SelectQuery cmsUsersQuery = new SelectQuery(WillowUser.class, cmsUsersExp);
		
		sitesQuery.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
		domainsQuery.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
		cmsUsersQuery.setCacheStrategy(QueryCacheStrategy.NO_CACHE);
		
		List<WebSite> sites = context.performQuery(sitesQuery);
		for (WebSite site : sites) {
			this.sites.put(site.getSiteKey(), site);
		}
		this.domains = context.performQuery(domainsQuery);
		this.cmsUsers = context.performQuery(cmsUsersQuery);
		
		String[] siteKeys = new String[sites.size()];
		int i = 0;
		for (WebSite site : sites) {
			siteKeys[i] = site.getSiteKey();
			i++;
		}
		siteSelectModel = new StringSelectModel(siteKeys);
	}
	
	@AfterRender
	void afterRender() {
		this.siteDeleteFailed = false;
	}
	
	@OnEvent(component="newDomainForm", value="success")
	void addDomain() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WebHostName domain = context.newObject(WebHostName.class);
		domain.setCollege((College) context.localObject(college.getObjectId(), null));
		
		Expression exp = ExpressionFactory.matchExp(WebSite.SITE_KEY_PROPERTY, newDomainSite);
		SelectQuery query = new SelectQuery(WebSite.class, exp);
		WebSite site = (WebSite) Cayenne.objectForQuery(context, query);
		
		domain.setWebSite(site);
		domain.setName(newDomainValue);
		domain.setCreated(new Date());
		domain.setModified(new Date());
		
		context.commitChanges();
	}
	
	@OnEvent(component="sitesEditForm", value="success")
	void saveSites() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		for (String key : sites.keySet()) {
			if (sites.get(key) != null) {
				WebSite webSite = (WebSite) context.localObject(sites.get(key).getObjectId(), null);
				
				if (webSite != null) {
					webSite.setGoogleAnalyticsAccount(sites.get(key).getGoogleAnalyticsAccount());
				}
			}
		}
		
		context.commitChanges();
	}
	
	@OnEvent(component="sitesForm", value="success")
	void addSite() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		Date now = new Date();
		
		WebSite site = context.newObject(WebSite.class);
		site.setCollege((College) context.localObject(college.getObjectId(), null));
		site.setName(newSiteNameValue);
		site.setSiteKey(newSiteKeyValue);
		site.setGoogleAnalyticsAccount(newSiteGoogleAnalyticsValue);
		site.setCreated(now);
		site.setModified(now);
		
		WebNodeType page = context.newObject(WebNodeType.class);
		page.setName("page");
		page.setCreated(now);
		page.setModified(now);
		page.setLayoutKey("default");
		page.setWebSite(site);

        WebNode node = webNodeService.createNewNodeBy(site, page, DEFAULT_HOME_PAGE_NAME, DEFAULT_HOME_PAGE_NAME, 1);
		node.setPublished(true);
		
		WebMenu menu = context.newObject(WebMenu.class);
		menu.setName("Home");
		menu.setCreated(now);
		menu.setModified(now);
		menu.setWebSite(site);
		menu.setWeight(1);
		menu.setWebNode(node);
		
		College college = (College) context.localObject(this.college.getObjectId(), null);
		if (college != null) {
			Expression exp = ExpressionFactory.matchExp(Tag.COLLEGE_PROPERTY, college).andExp(
					ExpressionFactory.matchExp(Tag.NAME_PROPERTY, Tag.SUBJECTS_TAG_NAME));
			@SuppressWarnings("unchecked")
			List<Tag> subjectsTags = context.performQuery(new SelectQuery(Tag.class, exp));
			
			if (subjectsTags.size() == 0) {
				Tag tag = context.newObject(Tag.class);
				tag.setCollege((College) context.localObject(college.getObjectId(), null));
				tag.setName(Tag.SUBJECTS_TAG_NAME);
				tag.setIsWebVisible(true);
				tag.setIsTagGroup(true);
				tag.setCreated(now);
				tag.setModified(now);
			}
		}
		
		context.commitChanges();
		
		WebUrlAlias urlAlias = context.newObject(WebUrlAlias.class);
		urlAlias.setWebSite(site);
		urlAlias.setUrlPath("/");
		urlAlias.setWebNode(node);
		node.setDefaultWebURLAlias(urlAlias);
		
		context.commitChanges();
	}
	
	@OnEvent(component="cmsUsersForm", value="success")
	void addUser() throws UnsupportedEncodingException {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WillowUser user = context.newObject(WillowUser.class);
		user.setCollege((College) context.localObject(college.getObjectId(), null));
		user.setEmail(newUserEmailValue);
		final String hashedPassword = SecurityUtil.hashPassword(newUserPasswordValue);
		user.setPassword(newUserPasswordValue);//TODO: migrate when found logic which will update old passwords
		user.setFirstName(newUserFirstNameValue);
		user.setLastName(newUserLastNameValue);
		
		context.commitChanges();
	}
	
	Object onActionFromDeleteDomain(String name) {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Expression exp = ExpressionFactory.matchExp(WebHostName.NAME_PROPERTY, name);
		SelectQuery query = new SelectQuery(WebHostName.class, exp);
		WebHostName domain = (WebHostName) Cayenne.objectForQuery(context, query);
		context.deleteObject(domain);
		context.commitChanges();
		
		return null;
	}
	
	Object onActionFromDeleteSite(String siteKey) {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Expression exp = ExpressionFactory.matchExp(WebSite.SITE_KEY_PROPERTY, siteKey);
		SelectQuery query = new SelectQuery(WebSite.class, exp);
		WebSite site = (WebSite) Cayenne.objectForQuery(context, query);
			
		List<WebNode> nodes = new ArrayList<WebNode>(site.getWebNodes());
		List<WebNodeType> nodeTypes = new ArrayList<WebNodeType>(site.getWebNodeTypes());
		List<WebMenu> menus = new ArrayList<WebMenu>(site.getWebMenus());
		
		if (nodes.size() > 1 && menus.size() > 1) {
			siteDeleteFailed = true;
			return null;
		}
		
		if (nodes.size() == 1 && !"Home page".equals(nodes.get(0).getName())) {
			siteDeleteFailed = true;
			return null;
		}
		
		if (menus.size() == 1 && !"Home".equals(menus.get(0).getName())) {
			siteDeleteFailed = true;
			return null;
		}
		
		try {
			for (WebNode node : nodes) {
				context.deleteObject(node);
			}
			
			for (WebNodeType nodeType : nodeTypes) {
				context.deleteObject(nodeType);
			}
				
			for (WebMenu menu : menus) {
				context.deleteObject(menu);
			}
			context.deleteObject(site);
				
			context.commitChanges();
		} catch (DeleteDenyException e) {
			this.siteDeleteFailed = true;
			return null;
		}
		
		return null;
	}
	
	Object onActionFromDeleteUser(String email) {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		Expression exp = ExpressionFactory.matchDbExp(WillowUser.EMAIL_PROPERTY, email)
				.andExp(ExpressionFactory.matchExp(WillowUser.COLLEGE_PROPERTY, college));
		SelectQuery query = new SelectQuery(WillowUser.class, exp);
		WillowUser user = (WillowUser) Cayenne.objectForQuery(context, query);
		context.deleteObject(user);
		context.commitChanges();
		
		return null;
	}
	
	public String getSelectedSite() {
		return currentDomain.getWebSite().getSiteKey();
	}
	
	public WebSite getCurrentWebSite() {
		return this.sites.get(currentSiteKey);
	}
	
}
