package ish.oncourse.admin.pages.college;

import java.util.Date;
import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WillowUser;
import ish.oncourse.selectutils.StringSelectModel;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

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
	private String newUserEmailValue;
	
	@Property
	private String newUserPasswordValue;
	
	@Property
	private String newUserFirstNameValue;
	
	@Property
	private String newUserLastNameValue;
	
	@Property
	@Persist
	private StringSelectModel siteSelectModel;
	
	private String selectedSite;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;
	
	Object onActivate(Long id) {
		this.college = collegeService.findById(id);
		return null;
	}

	@SetupRender
	void setupRender() {
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
	
	@OnEvent(component="domainsForm", value="success")
	void addDomain() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WebHostName domain = context.newObject(WebHostName.class);
		domain.setCollege((College) context.localObject(college.getObjectId(), null));
		domain.setName(newDomainValue);
		domain.setCreated(new Date());
		domain.setModified(new Date());
		
		context.commitChanges();
	}
	
	@OnEvent(component="sitesForm", value="success")
	void addSite() {
		ObjectContext context = cayenneService.newNonReplicatingContext();
		
		WebSite site = context.newObject(WebSite.class);
		site.setCollege((College) context.localObject(college.getObjectId(), null));
		site.setName(newSiteNameValue);
		site.setSiteKey(newSiteKeyValue);
		site.setCreated(new Date());
		site.setModified(new Date());
		
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
		context.deleteObject(site);
		context.commitChanges();
		
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
	
	public void setSelectedSite(String value) {
		
	}
	
}
