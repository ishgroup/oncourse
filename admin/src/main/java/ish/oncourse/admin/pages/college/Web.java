package ish.oncourse.admin.pages.college;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WillowUser;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.DeleteDenyException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class Web {
	
	@Property
	@Persist
	private College college;
	
	@Property
	private List<WebSite> sites;
	
	@Property
	private List<WebHostName> domains;
	
	@Property
	private List<WillowUser> cmsUsers;
	
	@Property
	private WebHostName currentDomain;
	
	@Property
	private WebSite currentSite;
	
	@Property
	private WillowUser currentUser;
	
	@Property
	private String newSiteNameValue;
	
	@Property
	private String newSiteKeyValue;
	
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
	
	@Property
	private String changeSiteUrl;
	
	@Property
	@Persist
	private StringSelectModel siteSelectModel;
	
	@Property
	@Persist
	private boolean siteDeleteFailed;
	
	private String selectedSite;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

	@SetupRender
	void setupRender() {
		this.changeSiteUrl = response.encodeURL(request.getContextPath() + "/college/changeDomainSite");
		
		this.sites = college.getWebSites();
		this.domains = college.getCollegeDomains();
		this.cmsUsers = college.getWillowUsers();
		
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
	
	@OnEvent(component="sitesForm", value="success")
	void addSite() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		Date now = new Date();
		
		WebSite site = context.newObject(WebSite.class);
		site.setCollege((College) context.localObject(college.getObjectId(), null));
		site.setName(newSiteNameValue);
		site.setSiteKey(newSiteKeyValue);
		site.setCreated(now);
		site.setModified(now);
		
		WebNodeType page = context.newObject(WebNodeType.class);
		page.setName("page");
		page.setCreated(now);
		page.setModified(now);
		page.setLayoutKey("default");
		page.setWebSite(site);
		
		WebNode node = context.newObject(WebNode.class);
		node.setName("Home page");
		node.setCreated(now);
		node.setModified(now);
		node.setWebNodeType(page);
		node.setWebSite(site);
		node.setNodeNumber(1);
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
	}
	
	@OnEvent(component="cmsUsersForm", value="success")
	void addUser() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WillowUser user = context.newObject(WillowUser.class);
		user.setCollege((College) context.localObject(college.getObjectId(), null));
		user.setEmail(newUserEmailValue);
		user.setPassword(newUserPasswordValue);
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
		
		Expression exp = ExpressionFactory.matchDbExp(WillowUser.EMAIL_PROPERTY, email);
		SelectQuery query = new SelectQuery(WillowUser.class, exp);
		WillowUser user = (WillowUser) Cayenne.objectForQuery(context, query);
		context.deleteObject(user);
		context.commitChanges();
		
		return null;
	}
	
	public String getSelectedSite() {
		return currentDomain.getWebSite().getSiteKey();
	}
	
}
